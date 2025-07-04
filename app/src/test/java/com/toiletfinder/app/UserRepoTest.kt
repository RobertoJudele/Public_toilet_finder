package com.toiletfinder.app
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.toiletfinder.app.data.model.User
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import com.toiletfinder.app.data.firebase.UserRepo


@RunWith(MockitoJUnitRunner::class)
class UserRepoTest {

    @Mock
    private lateinit var mockFirestore: FirebaseFirestore

    @Mock
    private lateinit var mockUsersCollection: CollectionReference

    @Mock
    private lateinit var mockDocument: DocumentReference

    @Mock
    private lateinit var mockSnapshot: DocumentSnapshot

    private val testUser = User(
        id = "user123",
        name = "Test User",
        email = "test@example.com"
    )

    @Before
    fun setUp() {
        mockStatic(FirebaseFirestore::class.java).use { mockedStatic ->
            mockedStatic.`when`<Any> { FirebaseFirestore.getInstance() }.thenReturn(mockFirestore)
            `when`(mockFirestore.collection("users")).thenReturn(mockUsersCollection)
        }
    }

    @Test
    fun `getUser returns user from Firestore`() = runTest {
        `when`(mockUsersCollection.document("user123")).thenReturn(mockDocument)
        `when`(mockDocument.get()).thenReturn(Tasks.forResult(mockSnapshot))
        `when`(mockSnapshot.toObject(User::class.java)).thenReturn(testUser)
        `when`(mockSnapshot.id).thenReturn("user123")

        val user = UserRepo.getUser("user123")

        assert(user != null)
        assert(user?.id == "user123")
        assert(user?.name == "Test User")
    }

    @Test
    fun `addUser adds user to Firestore and returns id`() = runTest {
        val mockAddTask = Tasks.forResult(mockDocument)
        `when`(mockUsersCollection.add(testUser)).thenReturn(mockAddTask)
        `when`(mockDocument.id).thenReturn("user123")

        val userId = UserRepo.addUser(testUser)

        assert(userId == "user123")
    }

    @Test
    fun `updateUser updates user in Firestore when id is not blank`() = runTest {
        `when`(mockUsersCollection.document("user123")).thenReturn(mockDocument)
        `when`(mockDocument.set(testUser)).thenReturn(Tasks.forResult(null))

        UserRepo.updateUser(testUser)

        verify(mockDocument).set(testUser)
    }

    @Test
    fun `updateUser does nothing when user id is blank`() = runTest {
        val blankUser = testUser.copy(id = "")

        UserRepo.updateUser(blankUser)

        verify(mockUsersCollection, never()).document(anyString())
    }
}
