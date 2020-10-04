package com.covid.nodetrace.database

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.getSystemService


object NetworkHelper {

    private var connectionState : ConnectionState = ConnectionState.DISCONNECTED

    enum class ConnectionState {
        CONNECTED,
        DISCONNECTED
    }

    fun isConnectedToNetwork(context: Context): Boolean {
        val connectionManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectionManager.activeNetworkInfo ?: return false

        if (networkInfo.isConnected)
            connectionState = ConnectionState.CONNECTED
        else {
            connectionState = ConnectionState.DISCONNECTED
        }
        return networkInfo.isConnected
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun registerNetworkCallback(context: Context) {
        try {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            val builder = NetworkRequest.Builder()
            builder.addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)

            val networkRequest = builder.build()
            connectivityManager.registerNetworkCallback(networkRequest,
                object : ConnectivityManager.NetworkCallback () {
                    override fun onAvailable(network: Network?) {
                        super.onAvailable(network)
                        connectionState = ConnectionState.CONNECTED
                    }

                    override fun onLost(network: Network?) {
                        super.onLost(network)
                        connectionState = ConnectionState.DISCONNECTED
                    }
                })

        } catch (e: Exception) {
            connectionState = ConnectionState.DISCONNECTED
        }
    }
}