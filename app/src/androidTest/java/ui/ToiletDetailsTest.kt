package ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.*
import com.google.firebase.firestore.GeoPoint
import com.toiletfinder.app.data.model.Toilet
import com.toiletfinder.app.data.firebase.ReviewRepo
import com.toiletfinder.app.ui.ToiletDetailsSheet
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test

class ToiletDetailsTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val sampleToilet = Toilet(
        id = "123",
        name = "Test Toilet",
        description = "Nice toilet for testing",
        rating = 4.5,
        accessible = true,
        free = false,
        photoUrl = "",
        location = GeoPoint(45.0, 25.0)
    )

    @Test
    fun toiletDetails_showsNameAndAddReviewButton() {
        // Mockăm getReviews să returneze listă goală
        coEvery { ReviewRepo.getReviews(any()) } returns emptyList()

        composeTestRule.setContent {
            ToiletDetailsSheet(
                toilet = sampleToilet,
                onDismissRequest = {},
                onAddReviewClick = {}
            )
        }

        composeTestRule.onNodeWithText("Test Toilet").assertIsDisplayed()
        composeTestRule.onNodeWithText("Add Review").assertIsDisplayed()
        composeTestRule.onNodeWithText("No reviews yet. Be the first!").assertIsDisplayed()
    }

    @Test
    fun toiletDetails_showsLoadingIndicatorWhileFetching() {
        runBlocking {
            coEvery { ReviewRepo.getReviews(any()) } coAnswers {
                delay(1000)
                emptyList()
            }

            composeTestRule.setContent {
                ToiletDetailsSheet(
                    toilet = sampleToilet,
                    onDismissRequest = {},
                    onAddReviewClick = {}
                )
            }

            composeTestRule.onNodeWithTag("LoadingIndicator").assertIsDisplayed()
        }
    }

}
