package com.toiletfinder.app

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.toiletfinder.app.ui.*

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

    val lightColors = lightTheme()
    val darkColors = darkTheme()

    MaterialTheme(colorScheme = if (isDarkMode) darkColors else lightColors) {
        Surface(color = MaterialTheme.colorScheme.background) {
            if (selectedScreen == "Login") {
                LoginScreen(
                    onLoginSuccess = {
                        currentUser = auth.currentUser
                        selectedScreen = pendingProtectedScreen ?: "Map"
                        pendingProtectedScreen = null
                    },
                    onBackClick = { selectedScreen = "Map" }
                )
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
                                selectedScreen = if (auth.currentUser != null) "Map" else "Login"
                            }
                            else -> selectedScreen = it
                        }
                    }
                ) {
                    when (selectedScreen) {
                        "Map" -> {
                            val context = LocalContext.current
                            Box(modifier = Modifier.fillMaxSize()) {
                                MapScreen(
                                    onAddToiletClick = {
                                        if (currentUser == null) {
                                            selectedScreen = "Login"
                                            pendingProtectedScreen = "AddToilet"
                                        } else {
                                            selectedScreen = "AddToilet"
                                        }
                                    }
                                )
                                if (currentUser != null) {
                                    FloatingActionButton(
                                        onClick = {
                                            selectedScreen = "AddToilet"
                                            Toast.makeText(context, "Navigate to Add Toilet", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier
                                            .align(Alignment.BottomEnd)
                                            .padding(16.dp)
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = "Add Toilet")
                                    }
                                }
                            }
                        }
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

@Composable
fun lightTheme() = lightColorScheme(
    primary = Color(0xFF6200EE),
    onPrimary = Color.White,
    surface = Color.White,
    onSurface = Color.Black,
)

@Composable
fun darkTheme() = darkColorScheme(
    primary = Color(0xFFBB86FC),
    onPrimary = Color.Black,
    surface = Color(0xFF121212),
    onSurface = Color.White,
)
