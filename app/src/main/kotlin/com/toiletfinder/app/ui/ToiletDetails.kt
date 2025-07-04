package com.toiletfinder.app.ui

import android.content.Intent
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.toiletfinder.app.data.firebase.ReviewRepo
import com.toiletfinder.app.data.model.Review
import com.toiletfinder.app.data.model.Toilet
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToiletDetailsSheet(
    toilet: Toilet,
    onDismissRequest: () -> Unit,
    onAddReviewClick: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var reviews: List<Review> by remember { mutableStateOf(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(toilet.id) {
        isLoading = true
        try {
            reviews = ReviewRepo.getReviews(toilet.id)
        } catch (e: Exception) {
            Log.e("ToiletDetails", "Error loading reviews: ${e.message}")
        }
        isLoading = false
    }

    ModalBottomSheet(onDismissRequest = onDismissRequest) {
        Column(
            modifier = Modifier
                .fillMaxHeight(0.95f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = toilet.name,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (toilet.photoUrl.isNotBlank()) {
                AsyncImage(
                    model = toilet.photoUrl,
                    contentDescription = "${toilet.name} photo",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(MaterialTheme.shapes.medium),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            Text("Description: ${toilet.description}", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(8.dp))

            Text("Rating: %.1f".format(toilet.rating), style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(4.dp))

            Text("Accessible: ${if (toilet.accessible) "Yes" else "No"}")
            Text("Free: ${if (toilet.free) "Yes" else "No"}")
            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    val mapsUrl = "https://www.google.com/maps/search/?api=1&query=${toilet.location.latitude},${toilet.location.longitude}"
                    val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse(mapsUrl)).apply {
                        setPackage("com.google.android.apps.maps")
                    }

                    if (mapIntent.resolveActivity(context.packageManager) != null) {
                        context.startActivity(mapIntent)
                    } else {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(mapsUrl)))
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Open in Google Maps")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { onAddReviewClick() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add Review")
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Reviews", style = MaterialTheme.typography.titleMedium)

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.padding(8.dp).testTag("LoadingIndicator"))
            } else if (reviews.isEmpty()) {
                Text("No reviews yet. Be the first!", style = MaterialTheme.typography.bodyMedium)
            } else {
                reviews.forEach { review ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("⭐ Rating: ${review.rating}")
                            if (review.comment.isNotBlank()) {
                                Text(
                                    text = review.comment,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                            Text(
                                text = "🕒 ${review.timestamp.toDate()}",
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}
