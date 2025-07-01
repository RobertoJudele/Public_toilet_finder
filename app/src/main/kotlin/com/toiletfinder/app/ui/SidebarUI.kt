package com.toiletfinder.app.ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SidebarLayout(
    isDarkMode: Boolean,
    onToggleTheme: () -> Unit,
    onScreenSelected: (String) -> Unit,
    content: @Composable () -> Unit
) {
    var sidebarExpanded by remember { mutableStateOf(false) }

    val sidebarWidth by animateDpAsState(
        targetValue = if (sidebarExpanded) 220.dp else 0.dp,
        label = "sidebarWidth"
    )

    val backgroundColor = if (isDarkMode) Color(0xFF121212) else Color.White
    val sidebarBackground = if (isDarkMode) Color(0xFF2B2B2B) else Color.White
    val textColor = if (isDarkMode) Color.White else Color.Black

    val buttons = listOf(
        "Home" to "🏠",
        "Map" to "🗺️",
        "Backend" to "🧪",
        "Settings" to "⚙️",
        "About" to "ℹ️",
        "Logout" to "⍈"
    )

    Row(modifier = Modifier.fillMaxSize().background(backgroundColor)) {
        // Sidebar
        Column(
            modifier = Modifier
                .width(sidebarWidth)
                .fillMaxHeight()
                .background(sidebarBackground)
                .padding(12.dp)
        ) {
            if (sidebarExpanded) {
                Text("Menu", color = textColor, fontSize = 20.sp, modifier = Modifier.padding(bottom = 16.dp))

                buttons.forEach { (label, emoji) ->
                    OutlinedButton(
                        onClick = {
                            onScreenSelected(label)
                            sidebarExpanded = false
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, textColor),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.Transparent,
                            contentColor = textColor
                        )
                    ) {
                        Text("$emoji $label", fontSize = 14.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = onToggleTheme,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, textColor),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = textColor
                    )
                ) {
                    Text(if (isDarkMode) "☀️ Light Mode" else "🌙 Dark Mode", fontSize = 14.sp)
                }
            }
        }

        // Main content + top bar
        Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
            Column {
                TopAppBar(
                    title = { Text("Public Toilets") },
                    navigationIcon = {
                        IconButton(onClick = { sidebarExpanded = !sidebarExpanded }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = textColor)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = if (isDarkMode) Color(0xFF333333) else Color(0xFFE0E0E0),
                        titleContentColor = textColor,
                        navigationIconContentColor = textColor
                    )
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(backgroundColor),
                    contentAlignment = Alignment.TopStart
                ) {
                    content()
                }
            }

            if (sidebarExpanded) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0x33000000))
                        .clickable { sidebarExpanded = false }
                )
            }
        }
    }
}
