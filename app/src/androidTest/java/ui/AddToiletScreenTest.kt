package ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.toiletfinder.app.ui.AddToiletScreen // Replace with actual path to AddToiletScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AddToiletScreenUITest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun fillFormAndTapSubmit_showsLoadingIndicator() {
        composeTestRule.setContent {
            AddToiletScreen(onToiletAdded = { /* No-op */ })
        }

        // Fill out the form fields
        composeTestRule.onNodeWithText("Name").performTextInput("Test Toilet")
        composeTestRule.onNodeWithText("Description").performTextInput("Clean and accessible")
        composeTestRule.onNodeWithText("Latitude").performTextInput("40.7128")
        composeTestRule.onNodeWithText("Longitude").performTextInput("-74.0060")

        // Tap the submit button
        composeTestRule.onNodeWithText("Submit").performClick()

        // Assert loading indicator appears
        composeTestRule.onNodeWithContentDescription("Loading").assertExists()

        // Optional: wait to allow Firebase to complete (or error)
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            // You can check for some visual confirmation here (e.g., screen reset, snackbar, etc.)
            true
        }
    }
}
