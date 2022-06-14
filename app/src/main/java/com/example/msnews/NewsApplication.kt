package com.example.msnews

import android.app.Application
import com.example.msnews.data.di.apiModule
import com.example.msnews.data.di.networkModule
import com.example.msnews.data.di.presentationModule
import com.example.msnews.data.di.repositoryModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

// Initialize koin when the app is created
class NewsApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin()
    }

    private fun initKoin() {
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@NewsApplication) // injects the application context with koin

            modules(
                listOf(
                    networkModule,
                    apiModule,
                    repositoryModule,
                    presentationModule
                )
            )
        }
    }
}
