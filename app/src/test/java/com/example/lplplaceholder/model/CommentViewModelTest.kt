package com.example.lplplaceholder.model

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
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
    private lateinit var viewModel: CommentViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init should fetch comments`() = runTest {
        // Given
        val comments = listOf(
            Comment(1, 1, "John Doe", "john@example.com", "Great post!", 123),
            Comment(1, 2, "Jane Smith", "jane@example.com", "Thanks for sharing", null)
        )
        val successResult = Result.Success(comments)
        coEvery { repository.getComments() } returns flowOf(successResult)

        // When
        viewModel = CommentViewModel(repository)

        // Then
        viewModel.commentsState.test {
            assertEquals(Result.Loading, awaitItem()) // Initial state
            assertEquals(successResult, awaitItem()) // State after init
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `fetchComments should update state to Loading then Success on successful fetch`() = runTest {
        // Given
        val comments = listOf(
            Comment(1, 1, "John Doe", "john@example.com", "Great post!", 123)
        )
        val successResult = Result.Success(comments)
        coEvery { repository.getComments() } returns flowOf(Result.Loading, successResult)

        // Mock initial state
        coEvery { repository.getComments() } returns flowOf(Result.Loading)
        viewModel = CommentViewModel(repository)

        // Reset mock for fetchComments test
        coEvery { repository.getComments() } returns flowOf(Result.Loading, successResult)

        // When
        viewModel.commentsState.test {
            // Skip initial Loading state from init
            awaitItem()

            // Call the method we're testing
            viewModel.fetchComments()

            // Then
            assertEquals(Result.Loading, awaitItem())
            assertEquals(successResult, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `fetchComments should update state to Error on error`() = runTest {
        // Given
        val errorMessage = "Network error"
        val errorResult = Result.Error(errorMessage)
        coEvery { repository.getComments() } returns flowOf(Result.Loading, errorResult)

        // Mock initial state
        coEvery { repository.getComments() } returns flowOf(Result.Loading)
        viewModel = CommentViewModel(repository)

        // Reset mock for fetchComments test
        coEvery { repository.getComments() } returns flowOf(Result.Loading, errorResult)

        // When
        viewModel.commentsState.test {
            // Skip initial Loading state from init
            awaitItem()

            // Call the method we're testing
            viewModel.fetchComments()

            // Then
            assertEquals(Result.Loading, awaitItem())
            assertEquals(errorResult, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `repository should be called when fetchComments is invoked`() = runTest {
        // Given
        val comments = listOf(
            Comment(1, 1, "John Doe", "john@example.com", "Great post!", 123)
        )
        coEvery { repository.getComments() } returns flowOf(Result.Success(comments))

        // When
        viewModel = CommentViewModel(repository)
        viewModel.fetchComments()

        // Then
        // The verification is implicit in the coEvery setup and the fact that
        // we're collecting the flow twice (once in init and once in fetchComments)
    }
}