import java.io.FileInputStream
import java.util.Properties

plugins {
    id(BuildPlugins.androidApplication)
    id(BuildPlugins.kotlinAndroid)
    id(BuildPlugins.ktlintPlugin)
}

android {
    compileSdk = AndroidSdk.compileSdkVersion

    defaultConfig {
        applicationId = "com.example.msnews"
        minSdk = AndroidSdk.minSdkVersion
        targetSdk = AndroidSdk.targetSdkVersion
        versionCode = AndroidSdk.versionCode
        versionName = AndroidSdk.versionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "API_KEY", getBearerTokenFromFile())
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    implementation(Libraries.ktxCore)
    implementation(Libraries.appCompat)
    implementation(Libraries.materialComponents)
    implementation(Libraries.constraintLayout)

    // DI - KOIN
    implementation(Libraries.koin)

    // Networking
    implementation(Libraries.retrofit)
    implementation(Libraries.moshi)
    implementation(Libraries.moshiConverter)
    implementation(Libraries.okhttp)
    implementation(Libraries.loggingInterceptor)

    // Lifecycle
    implementation(Libraries.viewModel)
    implementation(Libraries.livedata)
    implementation(Libraries.lifecycle)

    // Coroutines
    implementation(Libraries.coroutines)
    implementation(Libraries.coroutinesAndroid)

    // Unit Tests
    testImplementation(TestLibraries.junit4)
    testImplementation(TestLibraries.koinTest)

    // Android tests
    androidTestImplementation(TestLibraries.junit)
    androidTestImplementation(TestLibraries.testRunner)
    androidTestImplementation(TestLibraries.espresso)

    // Instrumentation Tests
    androidTestImplementation(TestLibraries.koinTest)
}

fun getBearerTokenFromFile(): String {
    val secretsFile = rootProject.file("secrets.properties")
    val secrets = Properties()
    if (secretsFile.exists()) {
        secrets.load(FileInputStream(secretsFile))
    }
    return secrets.getProperty("API_KEY", "")
}
