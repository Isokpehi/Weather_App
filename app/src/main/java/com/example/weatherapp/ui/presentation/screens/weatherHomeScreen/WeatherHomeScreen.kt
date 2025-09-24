package com.example.weatherapp.ui.presentation.screens.weatherHomeScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.weatherapp.R
import com.example.weatherapp.domain.model.GeoLocation

@Composable
fun WeatherHomeScreen(
    modifier: Modifier = Modifier,
    geoLocation: GeoLocation,
    onNavigateBack: () -> Unit = {},
    viewModel: WeatherHomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(geoLocation) {
        viewModel.loadWeatherData(geoLocation)
    }

    WeatherHomeContent(
        modifier = modifier,
        uiState = uiState,
        onRetry = viewModel::retry,
        onSaveFavorite = viewModel::saveFavoriteCity,
        onNavigateBack = onNavigateBack,
        viewModel = viewModel
    )
}


@Composable
fun WeatherHomeContent(
    modifier: Modifier = Modifier,
    uiState: WeatherHomeUiState,
    onRetry: () -> Unit = {},
    onSaveFavorite: () -> Unit = {},
    onNavigateBack: () -> Unit = {},
    viewModel: WeatherHomeViewModel? = null
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .aspectRatio(0.45f)
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF08244F),
                        Color(0xFF134CB5),
                        Color(0xFF0A42AB)
                    )
                )
            )
            .padding(32.dp)
    ) {
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White)
                }
            }

            uiState.errorMessage != null -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = uiState.errorMessage,
                        color = Color.White,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onRetry,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White.copy(alpha = 0.2f)
                        )
                    ) {
                        Text("Retry", color = Color.White)
                    }
                }
            }

            uiState.weatherData != null -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Location",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${uiState.currentLocation?.name}, ${uiState.currentLocation?.country}",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Replace with your weather icon based on weatherData.weather[0].icon
                    Image(
                        painter = painterResource(id = R.drawable.sun_cloud_angled_rain__1_),
                        contentDescription = uiState.weatherData.weather.firstOrNull()?.description
                            ?: "",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(200.dp)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Temperature
                    Text(
                        text = viewModel?.getFormattedTemperature(uiState.weatherData.main.temp)
                            ?: "${uiState.weatherData.main.temp.toInt()}°",
                        color = Color.White,
                        fontSize = 64.sp,
                        fontWeight = FontWeight.Bold
                    )

                    // Weather description
                    Text(
                        text = uiState.weatherData.weather.firstOrNull()?.description?.replaceFirstChar {
                            if (it.isLowerCase()) it.titlecase() else it.toString()
                        } ?: "",
                        color = Color.White,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Min/Max temperature
                    Text(
                        text = "Max: ${viewModel?.kelvinToCelsius(uiState.weatherData.main.tempMax) ?: (uiState.weatherData.main.tempMax - 273.15).toInt()}°  Min: ${
                            viewModel?.kelvinToCelsius(
                                uiState.weatherData.main.tempMin
                            ) ?: (uiState.weatherData.main.tempMin - 273.15).toInt()
                        }°",
                        color = Color.White,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Weather details row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Color.White.copy(alpha = 0.1f),
                                RoundedCornerShape(16.dp)
                            )
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // Precipitation (using rain data)
                        WeatherDetailItem(
                            icon = "💧",
                            value = viewModel?.getRealPrecipitationPercentage()
                                ?: viewModel?.getPrecipitationPercentage()
                                ?: "${uiState.weatherData.clouds.all}%"

                        )

                        // Humidity
                        WeatherDetailItem(
                            icon = "💧",
                            value = viewModel?.getFormattedHumidity(uiState.weatherData.main.humidity)
                                ?: "${uiState.weatherData.main.humidity}%"

                        )

                        // Wind speed
                        WeatherDetailItem(
                            icon = "💨",
                            value = viewModel?.getFormattedWindSpeed(uiState.weatherData.wind.speed)
                                ?: "${uiState.weatherData.wind.speed.toInt()} km/h"

                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Color.White.copy(alpha = 0.1f),
                                RoundedCornerShape(16.dp)
                            )
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Today",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = uiState.currentDate,
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 14.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            val times = viewModel?.getHourlyForecastTimes() ?: listOf(
                                "15:00",
                                "16:00",
                                "17:00",
                                "18:00"
                            )
                            val temps = viewModel?.getHourlyForecastTemperatures()
                                ?: List(4) { "${uiState.weatherData.main.temp.toInt()}°C" }

                            times.zip(temps).forEach { (time, temp) ->
                                HourlyForecastItem(time = time, temp = temp)
                            }
                        }

                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    // Next Forecast button
                    Button(
                        onClick = { /* Navigate to detailed forecast */ },
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1A365D)
                        ),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Text(
                            text = "Save Forecast",
                            color = Color.White,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun WeatherDetailItem(
    icon: String,
    value: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = icon,
            fontSize = 16.sp
        )
        Text(
            text = value,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun HourlyForecastItem(
    time: String,
    temp: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(4.dp)
    ) {
        Text(
            text = temp,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Weather icon placeholder
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(
                    Color.White.copy(alpha = 0.2f),
                    RoundedCornerShape(8.dp)
                )
        ) {
            // Use your weather icon here
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = time,
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 12.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun WeatherHomeContentPreview() {
    WeatherHomeContent(
        uiState = WeatherHomeUiState()
    )
}