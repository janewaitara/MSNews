object Versions {

    //Version codes for all the libraries
    const val androidApplication = "7.2.1"
    const val androidLibrary = "7.2.1"
    const val kotlin = "1.6.21"

    const val appCompat = "1.4.1"
    const val constraintLayout = "2.1.4"
    const val ktx = "1.7.0"
    const val material = "1.6.1"

    //Version codes for all the test libraries
    const val junit4 = "4.13.2"
    const val junit = "1.1.3"
    const val testRunner = "1.4.1-alpha03"
    const val espresso = "3.5.0-alpha03"

    // Gradle Plugins
    const val ktlint = "10.2.1"

    //DI - KOIN
    const val koin = "3.2.0"

    //Networking
    const val retrofit = "2.9.0"
    const val okhttp = "4.8.1"
    const val loggingInterceptor = "4.8.1"
    const val moshi = "1.9.3"

    // Lifecycle
    const val lifecycle = "2.4.1"

    // Coroutines
    const val coroutines = "1.3.9"

    // Navigation
    const val navigation = "2.4.2"

    //Coil
    const val coil = "1.1.1"

    //Shimmer
    const val shimmerEffect = "0.5.0"

    //Room
    const val room = "2.4.2"

    //paging
    const val  pagingVersion = "3.1.0"

}

object BuildPlugins {
    //All the build plugins are added here
    const val androidLibrary = "com.android.library"
    const val androidApplication = "com.android.application"
    const val kotlinAndroid = "org.jetbrains.kotlin.android"
    const val ktlintPlugin = "org.jlleitschuh.gradle.ktlint"
}

object Libraries {
    //Any Library is added here
    const val kotlinStandardLibrary = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Versions.kotlin}"
    const val appCompat = "androidx.appcompat:appcompat:${Versions.appCompat}"
    const val constraintLayout =
        "androidx.constraintlayout:constraintlayout:${Versions.constraintLayout}"
    const val ktxCore = "androidx.core:core-ktx:${Versions.ktx}"
    const val materialComponents = "com.google.android.material:material:${Versions.material}"

    // DI - KOIN
    const val koin = "io.insert-koin:koin-android:${Versions.koin}"

    // Networking - apollo, OKHTTP and loggingInterceptor
    const val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
    const val moshi = "com.squareup.moshi:moshi-kotlin:${Versions.moshi}"
    const val moshiConverter = "com.squareup.retrofit2:converter-moshi:${Versions.retrofit}"
    const val okhttp = "com.squareup.okhttp3:okhttp:${Versions.okhttp}"
    const val loggingInterceptor =
        "com.squareup.okhttp3:logging-interceptor:${Versions.loggingInterceptor}"

    // Lifecycle
    const val viewModel = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.lifecycle}"
    const val livedata = "androidx.lifecycle:lifecycle-livedata-ktx:${Versions.lifecycle}"
    const val lifecycle = "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.lifecycle}"

    // Coroutines
    const val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}"
    const val coroutinesAndroid =
        "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutines}"

    // Navigation
    const val navigationUI = "androidx.navigation:navigation-ui-ktx:${Versions.navigation}"
    const val navigationFragment = "androidx.navigation:navigation-fragment-ktx:${Versions.navigation}"

    //Coil
    const val coil = "io.coil-kt:coil:${Versions.coil}"

    //Shimmer Effect
    const val shimmerEffect = "com.facebook.shimmer:shimmer:${Versions.shimmerEffect}"

    //Room
    const val roomRuntime = "androidx.room:room-runtime:${Versions.room}"
    const val roomCompiler =  "androidx.room:room-compiler:${Versions.room}"
    const val roomKtx =  "androidx.room:room-ktx:${Versions.room}"
    const val roomPaging =  "androidx.room:room-paging:${Versions.room}"

    //Paging
    const val pagingRuntime = "androidx.paging:paging-runtime:${Versions.pagingVersion}"


}

object TestLibraries {
    //any test library is added here
    const val junit4 = "junit:junit:${Versions.junit4}"
    const val junit = "androidx.test.ext:junit:${Versions.junit}"
    const val testRunner = "androidx.test:runner:${Versions.testRunner}"
    const val espresso = "androidx.test.espresso:espresso-core:${Versions.espresso}"

    const val koinTest = "io.insert-koin:koin-test:${Versions.koin}"

}

object AndroidSdk {
    const val minSdkVersion = 21
    const val compileSdkVersion = 32
    const val targetSdkVersion = compileSdkVersion
    const val versionCode = 1
    const val versionName = "1.0"
}

