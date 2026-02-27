package com.example.pokemonsearch.ui.splash

import kotlinx.coroutines.flow.StateFlow

data class SplashState(
    val isLoading: Boolean = true,
    val showWelcome: Boolean = false
)

sealed class SplashIntent {
    object StartApp : SplashIntent()
    object DismissWelcome : SplashIntent()
}

// ViewModel 留到后面文件统一展示