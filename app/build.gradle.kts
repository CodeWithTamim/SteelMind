plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.mikepenz.aboutlibraries.plugin")
}

android {
    namespace = "com.nasahacker.steelmind"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.nasahacker.steelmind"
        minSdk = 21
        targetSdk = 35
        versionCode = 2
        versionName = "1.0.1-beta"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    // ViewModel
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    // LiveData
    implementation(libs.androidx.lifecycle.livedata.ktx)
    // Retrofit
    implementation(libs.retrofit)
    // Gson Converter for Retrofit
    implementation(libs.converter.gson)
    // MMKV
    implementation(libs.mmkv)
    //responsive
    implementation(libs.sdp.android)
    implementation(libs.ssp.android)
    //circle image view
    implementation(libs.nasacircleimageview)
    //Splash
    implementation(libs.androidx.core.splashscreen)
    //lottie
    implementation(libs.lottie)
    implementation (libs.aboutlibraries.core)
    implementation (libs.aboutlibraries)

}