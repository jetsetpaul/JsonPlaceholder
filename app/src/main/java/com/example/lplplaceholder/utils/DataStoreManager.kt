package com.example.lplplaceholder.utils

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.lplplaceholder.model.Comment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extension property to create DataStore
private val Context.dataStore by preferencesDataStore(name = "image_store")

class DataStoreManager(private val context: Context) {

    companion object {
        private val IMAGE_MAP_KEY = stringPreferencesKey("selected_images")
        private val COMMENT_LIST_KEY = stringPreferencesKey("comment_list")
    }

    private val gson = Gson()


    // Save comments (Convert List<Comment> → JSON String)
    suspend fun saveComments(comments: List<Comment>) {
        val commentsJson = gson.toJson(comments)
        context.dataStore.edit { preferences ->
            preferences[COMMENT_LIST_KEY] = commentsJson
        }
    }

    // Load comments (Convert JSON String → List<Comment>)
    fun getComments(): Flow<List<Comment>> {
        return context.dataStore.data.map { preferences ->
            val commentsJson = preferences[COMMENT_LIST_KEY]
            if (commentsJson.isNullOrEmpty()) {
                emptyList()
            } else {
                val type = object : TypeToken<List<Comment>>() {}.type
                gson.fromJson(commentsJson, type) ?: emptyList()
            }
        }
    }
}