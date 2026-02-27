package com.example.pokemonsearch.ui.splash

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SplashViewModel(private val context: Context) : ViewModel() {
    private val _state = MutableStateFlow(SplashState())
    val state = _state.asStateFlow()

    init {
        checkFirstLaunch()
    }

    private fun checkFirstLaunch() {
        viewModelScope.launch {
            val key = booleanPreferencesKey("is_first_launch")
            val isFirst = context.dataStore.data.map { it[key] ?: true }.first()

            if (isFirst) {
                _state.value = SplashState(isLoading = false, showWelcome = true)
            } else {
                _state.value = SplashState(isLoading = false, showWelcome = false)
                // 自动导航逻辑在 View 层处理
            }
        }
    }

    fun onIntent(intent: SplashIntent) {
        when (intent) {
            is SplashIntent.DismissWelcome -> {
                viewModelScope.launch {
                    context.dataStore.edit { it[booleanPreferencesKey("is_first_launch")] = false }
                    _state.value = _state.value.copy(showWelcome = false)
                }
            }
            else -> {}
        }
    }
}