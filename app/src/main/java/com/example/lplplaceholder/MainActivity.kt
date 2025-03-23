package com.example.lplplaceholder

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import com.example.lplplaceholder.view.CommentScreen
import com.example.lplplaceholder.viewmodel.CommentViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val commentViewModel: CommentViewModel by viewModels()

    private var selectedCommentId: Int? = null // Track selected comment ID
    private lateinit var imagePickerLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Register ActivityResultLauncher BEFORE setContent (fixes lifecycle issue)
        imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            selectedCommentId?.let { id ->
                if (uri != null) {
                    commentViewModel.updateSelectedImage(id, uri) // ViewModel handles state updates
                }
            }
        }

        setContent {
            CommentScreen(
                commentsState = commentViewModel.commentsState.collectAsState().value,
                onProfileClick = { comment ->
                    selectedCommentId = comment.id
                    imagePickerLauncher.launch("image/*") // Open gallery picker
                },
                selectedImages = commentViewModel.selectedImages.collectAsState().value // ViewModel holds state
            )
        }
    }
}
