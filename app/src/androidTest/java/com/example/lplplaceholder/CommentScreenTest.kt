package com.example.lplplaceholder

import androidx.activity.compose.setContent
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.lplplaceholder.model.Comment
import com.example.lplplaceholder.view.CommentScreen
import com.example.lplplaceholder.viewmodel.CommentUiState
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CommentScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun testCommentsDisplayedCorrectly() {
        val mockComments = listOf(
            Comment(1, 1, "John Doe", "john@example.com", "Test comment 1"),
            Comment(1, 2, "Jane Smith", "jane@example.com", "Test comment 2")
        )

        composeTestRule.activity.setContent {
            CommentScreen(
                uiState = CommentUiState.Success(mockComments),
                onProfileClick = {},
                selectedImages = emptyMap(),
            )
        }

        // Check if comments are displayed
        composeTestRule.onNodeWithText("John Doe").assertExists()
        composeTestRule.onNodeWithText("jane@example.com").assertExists()
        composeTestRule.onNodeWithText("Test comment 2").assertExists()
    }

    @Test
    fun testProfileImageClick() {
        val mockComments = listOf(
            Comment(1, 1, "John Doe", "john@example.com", "Test comment 1"),
        )

        var clickedComment: Comment? = null

        composeTestRule.activity.setContent {
            CommentScreen(
                uiState = CommentUiState.Success(mockComments),
                onProfileClick = { comment -> clickedComment = comment },
                selectedImages = emptyMap(),
            )
        }

        // Click on the profile image
        composeTestRule.onNodeWithContentDescription("Profile Picture").performClick()

        // Verify that the correct comment was passed to the callback
        assertEquals(mockComments[0], clickedComment)
    }

    @Test
    fun testErrorMessageDisplays() {
        composeTestRule.activity.setContent {
            CommentScreen(
                uiState = CommentUiState.Error("Network error"),
                onProfileClick = {},
                selectedImages = emptyMap(),
            )
        }

        // Check if error message is displayed
        composeTestRule.onNodeWithText("Network error").assertExists()
    }

}