package com.example.pokemonsearch.ui.detail

import com.example.pokemonsearch.data.model.PokemonData

data class DetailState(
    val isLoading: Boolean = true,
    val pokemons: PokemonData? = null,
    val error: String? = null
)

sealed class DetailIntent {
    object Retry : DetailIntent()
}

sealed class DetailEffect {
    object NavigateBack : DetailEffect()
}