package com.example.pokemonsearch.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GraphQLResponse(
    val data: SpecieData? = null,
    val errors: List<GraphQLError>? = null
)

@Serializable
data class GraphQLDetailResponse(
    val data: PokemonData? = null,
    val errors: List<GraphQLError>? = null
)

@Serializable
data class GraphQLError(val message: String)

@Serializable
data class SpecieData(
    @SerialName("pokemon_v2_pokemonspecies")
    val species: List<PokemonSpecie>
)

@Serializable
data class PokemonData(
    @SerialName("pokemon_v2_pokemon")
    val pokemons: List<Pokemon>
)

@Serializable
data class PokemonSpecie(
    val id: Int,
    val name: String,
    @SerialName("capture_rate")
    val captureRate: Int? = null,
    @SerialName("pokemon_v2_pokemoncolor")
    val color: PokemonColor? = null,
    @SerialName("pokemon_v2_pokemons")
    val pokemons: List<Pokemon> = emptyList()
)

@Serializable
data class PokemonColor(val name: String)

@Serializable
data class Pokemon(
    val id: Int,
    val name: String,
    @SerialName("pokemon_v2_pokemonabilities")
    val abilities: List<PokemonAbilityWrapper> = emptyList()
)

@Serializable
data class PokemonAbilityWrapper(
    @SerialName("pokemon_v2_ability")
    val ability: Ability
)

@Serializable
data class Ability(val id: Int, val name: String)