package com.example.lplplaceholder.view

import android.net.Uri
import androidx.compose.foundation.Image
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.lplplaceholder.R
import com.example.lplplaceholder.utils.Result
import com.example.lplplaceholder.model.Comment


@Composable
fun CommentScreen(
    commentsState: Result<List<Comment>>,
    onProfileClick: (Comment) -> Unit,
    selectedImages: Map<Int, Uri?> // Map of selected images
) {
    when (commentsState) {
        is Result.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is Result.Success -> {
            val comments = commentsState.data
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                items(comments) { comment ->
                    CommentItem(comment, onProfileClick, selectedImages[comment.id])
                }
            }
        }
        is Result.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Text(
                    text = commentsState.message,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCommentScreen() {
    val sampleComments = listOf(
        Comment(1, 1, "John Doe", "john@example.com", "This is a sample comment."),
        Comment(1, 2, "Jane Smith", "jane@example.com", "Another example comment."),
        Comment(1, 3, "Alice Brown", "alice@example.com", "samples on samples")
    )

    CommentScreen(Result.Success(sampleComments), { profile ->
        print(profile.name)
    }, mapOf())
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
        elevation = CardDefaults.elevatedCardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp)
        ) {
            // Display either selected image or default icon
            Image(
                painter = if (selectedImageUri != null)
                    rememberAsyncImagePainter(model = selectedImageUri)
                else
                    painterResource(id = R.drawable.ic_person),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .clickable { onProfileClick(comment) }
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = comment.name,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = comment.email,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = comment.id.toString(),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = comment.body,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}