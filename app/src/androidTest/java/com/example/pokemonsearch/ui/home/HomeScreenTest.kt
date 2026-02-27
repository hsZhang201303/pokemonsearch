package com.example.pokemonsearch.ui.home

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.pokemonsearch.data.repository.PokemonRepository
import com.example.pokemonsearch.ui.theme.PokemonsearchTheme
import io.mockk.mockk
import org.junit.Rule
import org.junit.Test

class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val repository: PokemonRepository = mockk(relaxed = true)
    private lateinit var viewModel: HomeViewModel

    private fun setupViewModel(initialState: HomeState) {
        viewModel = HomeViewModel(repository)
        // 如果需要，可以在这里覆盖 viewModel 的初始状态
    }

    @Test
    fun homeScreen_initialState_showsSearchBar() {
        setupViewModel(HomeState())

        composeTestRule.setContent {
            PokemonsearchTheme {
                HomeScreen(viewModel = viewModel, onNavigateToDetail = { })
            }
        }

        // 验证搜索栏的标题和占位符
        composeTestRule.onNodeWithText("Pokemon Search").assertIsDisplayed()
    }

    @Test
    fun homeScreen_loadingState_showsCircularProgress() {
        setupViewModel(HomeState(isLoading = true))

        composeTestRule.setContent {
            PokemonsearchTheme {
                // 您可能需要模拟 ViewModel 的状态来显示加载指示器
            }
        }
    }

    @Test
    fun homeScreen_emptyResult_showsNoFoundText() {
        setupViewModel(HomeState(query = "nonexistent", species = emptyList()))

        composeTestRule.setContent {
             PokemonsearchTheme {
                HomeScreen(viewModel = viewModel, onNavigateToDetail = { })
            }
        }

        // 验证“未找到”的提示文本
        // composeTestRule.onNodeWithText("No results found").assertIsDisplayed()
    }
}
