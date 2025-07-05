package com.toiletfinder.app
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.*
import com.toiletfinder.app.data.firebase.ReviewRepo
import com.toiletfinder.app.data.model.Review
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ReviewRepoTest {

    private val mockFirestore = mockk<FirebaseFirestore>(relaxed = true)
    private val mockCollection = mockk<CollectionReference>(relaxed = true)
    private val mockQuery = mockk<Query>(relaxed = true)
    private val mockQuerySnapshot = mockk<QuerySnapshot>()
    private val mockDocumentSnapshot = mockk<DocumentSnapshot>()
    private val mockDocumentReference = mockk<DocumentReference>()

    @Before
    fun setup() {
        mockkStatic(FirebaseFirestore::class)
        every { FirebaseFirestore.getInstance() } returns mockFirestore
        every { mockFirestore.collection("reviews") } returns mockCollection
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `getReviews returns list of reviews`() = runBlocking {
        val toiletId = "toilet123"

        // Setup Firestore query chain
        every { mockCollection.whereEqualTo("toiletId", toiletId) } returns mockQuery
        every { mockQuery.get() } returns Tasks.forResult(mockQuerySnapshot)

        // Setup documents
        every { mockQuerySnapshot.documents } returns listOf(mockDocumentSnapshot)
        val review = Review(id = "review1", toiletId = toiletId, rating = 5, comment = "Great!", userId = "user1")
        every { mockDocumentSnapshot.toObject(Review::class.java) } returns review
        every { mockDocumentSnapshot.id } returns "review1"

        val reviews = ReviewRepo.getReviews(toiletId)

        assertEquals(1, reviews.size)
        assertEquals("Great!", reviews[0].comment)
        verify { mockCollection.whereEqualTo("toiletId", toiletId) }
        verify { mockQuery.get() }
    }

    @Test
    fun `addReview adds review and returns id`() = runBlocking {
        val review = Review(id = "", toiletId = "toilet123", rating = 4, comment = "Clean", userId = "user2")

        every { mockCollection.add(review) } returns Tasks.forResult(mockDocumentReference)
        every { mockDocumentReference.id } returns "newReviewId"

        val id = ReviewRepo.addReview(review)
        assertEquals("newReviewId", id)
        verify { mockCollection.add(review) }
    }

    @Test
    fun `deleteReview deletes review document`() = runBlocking {
        val reviewId = "reviewToDelete"
        val mockDocRef = mockk<DocumentReference>(relaxed = true)

        every { mockCollection.document(reviewId) } returns mockDocRef
        every { mockDocRef.delete() } returns Tasks.forResult(null)

        ReviewRepo.deleteReview(reviewId)
        verify { mockCollection.document(reviewId) }
        verify { mockDocRef.delete() }
    }

    @Test
    fun `getAverage returns average rating`() = runBlocking {
        val toiletId = "toilet123"
        val reviews = listOf(
            Review(id = "1", toiletId = toiletId, rating = 4, comment = "Good", userId = "user1"),
            Review(id = "2", toiletId = toiletId, rating = 5, comment = "Excellent", userId = "user2"),
        )

        // Mock getReviews to return the above reviews
        mockkObject(ReviewRepo)
        coEvery { ReviewRepo.getReviews(toiletId) } returns reviews

        val average = ReviewRepo.getAverage(toiletId)
        assertTrue(average in 4.4..4.6)

        unmockkObject(ReviewRepo)
    }

}
