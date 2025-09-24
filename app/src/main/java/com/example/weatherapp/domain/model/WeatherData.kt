package com.example.weatherapp.domain.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable


@Parcelize
data class WeatherResponse(
    val coord: Coord,
    val weather: List<Weather>,
    val base: String,
    val main: Main,
    val visibility: Int,
    val wind: Wind,
    val clouds: Clouds,
    val dt: Long,
    val sys: Sys,
    val timezone: Int,
    val id: Int,
    val name: String,
    val cod: Int
) : Parcelable

@Parcelize
data class Coord(
    val lon: Double,
    val lat: Double
) : Parcelable

@Parcelize
data class Main(
    val temp: Double,
    @SerializedName("feels_like")
    val feelsLike: Double,
    @SerializedName("temp_min")
    val tempMin: Double,
    @SerializedName("temp_max")
    val tempMax: Double,
    val pressure: Int,
    val humidity: Int,
    @SerializedName("sea_level")
    val seaLevel: Int? = null,
    @SerializedName("grnd_level")
    val grndLevel: Int? = null
) : Parcelable

@Parcelize
data class Weather(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
) : Parcelable

@Parcelize
data class Wind(
    val speed: Double,
    val deg: Int,
    val gust: Double? = null
) : Parcelable

@Parcelize
data class Clouds(
    val all: Int
) : Parcelable

@Parcelize
data class Sys(
    val type: Int? = null,
    val id: Int? = null,
    val country: String,
    val sunrise: Long,
    val sunset: Long
) : Parcelable

// Geocoding API models (for city search with lat/lon)
@Parcelize
@Serializable  // Add this
data class GeoLocation(
    val name: String,
    val lat: Double,
    val lon: Double,
    val country: String,
    val state: String? = null,
    @SerializedName("local_names")
    val localNames: Map<String, String>? = null
) : Parcelable


// Hourly Forecast API Response (5-day/3-hour forecast)
@Parcelize
data class HourlyForecastResponse(
    val cod: String,
    val message: Int,
    val cnt: Int,
    val list: List<ForecastItem>,
    val city: City
) : Parcelable

@Parcelize
data class ForecastItem(
    val dt: Long,
    val main: ForecastMain,
    val weather: List<Weather>, // Reusing Weather class
    val clouds: Clouds, // Reusing Clouds class
    val wind: ForecastWind,
    val visibility: Int,
    val pop: Double, // Probability of precipitation (0-1)
    val rain: Rain? = null,
    val snow: Snow? = null,
    val sys: ForecastSys,
    @SerializedName("dt_txt")
    val dtTxt: String
) : Parcelable

@Parcelize
data class ForecastMain(
    val temp: Double,
    @SerializedName("feels_like")
    val feelsLike: Double,
    @SerializedName("temp_min")
    val tempMin: Double,
    @SerializedName("temp_max")
    val tempMax: Double,
    val pressure: Int,
    @SerializedName("sea_level")
    val seaLevel: Int,
    @SerializedName("grnd_level")
    val grndLevel: Int,
    val humidity: Int,
    @SerializedName("temp_kf")
    val tempKf: Double
) : Parcelable

@Parcelize
data class ForecastWind(
    val speed: Double,
    val deg: Int,
    val gust: Double? = null
) : Parcelable

@Parcelize
data class Rain(
    @SerializedName("3h")
    val threeHour: Double? = null
) : Parcelable

@Parcelize
data class Snow(
    @SerializedName("3h")
    val threeHour: Double? = null
) : Parcelable

@Parcelize
data class ForecastSys(
    val pod: String // Part of day (d = day, n = night)
) : Parcelable

@Parcelize
data class City(
    val id: Int,
    val name: String,
    val coord: Coord, // Reusing Coord class
    val country: String,
    val population: Int,
    val timezone: Int,
    val sunrise: Long,
    val sunset: Long
) : Parcelable