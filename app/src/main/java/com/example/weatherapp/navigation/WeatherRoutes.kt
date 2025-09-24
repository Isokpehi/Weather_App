package com.example.weatherapp.navigation

import com.example.weatherapp.domain.model.GeoLocation
import kotlinx.serialization.Serializable

sealed class WeatherRoutes {

    @Serializable
    data object SplashScreen : WeatherRoutes()

    @Serializable
    data object SearchCityScreen : WeatherRoutes()

    @Serializable
    data class WeatherHomeScreen(
    val cityName: String,        // Simple strings and numbers
    val latitude: Double,        // are easily serialized
    val longitude: Double,
    val country: String,
    val state: String? = null
    ) : WeatherRoutes()
}