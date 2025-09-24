package com.example.weatherapp.data.remote

import com.example.weatherapp.domain.model.HourlyForecastResponse
import com.example.weatherapp.domain.model.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Interface for weather API operations.
 */
interface WeatherApi {

    /**
     * Gets weather data for a specific location.
     *
     * @param lat The latitude of the location.
     * @param lon The longitude of the location.
     * @param apiKey The API key for authentication.
     * @param units The unit of measurement for temperature (default is "metric").
     * @return A [Response] containing the weather data.
     */
    @GET("weather")
    suspend fun getWeatherByCoordinates(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): Response<WeatherResponse>

    /**
     * Gets hourly weather forecast for a specific location.
     *
     * @param lat The latitude of the location.
     * @param lon The longitude of the location.
     * @param apiKey The API key for authentication.
     * @param units The unit of measurement for temperature (default is "metric").
     * @param cnt The number of forecasts to retrieve (default is 8).
     * @return A [Response] containing the hourly weather forecast.
     */
    @GET("forecast")
    suspend fun getHourlyForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric",
        @Query("cnt") cnt: Int = 8 // Get 8 forecasts (24 hours worth at 3-hour intervals)
    ): Response<HourlyForecastResponse>
}