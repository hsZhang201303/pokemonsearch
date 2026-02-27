package com.example.pokemonsearch.di

import com.example.pokemonsearch.data.remote.PokemonApi
import com.example.pokemonsearch.data.repository.PokemonRepository
import com.example.pokemonsearch.ui.detail.DetailViewModel
import com.example.pokemonsearch.ui.home.HomeViewModel
import com.example.pokemonsearch.ui.splash.SplashViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import java.util.concurrent.TimeUnit

val appModule = module {
    // Network
    single {
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
            .connectTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    single { PokemonApi(get()) }
    single { PokemonRepository(get()) }

    // ViewModels
    viewModel { SplashViewModel(get()) }
    viewModel { HomeViewModel(get()) }
    viewModel { (id: Int) -> DetailViewModel(id, get()) }
}