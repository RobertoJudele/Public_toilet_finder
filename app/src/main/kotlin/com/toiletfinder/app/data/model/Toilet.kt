package com.toiletfinder.app.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

// Updated data class for a Toilet, including description and photoUrl from the old MainActivity.kt.
// These fields will be populated from Firestore if they exist in the document, otherwise they will use default values.
data class Toilet(
    val id: String = "",
    val name: String = "",
    val location: GeoPoint = GeoPoint(0.0, 0.0),
    val accessible: Boolean = false,
    val free: Boolean = true,
    val rating: Double = 0.0,
    val description: String = "", // Added description field
    val photoUrl: String = ""    // Added photoUrl field for custom marker icons or details view
)


