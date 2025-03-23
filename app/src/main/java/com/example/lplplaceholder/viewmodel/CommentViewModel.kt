package com.example.lplplaceholder.viewmodel

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
class CommentViewModel @Inject constructor(private val repository: CommentRepository) : ViewModel() {

    private val _commentsState = MutableStateFlow<Result<List<Comment>>>(Result.Loading)
    val commentsState: StateFlow<Result<List<Comment>>> = _commentsState

    init {
        fetchComments()
    }

    fun fetchComments() {
        viewModelScope.launch {
            repository.getComments().collect { result ->
                _commentsState.value = result
            }
        }
    }
}