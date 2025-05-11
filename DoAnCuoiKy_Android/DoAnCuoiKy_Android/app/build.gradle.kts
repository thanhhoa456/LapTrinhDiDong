plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.laixea1"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.laixea1"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    buildFeatures {
        viewBinding = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation ("androidx.appcompat:appcompat:1.6.1")
    implementation ("com.google.android.material:material:1.9.0") // Cho nút, SeekBar
    implementation ("com.github.bumptech.glide:glide:4.12.0") // Cho carousel quảng cáo
    // Retrofit
    implementation ("com.squareup.retrofit2:retrofit:2.11.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.11.0")
    // Gson
    implementation ("com.google.code.gson:gson:2.11.0")
    // OkHttp (cho logging, nếu cần)
    implementation ("com.squareup.okhttp3:logging-interceptor:4.12.0")

    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.google.code.gson:gson:2.10.1")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.10.0")

    implementation ("com.squareup.picasso:picasso:2.71828")
}