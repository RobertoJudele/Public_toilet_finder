// app/build.gradle.kts

// Plugins applied to this Android application module.
plugins {
    // Standard Android application plugin. Version defined in settings.gradle.kts.
    id("com.android.application")
    // Kotlin Android plugin. Version defined in settings.gradle.kts.
    kotlin("android")
    // Kotlin Annotation Processing Tool (KAPT) plugin, required for libraries like Glide.
    id("kotlin-kapt")
    // Google Services Gradle Plugin, for Firebase integration. Version defined in settings.gradle.kts.
    id("com.google.gms.google-services")
    // Jetpack Compose Compiler Plugin. Version defined in settings.gradle.kts (matches Kotlin version).
    id("org.jetbrains.kotlin.plugin.compose")
}

// Android configuration block.
android {
    // Defines the package namespace for generated R.java classes and BuildConfig.
    namespace = "com.toiletfinder.app"
    // The Android API level to compile your app against.
    compileSdk = 34 // Ensure you have SDK Platform 34 installed via SDK Manager.

    // Default configuration for all build variants.
    defaultConfig {
        // The unique application ID for your app. Must match Firebase and Google Maps Console.
        applicationId = "com.toiletfinder.app"
        // The minimum API level required to run your app.
        minSdk = 24
        // The API level your app is designed to run on.
        targetSdk = 34
        // Version code for app updates (integer).
        versionCode = 1
        // Version name for users (string).
        versionName = "1.0"

        // The test instrumentation runner to use for Android tests.
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // Build type specific configurations (e.g., debug, release).
    buildTypes {
        release {
            // Whether to minify code (e.g., remove unused code, obfuscate). Set to false for easier debugging.
            isMinifyEnabled = false
            // ProGuard rules files for code shrinking and obfuscation.
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    // Enable specific build features.
    buildFeatures {
        // Enable View Binding (set to false or remove if not using old XML layouts).
        viewBinding = true
        // Enable Jetpack Compose support.
        compose = true
    }

    // Compose-specific compiler options.
    composeOptions {
        // The Compose compiler extension version is now managed by the 'org.jetbrains.kotlin.plugin.compose' plugin.
        // Therefore, this line should remain commented out or removed.
        // kotlinCompilerExtensionVersion = "1.5.14"
    }

    // Java compatibility options for source and target bytecode.
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17 // Ensure your JDK is 17 or higher.
        targetCompatibility = JavaVersion.VERSION_17
    }

    // Kotlin compiler options.
    kotlinOptions {
        jvmTarget = "17" // Align with Java compatibility.
    }
    packaging {
        resources {
            excludes.add("/META-INF/LICENSE.md")
            excludes.add("/META-INF/LICENSE-notice.md")
        }
    }
}

// Define project dependencies.
dependencies {
    implementation("com.google.firebase:firebase-auth-ktx:23.2.1")
    // Optional: Room Database. Comment out this line if you are not using Room.
    // implementation("androidx.room:room-runtime-android:2.7.2")

    // Define the Jetpack Compose Bill of Materials (BOM) for consistent Compose library versions.
    // Using 2024.05.00 from your previous config. Can be updated to a newer 2025.xx.xx version later if needed.
    val composeBom = platform("androidx.compose:compose-bom:2024.05.00")

    // Core Android Jetpack libraries.
    implementation("androidx.core:core-ktx:1.13.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Jetpack Compose UI and Material Design libraries.
    implementation(composeBom) // Import Compose BOM to manage versions.
    androidTestImplementation(composeBom) // Also for Android tests.

    implementation("androidx.activity:activity-compose:1.9.0") // For Compose activity.
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3") // Material Design 3 components.
    implementation("androidx.compose.ui:ui-tooling-preview") // For Compose previews in Android Studio.
    debugImplementation("androidx.compose.ui:ui-tooling") // Tools for Compose development.

    // Firebase Bill of Materials (BOM) for consistent Firebase library versions.
    // Using the latest version.
    implementation(platform("com.google.firebase:firebase-bom:33.15.0"))
    // Specific Firebase products (versions managed by the BOM, so no explicit version numbers here).
    implementation("com.google.firebase:firebase-analytics-ktx") // Firebase Analytics.
    implementation("com.google.firebase:firebase-firestore-ktx") // Cloud Firestore database.
    implementation("com.google.firebase:firebase-common-ktx") // Common Firebase utilities.
    implementation("com.google.firebase:firebase-installations-ktx") // Firebase Installations (crucial for client-side Firebase ID management).

    // Glide library for image loading (used for marker icons).
    implementation("com.github.bumptech.glide:glide:4.16.0")
    // KAPT annotation processor for Glide.
    kapt("com.github.bumptech.glide:compiler:4.16.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.2.1")

    // Testing dependencies.
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    testImplementation ("org.jetbrains.kotlin:kotlin-test:1.8.22")

    // For unit tests
    testImplementation ("io.mockk:mockk:1.13.8")
    testImplementation ("org.slf4j:slf4j-simple:2.0.7")

    testImplementation ("org.mockito:mockito-core:5.11.0")
    testImplementation ("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation ("org.mockito.kotlin:mockito-kotlin:5.2.1")

// For Android instrumented tests (if you want)
    androidTestImplementation ("io.mockk:mockk-android:1.13.5")

// For coroutine testing
    testImplementation ("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")

// JUnit
    testImplementation ("junit:junit:4.13.2")
    androidTestImplementation ("org.mockito.kotlin:mockito-kotlin:4.1.0")
    // Google Maps and Compose Maps integration.
    implementation("com.google.maps.android:maps-compose:3.1.0") // Compose Maps library.
    implementation("com.google.android.gms:play-services-maps:18.2.0") // Core Google Play Services Maps SDK.
    implementation("com.google.android.gms:play-services-location:21.0.1")


    // Android Lifecycle ViewModel support for Compose.
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.0")
    implementation("io.coil-kt:coil-compose:2.6.0")
    testImplementation(kotlin("test"))
}

// Apply the Google Services plugin. This line must be at the very bottom of the file.
// It processes the google-services.json file to configure Firebase.
apply(plugin = "com.google.gms.google-services")
