package com.example.weatherapp.data.local.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.weatherapp.data.local.model.SavedCityModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * The name used for the DataStore file.
 */
const val DataStore_NAME = "Saved_City_DataStore"

/**
 * Extension property to create a singleton instance of [DataStore] for [Preferences].
 */
val Context.datastore: DataStore<Preferences> by preferencesDataStore(name = DataStore_NAME)

/**
 * Repository implementation for managing city persistence using [DataStore].
 *
 * @param context The application context required to access the DataStore.
 */
class SavedWeatherRepo(private val context: Context) : SavedCityInterface {

    companion object {
        /**
         * Key used to store/retrieve the city string from the DataStore.
         */
        val CITY = stringPreferencesKey("CITY")
    }

    /**
     * Saves the city to the DataStore.
     *
     * @param savedCityModel The [SavedCityModel] to be saved.
     */
    override suspend fun saveDataStore(savedCityModel: SavedCityModel) {
        context.datastore.edit { data ->
            data[CITY] = savedCityModel.city!!

        }
    }

    /**
     * Reads the stored category from the DataStore and maps it into a [SavedCityModel].
     * If no category is stored, it returns an empty string as the default.
     *
     * @return A [Flow] emitting the stored [SavedCityModel].
     */
    override fun getDataStore(): Flow<SavedCityModel> =
        context.datastore.data.map { data ->
            SavedCityModel(
                city = data[CITY]
            )
        }

    /**
     * Clears the DataStore.
     */
    override suspend fun clearDataStore() {
        context.datastore.edit { data ->
            data.clear()
        }
    }
}

