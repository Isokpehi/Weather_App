package com.example.weatherapp.data.local.model

import android.os.Parcelable
import com.example.weatherapp.domain.model.GeoLocation
import kotlinx.parcelize.Parcelize

@Parcelize
data class SavedWeatherCity(
    val cityName: String,
    val country: String,
    val temperature: Double,
    val weatherCondition: String,
    val weatherIcon: String,
    val humidity: Int,
    val windSpeed: Double,
    val geoLocation: GeoLocation,
    val lastUpdated: Long = System.currentTimeMillis()
): Parcelable
