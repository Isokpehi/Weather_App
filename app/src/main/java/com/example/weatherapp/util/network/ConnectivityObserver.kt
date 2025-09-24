package com.example.weatherapp.util.network

import kotlinx.coroutines.flow.Flow

/**
 * Interface for observing the device's current network connectivity status.
 */
interface ConnectivityObserver {

    /**
     * A [Flow] that emits `true` when the device is connected to a validated network,
     * and `false` otherwise.
     */
    val isConnected: Flow<Boolean>
}

