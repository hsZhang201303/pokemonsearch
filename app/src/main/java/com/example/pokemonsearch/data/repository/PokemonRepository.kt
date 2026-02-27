package com.example.pokemonsearch.data.repository

import com.example.pokemonsearch.data.model.PokemonData
import com.example.pokemonsearch.data.model.SpecieData
import com.example.pokemonsearch.data.remote.PokemonApi

class PokemonRepository(private val api: PokemonApi) {
    suspend fun search(name: String, page: Int, pageSize: Int = 20): Result<SpecieData> {
        return api.searchSpecies(name, page, pageSize)
    }

    suspend fun getDetail(id: Int): Result<PokemonData?> {
        return api.getPokemonDetail(id)
    }
}