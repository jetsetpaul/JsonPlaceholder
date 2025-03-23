package com.example.lplplaceholder.model

import android.net.Uri
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.example.lplplaceholder.data.CommentRepository
import com.example.lplplaceholder.viewmodel.CommentViewModel
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import com.example.lplplaceholder.utils.Result
import io.mockk.coEvery
import kotlinx.coroutines.flow.flowOf
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import kotlin.test.Test
import kotlin.time.ExperimentalTime

@ExperimentalCoroutinesApi
@ExperimentalTime
class CommentViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private val repository = mockk<CommentRepository>()
    private val savedStateHandle = SavedStateHandle()
    private lateinit var viewModel: CommentViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        // Initialize SavedStateHandle with a regular HashMap instead of mutableStateMapOf
        savedStateHandle["selectedImages"] = HashMap<Int, Uri?>()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `updateSelectedImage should update the image for specific comment ID`() = runTest {
        val commentId = 1
        val uri = mockk<Uri>()
        coEvery { repository.getComments() } returns flowOf(Result.Loading)
        viewModel = CommentViewModel(repository, savedStateHandle)

        viewModel.updateSelectedImage(commentId, uri)

        viewModel.selectedImages.test {
            val images = awaitItem()
            assertEquals(uri, images[commentId])
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `selectedImages should be empty initially`() = runTest {
        coEvery { repository.getComments() } returns flowOf(Result.Loading)

        viewModel = CommentViewModel(repository, savedStateHandle)

        viewModel.selectedImages.test {
            val images = awaitItem()
            assertTrue(images.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `fetchComments should transition from Loading to Success with correct data`() = runTest {
        // Given
        val expectedComments = listOf(
            Comment(1, 1, "Alice", "alice@example.com", "First comment", 101),
            Comment(1, 2, "Bob", "bob@example.com", "Second comment", 102),
            Comment(2, 3, "Charlie", "charlie@example.com", "Another thread", null)
        )
        val loadingState = Result.Loading
        val successState = Result.Success(expectedComments)

        // Setup repository to return a loading state followed by a success state
        coEvery { repository.getComments() } returns flowOf(loadingState, successState)

        viewModel = CommentViewModel(repository, savedStateHandle)

        viewModel.commentsState.test {
            // First emit should be Loading (from init)
            val initialState = awaitItem()
            assertTrue(initialState is Result.Loading)

            // Second emit should be Success with our comments
            val resultState = awaitItem()
            assertTrue(resultState is Result.Success)

            // Verify the data matches what we expect
            val comments = (resultState as Result.Success<List<Comment>>).data
            assertEquals(3, comments.size)
            assertEquals("Alice", comments[0].name)
            assertEquals("bob@example.com", comments[1].email)
            assertEquals("Another thread", comments[2].body)
            assertEquals(101, comments[0].imageId)
            assertNull(comments[2].imageId)

            // No more emissions expected
            expectNoEvents()
            cancelAndIgnoreRemainingEvents()
        }
    }
}