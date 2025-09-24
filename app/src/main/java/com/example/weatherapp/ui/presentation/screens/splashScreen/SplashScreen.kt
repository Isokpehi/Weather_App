package com.example.weatherapp.ui.presentation.screens.splashScreen

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapp.R
import kotlinx.coroutines.delay


/**
 * Splash screen composable that displays the weather app logo with animations
 * and automatically navigates to the search city screen after a delay.
 *
 * @param modifier Modifier to be applied to the root composable
 * @param onNavigateToSearchCityScreen Callback function to navigate to the next screen
 */
@Composable
fun SplashContent(
    modifier: Modifier = Modifier,
    onNavigateToSearchCityScreen: () -> Unit
) {

    // State variable to control when animations should start
    var startAnimation by remember { mutableStateOf(false) }

    // Animation for fade-in effect (opacity from 0 to 1)
    val alphaAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 1000),
        label = "alpha"
    )
    // Animation for scale effect (grows from 30% to 100% size)
    val scaleAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.3f,
        animationSpec = tween(durationMillis = 1000),
        label = "scale"
    )

    // Side effect that runs once when the composable is first composed
    LaunchedEffect(key1 = true) {
        startAnimation = true
        delay(1000) // Show splash for 2seconds
        onNavigateToSearchCityScreen()
    }

    // Root container that fills the entire screen
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
                .alpha(alphaAnim)
                .scale(scaleAnim)
        ) {
            // Weather App Logo and Text
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "W",
                    color = Color(0xFFFF0601),
                    fontSize = 80.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "weather",
                        color = Color.Black,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Normal
                    )
                    Text(
                        text = "App",
                        color = Color(0xFF757575),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            // Weather Icon
            Image(
                painter = painterResource(id = R.drawable.sun_cloud_angled_rain__1_), // Replace with your SVG resource
                contentDescription = "Weather Icon",
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(280.dp)
            )
        }

    }
}

/**
 * Preview composable for the splash screen
 * Allows viewing the splash screen in Android Studio's design preview
 */
@Preview(showBackground = true)
@Composable
fun SplashContentPreview() {
    SplashContent(
        onNavigateToSearchCityScreen = {}
    )
}