package com.toiletfinder.app.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import com.google.android.gms.location.LocationServices
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.toiletfinder.app.data.model.Toilet
import java.util.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription
@Composable
fun AddToiletScreen(
    onToiletAdded: () -> Unit
) {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var latitude by remember { mutableStateOf("") }
    var longitude by remember { mutableStateOf("") }
    var accessible by remember { mutableStateOf(false) }  // Accessible checkbox state
    var free by remember { mutableStateOf(false) }  // Free checkbox state
    var isSubmitting by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    @SuppressLint("MissingPermission")
    fun fetchLocation() {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    latitude = location.latitude.toString()
                    longitude = location.longitude.toString()
                }
            }
        } else {
            error = "Location permission not granted."
        }
    }

    // Validation function
    fun validateFields(): Boolean {
        // Check if fields are non-empty
        if (name.isBlank()) {
            error = "Title cannot be empty."
            return false
        }

        if (description.isBlank()) {
            error = "Description cannot be empty."
            return false
        }

        if (!accessible && !free) {
            error = "Please specify if the toilet is accessible and/or free."
            return false
        }

        // Validate coordinates
        val lat = latitude.toDoubleOrNull()
        val lon = longitude.toDoubleOrNull()

        if (lat == null || lon == null || lat !in -90.0..90.0 || lon !in -180.0..180.0) {
            error = "Invalid coordinates. Please enter valid latitude and longitude."
            return false
        }

        error = null  // Reset any existing error
        return true
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("🚽 Add a Public Toilet", style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = latitude,
                onValueChange = { latitude = it },
                label = { Text("Latitude") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedTextField(
                value = longitude,
                onValueChange = { longitude = it },
                label = { Text("Longitude") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Button to use current location
        Button(
            onClick = { fetchLocation() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("📍 Use Current Location")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Accessible checkbox
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = accessible,
                onCheckedChange = { accessible = it }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Accessible")
        }

        // Free checkbox
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = free,
                onCheckedChange = { free = it }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Free")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Submit Button
        Button(
            onClick = {
                if (validateFields()) {
                    isSubmitting = true
                    error = null

                    val toilet = Toilet(
                        id = UUID.randomUUID().toString(),
                        name = name,
                        description = description,
                        location = GeoPoint(
                            latitude.toDouble(),
                            longitude.toDouble()
                        ),
                        accessible = accessible,
                        free = free,
                        rating = 0.0,
                        photoUrl = ""  // Assume no photo URL initially
                    )

                    FirebaseFirestore.getInstance()
                        .collection("toilets")
                        .document(toilet.id)
                        .set(toilet)
                        .addOnSuccessListener {
                            isSubmitting = false
                            onToiletAdded()
                        }
                        .addOnFailureListener {
                            isSubmitting = false
                            error = it.message
                        }
                }
            },
            enabled = !isSubmitting,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isSubmitting) "Submitting..." else "Submit")
        }

        // Show loading indicator *inside* the Column, so .align works
        if (isSubmitting) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 16.dp)
                    .semantics { contentDescription = "Loading" },
                color = MaterialTheme.colorScheme.primary
            )
        }

        // Display error message if exists
        error?.let {
            Spacer(modifier = Modifier.height(12.dp))
            Text("Error: $it", color = MaterialTheme.colorScheme.error)
        }
    }
}
