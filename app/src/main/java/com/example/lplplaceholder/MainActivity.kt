package com.example.lplplaceholder

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.example.lplplaceholder.view.CommentScreen
import com.example.lplplaceholder.viewmodel.CommentViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val commentViewModel: CommentViewModel by viewModels()

    // MutableStateMap to track selected images per comment ID
    private val selectedImages = mutableStateMapOf<Int, Uri?>()

    // Image picker launcher, defined outside composable scope
    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        selectedCommentId?.let { id ->
            uri?.let { selectedImages[id] = it } // Store the image only for the clicked comment
        }
    }

    // State variable to store the selected comment ID
    private var selectedCommentId by mutableStateOf<Int?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            // Use rememberSaveable to retain selected images and comment ID across config changes
            var selectedCommentIdState by rememberSaveable { mutableStateOf<Int?>(null) }

            CommentScreen(
                commentsState = commentViewModel.commentsState.collectAsState().value,
                onProfileClick = { comment ->
                    selectedCommentIdState = comment.id // Store the clicked comment ID
                    selectedCommentId = comment.id // Update selected comment ID globally
                    imagePickerLauncher.launch("image/*") // Open gallery picker
                },
                selectedImages = selectedImages // Pass state map to screen
            )
        }
    }
}