package com.example.lplplaceholder.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
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
    application: Application,
) : AndroidViewModel(application) {

    private val dataStoreManager = DataStoreManager(application)

    private val _commentsState = MutableStateFlow<Result<List<Comment>>>(Result.Loading)
    val commentsState: StateFlow<Result<List<Comment>>> = _commentsState

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
                    _commentsState.value = Result.Success(comments)
                }
            }
        }
    }

    fun fetchComments() {
        viewModelScope.launch {
            try {
                if ( _commentsState.value is Result.Loading) {
                    repository.getComments().collect { result ->
                        if (result is Result.Success) {
                            dataStoreManager.saveComments(result.data) // Save to DataStore
                        }
                        _commentsState.value = result
                    }
                }
            } catch (e: Exception) {
                _commentsState.value = Result.Error("Failed to fetch comments: ${e.message}")
            }
        }
    }
}