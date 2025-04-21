package com.example.lplplaceholder.viewmodel

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lplplaceholder.data.CommentRepository
import com.example.lplplaceholder.model.Comment
import com.example.lplplaceholder.utils.DataStoreManager
import com.example.lplplaceholder.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommentViewModel @Inject constructor(
    private val repository: CommentRepository,
    savedStateHandle: SavedStateHandle,
    private val dataStoreManager: DataStoreManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow<CommentUiState>(CommentUiState.Loading)
    val uiState: StateFlow<CommentUiState> = _uiState

    // Use a regular HashMap for state restoration
    private val _selectedImages = MutableStateFlow<Map<Int, Uri?>>(
        savedStateHandle.get<HashMap<Int, Uri?>>("selectedImages") ?: HashMap()
    )
    val selectedImages: StateFlow<Map<Int, Uri?>> = _selectedImages

    // Update selected image for a specific comment
    fun updateSelectedImage(commentId: Int, uri: Uri?) {
        viewModelScope.launch {
            val currentImages = _selectedImages.value.toMutableMap()
            currentImages[commentId] = uri
            _selectedImages.value = currentImages
        }
    }

    init {
        loadCommentsFromDataStore() // Load cached comments first
        fetchComments() // Fetch new comments from API if needed

    }

    private fun loadCommentsFromDataStore() {
        viewModelScope.launch {
            dataStoreManager.getComments().collect { comments ->
                if (comments.isNotEmpty()) {
                    _uiState.value = CommentUiState.Success(comments)
                }
            }
        }
    }

    fun fetchComments() {
        viewModelScope.launch {
            try {
                if (_uiState.value is CommentUiState.Loading) {
                    repository.getComments().collect { result ->
                        when (result) {
                            is Result.Success -> {
                                dataStoreManager.saveComments(result.data) // Save to DataStore
                                _uiState.value = CommentUiState.Success(result.data)
                            }

                            is Result.Error -> {
                                _uiState.value = CommentUiState.Error(result.message)
                            }

                            else -> {
                                _uiState.value = CommentUiState.Error("Unexpected result")
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.value = CommentUiState.Error("Failed to fetch comments: ${e.message}")
            }
        }
    }
}

sealed interface CommentUiState {
    object Loading : CommentUiState
    data class Success(val comments: List<Comment>) : CommentUiState
    data class Error(val message: String) : CommentUiState
}