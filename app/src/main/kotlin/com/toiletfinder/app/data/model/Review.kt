package com.toiletfinder.app.data.model
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

data class Review(
    val id: String = "",
    val userId: String = "",
    val toiletId: String = "",
    val rating: Int = 0,
    val comment: String = "",
    val timestamp: Timestamp = Timestamp.now()
)
