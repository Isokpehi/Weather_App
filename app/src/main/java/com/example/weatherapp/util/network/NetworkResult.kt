package com.example.weatherapp.util.network

/**
 * Represents the state of a network operation and wraps the result or error if present.
 *
 * Used to model success, loading, or error states in network-related logic.
 *
 * @param T The type of data being returned by the network operation.
 * @param data Optional data returned by the network operation.
 * @param message Optional error message if an error occurred.
 */
sealed class NetworkResult<T>(
    val data: T? = null,
    val message: String? = null,
) {
    class Success<T>(data: T) : NetworkResult<T>(data)

    class Error<T>(message: String?, data: T? = null) : NetworkResult<T>(data, message)

    class Loading<T> : NetworkResult<T>()
}