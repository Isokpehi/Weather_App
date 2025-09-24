package com.example.weatherapp.di.dataStore

import android.content.Context
import com.example.weatherapp.data.local.repository.SavedWeatherRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton



@Module
@InstallIn(SingletonComponent::class)
object SavedWeatherDataStore {

    @Singleton
    @Provides
    fun provideDataStoreRepository(
        @ApplicationContext context: Context
    ): SavedWeatherRepo = SavedWeatherRepo(context)
}
