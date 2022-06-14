package com.example.msnews.data.di

import com.example.msnews.data.api.NewsApiService
import com.example.msnews.data.repository.NewsRepository
import com.example.msnews.data.repository.NewsRepositoryImpl
import com.example.msnews.data.utils.Constants.REST_BASE_URL
import com.example.msnews.viewmodels.NewsViewModel
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

val networkModule = module {
    /**
     * Single = singleton =  instance of an object that only exist once during the whole
     * application lifetime
     * */

    // OKHTTPClient class
    single {
        // logging interceptor
        val httpLoggingInterceptor = HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY)

        OkHttpClient.Builder()
            .writeTimeout(15, TimeUnit.SECONDS)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .addInterceptor(httpLoggingInterceptor)
            .build()
    }

    // Moshi instance with Kotlin adapter factory that Retrofit will be using to parse JSON
    single {
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    // retrofit class
    single {
        Retrofit.Builder()
            .client(get())
            .addConverterFactory(MoshiConverterFactory.create(get()))
            .baseUrl(REST_BASE_URL)
            .build()
    }

    // create NewsRemoteApi
    single {
        get<Retrofit>().create(NewsApiService::class.java)
    }
}

val apiModule = module {
    single { NewsApiService::class.java }
}

val repositoryModule = module {
    single<NewsRepository> { NewsRepositoryImpl(get()) }
}

val presentationModule = module {
    viewModel { NewsViewModel(get()) }
}
