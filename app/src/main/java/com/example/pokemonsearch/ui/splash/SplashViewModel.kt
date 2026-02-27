package com.example.pokemonsearch.ui.splash

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class SplashViewModel(private val dataStore: DataStore<Preferences>) : ViewModel() {
    private val _state = MutableStateFlow(SplashState())
    val state = _state.asStateFlow()

    companion object {
        private val IS_FIRST_LAUNCH = booleanPreferencesKey("is_first_launch")
    }

    init {
        checkFirstLaunch()
    }

    private fun checkFirstLaunch() {
        viewModelScope.launch {
            val isFirst = dataStore.data.map { it[IS_FIRST_LAUNCH] ?: true }.first()

            if (isFirst) {
                _state.value = SplashState(isLoading = false, showWelcome = true)
            } else {
                _state.value = SplashState(isLoading = false, showWelcome = false)
            }
        }
    }

    fun onIntent(intent: SplashIntent) {
        when (intent) {
            is SplashIntent.DismissWelcome -> {
                viewModelScope.launch {
                    dataStore.edit { it[IS_FIRST_LAUNCH] = false }
                    _state.value = _state.value.copy(showWelcome = false)
                }
            }
            else -> {}
        }
    }
}
