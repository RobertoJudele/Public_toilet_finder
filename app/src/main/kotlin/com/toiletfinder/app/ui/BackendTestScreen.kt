package com.toiletfinder.app.ui

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.material3.Text
import androidx.compose.material3.Surface
import androidx.compose.ui.tooling.preview.Preview
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.Timestamp
import com.toiletfinder.app.data.firebase.ToiletRepo
import com.toiletfinder.app.data.firebase.ReviewRepo
import com.toiletfinder.app.data.firebase.UserRepo
import com.toiletfinder.app.data.model.Toilet
import com.toiletfinder.app.data.model.Review
import com.toiletfinder.app.data.model.User
import kotlinx.coroutines.delay

@Composable
fun BackendTestScreen() {
    val tag = remember { "FirestoreTest" }

    Surface {
        Text("Firestore backend test running... Check logcat for output.")
    }

    LaunchedEffect(Unit) {
        try {
            Log.d(tag, "=== Testing Firestore Backend ===")

            delay(500) // Give Firebase time to initialize

            // 1. Add a toilet
            val toilet = Toilet(
                name = "Compose Test Toilet",
                location = GeoPoint(44.4268, 26.1025),
                accessible = true,
            )
            val toiletId = ToiletRepo.addToilet(toilet)
            Log.d(tag, "Toilet added: ID = $toiletId")

            // 2. Fetch all toilets
            val toilets = ToiletRepo.getToilets()
            Log.d(tag, "Fetched ${toilets.size} toilets")
            toilets.forEach { Log.d(tag, it.toString()) }

            // 3. Add a review
            val review = Review(
                userId = "composeUser",
                toiletId = toiletId,
                rating = 4,
                comment = "Nice and functional",
                timestamp = Timestamp.now()
            )
            val reviewId = ReviewRepo.addReview(review)
            Log.d(tag, "Review added: ID = $reviewId")

            // 4. Fetch updated rating
            val updated = ToiletRepo.getToilets().find { it.id == toiletId }
            Log.d(tag, "Updated rating: ${updated?.rating}")

        } catch (e: Exception) {
            Log.e(tag, "Error: ${e.message}", e)
        }

        // 5. Add test users
        val admin = User(
            email = "admin@example.com",
            name = "App Admin",
            role = "admin"
        )
        val testUser = User(
            email = "test@example.com",
            name = "Test User",
            role = "user"
        )

        val adminId = UserRepo.addUser(admin)
        val testId = UserRepo.addUser(testUser)

        Log.d(tag, "Admin added: ID = $adminId")
        Log.d(tag, "Test user added: ID = $testId")
    }
}

@Preview
@Composable
fun PreviewBackendTestScreen() {
    BackendTestScreen()
}
