package com.example.pokemonsearch.di

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.preferencesDataStoreFile
import com.example.pokemonsearch.data.remote.PokemonApi
import com.example.pokemonsearch.data.repository.PokemonRepository
import com.example.pokemonsearch.ui.detail.DetailViewModel
import com.example.pokemonsearch.ui.home.HomeViewModel
import com.example.pokemonsearch.ui.splash.SplashViewModel
import okhttp3.CertificatePinner
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
        // 实际生产环境, 需要将其替换为目标服务器真实的证书公钥哈希值
//        val certificatePinner = CertificatePinner.Builder()
//            .add("beta.pokeapi.co", "sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=")
//            .build()

        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
//            .certificatePinner(certificatePinner)
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
