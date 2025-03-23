package com.example.lplplaceholder.viewmodel

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lplplaceholder.data.CommentRepository
import com.example.lplplaceholder.model.Comment
import com.example.lplplaceholder.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommentViewModel @Inject constructor(
    private val repository: CommentRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _commentsState = MutableStateFlow<Result<List<Comment>>>(Result.Loading)
    val commentsState: StateFlow<Result<List<Comment>>> = _commentsState

    // Use a regular HashMap for state restoration
    private val _selectedImages = MutableStateFlow<Map<Int, Uri?>>(
        savedStateHandle.get<HashMap<Int, Uri?>>("selectedImages") ?: HashMap()
    )
    val selectedImages: StateFlow<Map<Int, Uri?>> = _selectedImages

    // Update selected image for a specific comment
    fun updateSelectedImage(commentId: Int, uri: Uri?) {
        val currentImages = _selectedImages.value.toMutableMap()
        currentImages[commentId] = uri
        _selectedImages.value = currentImages
        // Save a regular HashMap for state restoration
        savedStateHandle["selectedImages"] = HashMap(currentImages)
    }

    init {
        fetchComments()
    }

    fun fetchComments() {
        viewModelScope.launch {
            try {
                repository.getComments().collect { result ->
                    _commentsState.value = result
                }
            } catch (e: Exception) {
                _commentsState.value = Result.Error("Failed to fetch comments: ${e.message}")
            }
        }
    }
}