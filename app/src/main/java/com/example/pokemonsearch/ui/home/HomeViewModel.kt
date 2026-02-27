package com.example.pokemonsearch.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokemonsearch.data.repository.PokemonRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.random.Random

class HomeViewModel(private val repository: PokemonRepository) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    private val _effect = Channel<HomeEffect>()
    val effect: Flow<HomeEffect> = _effect.receiveAsFlow()

    init {
        // 初始化时，自动触发一次搜索（空字符串，匹配所有）
        search(isRefresh = true)
    }

    fun onIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.QueryChanged -> {
                _state.update { it.copy(query = intent.query) }
            }
            is HomeIntent.SearchClicked -> {
                search(isRefresh = true)
            }
            is HomeIntent.LoadMore -> {
                search(isRefresh = false)
            }
            is HomeIntent.Retry -> {
                search(isRefresh = true)
            }
        }
    }

    private fun search(isRefresh: Boolean) {
        val currentState = _state.value

        // 防止重复搜索
        if (currentState.isLoading || currentState.isLoadingMore) return
        if (!isRefresh && !currentState.hasMore) return

        val page = if (isRefresh) 1 else currentState.page + 1
        val query = currentState.query.trim()

        viewModelScope.launch {
            // 更新 Loading 状态
            _state.update {
                if (isRefresh) it.copy(
                    isLoading = true,
                    error = null,
                    )
                else it.copy(isLoadingMore = true)
            }

            val result = repository.search(query, page)

            result.onSuccess { data ->
                _state.update { state ->
                    val newList = if (isRefresh) data.species else state.species + data.species
                    state.copy(
                        isLoading = false,
                        isLoadingMore = false,
                        species = newList,
                        page = page,
                        hasMore = data.species.size >= 20,
                        error = null
                    )
                }

                // 发送一次性事件通知 UI
                if (isRefresh) {
                    _effect.send(HomeEffect.ScrollToTop)
                    _effect.send(HomeEffect.HideKeyboard)
                }
            }.onFailure { error ->
                _state.update { it.copy(isLoading = false, isLoadingMore = false, error = error.message) }
                _effect.send(HomeEffect.ShowToast(error.message ?: "Unknown Error"))
            }
        }
    }
}
