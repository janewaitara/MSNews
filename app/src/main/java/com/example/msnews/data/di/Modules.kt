package com.example.msnews.data.di

import com.example.msnews.data.api.NewsApiService
import com.example.msnews.data.utils.Constants.REST_BASE_URL
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

val networkModule = module {

    //Moshi instance with Kotlin adapter factory that Retrofit will be using to parse JSON
    single {
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    // retrofit class
    single {
        Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create(get()))
            .baseUrl(REST_BASE_URL)
            .build()
    }

    // create NewsRemoteApi
    single {
        get<Retrofit>().create(NewsApiService::class.java)
    }
}
