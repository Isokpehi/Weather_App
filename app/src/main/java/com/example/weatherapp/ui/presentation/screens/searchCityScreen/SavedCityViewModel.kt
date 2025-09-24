package com.example.weatherapp.ui.presentation.screens.searchCityScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.local.model.SavedCityModel
import com.example.weatherapp.data.local.repository.SavedWeatherRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for managing saved city data.
 */
@HiltViewModel
class SavedCityViewModel @Inject constructor(private val savedWeatherRepo: SavedWeatherRepo) :
    ViewModel() {

    /**
     * StateFlow representing the saved city data.
     */
    val getData: StateFlow<SavedCityModel> =
        savedWeatherRepo.getDataStore().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = SavedCityModel(null)
        )


    /**
     * Saves the category to the DataStore.
     *
     * @param savedCityModel The [SavedCityModel] to be saved.
     */
    fun saveData(savedCityModel: SavedCityModel) {
        viewModelScope.launch(Dispatchers.IO) {
            savedWeatherRepo.saveDataStore(savedCityModel)
        }
    }

    /**
     * Clears the category from the DataStore.
     */
    fun clearCategory() {
        viewModelScope.launch(Dispatchers.IO) {
            savedWeatherRepo.clearDataStore()
        }
    }

}