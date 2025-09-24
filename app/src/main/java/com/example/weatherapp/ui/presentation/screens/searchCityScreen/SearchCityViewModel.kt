package com.example.weatherapp.ui.presentation.screens.searchCityScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.repository.WeatherRepository
import com.example.weatherapp.domain.model.GeoLocation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


data class SearchCityUiState(
    val searchText: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSearchButtonEnabled: Boolean = false
)

@HiltViewModel
class SearchCityViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchCityUiState())
    val uiState: StateFlow<SearchCityUiState> = _uiState.asStateFlow()

    fun onSearchTextChanged(text: String) {
        _uiState.value = _uiState.value.copy(
            searchText = text,
            errorMessage = null,
            isSearchButtonEnabled = text.trim().isNotEmpty()
        )
    }

    fun onSubmitSearch(
        onNavigateToWeatherHomeScreen: (GeoLocation) -> Unit
    ) {
        val currentText = _uiState.value.searchText.trim()
        if (currentText.isNotEmpty()) {
            searchCityAndNavigate(currentText, onNavigateToWeatherHomeScreen)
        }
    }

    private fun searchCityAndNavigate(query: String, onNavigateToWeather: (GeoLocation) -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            try {
                val response = weatherRepository.searchCities(query)
                if (response.isSuccessful) {
                    val cities = response.body() ?: emptyList()
                    if (cities.isNotEmpty()) {
                        // Get the first city result and navigate immediately
                        val firstCity = cities.first()
                        _uiState.value = _uiState.value.copy(isLoading = false)
                        onNavigateToWeather(firstCity)
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = "City not found. Please try a different city name."
                        )
                    }
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Search failed. Please check your internet connection."
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Network error. Please try again."
                )
            }
        }
    }

    fun clearSearch() {
        _uiState.value = SearchCityUiState()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}