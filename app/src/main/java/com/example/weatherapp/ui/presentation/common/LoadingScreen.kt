//package com.example.starwars.ui.presentation.common
//
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.material3.CircularProgressIndicator
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import com.example.starwars.ui.theme.StarWarsTheme
//
///**
// * A composable that shows a centered loading spinner with a message.
// *
// * @param modifier Modifier applied to the container.
// * @param message Text message to show below the spinner (default: "Loading...").
// */
//@Composable
//fun LoadingScreen(
//    modifier: Modifier = Modifier,
//    message: String = "Loading...",
//) {
//    Box(
//        contentAlignment = Alignment.Center,
//        modifier =
//            modifier
//                .fillMaxSize()
//                .padding(16.dp),
//    ) {
//        Column(horizontalAlignment = Alignment.CenterHorizontally) {
//            CircularProgressIndicator()
//            Spacer(modifier = Modifier.height(12.dp))
//            Text(
//                text = message,
//                style = MaterialTheme.typography.bodyMedium,
//            )
//        }
//    }
//}
//
///**
// * Preview of [LoadingScreen] showing the default message.
// */
//@Preview(showBackground = true)
//@Composable
//fun LoadingScreenPreview() {
//    StarWarsTheme {
//        LoadingScreen()
//    }
//}
