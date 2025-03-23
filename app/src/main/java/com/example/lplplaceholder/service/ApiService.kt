package com.example.lplplaceholder.service

import com.example.lplplaceholder.model.Comment
import retrofit2.http.GET

interface ApiService {
    @GET("comments")
    suspend fun getComments(): List<Comment>
}