package com.toiletfinder.app.data.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.toiletfinder.app.data.model.Toilet
import kotlinx.coroutines.tasks.await

object ToiletRepo {
    private val db: FirebaseFirestore
        get() = FirebaseFirestore.getInstance()

    private val toiletsCollection
        get() = db.collection("toilets")

    suspend fun getToilets(): List<Toilet> =
        toiletsCollection
            .get().await()
            .documents.mapNotNull { it.toObject(Toilet::class.java)?.copy(id = it.id) }
            .map { toilet ->
                val avgRating = ReviewRepo.getAverage(toilet.id)
                toilet.copy(rating = avgRating)
            }

    suspend fun addToilet(toilet: Toilet): String {
        val docRef = toiletsCollection.add(toilet).await()
        return docRef.id
    }

    suspend fun updateToilet(toilet: Toilet) {
        if (toilet.id.isNotBlank()) {
            toiletsCollection.document(toilet.id).set(toilet).await()
        }
    }

    suspend fun deleteToilet(id: String) {
        toiletsCollection.document(id).delete().await()
    }

    suspend fun refreshRating(toiletId: String) {
        val avgRating = ReviewRepo.getAverage(toiletId)
        toiletsCollection.document(toiletId).update("rating", avgRating).await()
    }
}
