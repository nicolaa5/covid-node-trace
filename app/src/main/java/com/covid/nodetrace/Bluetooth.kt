package com.covid.nodetrace

import android.content.Context
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.messages.MessagesClient
import com.google.android.gms.nearby.messages.MessagesOptions
import com.google.android.gms.nearby.messages.NearbyPermissions


object Bluetooth {

    public fun getBluetoothAccess(context: Context) : MessagesClient? {
        return Nearby.getMessagesClient(context, MessagesOptions.Builder()
                .setPermissions(NearbyPermissions.BLE)
                .build()
        )
    }
}