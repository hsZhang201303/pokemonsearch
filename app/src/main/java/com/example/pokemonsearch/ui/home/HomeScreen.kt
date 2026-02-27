package com.example.pokemonsearch.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import com.example.pokemonsearch.ui.theme.LocalDimens
import com.example.pokemonsearch.data.model.PokemonSpecie
import com.example.pokemonsearch.ui.theme.parsePokemonColor
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToDetail: (Int) -> Unit
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    // listState 提升到 HomeScreen 级别
    val listState = rememberLazyListState()
    val keyboardController = LocalSoftwareKeyboardController.current

    // 处理副作用
    LaunchedEffect(key1 = true) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is HomeEffect.ShowToast -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
                is HomeEffect.ScrollToTop -> {
                    listState.scrollToItem(0)
                }
                is HomeEffect.HideKeyboard -> {
                    keyboardController?.hide()
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(title = { Text("Pokemon Search") })
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // 搜索栏
            SearchBar(
                query = state.query,
                isLoading = state.isLoading,
                onQueryChange = { viewModel.onIntent(HomeIntent.QueryChanged(it)) },
                onSearch = { viewModel.onIntent(HomeIntent.SearchClicked) }
            )

            // 内容区域
            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    state.isLoading && state.species.isEmpty() -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                    state.error != null && state.species.isEmpty() -> {
                        ErrorView(error = state.error, onRetry = { viewModel.onIntent(HomeIntent.Retry) })
                    }
                    state.species.isEmpty() && state.query.isNotEmpty() && !state.isLoading -> {
                        Text("No results found", modifier = Modifier.align(Alignment.Center))
                    }
                    else -> {
                        PokemonList(
                            species = state.species,
                            curPage = state.page,
                            isLoadingMore = state.isLoadingMore,
                            hasMore = state.hasMore,
                            onLoadMore = { viewModel.onIntent(HomeIntent.LoadMore) },
                            onItemClick = { onNavigateToDetail(it) },
                            listState = listState
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SearchBar(query: String, isLoading: Boolean, onQueryChange: (String) -> Unit, onSearch: () -> Unit) {
    val dimens = LocalDimens.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimens.paddingLarge),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.weight(1f),
            placeholder = { Text("Input Pokemon name") }
        )
        Spacer(modifier = Modifier.width(dimens.paddingSmall))
        Button(
            onClick = onSearch,
            enabled = query.isNotEmpty() && !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(dimens.paddingLarge24),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Icon(Icons.Default.Search, contentDescription = "Search")
            }
        }
    }
}

@Composable
fun PokemonList(
    species: List<PokemonSpecie>,
    curPage: Int,
    isLoadingMore: Boolean,
    hasMore: Boolean,
    onLoadMore: () -> Unit,
    onItemClick: (Int) -> Unit,
    listState: LazyListState
) {
    val dimens = LocalDimens.current

    // 检测滚动到底部，触发加载更多 Intent
    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleIndex ->
                if (lastVisibleIndex != null && lastVisibleIndex >= species.size - 1 && hasMore && !isLoadingMore) {
                    onLoadMore()
                }
            }
    }

    LazyColumn(state = listState, contentPadding = PaddingValues(dimens.paddingLarge), verticalArrangement = Arrangement.spacedBy(dimens.paddingSmall)) {
        items(species, key = { it.id }) { item ->
            PokemonItem(specie = item, onPokemonClick = { onItemClick(it) })
        }

        item {
            if (isLoadingMore) {
                Box(modifier = Modifier.fillMaxWidth().padding(dimens.paddingLarge)) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            } else {
                // --- 显示“没有更多数据” ---
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(dimens.paddingLarge),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "共 $curPage 页，数据加载完毕",
                        color = Color.Gray,
                        fontSize = MaterialTheme.typography.bodyLarge.fontSize
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PokemonItem(specie: PokemonSpecie, onPokemonClick: (Int) -> Unit) {
    val dimens = LocalDimens.current
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(dimens.paddingLarge)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(parsePokemonColor(specie.color?.name))
                .padding(dimens.paddingLarge)
        ) {
            // 标题：物种名称
            Text(
                text = "Name: ${specie.name.replaceFirstChar { it.uppercase() }}",
                style = MaterialTheme.typography.titleLarge,
            )

            Spacer(modifier = Modifier.height(dimens.paddingExtraSmall))

            // 描述：捕获率
            Text(
                text = "Capture Rate: ${specie.captureRate ?: "N/A"}",
            )

            Spacer(modifier = Modifier.height(dimens.paddingMedium))

            // 区域：Pokemon 列表 (可点击标签)
            Text(
                text = "Pokémons:",
                style = MaterialTheme.typography.bodyMedium,
            )

            Spacer(modifier = Modifier.height(dimens.paddingSmall))

            // 【关键修改】使用 FlowRow 实现自动换行的标签布局
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(dimens.paddingSmall),
                verticalArrangement = Arrangement.spacedBy(dimens.paddingSmall)
            ) {
                specie.pokemons.forEach { pokemon ->
                    // 每个 Pokemon 是一个独立的“胶囊”形状标签
                    Box(
                        modifier = Modifier
                            .background(
                                color = Color.White.copy(alpha = 0.25f) // 半透明白色背景
                            )
                            .clickable { onPokemonClick(pokemon.id) } // 【关键】点击触发具体 ID
                            .padding(horizontal = dimens.paddingMedium, vertical = dimens.paddingSmall6),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = pokemon.name.replaceFirstChar { it.uppercase() },
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ErrorView(error: String?, onRetry: () -> Unit) {
    val dimens = LocalDimens.current
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Error: $error")
        Spacer(modifier = Modifier.height(dimens.paddingSmall))
        Button(onClick = onRetry) { Text("Retry") }
    }
}
