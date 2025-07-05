package ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.toiletfinder.app.ui.MapScreen
import com.toiletfinder.app.ui.MapScreenFab
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.assertTrue

@RunWith(AndroidJUnit4::class)
class MapScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun fabClick_invokesCallback() {
        var wasClicked = false

        composeTestRule.setContent {
            MapScreenFab { wasClicked = true }
        }

        composeTestRule
            .onNodeWithContentDescription("Add Toilet")
            .assertExists()
            .performClick()

        assertTrue(wasClicked)
    }

}
