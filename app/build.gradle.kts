plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.streamtv"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.streamtv"
        minSdk = 23            // Android 6.0 и выше
        targetSdk = 34
        versionCode = 1
        versionName = "0.1.0"
        // Адрес вашего JSON-каталога. Пусто = встроенные демо-данные.
        buildConfigField("String", "API_URL", "\"\"")
    }
    buildFeatures { buildConfig = true }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions { jvmTarget = "1.8" }
    buildTypes {
        release { isMinifyEnabled = false }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.media3:media3-exoplayer:1.3.1")
    implementation("androidx.media3:media3-exoplayer-hls:1.3.1")
    implementation("androidx.media3:media3-ui:1.3.1")
    implementation("com.github.bumptech.glide:glide:4.16.0")
}
