package com.example.pokemonsearch.ui.detail

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.pokemonsearch.ui.theme.LocalDimens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    viewModel: DetailViewModel,
    onBack: () -> Unit
) {
    val dimens = LocalDimens.current
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.pokemons?.pokemons?.firstOrNull()?.name?.replaceFirstChar { it.uppercase() } ?: "Detail") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(androidx.compose.ui.Alignment.Center))
                }
                state.error != null -> {
                    Column(
                        modifier = Modifier.align(androidx.compose.ui.Alignment.Center),
                        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                    ) {
                        Text("Error: ${state.error}")
                        Spacer(modifier = Modifier.height(dimens.paddingSmall))
                        Button(onClick = { viewModel.onIntent(DetailIntent.Retry) }) {
                            Text("Retry")
                        }
                    }
                }
                state.pokemons != null -> {
                    Column(modifier = Modifier.padding(dimens.paddingLarge).fillMaxWidth()) {
                        Text("Ability names:", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(dimens.paddingSmall))

                        // 假设我们显示第一个 Pokemon 的 abilities
                        state.pokemons!!.pokemons.firstOrNull()?.abilities?.forEach { abilityWrapper ->
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(vertical = dimens.paddingExtraSmall),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Text(
                                    text = abilityWrapper.ability.name.replaceFirstChar { it.uppercase() },
                                    modifier = Modifier.padding(dimens.paddingLarge)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}