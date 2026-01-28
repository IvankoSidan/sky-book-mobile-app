plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("kotlin-parcelize")
}

android {
    namespace = "com.wheezy.myjetpackproject.feature.booking"
    compileSdk = 34

    defaultConfig {
        minSdk = 26
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures { compose = true }
    composeOptions { kotlinCompilerExtensionVersion = "1.5.11" }
}

dependencies {
    // Базовые модули
    implementation(project(":core:model"))
    implementation(project(":core:common"))
    implementation(project(":core:common-vm"))
    implementation(project(":core:network"))
    implementation(project(":core:ui"))
    implementation(project(":data"))
    implementation(project(":navigation"))

    // Библиотеки
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material")
    implementation("androidx.constraintlayout:constraintlayout-compose:1.1.0")
    implementation("io.coil-kt:coil-compose:2.5.0")
    implementation("com.stripe:stripe-android:20.40.1")

    // Hilt
    implementation("com.google.dagger:hilt-android:2.50")
    kapt("com.google.dagger:hilt-android-compiler:2.50")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
}