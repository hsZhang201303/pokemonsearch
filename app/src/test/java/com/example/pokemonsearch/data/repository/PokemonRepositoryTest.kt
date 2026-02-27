package com.example.pokemonsearch.data.repository

import com.example.pokemonsearch.data.model.SpecieData
import com.example.pokemonsearch.data.remote.PokemonApi
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class PokemonRepositoryTest {

    private val api: PokemonApi = mockk()
    private val repository = PokemonRepository(api)

    @Test
    fun `search should return data from api`() = runTest {
        val mockData = SpecieData(emptyList())
        coEvery { api.searchSpecies("pikachu", 1, 20) } returns Result.success(mockData)

        val result = repository.search("pikachu", 1)

        assertEquals(Result.success(mockData), result)
    }
}
