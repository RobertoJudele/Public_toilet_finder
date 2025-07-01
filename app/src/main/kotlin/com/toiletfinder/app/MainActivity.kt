package com.toiletfinder.app

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.toiletfinder.app.ui.MapScreen
import com.toiletfinder.app.ui.BackendTestScreen
import com.toiletfinder.app.ui.LoginScreen
import com.toiletfinder.app.ui.SidebarLayout
import com.toiletfinder.app.ui.AddToiletScreen

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
    var selectedScreen by remember { mutableStateOf("Map") }
    var pendingProtectedScreen by remember { mutableStateOf<String?>(null) }

    Surface(color = MaterialTheme.colorScheme.background) {
        if (selectedScreen == "Login") {
            LoginScreen(onLoginSuccess = {
                selectedScreen = pendingProtectedScreen ?: "Map"
                pendingProtectedScreen = null
            })
        } else {
            SidebarLayout(
                isDarkMode = isDarkMode,
                onToggleTheme = { isDarkMode = !isDarkMode },
                onScreenSelected = {
                    if (it == "Logout") {
                        auth.signOut()
                        selectedScreen = "Map"
                    } else if (it == "AddToilet" && auth.currentUser == null) {
                        pendingProtectedScreen = "AddToilet"
                        selectedScreen = "Login"
                    } else {
                        selectedScreen = it
                    }
                }
            ) {
                when (selectedScreen) {
                    "Map" -> MapScreen()
                    "Backend" -> BackendTestScreen()
                    "AddToilet" -> AddToiletScreen(onToiletAdded = { selectedScreen = "Map" })
                    "Home" -> Text("🏠 Home Screen")
                    "Settings" -> Text("⚙️ Settings")
                    else -> Text("🚧 Screen not implemented: $selectedScreen")
                }
            }
        }
    }
}
