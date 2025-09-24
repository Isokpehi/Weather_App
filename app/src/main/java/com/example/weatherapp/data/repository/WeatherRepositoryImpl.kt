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

/**
 * Implementation of [WeatherRepository] that provides actual data from remote sources.
 */
@Singleton
class WeatherRepositoryImpl @Inject constructor(
    private val weatherApi: WeatherApi,
    private val geocodingApi: GeocodingApi
) : WeatherRepository {

    /**
     * Searches for cities based on the query.
     *
     * @param query The search query.
     * @return A [Response] containing the list of cities.
     */
    override suspend fun searchCities(query: String): Response<List<GeoLocation>> {
        return geocodingApi.searchCities(
            query = query,
            apiKey = BuildConfig.API_KEY // uses your key from BuildConfig
        )
    }

    /**
     * Gets weather data for a specific location.
     *
     * @param lat The latitude of the location.
     * @param lon The longitude of the location.
     * @return A [Response] containing the weather data.
     */
    override suspend fun getWeatherByCoordinates(lat: Double, lon: Double): Response<WeatherResponse> {
        return weatherApi.getWeatherByCoordinates(
            lat = lat,
            lon = lon,
            apiKey = BuildConfig.API_KEY, // uses your key from BuildConfig
            units = "metric"
        )
    }

    /**
     * Gets hourly weather forecast for a specific location.
     *
     * @param lat The latitude of the location.
     * @param lon The longitude of the location.
     * @return A [Response] containing the hourly weather forecast.
     */
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