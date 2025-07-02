package com.toiletfinder.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.toiletfinder.app.data.firebase.ReviewRepo
import com.toiletfinder.app.data.model.Review
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.Row


@Composable
fun AddReviewDialog(
    toiletId: String,
    onDismiss: () -> Unit
) {
    var rating by remember { mutableStateOf("") }
    var comment by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            tonalElevation = 8.dp,
            modifier = Modifier
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Add Review", style = MaterialTheme.typography.titleLarge)

                Spacer(modifier = Modifier.height(12.dp))

                TextField(
                    value = rating,
                    onValueChange = { if (it.all { c -> c.isDigit() }) rating = it },
                    label = { Text("Rating (0-5)") },
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                TextField(
                    value = comment,
                    onValueChange = { comment = it },
                    label = { Text("Comment") },
                    maxLines = 4
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        val review = Review(
                            userId = "temp-user", // Replace with actual user ID
                            toiletId = toiletId,
                            rating = rating.toIntOrNull() ?: 0,
                            comment = comment
                        )
                        coroutineScope.launch {
                            try {
                                ReviewRepo.addReview(review)
                                onDismiss()
                            } catch (e: Exception) {
                                // Handle error (e.g. show a Snackbar)
                            }
                        }
                    }) {
                        Text("Submit")
                    }
                }
            }
        }
    }
}
