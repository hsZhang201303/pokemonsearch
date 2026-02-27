package com.example.pokemonsearch.ui.home

import com.example.pokemonsearch.data.model.PokemonSpecie
import kotlinx.coroutines.flow.StateFlow

// 1. State: UI 的完整快照
data class HomeState(
    val query: String = "",
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val species: List<PokemonSpecie> = emptyList(),
    val error: String? = null,
    val page: Int = 1,
    val hasMore: Boolean = true,
)

// 2. Intent: 用户的意图
sealed class HomeIntent {
    data class QueryChanged(val query: String) : HomeIntent()
    object SearchClicked : HomeIntent()
    object LoadMore : HomeIntent()
    object Retry : HomeIntent()
}

// 3. Effect: 一次性副作用
sealed class HomeEffect {
    data class ShowToast(val message: String) : HomeEffect()
    data object ScrollToTop : HomeEffect()
    data object HideKeyboard : HomeEffect()

}