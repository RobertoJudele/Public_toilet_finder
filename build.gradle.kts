// build.gradle.kts (Project Level)

// This is the root-level build file where you can add configuration options common to all sub-projects/modules.

// Define the plugins that are applied to your sub-projects.
// 'apply false' means these plugins are only declared here, and applied in individual modules (like ':app').
plugins {
    // Apply the Android Application plugin defined in settings.gradle.kts.
    id("com.android.application") apply false
    // Apply the Kotlin Android plugin defined in settings.gradle.kts.
    id("org.jetbrains.kotlin.android") apply false
    // Apply the Jetpack Compose Compiler plugin defined in settings.gradle.kts.
    id("org.jetbrains.kotlin.plugin.compose") apply false
    // Apply the Google Services plugin defined in settings.gradle.kts.
    id("com.google.gms.google-services") apply false
}

// This block is for repositories and dependencies common to all modules.
// For modern Gradle, the 'buildscript' block containing classpath dependencies for plugins
// (like 'com.android.tools.build:gradle') is now managed by the 'pluginManagement' block in settings.gradle.kts.
// So, we'll keep this simple for repositories.
allprojects {
    repositories {
        google()       // Required for Google's libraries (Maps, Firebase, etc.)
        mavenCentral() // Standard Maven repository for many libraries.
    }
}

// This block defines tasks that can be run from the root project, often for cleaning.
tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir) // Deletes the entire build directory for a clean slate.
}
