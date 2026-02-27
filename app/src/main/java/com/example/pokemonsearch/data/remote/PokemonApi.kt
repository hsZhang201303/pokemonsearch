package com.example.pokemonsearch.data.remote

import com.example.pokemonsearch.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
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

    private fun sanitizeInput(input: String): String {
        return input
            .replace(Regex("[\"';]"), "") // 移除潜在的注⼊字符
            .replace(Regex("[\\r\\n]"), "") // 移除换⾏符
            .take(50)
    }

    // 搜索列表 Query
    private fun buildSearchQuery(name: String, limit: Int, offset: Int): String {
        // 防注入
        val sanitizedName = sanitizeInput(name)

        return """
            query {
                pokemon_v2_pokemonspecies(
                    where: { name: { _like: "%$sanitizedName%" } }
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
        return executeQuery(query) { responseBody ->
            val parsed = json.decodeFromString<GraphQLResponse>(responseBody)
            parsed.errors?.firstOrNull()?.let { error ->
                Result.failure(Exception(error.message))
            } ?: parsed.data?.let { data ->
                Result.success(data)
            } ?: Result.failure(Exception("Empty data"))
        }
    }

    suspend fun getPokemonDetail(id: Int): Result<PokemonData?> {
        val query = buildDetailQuery(id)
        return executeQuery(query) { responseBody ->
            val parsed = json.decodeFromString<GraphQLDetailResponse>(responseBody)
            parsed.errors?.firstOrNull()?.let { error ->
                Result.failure(Exception(error.message))
            } ?: parsed.data?.let { data ->
                Result.success(data)
            } ?: Result.failure(Exception("Empty data"))
        }
    }

    private suspend fun <T> executeQuery(
        queryStr: String,
        transform: (String) -> Result<T>
    ): Result<T> {
        val body = """{"query":"${queryStr.replace("\"", "\\\"").replace("\n", "\\n")}"}"""
            .toRequestBody(JSON_TYPE)

        val request = Request.Builder().url(URL).post(body).build()

        return withContext(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                if (!response.isSuccessful) {
                    Result.failure(Exception("HTTP ${response.code}"))
                } else {
                    val responseBody = response.body.string()
                    transform(responseBody)
                }
            } catch (e: IOException) {
                Result.failure(Exception("Network error: ${e.message}"))
            } catch (e: SerializationException) {
                Result.failure(Exception("Data parsing error: ${e.message}"))
            } catch (e: TimeoutCancellationException) {
                Result.failure(Exception("Request timeout"))
            } catch (e: Exception) {
                Result.failure(Exception("Unexpected error: ${e.message}"))
            }
        }
    }
}
