package com.example.lplplaceholder.utils

import android.app.Application
import com.example.lplplaceholder.data.CommentRepository
import com.example.lplplaceholder.service.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://jsonplaceholder.typicode.com/posts/1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideCommentRepository(apiService: ApiService): CommentRepository {
        return CommentRepository(apiService)
    }

    @Provides
    @Singleton
    fun provideDataStoreManager(application: Application): DataStoreManager {
        return DataStoreManager(application)
    }
}