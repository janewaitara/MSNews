object Versions {

    //Version codes for all the libraries
    const val androidApplication = "7.2.1"
    const val androidLibrary = "7.2.1"
    const val kotlin = "1.6.21"

    const val appCompat = "1.4.1"
    const val constraintLayout = "2.1.4"
    const val ktx = "1.7.0"
    const val material = "1.6.0-alpha02"

    //Version codes for all the test libraries
    const val junit4 = "4.13.2"
    const val junit = "4.13.2"
    const val testRunner = "1.4.1-alpha03"
    const val espresso = "3.5.0-alpha03"


    // Gradle Plugins
    const val ktlint = "10.2.1"

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
}

object TestLibraries {
    //any test library is added here
    const val junit4 = "junit:junit:${Versions.junit4}"
    const val junit = "androidx.test.ext:junit:${Versions.junit}"
    const val testRunner = "androidx.test:runner:${Versions.testRunner}"
    const val espresso = "androidx.test.espresso:espresso-core:${Versions.espresso}"


}

object AndroidSdk {
    const val minSdkVersion = 21
    const val compileSdkVersion = 32
    const val targetSdkVersion = compileSdkVersion
    const val versionCode = 1
    const val versionName = "1.0"
}

