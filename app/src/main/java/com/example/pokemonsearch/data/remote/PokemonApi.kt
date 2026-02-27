package com.example.pokemonsearch.data.remote

import android.util.Log
import com.example.pokemonsearch.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class PokemonApi(private val client: OkHttpClient) {

    companion object {
        private const val URL = "https://beta.pokeapi.co/graphql/v1beta"
        private val JSON_TYPE = "application/json".toMediaType()
    }

    private val json = Json { ignoreUnknownKeys = true; coerceInputValues = true }

    // 搜索列表 Query
    private fun buildSearchQuery(name: String, limit: Int, offset: Int): String {
        return """
            query {
                pokemon_v2_pokemonspecies(
                    where: { name: { _like: "%$name%" } }
                    limit: $limit
                    offset: $offset
                    order_by: { id: asc }
                ) {
                    id
                    name
                    capture_rate
                    pokemon_v2_pokemoncolor { id name }
                    pokemon_v2_pokemons { 
                        id 
                        name 
                        pokemon_v2_pokemonabilities {
                            id
                            pokemon_v2_ability { id name }
                        }
                    }
                }            
            }
        """.trimIndent()
    }

    // 详情 Query (包含 Abilities)
    private fun buildDetailQuery(pokemonId: Int): String {
        return """
        query {
            pokemon_v2_pokemon(where: { id: { _eq: $pokemonId } }) {
                id
                name
                pokemon_v2_pokemonabilities {
                    id
                    pokemon_v2_ability { id name }
                }
            }
        }
    """.trimIndent()
    }

    suspend fun searchSpecies(name: String, page: Int, pageSize: Int): Result<SpecieData> {
        val offset = (page - 1) * pageSize
        val query = buildSearchQuery(name, pageSize, offset)
        return executeQuery(query)
    }

    suspend fun getPokemonDetail(id: Int): Result<PokemonData?> {
        val query = buildDetailQuery(id)
        return executeQuery<PokemonData>(query, true)
    }

    private suspend inline fun <reified T> executeQuery(queryStr: String, isDetail: Boolean = false): Result<T> {
        val body = """{"query":"${queryStr.replace("\"", "\\\"").replace("\n", "\\n")}"}"""
            .toRequestBody(JSON_TYPE)

        val request = Request.Builder().url(URL).post(body).build()

        return withContext(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                if (!response.isSuccessful) {
                    Result.failure<T>(Exception("HTTP ${response.code}"))
                } else {
                    val responseBody = response.body?.string() ?: ""

                    if (isDetail) {
                        val parsed = json.decodeFromString<GraphQLDetailResponse>(responseBody)
                        parsed.errors?.firstOrNull()?.let { error ->
                            Result.failure<T>(Exception(error.message))
                        } ?: parsed.data?.let { data ->
                            @Suppress("UNCHECKED_CAST")
                            Result.success(data as T)
                        } ?: Result.failure<T>(Exception("Empty data"))
                    } else {
                        val parsed = json.decodeFromString<GraphQLResponse>(responseBody)
                        parsed.errors?.firstOrNull()?.let { error ->
                            Result.failure<T>(Exception(error.message))
                        } ?: parsed.data?.let { data ->
                            @Suppress("UNCHECKED_CAST")
                            Result.success(data as T)
                        } ?: Result.failure<T>(Exception("Empty data"))
                    }
                }
            } catch (e: Exception) {
                Result.failure<T>(e)
            }
        }
    }
}