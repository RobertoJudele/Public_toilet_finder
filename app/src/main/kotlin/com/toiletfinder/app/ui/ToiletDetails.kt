package com.toiletfinder.app.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.toiletfinder.app.data.model.Toilet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToiletDetailsSheet(
    toilet: Toilet,
    onDismissRequest: () -> Unit,
    onAddReviewClick: () -> Unit // NEW callback
) {
    val context = LocalContext.current

    ModalBottomSheet(onDismissRequest = onDismissRequest) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
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
                Spacer(Modifier.height(16.dp))
            }

            if (toilet.description.isNotBlank()) {
                Text(
                    text = toilet.description,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Spacer(Modifier.height(8.dp))
            }

            InfoRow(label = "Rating:", value = "%.1f".format(toilet.rating))
            InfoRow(label = "Accessible:", value = if (toilet.accessible) "Yes" else "No")
            InfoRow(label = "Free:", value = if (toilet.free) "Yes" else "No")

            Spacer(Modifier.height(24.dp))

            // Google Maps Button
            Button(
                onClick = {
                    val mapsUrl = "https://www.google.com/maps/search/?api=1&query=${toilet.location.latitude},${toilet.location.longitude}"
                    val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse(mapsUrl)).apply {
                        setPackage("com.google.android.apps.maps")
                    }

                    if (mapIntent.resolveActivity(context.packageManager) != null) {
                        context.startActivity(mapIntent)
                    } else {
                        val fallbackUrl = Uri.parse("https://maps.google.com/?q=${toilet.location.latitude},${toilet.location.longitude}")
                        context.startActivity(Intent(Intent.ACTION_VIEW, fallbackUrl))
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Open in Google Maps")
            }

            Spacer(Modifier.height(16.dp))

            // Add Review Button
            Button(
                onClick = onAddReviewClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add Review")
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
    ) {
        Text(text = label, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.width(8.dp))
        Text(text = value, style = MaterialTheme.typography.bodyLarge)
    }
}
