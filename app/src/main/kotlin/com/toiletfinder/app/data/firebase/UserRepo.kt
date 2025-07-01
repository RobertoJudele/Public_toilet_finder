package com.toiletfinder.app.data.firebase

import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await
import com.toiletfinder.app.data.model.Toilet
import com.toiletfinder.app.data.model.Review
import com.toiletfinder.app.data.model.User


object UserRepo{
    private val db: FirebaseFirestore
        get() = FirebaseFirestore.getInstance()

    private val usersCollection
        get() = db.collection("users")
    suspend fun getUser(id: String): User?{
        val snapshot = db.collection("users").document(id).get().await()
        return snapshot.toObject(User::class.java)?.copy(id = snapshot.id)
    }

    suspend fun addUser(user: User): String =
        db.collection("users").add(user).await().id

    suspend fun updateUser(user: User) {
        if (user.id.isNotBlank()) {
            db.collection("users").document(user.id).set(user).await()
        }
    }
}