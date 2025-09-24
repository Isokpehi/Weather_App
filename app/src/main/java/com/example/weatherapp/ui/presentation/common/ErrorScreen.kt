package com.example.weatherapp.ui.presentation.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * A composable that displays an error icon and message, with an optional retry button.
 *
 * @param modifier Modifier applied to the outer container.
 * @param errorMessage The message to display explaining the error.
 * @param onRetry Callback triggered when retry button is pressed.
 * @param showRetryButton Whether to display the retry button (default: true).
 */
@Composable
fun ErrorScreen(
    modifier: Modifier = Modifier,
    errorMessage: String = "Oppppsss... Something went wrong.",
    onRetry: () -> Unit = {},
    showRetryButton: Boolean = true
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier =
            modifier
                .fillMaxSize()
                .padding(16.dp),
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Outlined.Close,
                contentDescription = "Error",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(64.dp),
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                textAlign = TextAlign.Justify,
                text = errorMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
            )

            if (showRetryButton == true) {
                Spacer(modifier = Modifier.height(12.dp))

                Button(onClick = onRetry) {
                    Text("Retry")
                }
            } else {
                // do not show button
            }

        }
    }
}

/**
 * Preview of [ErrorScreen] with retry disabled.
 */
@Preview(showBackground = true)
@Composable
fun ErrorScreenPreview() {
    ErrorScreen(
        errorMessage = "Unable to load data.",
        showRetryButton = false
    )

}
