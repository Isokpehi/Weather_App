package com.example.weatherapp.data.local.repository

import com.example.weatherapp.data.local.model.SavedCityModel
import kotlinx.coroutines.flow.Flow


interface SavedCityInterface {

    /**
     * Saves the category to the DataStore.
     *
     * @param savedCityModel The [SavedCityModel] to be saved.
     */
    suspend fun saveDataStore(savedCityModel: SavedCityModel)


    /**
     * Reads the stored category from the DataStore and maps it into a [SavedCityModel].
     * If no category is stored, it returns an empty string as the default.
     *
     * @return A [Flow] emitting the stored [SavedCityModel].
     */
    fun getDataStore(): Flow<SavedCityModel>

    /**
     * Clears the DataStore.
     */
    suspend fun clearDataStore()

}