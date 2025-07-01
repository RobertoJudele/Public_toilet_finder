package com.toiletfinder.app.data.model
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await


data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val registeredAt: Timestamp = Timestamp.now(),
    val role: String = "user"
)