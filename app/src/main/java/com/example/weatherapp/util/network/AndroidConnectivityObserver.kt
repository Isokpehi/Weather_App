package com.example.weatherapp.util.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import androidx.core.content.getSystemService
import com.example.weatherapp.util.network.ConnectivityObserver
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

/**
 * Implementation of [ConnectivityObserver] that uses the Android [ConnectivityManager]
 * to observe network connectivity changes in real time.
 *
 * @constructor Injects the [Context] using Hilt's [ApplicationContext] qualifier.
 *
 * @param context Application context used to get system service for connectivity monitoring.
 */
class AndroidConnectivityObserver @Inject constructor(
    @ApplicationContext private val context: Context,
) : ConnectivityObserver {
    private val connectivityManager = context.getSystemService<ConnectivityManager>()!!

    /**
     * Emits the connectivity status as a [Flow] of [Boolean].
     * Emits `true` when the device has validated network capability, otherwise `false`.
     *
     * Uses [callbackFlow] to bridge the Android connectivity callback API to a Flow.
     * Properly unregisters the network callback when the flow collector is cancelled.
     */
    override val isConnected: Flow<Boolean>
        get() = callbackFlow {
            val callback = object : NetworkCallback() {
                override fun onCapabilitiesChanged(
                    network: Network,
                    networkCapabilities: NetworkCapabilities,
                ) {
                    super.onCapabilitiesChanged(network, networkCapabilities)
                    val connected = networkCapabilities.hasCapability(
                        NetworkCapabilities.NET_CAPABILITY_VALIDATED,
                    )
                    trySend(connected)
                }

                override fun onUnavailable() {
                    super.onUnavailable()
                    trySend(false)
                }

                override fun onLost(network: Network) {
                    super.onLost(network)
                    trySend(false)
                }

                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    trySend(true)
                }
            }

            connectivityManager.registerDefaultNetworkCallback(callback)

            awaitClose {
                connectivityManager.unregisterNetworkCallback(callback)
            }
        }
}
