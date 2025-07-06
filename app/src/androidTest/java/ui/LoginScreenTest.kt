package ui

import android.widget.Toast
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import io.mockk.*
import org.junit.Rule
import org.junit.Test
import com.toiletfinder.app.ui.LoginScreen
class LoginScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // Mock FirebaseAuth using mockk
    private val mockAuth = mockk<FirebaseAuth>()

    // Create a mock AuthResult
    private val mockAuthResult = mockk<AuthResult>()

    @Test
    fun testLoginSuccess() {
        // Setup mock behavior for Firebase login success
        every { mockAuth.signInWithEmailAndPassword(any(), any()) } returns mockTask(true)

        // Set the content to the LoginScreen
        composeTestRule.setContent {
            LoginScreen(
                onLoginSuccess = { /* Do something when login is successful */ },
                onBackClick = { /* Handle back click */ }
            )
        }

        // Perform input actions
        composeTestRule.onNodeWithText("Email").performTextInput("test@example.com")
        composeTestRule.onNodeWithText("Password").performTextInput("password123")

        // Click the login button
        composeTestRule.onNodeWithText("Login").performClick()

        // Assert that Toast was shown with the expected message
        composeTestRule.onNodeWithText("Welcome back!").assertExists()

        // You can also verify that the onLoginSuccess callback was invoked.
        verify { mockAuth.signInWithEmailAndPassword("test@example.com", "password123") }
    }

    @Test
    fun testLoginFailure() {
        // Setup mock behavior for Firebase login failure (e.g., invalid credentials)
        every { mockAuth.signInWithEmailAndPassword(any(), any()) } throws FirebaseAuthInvalidCredentialsException("ERROR", "Invalid credentials")

        // Set the content to the LoginScreen
        composeTestRule.setContent {
            LoginScreen(
                onLoginSuccess = { /* Do something when login is successful */ },
                onBackClick = { /* Handle back click */ }
            )
        }

        // Perform input actions
        composeTestRule.onNodeWithText("Email").performTextInput("test@example.com")
        composeTestRule.onNodeWithText("Password").performTextInput("wrongPassword")

        // Click the login button
        composeTestRule.onNodeWithText("Login").performClick()

        // Assert that Toast was shown with the error message
        composeTestRule.onNodeWithText("Invalid credentials").assertExists()

        // You can also verify that the error was handled correctly
        verify { mockAuth.signInWithEmailAndPassword("test@example.com", "wrongPassword") }
    }

    @Test
    fun testRegisterSwitch() {
        // Set the content to the LoginScreen
        composeTestRule.setContent {
            LoginScreen(
                onLoginSuccess = { /* Do something when login is successful */ },
                onBackClick = { /* Handle back click */ }
            )
        }

        // Perform the "Don't have an account? Register" click
        composeTestRule.onNodeWithText("Don't have an account? Register").performClick()

        // Check if the text is updated to "Register"
        composeTestRule.onNodeWithText("Register").assertExists()

        // Check if the action buttons and fields are in the registration mode
        composeTestRule.onNodeWithText("Create Account").assertExists()
    }

    // Mock Task for Firebase Auth result simulation
    private fun mockTask(isSuccess: Boolean): Task<AuthResult> {
        val task = mockk<Task<AuthResult>>()

        // Mock the behavior of the task depending on the success flag
        every { task.isSuccessful } returns isSuccess
        every { task.exception } returns
                if (isSuccess) null
                else FirebaseAuthInvalidCredentialsException("ERROR", "Invalid credentials")

        // Mock the result to return a mocked AuthResult object
        every { task.result } returns mockAuthResult

        return task
    }
}
