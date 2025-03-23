package com.example.lplplaceholder.data

import com.example.lplplaceholder.model.Comment
import com.example.lplplaceholder.service.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import com.example.lplplaceholder.utils.Result
import retrofit2.HttpException
import java.io.IOException

class CommentRepository(val service: ApiService) {

    suspend fun getComments(): Flow<Result<List<Comment>>> = flow {
        emit(Result.Loading)
        try {
            val comments = service.getComments() // Retrofit call (suspend function)
            emit(Result.Success(comments))
        } catch (e: IOException) { // Network error
            emit(Result.Error("Network error, please check your connection."))
        } catch (e: HttpException) { // HTTP error (e.g., 404, 500)
            emit(Result.Error("Server error: ${e.message()}"))
        }
    }
}