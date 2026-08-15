// Top-level build file — plugin declarations only.
// AGP 9 includes built-in Kotlin support; kotlin-android plugin is no longer needed here.
// Version numbers are managed in gradle/libs.versions.toml
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.room) apply false
    kotlin("plugin.serialization") version "2.1.21" apply false
}
