package com.example.msnews.data.di

import com.example.msnews.data.utils.Constants.REST_BASE_URL
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

val networkModule = module {

    single {
        Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create())
            .baseUrl(REST_BASE_URL)
            .build()
    }
}
