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
import com.google.firebase.auth.FirebaseAuth


@Composable
fun AddReviewDialog(
    toiletId: String,
    onDismiss: () -> Unit
) {
    val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email ?: "anonymous"
    var rating by remember { mutableStateOf("") }
    var comment by remember { mutableStateOf("") }
    var ratingError by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            tonalElevation = 8.dp,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Add Review", style = MaterialTheme.typography.titleLarge)

                Spacer(modifier = Modifier.height(12.dp))

                // Show user email
                Text(
                    text = "Reviewing as: $currentUserEmail",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(12.dp))

                TextField(
                    value = rating,
                    onValueChange = {
                        if (it.all { c -> c.isDigit() }) {
                            rating = it
                            ratingError = false
                        }
                    },
                    label = { Text("Rating (1-5)") },
                    isError = ratingError,
                    singleLine = true
                )

                if (ratingError) {
                    Text(
                        "Please enter a rating between 1 and 5",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

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
                        val ratingValue = rating.toIntOrNull()
                        if (ratingValue == null || ratingValue !in 1..5) {
                            ratingError = true
                            return@Button
                        }

                        val review = Review(
                            userId = currentUserEmail,
                            toiletId = toiletId,
                            rating = ratingValue,
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
