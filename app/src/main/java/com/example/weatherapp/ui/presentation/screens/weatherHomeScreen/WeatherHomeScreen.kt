package com.example.weatherapp.ui.presentation.screens.weatherHomeScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.weatherapp.R
import com.example.weatherapp.data.local.model.SavedCityModel
import com.example.weatherapp.domain.model.Clouds
import com.example.weatherapp.domain.model.Coord
import com.example.weatherapp.domain.model.GeoLocation
import com.example.weatherapp.domain.model.Main
import com.example.weatherapp.domain.model.Sys
import com.example.weatherapp.domain.model.Weather
import com.example.weatherapp.domain.model.WeatherResponse
import com.example.weatherapp.domain.model.Wind
import com.example.weatherapp.ui.presentation.screens.searchCityScreen.SavedCityViewModel

/**
 * Main screen composable for displaying weather details.
 * Handles loading, error, and content states using uiState.
 */
@Composable
fun WeatherHomeScreen(
    modifier: Modifier = Modifier,
    geoLocation: GeoLocation,
    onNavigateBack: () -> Unit = {},
    viewModel: WeatherHomeViewModel = hiltViewModel(),
    savedCityViewModel: SavedCityViewModel = hiltViewModel()

) {
    val uiState by viewModel.uiState.collectAsState()

    val savedCityState by savedCityViewModel.getData.collectAsState()
    val cityFromStore = savedCityState.city

    LaunchedEffect(geoLocation) {
        viewModel.loadWeatherData(geoLocation)
    }

    // Fixed temperature calculations with proper null safety
    val temperature = "${uiState.weatherData?.main?.temp}°C"

    val feelsLike = "Feels like ${uiState.weatherData?.main?.feelsLike}°C"

    val humidity = uiState.weatherData?.main?.humidity?.let {
        "${it}%" // Don't use getFormattedHumidity since it already adds %
    } ?: "--"

    val windSpeed = uiState.weatherData?.wind?.speed?.let {
        viewModel.getFormattedWindSpeed(it)
    } ?: "--"

    val visibility = uiState.weatherData?.visibility?.let {
        viewModel.getFormattedVisibility(it)
    } ?: "--"

    WeatherHomeContent(
        modifier = modifier,
        uiState = uiState,
        onRetry = viewModel::retry,
        onSaveFavorite = {
            // Save the current city name to DataStore
            val cityName = geoLocation.name
            savedCityViewModel.saveData(SavedCityModel(cityName))
        },
        onNavigateBack = onNavigateBack,
        temperature = temperature,
        feelsLike = feelsLike,
        humidity = humidity,
        windSpeed = windSpeed,
        visibility = visibility
    )
}


@Composable
fun WeatherHomeContent(
    modifier: Modifier = Modifier,
    uiState: WeatherHomeUiState,
    onRetry: () -> Unit = {},
    onSaveFavorite: () -> Unit = {},
    onNavigateBack: () -> Unit = {},
    temperature: String,
    feelsLike: String,
    humidity: String,
    windSpeed: String,
    visibility: String,


    ) {
    Box(
        modifier = modifier
            .fillMaxSize()
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp),
                        horizontalArrangement = Arrangement.Center,
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

                    // Weather Icon using Coil to load from OpenWeatherMap API
                    val weatherIcon = uiState.weatherData.weather.firstOrNull()?.icon
                    if (weatherIcon != null) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data("https://openweathermap.org/img/wn/$weatherIcon@4x.png")
                                .crossfade(true)
                                .build(),
                            contentDescription = uiState.weatherData.weather.firstOrNull()?.description,
                            modifier = Modifier.size(160.dp),
                            contentScale = ContentScale.Fit,
                            placeholder = painterResource(id = R.drawable.sun_cloud_angled_rain__1_),
                            error = painterResource(id = R.drawable.sun_cloud_angled_rain__1_)
                        )
                    } else {
                        // Fallback to local image
                        Image(
                            painter = painterResource(id = R.drawable.sun_cloud_angled_rain__1_),
                            contentDescription = "Weather Icon",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.size(200.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Temperature
                    Text(
                        text = temperature,
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

                    // feels like temperature
                    Text(
                        text = feelsLike,
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
                            icon = painterResource(R.drawable.smoke),
                            value = humidity,
                            label = "Humidity"

                        )

                        WeatherDetailItem(
                            icon = painterResource(R.drawable.smoke),
                            value = windSpeed,
                            label = "km/h"

                        )

                        WeatherDetailItem(
                            icon = painterResource(R.drawable.eye),
                            value = visibility,
                            label = "km"

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
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    // Next Forecast button
                    Button(
                        onClick = {
                            onSaveFavorite()
                        },
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

                    Spacer(modifier = Modifier.height(40.dp))

                    Button(
                        onClick = { onNavigateBack() },
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF175DE0)
                        ),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Text(
                            text = "Search Another City",
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
    icon: Painter,
    value: String,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = icon,
            contentDescription = "Weather Icon",
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = value,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            color = Color.White,
            fontSize = 12.sp,
        )
    }
}


@Preview(showBackground = true)
@Composable
fun WeatherHomeContentPreview() {
    // Create mock data directly in the UiState
    val mockUiState = WeatherHomeUiState(
        isLoading = false,
        weatherData = WeatherResponse(
            main = Main(
                temp = 298.15, // 25°C in Kelvin
                feelsLike = 295.15, // 22°C in Kelvin
                humidity = 65,
                pressure = 1013,
                tempMin = 295.15,
                tempMax = 300.15
            ),
            weather = listOf(
                Weather(
                    id = 802,
                    main = "Clouds",
                    description = "partly cloudy",
                    icon = "03d"
                )
            ),
            wind = Wind(speed = 1.39, deg = 240), // 1.39 m/s = ~5 km/h
            visibility = 10000,
            name = "Lagos",
            sys = Sys(country = "NG", sunrise = 0, sunset = 0),
            coord = Coord(1.1, 1.1),
            base = "",
            clouds = Clouds(1),
            dt = 5L,
            timezone = 4,
            id = 6,
            cod = 6
        ),
        currentLocation = GeoLocation(
            lat = 6.5244,
            lon = 3.3792,
            name = "Lagos",
            country = "NG"
        ),
        errorMessage = null,
        currentDate = "Sep 24, 2025",
        currentTime = "14:30"
    )

    WeatherHomeContent(
        uiState = mockUiState,
        temperature = "25°C",
        feelsLike = "Feels like: 22°C",
        humidity = "65%",
        windSpeed = "5 km/h",
        visibility = "10km"
    )
}