package com.toiletfinder.app

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.*
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.toiletfinder.app.ui.AboutScreen
import com.toiletfinder.app.ui.MapScreen
import com.toiletfinder.app.ui.BackendTestScreen
import com.toiletfinder.app.ui.LoginScreen
import com.toiletfinder.app.ui.SidebarLayout
import com.toiletfinder.app.ui.AddToiletScreen
import com.toiletfinder.app.ui.*
import androidx.compose.ui.graphics.Color

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            val options = FirebaseApp.getInstance().options
            Log.d("FIREBASE-CHECK", """
                ✅ Firebase Initialized:
                Project ID: ${options.projectId}
                App ID: ${options.applicationId}
                API Key: ${options.apiKey}
            """.trimIndent())
        } catch (e: Exception) {
            Log.e("FIREBASE-CHECK", "❌ Firebase failed to initialize: ${e.message}", e)
        }

        setContent {
            ToiletFinderApp()
        }
    }
}

@Composable
fun ToiletFinderApp() {
    var isDarkMode by remember { mutableStateOf(true) }
    val auth = remember { FirebaseAuth.getInstance() }
    var currentUser by remember { mutableStateOf(auth.currentUser) }
    var selectedScreen by remember { mutableStateOf("Map") }
    var pendingProtectedScreen by remember { mutableStateOf<String?>(null) }

    // Define color schemes for light and dark themes
    val lightColors = lightColorScheme()
    val darkColors = darkColorScheme()

    // Color Scheme for Light Mode
    fun lightColorScheme() = lightColorScheme(
        primary = Color(0xFF6200EE),
        onPrimary = Color.White,
        surface = Color.White,
        onSurface = Color.Black,
        // Add other colors as needed
    )

    // Color Scheme for Dark Mode
    fun darkColorScheme() = darkColorScheme(
        primary = Color(0xFFBB86FC),
        onPrimary = Color.Black,
        surface = Color(0xFF121212),
        onSurface = Color.White,
        // Add other colors as needed
    )

    // Wrap your app content in MaterialTheme and switch color schemes based on isDarkMode
    MaterialTheme(
        colorScheme = if (isDarkMode) darkColors else lightColors
    ) {
        Surface(color = MaterialTheme.colorScheme.background) {
            if (selectedScreen == "Login") {
                LoginScreen(onLoginSuccess = {
                    currentUser = auth.currentUser
                    selectedScreen = pendingProtectedScreen ?: "Map"
                    pendingProtectedScreen = null
                }, onBackClick = { selectedScreen = "Map" })
            } else {
                SidebarLayout(
                    isDarkMode = isDarkMode,
                    isUserLoggedIn = currentUser != null,
                    onToggleTheme = { isDarkMode = !isDarkMode },
                    onScreenSelected = {
                        when (it) {
                            "Logout" -> {
                                auth.signOut()
                                currentUser = null
                                selectedScreen = "Map"
                            }
                            "AddToilet" -> {
                                if (auth.currentUser == null) {
                                    pendingProtectedScreen = "AddToilet"
                                    selectedScreen = "Login"
                                } else {
                                    selectedScreen = "AddToilet"
                                }
                            }
                            "Login" -> {
                                if (auth.currentUser != null) {
                                    selectedScreen = "Map"
                                } else {
                                    selectedScreen = "Login"
                                }
                            }
                            else -> {
                                selectedScreen = it
                            }
                        }
                    }
                ) {
                    when (selectedScreen) {
                        "Map" -> MapScreen(onAddToiletClick = { selectedScreen = "AddToilet" })
                        "Backend" -> BackendTestScreen()
                        "AddToilet" -> AddToiletScreen(onToiletAdded = { selectedScreen = "Map" })
                        "Home" -> Text("🏠 Home Screen")
                        "Settings" -> Text("⚙️ Settings")
                        "About" -> AboutScreen(onBackClick = { selectedScreen = "Map" })
                        else -> Text("🚧 Screen not implemented: $selectedScreen")
                    }
                }
            }
        }
    }
}
