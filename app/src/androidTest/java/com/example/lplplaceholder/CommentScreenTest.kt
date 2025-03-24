package com.example.lplplaceholder

import androidx.activity.compose.setContent
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.lplplaceholder.model.Comment
import com.example.lplplaceholder.view.CommentScreen
import com.example.lplplaceholder.utils.Result
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
                commentsState = Result.Success(mockComments),
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
    fun testErrorMessageDisplays() {
        composeTestRule.activity.setContent {
            CommentScreen(
                commentsState = Result.Error("Network error"),
                onProfileClick = {},
                selectedImages = emptyMap(),
            )
        }

        // Check if error message is displayed
        composeTestRule.onNodeWithText("Network error").assertExists()
    }

}