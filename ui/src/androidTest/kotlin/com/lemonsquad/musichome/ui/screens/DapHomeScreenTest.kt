package com.lemonsquad.musichome.ui.screens

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.lemonsquad.musichome.ui.theme.MusicDestination
import com.lemonsquad.musichome.ui.viewmodels.MusicUiState
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DapHomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun rendersLoadingState() {
        composeTestRule.setContent {
            DapHomeScreen(
                uiState = MusicUiState.Loading,
                onNavigateToDestination = {},
                onSongClick = {}
            )
        }
        composeTestRule.onNodeWithText("RECENTLY ADDED").assertDoesNotExist()
    }

    @Test
    fun rendersErrorState() {
        val errorMessage = "Database failure"
        composeTestRule.setContent {
            DapHomeScreen(
                uiState = MusicUiState.Error(errorMessage),
                onNavigateToDestination = {},
                onSongClick = {}
            )
        }
        composeTestRule.onNodeWithText("Error: $errorMessage").assertIsDisplayed()
        composeTestRule.onNodeWithText("RECENTLY ADDED").assertDoesNotExist()
    }

    @Test
    fun rendersEmptyState() {
        composeTestRule.setContent {
            DapHomeScreen(
                uiState = MusicUiState.Empty,
                onNavigateToDestination = {},
                onSongClick = {}
            )
        }
        composeTestRule.onNodeWithText("No recent tracks found").assertIsDisplayed()
        composeTestRule.onNodeWithText("RECENTLY ADDED").assertIsDisplayed()
    }

    @Test
    fun verifyNavigationRoutes() {
        var navigatedRoute: String? = null
        composeTestRule.setContent {
            DapHomeScreen(
                uiState = MusicUiState.Empty,
                onNavigateToDestination = { route -> navigatedRoute = route },
                onSongClick = {}
            )
        }

        // Click Library tile
        composeTestRule.onNodeWithText("Library").performClick()
        assertEquals(MusicDestination.Library.route, navigatedRoute)

        // Click Storage tile
        composeTestRule.onNodeWithText("Storage").performClick()
        assertEquals(MusicDestination.ROUTE_SETTINGS_LIBRARY, navigatedRoute)

        // Click Playlists tile
        composeTestRule.onNodeWithText("Playlists").performClick()
        assertEquals(MusicDestination.ROUTE_QUEUE, navigatedRoute)

        // Click Audio Engine tile
        composeTestRule.onNodeWithText("Audio Engine").performClick()
        assertEquals(MusicDestination.Sound.route, navigatedRoute)
    }
}
