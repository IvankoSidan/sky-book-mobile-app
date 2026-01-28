plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.wheezy.myjetpackproject.feature.search"
    compileSdk = 34

    defaultConfig {
        minSdk = 26
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.11"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:common"))
    implementation(project(":core:ui"))
    implementation(project(":data"))
    implementation(project(":core:network"))
    implementation(project(":navigation"))
    implementation(project(":core:common-vm"))


    // Compose
    implementation(platform("androidx.compose:compose-bom:2024.02.02"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // Hilt
    implementation("com.google.dagger:hilt-android:2.50")
    kapt("com.google.dagger:hilt-android-compiler:2.50")

    // Coil (для логотипов авиакомпаний)
    implementation("io.coil-kt:coil-compose:2.5.0")
    implementation("io.github.muddz:styleabletoast:2.4.0")
    implementation("androidx.constraintlayout:constraintlayout-compose:1.1.0")
}