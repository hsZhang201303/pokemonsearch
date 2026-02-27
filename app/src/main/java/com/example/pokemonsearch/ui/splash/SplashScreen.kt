package com.example.pokemonsearch.ui.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SplashScreen(
    viewModel: SplashViewModel,
    onNavigateToHome: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    // 监听状态变化，如果不需要欢迎页，直接跳转
    LaunchedEffect(state.isLoading, state.showWelcome) {
        if (!state.isLoading && !state.showWelcome) {
            onNavigateToHome()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEF3350)),
        contentAlignment = Alignment.Center
    ) {
        if (state.isLoading) {
            CircularProgressIndicator(color = Color.White)
        } else if (state.showWelcome) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Welcome to", style = MaterialTheme.typography.headlineMedium, color = Color.White)
                Text("Pokemon Search Demo", style = MaterialTheme.typography.headlineLarge, color = Color.White)
                Spacer(modifier = Modifier.height(32.dp))
                Button(onClick = { viewModel.onIntent(SplashIntent.DismissWelcome) }) {
                    Text("Get Started")
                }
            }
        }
    }
}