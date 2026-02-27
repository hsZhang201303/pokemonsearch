package com.example.pokemonsearch.ui.home

import com.example.pokemonsearch.data.model.PokemonSpecie
import com.example.pokemonsearch.data.model.SpecieData
import com.example.pokemonsearch.data.repository.PokemonRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private lateinit var viewModel: HomeViewModel
    private val repository: PokemonRepository = mockk()
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        // 屏蔽 init 中的初始化调用，或者让它成功
        coEvery { repository.search(any(), any()) } returns Result.success(SpecieData(emptyList()))
        viewModel = HomeViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should be empty`() = runTest {
        val state = viewModel.state.value
        assertEquals("", state.query)
        assertTrue(state.species.isEmpty())
        assertEquals(false, state.isLoading)
    }

    @Test
    fun `onIntent QueryChanged should update query`() = runTest {
        viewModel.onIntent(HomeIntent.QueryChanged("Pikachu"))
        assertEquals("Pikachu", viewModel.state.value.query)
    }

    @Test
    fun `onIntent SearchClicked should update species on success`() = runTest {
        val mockSpecies = listOf(
            PokemonSpecie(id = 1, name = "bulbasaur"),
            PokemonSpecie(id = 2, name = "ivysaur")
        )
        coEvery { repository.search(any(), any()) } returns Result.success(SpecieData(mockSpecies))

        viewModel.onIntent(HomeIntent.SearchClicked)
        
        // 推进协程执行
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(mockSpecies, state.species)
        assertEquals(false, state.isLoading)
        assertEquals(null, state.error)
    }

    @Test
    fun `onIntent SearchClicked should update error on failure`() = runTest {
        val errorMessage = "Network Error"
        coEvery { repository.search(any(), any()) } returns Result.failure(Exception(errorMessage))

        viewModel.onIntent(HomeIntent.SearchClicked)
        
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(errorMessage, state.error)
        assertEquals(false, state.isLoading)
    }
}
