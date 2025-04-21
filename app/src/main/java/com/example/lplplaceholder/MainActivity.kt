package com.example.lplplaceholder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.lplplaceholder.view.CommentScreen
import com.example.lplplaceholder.viewmodel.CommentViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val commentViewModel: CommentViewModel by viewModels()

    private var selectedCommentId: Int? = null // Track selected comment ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val uiState by commentViewModel.uiState.collectAsState()
            val selectedImages by commentViewModel.selectedImages.collectAsState()
            val imagePickerLauncher =
                rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                    selectedCommentId?.let { id ->
                        if (uri != null) {
                            commentViewModel.updateSelectedImage(
                                id,
                                uri
                            ) // ViewModel handles state updates
                        }
                    }
                }
            CommentScreen(
                uiState = uiState,
                onProfileClick = { comment ->
                    selectedCommentId = comment.id
                    imagePickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) // Open gallery picker
                },
                selectedImages = selectedImages
            )
        }
    }
}
