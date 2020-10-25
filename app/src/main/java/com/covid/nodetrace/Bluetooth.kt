package com.covid.nodetrace

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.messages.MessagesClient
import com.google.android.gms.nearby.messages.MessagesOptions
import com.google.android.gms.nearby.messages.NearbyPermissions


object Bluetooth {

    public fun getBluetoothAccess(context: Context) : MessagesClient? {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                return Nearby.getMessagesClient(context, MessagesOptions.Builder()
                        .setPermissions(NearbyPermissions.BLE)
                        .build()
                )
        }
        else {
            return null
        }
    }
}