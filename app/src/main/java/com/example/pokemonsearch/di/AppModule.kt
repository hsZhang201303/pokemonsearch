package com.example.pokemonsearch.di

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.preferencesDataStoreFile
import com.example.pokemonsearch.data.remote.PokemonApi
import com.example.pokemonsearch.data.repository.PokemonRepository
import com.example.pokemonsearch.ui.detail.DetailViewModel
import com.example.pokemonsearch.ui.home.HomeViewModel
import com.example.pokemonsearch.ui.splash.SplashViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import java.util.concurrent.TimeUnit

val appModule = module {
    // DataStore
    single {
        PreferenceDataStoreFactory.create(
            produceFile = { androidContext().preferencesDataStoreFile("settings") }
        )
    }

    // Network
    single {
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .connectTimeout(15, TimeUnit.SECONDS)  // 连接超时
            .readTimeout(30, TimeUnit.SECONDS)     // 读取超时
            .writeTimeout(30, TimeUnit.SECONDS)     // 写⼊超时
            .retryOnConnectionFailure(true)         // 连接失败⾃动重试
            .build()
    }

    single { PokemonApi(get()) }
    single { PokemonRepository(get()) }

    // ViewModels
    viewModel { SplashViewModel(get()) }
    viewModel { HomeViewModel(get()) }
    viewModel { (id: Int) -> DetailViewModel(id, get()) }
}
