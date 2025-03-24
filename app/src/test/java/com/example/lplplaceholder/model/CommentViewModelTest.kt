package com.example.lplplaceholder.model

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import com.example.lplplaceholder.data.CommentRepository
import com.example.lplplaceholder.utils.DataStoreManager
import com.example.lplplaceholder.viewmodel.CommentViewModel
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import com.example.lplplaceholder.utils.Result
import com.example.lplplaceholder.viewmodel.CommentUiState
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockkConstructor
import io.mockk.unmockkAll
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import kotlin.test.Test

@ExperimentalCoroutinesApi
class CommentViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: CommentViewModel
    private lateinit var repository: CommentRepository
    private lateinit var dataStoreManager: DataStoreManager
    private lateinit var application: Application
    private lateinit var savedStateHandle: SavedStateHandle

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        repository = mockk()
        application = mockk(relaxed = true)
        savedStateHandle = SavedStateHandle()
        dataStoreManager = mockk()

        // Setup DataStoreManager mock with coEvery for suspend functions
        mockkConstructor(DataStoreManager::class)
        every { anyConstructed<DataStoreManager>().getComments() } returns flowOf(emptyList())
        coEvery { anyConstructed<DataStoreManager>().saveComments(any()) } just Runs

        // Setup repository with coEvery for suspend functions
        coEvery { repository.getComments() } returns flowOf(Result.Loading)

        viewModel = CommentViewModel(repository, savedStateHandle, application)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `initial state is loading`() = runTest {
        // Create a fresh instance to verify initial state
        val freshViewModel = CommentViewModel(repository, savedStateHandle, application)

        assertTrue(freshViewModel.uiState.value is CommentUiState.Loading)
    }

    @Test
    fun `fetchComments updates state with success`() = runTest {
        // Arrange: Create mock comments
        val mockComments = listOf(
            Comment(1, 1, "Alice", "alice@example.com", "First comment", 101),
            Comment(1, 2, "Bob", "bob@example.com", "Second comment", 102),
            Comment(2, 3, "Charlie", "charlie@example.com", "Another thread", null)
        )

        // Mock repository to return a flow emitting success
        coEvery { repository.getComments() } returns flowOf(Result.Success(mockComments))

        val viewModel = CommentViewModel(repository, savedStateHandle, application)

        // Act
        viewModel.fetchComments()
        advanceUntilIdle() // Ensures coroutine execution completes

        // Assert: Verify the UI state was updated
        val result = viewModel.uiState.value
        assertTrue(result is CommentUiState.Success)
        assertEquals(mockComments, (result as CommentUiState.Success).comments)

        // Verify repository was called once
        coVerify { repository.getComments() }

    }


    @Test
    fun `fetchComments handles error`() = runTest {
        // Arrange
        val errorMessage = "Network error"
        coEvery { repository.getComments() } returns flowOf(Result.Error(errorMessage))

        val viewModel = CommentViewModel(repository, savedStateHandle, application)
        // Act
        viewModel.fetchComments()
        advanceUntilIdle()

        // Assert
        val result = viewModel.uiState.value
        assertTrue(result is CommentUiState.Error)
        assertEquals(errorMessage, (result as CommentUiState.Error).message)
    }

    @Test
    fun `loadCommentsFromDataStore updates state when data exists`() = runTest {
        // Arrange
        val cachedComments = listOf(
            Comment(1, 1, "Alice", "alice@example.com", "Cached comment", 101),
        )

        // Setup mock to return cached comments
        every { anyConstructed<DataStoreManager>().getComments() } returns flowOf(cachedComments)

        // Create new ViewModel to trigger init block
        val viewModel = CommentViewModel(repository, savedStateHandle, application)
        advanceUntilIdle()

        // Assert
        val result = viewModel.uiState.value
        assertTrue(result is CommentUiState.Success)
        assertEquals(cachedComments, (result as CommentUiState.Success).comments)
    }

}