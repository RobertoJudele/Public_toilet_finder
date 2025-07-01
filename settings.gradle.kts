// settings.gradle.kts

// Configure how Gradle discovers and resolves plugins.
pluginManagement {
    // Define the repositories where Gradle should look for plugins.
    repositories {
        google()             // Google's Maven repository, essential for Android and Google-specific plugins.
        gradlePluginPortal() // Gradle Plugin Portal, for common community and JetBrains plugins.
        mavenCentral()       // Maven Central, a large repository for general dependencies.
    }

    // Define the versions of the plugins used in your project.
    // This makes plugin versions consistent across all modules.
    plugins {
        // Android Application Plugin (AGP) - for building Android applications.
        // Using 8.4.1 as it's a recent stable version. Adjust if your Android Studio needs an older one.
        id("com.android.application") version "8.9.2" apply false

        // Kotlin Android Plugin - integrates Kotlin with Android.
        // Version 2.0.0 is crucial for your Kotlin 2.0.0 setup.
        id("org.jetbrains.kotlin.android") version "2.0.0" apply false

        // Jetpack Compose Compiler Plugin - for Kotlin 2.0.0+, it manages the Compose compiler extension version.
        // Its version should match your Kotlin plugin version (2.0.0).
        id("org.jetbrains.kotlin.plugin.compose") version "2.0.0" apply false

        // Google Services Plugin - for Firebase and other Google services integration.
        // Using 4.4.2 as a recent stable version.
        id("com.google.gms.google-services") version "4.4.2" apply false
    }
}

// Configure how dependencies are resolved for the entire project.
dependencyResolutionManagement {
    // Define repositories for project dependencies (artifacts like libraries).
    repositories {
        google()             // Google's Maven repository.
        mavenCentral()       // Maven Central.
    }
}

// Define the root project name and include sub-modules.
rootProject.name = "public-toilet-finder"
include(":app") // Includes your 'app' module.
