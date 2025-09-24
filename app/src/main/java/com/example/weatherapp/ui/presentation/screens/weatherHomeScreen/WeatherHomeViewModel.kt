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

    fun getFormattedWindSpeed(speed: Double): String {
        // Convert from m/s to km/h
        val kmh = (speed * 3.6).toInt()
        return "$kmh"
    }

    fun getFormattedHumidity(humidity: Int): String {
        return "$humidity%"
    }

    fun getFormattedVisibility(visibility: Int): String {
        return "${visibility / 1000}"
    }


    fun retry() {
        _uiState.value.currentLocation?.let { location ->
            loadWeatherData(location)
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

}