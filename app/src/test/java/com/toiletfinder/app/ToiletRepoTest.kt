package com.toiletfinder.app

import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.firebase.firestore.*
import com.toiletfinder.app.data.firebase.ToiletRepo
import com.toiletfinder.app.data.firebase.ReviewRepo
import com.toiletfinder.app.data.model.Toilet
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import com.google.android.gms.tasks.Tasks
fun <T> completedTask(result: T): Task<T> = Tasks.forResult(result)

class ToiletRepoTest {

    private val mockFirestore = mockk<FirebaseFirestore>(relaxed = true)
    private val mockCollection = mockk<CollectionReference>(relaxed = true)
    private val mockDocument = mockk<DocumentReference>(relaxed = true)
    private val mockQuerySnapshot = mockk<QuerySnapshot>(relaxed = true)
    private val mockDocumentSnapshot = mockk<DocumentSnapshot>(relaxed = true)

    @Before
    fun setup() {
        // Mock static calls for Firestore.getInstance()
        mockkStatic(FirebaseFirestore::class)
        every { FirebaseFirestore.getInstance() } returns mockFirestore

        // Mock getting collection
        every { mockFirestore.collection("toilets") } returns mockCollection
    }

    @After
    fun tearDown() {
        unmockkAll()
    }
    // Helper to create a completed Task for Firestore
    private fun <T> completedTask(result: T): Task<T> {
        val tcs = TaskCompletionSource<T>()
        tcs.setResult(result)
        return tcs.task
    }

    @Test
    fun `getToilets returns list with average ratings`() = runBlocking {
        val toilet1 = Toilet(id = "1", name = "Toilet1", rating = 0.0)
        val toilet2 = Toilet(id = "2", name = "Toilet2", rating = 0.0)
        val toilets = listOf(toilet1, toilet2)

        // Mock documents in query snapshot
        every { mockQuerySnapshot.documents } returns listOf(mockDocumentSnapshot, mockDocumentSnapshot)

        // Mock mapping document snapshots to Toilet objects with correct ids
        every { mockDocumentSnapshot.toObject(Toilet::class.java) } returnsMany listOf(toilet1.copy(id = "1"), toilet2.copy(id = "2"))
        every { mockDocumentSnapshot.id } returnsMany listOf("1", "2")

        // Mock collection.get() to return completed task with query snapshot
        every { mockCollection.get() } returns completedTask(mockQuerySnapshot)

        // Mock ReviewRepo.getAverage to return fixed values for each toilet id
        mockkObject(ReviewRepo)
        coEvery { ReviewRepo.getAverage("1") } returns 4.5
        coEvery { ReviewRepo.getAverage("2") } returns 3.5

        // Call method under test
        val result = ToiletRepo.getToilets()

        // Verify returned toilets have ratings from ReviewRepo
        assertEquals(2, result.size)
        assertEquals(4.5, result[0].rating)
        assertEquals(3.5, result[1].rating)
    }

    @Test
    fun `addToilet returns document id`() = runBlocking {
        val newToilet = Toilet(id = "", name = "New Toilet", rating = 0.0)
        val newDocRef = mockk<DocumentReference>(relaxed = true)

        every { mockCollection.add(newToilet) } returns completedTask(newDocRef)
        every { newDocRef.id } returns "newId"

        val id = ToiletRepo.addToilet(newToilet)

        assertEquals("newId", id)
    }

    @Test
    fun `updateToilet updates document when id is not blank`() = runBlocking {
        val toilet = Toilet(id = "toiletId", name = "Updated", rating = 5.0)

        every { mockCollection.document("toiletId") } returns mockDocument
        every { mockDocument.set(toilet) } returns completedTask(mockk())

        ToiletRepo.updateToilet(toilet)

        verify { mockDocument.set(toilet) }
    }

    @Test
    fun `updateToilet does nothing if id is blank`() = runBlocking {
        val toilet = Toilet(id = "", name = "No ID", rating = 0.0)

        // Collection.document() should not be called if id blank
        ToiletRepo.updateToilet(toilet)

        verify(exactly = 0) { mockCollection.document(any()) }
    }

    @Test
    fun `deleteToilet calls delete on document`() = runBlocking {
        every { mockCollection.document("id123") } returns mockDocument
        every { mockDocument.delete() } returns completedTask(mockk())

        ToiletRepo.deleteToilet("id123")

        verify { mockDocument.delete() }
    }

    @Test
    fun `refreshRating updates rating field`() = runBlocking {
        every { mockCollection.document("id123") } returns mockDocument
        every { mockDocument.update("rating", 4.2) } returns completedTask(mockk())

        mockkObject(ReviewRepo)
        coEvery { ReviewRepo.getAverage("id123") } returns 4.2

        ToiletRepo.refreshRating("id123")

        verify { mockDocument.update("rating", 4.2) }
    }
}
