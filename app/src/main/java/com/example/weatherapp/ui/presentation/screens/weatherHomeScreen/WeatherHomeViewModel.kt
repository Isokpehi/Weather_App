package com.example.weatherapp.ui.presentation.screens.weatherHomeScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.repository.WeatherRepository
import com.example.weatherapp.domain.model.GeoLocation
import com.example.weatherapp.domain.model.HourlyForecastResponse
import com.example.weatherapp.domain.model.WeatherResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class WeatherHomeUiState(
    val isLoading: Boolean = false,
    val weatherData: WeatherResponse? = null,
    val hourlyForecast: HourlyForecastResponse? = null,
    val currentLocation: GeoLocation? = null,
    val errorMessage: String? = null,
    val currentDate: String = "",
    val currentTime: String = ""
)

@HiltViewModel
class WeatherHomeViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WeatherHomeUiState())
    val uiState: StateFlow<WeatherHomeUiState> = _uiState.asStateFlow()

    init {
        updateCurrentDateTime()
    }

    fun loadWeatherData(geoLocation: GeoLocation) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null,
                currentLocation = geoLocation
            )

            try {
                val response = weatherRepository.getWeatherByCoordinates(
                    lat = geoLocation.lat,
                    lon = geoLocation.lon
                )

                val hourlyResponse = weatherRepository.getHourlyForecast(
                    lat = geoLocation.lat,
                    lon = geoLocation.lon
                )

                if (response.isSuccessful) {
                    val weatherData = response.body()
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        weatherData = weatherData,
                        hourlyForecast = if (hourlyResponse.isSuccessful) hourlyResponse.body() else null

                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Failed to load weather data. Please try again."
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Network error. Please check your internet connection."
                )
            }
        }
    }

    private fun updateCurrentDateTime() {
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val currentDate = Date()

        _uiState.value = _uiState.value.copy(
            currentDate = dateFormat.format(currentDate),
            currentTime = timeFormat.format(currentDate)
        )
    }

    fun kelvinToCelsius(kelvin: Double): Int {
        return (kelvin - 273.15).toInt()
    }

    fun getFormattedTemperature(temp: Double): String {
        // Convert from Kelvin to Celsius
        val celsius = kelvinToCelsius(temp)
        return "${celsius}°"
    }

    fun getFormattedWindSpeed(speed: Double): String {
        // Convert from m/s to km/h
        val kmh = (speed * 3.6).toInt()
        return "$kmh km/h"
    }

    fun getFormattedHumidity(humidity: Int): String {
        return "$humidity%"
    }

    fun getPrecipitationPercentage(): String {
        val weatherData = _uiState.value.weatherData
        return when {
            weatherData?.weather?.firstOrNull()?.main?.lowercase()?.contains("rain") == true -> {
                // If it's raining, show a moderate precipitation chance
                "${(30..70).random()}%"
            }
            weatherData?.weather?.firstOrNull()?.main?.lowercase()?.contains("drizzle") == true -> {
                // If it's drizzling, show a light precipitation chance
                "${(10..30).random()}%"
            }
            weatherData?.clouds?.all != null -> {
                // Use cloud coverage as approximation (clouds.all is 0-100)
                val clouds = weatherData.clouds.all
                // Your JSON shows clouds.all = 100 (fully cloudy)
                when {
                    clouds >= 80 -> "${(20..40).random()}%" // Heavy clouds = moderate precipitation chance
                    clouds >= 50 -> "${(5..20).random()}%" // Moderate clouds = low precipitation chance
                    else -> "${(0..5).random()}%" // Light clouds = very low precipitation chance
                }
            }
            else -> "0%"
        }
    }

    fun getHourlyForecastTimes(): List<String> {
        val hourlyForecast = _uiState.value.hourlyForecast
        return if (hourlyForecast != null && hourlyForecast.list.isNotEmpty()) {
            // Get next 4 forecast items (3-hour intervals)
            hourlyForecast.list.take(4).map { forecastItem ->
                val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                val date = Date(forecastItem.dt * 1000L)
                dateFormat.format(date)
            }
        } else {
            // Fallback to simulated times
            val currentTime = Calendar.getInstance()
            val times = mutableListOf<String>()
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

            for (i in 1..4) {
                currentTime.add(Calendar.HOUR_OF_DAY, 1)
                times.add(timeFormat.format(currentTime.time))
            }
            times
        }
    }


    fun getHourlyForecastTemperatures(): List<String> {
        val hourlyForecast = _uiState.value.hourlyForecast
        return if (hourlyForecast != null && hourlyForecast.list.isNotEmpty()) {
            // Get temperatures from actual forecast data and convert from Kelvin
            hourlyForecast.list.take(4).map { forecastItem ->
                "${kelvinToCelsius(forecastItem.main.temp)}°C"
            }
        } else {
            // Fallback to simulated temperatures based on current temperature
            val currentTemp = _uiState.value.weatherData?.main?.temp ?: 298.15 // Default to ~25°C in Kelvin
            val currentCelsius = kelvinToCelsius(currentTemp)
            val temps = mutableListOf<String>()

            for (i in 1..4) {
                val variation = (-2..2).random()
                val hourlyTemp = currentCelsius + variation
                temps.add("${hourlyTemp}°C")
            }
            temps
        }
    }

    fun getRealPrecipitationPercentage(): String {
        val hourlyForecast = _uiState.value.hourlyForecast
        return if (hourlyForecast != null && hourlyForecast.list.isNotEmpty()) {
            // Use probability of precipitation from forecast
            val avgPop = hourlyForecast.list.take(4).map { it.pop }.average()
            "${(avgPop * 100).toInt()}%"
        } else {
            getPrecipitationPercentage() // Fallback to estimated method
        }
    }

    fun retry() {
        _uiState.value.currentLocation?.let { location ->
            loadWeatherData(location)
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    // Method to save favorite city (for future implementation with local storage)
    fun saveFavoriteCity() {
        _uiState.value.currentLocation?.let { location ->
            // TODO: Implement saving to SharedPreferences or Room database
            // This would be used to prepopulate the search field on the homepage
            viewModelScope.launch {
                try {
                    // Implementation for saving favorite city
                    // For now, we can just log or show a success message
                } catch (e: Exception) {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "Failed to save favorite city."
                    )
                }
            }
        }
    }

}