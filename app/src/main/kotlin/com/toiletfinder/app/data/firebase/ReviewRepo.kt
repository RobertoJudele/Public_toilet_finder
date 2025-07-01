package com.toiletfinder.app.data.firebase

import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.toiletfinder.app.data.model.Toilet
import com.toiletfinder.app.data.model.Review
import com.toiletfinder.app.data.model.User

object ReviewRepo{
    private val db = FirebaseFirestore.getInstance()

    suspend fun getReviews(toiletId: String): List<Review> =
        db.collection("reviews")
            .whereEqualTo("toiletId", toiletId)
            .get().await()
            .documents.mapNotNull { it.toObject(Review::class.java)?.copy(id = it.id) }

    suspend fun addReview(review: Review): String =
        db.collection("reviews").add(review).await().id

    suspend fun deleteReview(id: String) {
        db.collection("reviews").document(id).delete().await()
    }

    suspend fun getAverage(toiletId: String): Double{
        val reviews = getReviews(toiletId)
        if(reviews.isEmpty())return 0.0
        return reviews.map{it.rating}.average()
    }
}
