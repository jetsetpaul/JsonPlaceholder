package com.example.lplplaceholder.model

data class Comment(
    val postId: Int?,
    val id: Int?,
    val name: String?,
    val email: String?,
    val body: String?,
    val imageId: Int? = null
)