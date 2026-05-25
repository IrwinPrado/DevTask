// Activamos los plugins que este módulo necesita
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.hilt.android)
    // Plugins de Firebase
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")

}

android {
    namespace = "com.devtask.app"   // Identificador único de tu app
    compileSdk = 35                 // Versión del SDK con que se compila

    defaultConfig {
        applicationId = "com.devtask.app"  // ID de la app en Play Store
        minSdk = 26                        // Android 8.0 mínimo
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables { useSupportLibrary = true }
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

    // Usamos Java 17 para compatibilidad con las librerías modernas
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }

    // Habilitamos Jetpack Compose (el sistema de UI que usaremos)
    buildFeatures { compose = true }
    composeOptions { kotlinCompilerExtensionVersion = "1.5.13" }

    packaging {
        resources { excludes += "/META-INF/{AL2.0,LGPL2.1}" }
    }
}

dependencies {
    // --- UI base ---
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Compose BOM: garantiza que todas las librerías de Compose usen versiones compatibles
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended) // Íconos extra de Material

    // --- Room: base de datos local ---
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)       // Extensiones de Kotlin para Room (coroutines)
    kapt(libs.room.compiler)            // Genera el código de Room en tiempo de compilación

    // --- Hilt: inyección de dependencias ---
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose) // Integra Hilt con Navigation Compose

    // --- Navegación entre pantallas ---
    implementation(libs.navigation.compose)

    // --- Coroutines: operaciones asíncronas (leer BD sin bloquear la UI) ---
    implementation(libs.coroutines.android)

    // --- WorkManager: programar notificaciones en segundo plano ---
    implementation(libs.work.runtime.ktx)
    implementation(libs.hilt.work)
    kapt(libs.hilt.work.compiler)

    // --- Lifecycle ---
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.lifecycle.runtime.compose)

    // --- DataStore: guardar preferencias simples (como el tema oscuro/claro) ---
    implementation(libs.datastore.preferences)

    // --- Testing ---
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    testImplementation(libs.junit)

    // Firebase BOM — garantiza versiones compatibles entre librerías Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.5.1"))

// Crashlytics — monitoreo de crashes en tiempo real
    implementation("com.google.firebase:firebase-crashlytics-ktx")

// Analytics — métricas de uso
    implementation("com.google.firebase:firebase-analytics-ktx")
}

// Necesario para que kapt no falle con tipos incorrectos
kapt { correctErrorTypes = true }