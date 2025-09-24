package com.example.weatherapp.data.repository

import com.example.weatherapp.BuildConfig
import com.example.weatherapp.data.remote.GeocodingApi
import com.example.weatherapp.data.remote.WeatherApi
import com.example.weatherapp.domain.model.GeoLocation
import com.example.weatherapp.domain.model.HourlyForecastResponse
import com.example.weatherapp.domain.model.WeatherResponse
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepositoryImpl @Inject constructor(
    private val weatherApi: WeatherApi,
    private val geocodingApi: GeocodingApi
) : WeatherRepository {

    override suspend fun searchCities(query: String): Response<List<GeoLocation>> {
        return geocodingApi.searchCities(
            query = query,
            apiKey = BuildConfig.API_KEY // uses your key from BuildConfig
        )
    }

    override suspend fun getWeatherByCoordinates(lat: Double, lon: Double): Response<WeatherResponse> {
        return weatherApi.getWeatherByCoordinates(
            lat = lat,
            lon = lon,
            apiKey = BuildConfig.API_KEY, // uses your key from BuildConfig
            units = "metric"
        )
    }

    override suspend fun getHourlyForecast(lat: Double, lon: Double): Response<HourlyForecastResponse> {
        return weatherApi.getHourlyForecast(
            lat = lat,
            lon = lon,
            apiKey = BuildConfig.API_KEY,
            units = "metric", // This will give you Celsius directly
            cnt = 8 // Get next 24 hours (8 forecasts at 3-hour intervals)
        )
    }
}