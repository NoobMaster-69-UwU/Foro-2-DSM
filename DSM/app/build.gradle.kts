plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services") // 🔹 importante para Firebase
}

android {
    namespace = "com.example.controlgastos"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.controlgastos"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }
}

dependencies {
    // Firebase BOM (maneja versiones automáticamente)
    implementation(platform("com.google.firebase:firebase-bom:33.3.0"))

    // Firebase Authentication
    implementation("com.google.firebase:firebase-auth")

    // Firestore
    implementation("com.google.firebase:firebase-firestore")

    // Google Sign-In
    implementation("com.google.android.gms:play-services-auth:21.2.0")

    // Jetpack Compose
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation("androidx.compose.ui:ui:1.7.0")
    implementation("androidx.compose.material3:material3:1.3.0")
    implementation("androidx.compose.ui:ui-tooling-preview:1.7.0")
    debugImplementation("androidx.compose.ui:ui-tooling:1.7.0")
}
