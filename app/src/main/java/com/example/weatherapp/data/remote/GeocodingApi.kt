package com.example.weatherapp.data.remote

import com.example.weatherapp.domain.model.GeoLocation
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GeocodingApi {

    // Search cities and get their coordinates
    @GET("direct")
    suspend fun searchCities(
        @Query("q") query: String,
        @Query("limit") limit: Int = 5,
        @Query("appid") apiKey: String
    ): Response<List<GeoLocation>>

}