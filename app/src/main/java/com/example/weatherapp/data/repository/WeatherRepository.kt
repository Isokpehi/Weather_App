package com.example.weatherapp.data.repository

import com.example.weatherapp.domain.model.GeoLocation
import com.example.weatherapp.domain.model.HourlyForecastResponse
import com.example.weatherapp.domain.model.WeatherResponse
import retrofit2.Response

/**
 * Interface for weather repository operations.
 */
interface WeatherRepository {
    suspend fun searchCities(query: String): Response<List<GeoLocation>>
    suspend fun getWeatherByCoordinates(lat: Double, lon: Double): Response<WeatherResponse>
    suspend fun getHourlyForecast(lat: Double, lon: Double): Response<HourlyForecastResponse>

}
