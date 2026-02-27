package com.example.pokemonsearch.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokemonsearch.data.repository.PokemonRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DetailViewModel(
    private val pokemonId: Int,
    private val repository: PokemonRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DetailState())
    val state: StateFlow<DetailState> = _state.asStateFlow()

    private val _effect = Channel<DetailEffect>()
    val effect: Flow<DetailEffect> = _effect.receiveAsFlow()

    init {
        loadDetail()
    }

    fun onIntent(intent: DetailIntent) {
        when (intent) {
            is DetailIntent.Retry -> loadDetail()
            else -> {}
        }
    }

    private fun loadDetail() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            repository.getDetail(pokemonId)
                .onSuccess { pokemons ->
                    pokemons?.let { data ->
                        _state.update { it.copy(isLoading = false, pokemons = data) }
                    } ?: run {
                        _state.update { it.copy(isLoading = false, error = "Pokemon not found") }
                    }
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false, error = error.message) }
                }
        }
    }
}