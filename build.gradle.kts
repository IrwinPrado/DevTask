plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.kapt) apply false
    alias(libs.plugins.hilt.android) apply false
    // Plugin de Google Services para Firebase
    id("com.google.gms.google-services") version "4.4.1" apply false
    // Plugin de Crashlytics
    id("com.google.firebase.crashlytics") version "3.0.2" apply false
}