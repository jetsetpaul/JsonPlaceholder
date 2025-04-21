package com.example.lplplaceholder.view

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.lplplaceholder.R
import com.example.lplplaceholder.model.Comment
import com.example.lplplaceholder.viewmodel.CommentUiState


@Composable
fun CommentScreen(
    uiState: CommentUiState,
    onProfileClick: (Comment) -> Unit,
    selectedImages: Map<Int, Uri?>
) {
    val images = selectedImages

    when (uiState) {
        is CommentUiState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = androidx.compose.ui.graphics.Color.White),
                contentAlignment = androidx.compose.ui.Alignment.Center,

                ) {
                CircularProgressIndicator()
            }
        }

        is CommentUiState.Success -> {
            val comments = uiState.comments
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = androidx.compose.ui.graphics.Color.White)
                    .padding(16.dp)
            ) {
                items(comments) { comment ->
                    CommentItem(comment, onProfileClick, images[comment.id])
                }
            }
        }

        is CommentUiState.Error -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = androidx.compose.ui.graphics.Color.White),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Text(
                    text = uiState.message, color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewCommentScreen() {
    // Mock data for the comments list
    val sampleComments = listOf(
        Comment(1, 1, "John Doe", "john@example.com", "This is a sample comment."),
        Comment(1, 2, "Jane Smith", "jane@example.com", "Another example comment."),
        Comment(1, 3, "Alice Brown", "alice@example.com", "samples on samples")
    )

    val mockSelectedImages = remember {
        mutableStateMapOf<Int, Uri?>().apply {
            put(1, Uri.parse(""))
            put(2, Uri.parse(""))
        }
    }

    val mockSelectedImagesStateFlow = remember { mutableStateOf(mockSelectedImages) }

    val mockCommentsState = CommentUiState.Success(sampleComments)

    CommentScreen(
        uiState = mockCommentsState,
        onProfileClick = { /* Simulate a click */ },
        selectedImages = mockSelectedImagesStateFlow.value
    )
}

@Composable
fun CommentItem(
    comment: Comment,
    onProfileClick: (Comment) -> Unit,
    selectedImageUri: Uri? // Specific image for this comment
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.elevatedCardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.White)

    ) {
        Row(
            modifier = Modifier.padding(16.dp)
        ) {
            // Display either selected image or default icon
            Image(painter = if (selectedImageUri != null) rememberAsyncImagePainter(model = selectedImageUri)
            else painterResource(id = R.drawable.ic_person_round),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .clickable { onProfileClick(comment) })

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    comment.name?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.weight(1f),
                            color = androidx.compose.ui.graphics.Color.Black
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    comment.email?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.labelMedium,
                            color = androidx.compose.ui.graphics.Color.Black
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = comment.id.toString(),
                    style = MaterialTheme.typography.labelMedium,
                    color = androidx.compose.ui.graphics.Color.Black
                )

                comment.body?.let {
                    Text(
                        text = it, style = MaterialTheme.typography.bodySmall,
                        color = androidx.compose.ui.graphics.Color.Black
                    )
                }
            }
        }
    }
}