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
fun ToiletDetailsSheet(toilet: Toilet, onDismissRequest: () -> Unit) {
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
                    contentScale = ContentScale.Crop,
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

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Rating: ", style = MaterialTheme.typography.titleMedium)
                Text(text = "%.1f".format(toilet.rating), style = MaterialTheme.typography.bodyLarge)
                Spacer(Modifier.width(8.dp))
            }
            Spacer(Modifier.height(4.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Accessible: ", style = MaterialTheme.typography.titleMedium)
                Text(
                    text = if (toilet.accessible) "Yes" else "No",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Spacer(Modifier.height(4.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Free: ", style = MaterialTheme.typography.titleMedium)
                Text(
                    text = if (toilet.free) "Yes" else "No",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Spacer(Modifier.height(24.dp))

            // Button to open location in Google Maps
            Button(
                onClick = {
                    // Method 1: Use 'geo:latitude,longitude' for a direct pin, no search.
                    val gmmIntentUri = Uri.parse("geo:${toilet.location.latitude},${toilet.location.longitude}")

                    // Method 2: For a named marker with specific coordinates, use a 'q' parameter with just the name
                    // and let it infer the location. Less reliable for exact pin placement.
                    // val gmmIntentUri = Uri.parse("geo:0,0?q=${toilet.location.latitude},${toilet.location.longitude}(${Uri.encode(toilet.name)})")

                    // Method 3: Use the 'maps.google.com' URL which is more robust for place IDs or direct pins with labels.
                    // This is generally the most reliable for consistent behavior across devices.
                    val mapsUrl = "https://www.google.com/maps/search/?api=1&query=${toilet.location.latitude},${toilet.location.longitude}"
                    val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse(mapsUrl))


                    mapIntent.setPackage("com.google.android.apps.maps") // Explicitly target Google Maps app

                    if (mapIntent.resolveActivity(context.packageManager) != null) {
                        context.startActivity(mapIntent)
                    } else {
                        // Fallback: If Google Maps app is not installed, open in web browser
                        val webIntentUri = Uri.parse("https://maps.google.com/?q=${toilet.location.latitude},${toilet.location.longitude}")
                        val webMapIntent = Intent(Intent.ACTION_VIEW, webIntentUri)
                        context.startActivity(webMapIntent)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Open in Google Maps")
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}
