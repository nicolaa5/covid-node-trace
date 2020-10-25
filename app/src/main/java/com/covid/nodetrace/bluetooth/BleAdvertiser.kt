package com.trace.api.data

import android.annotation.TargetApi
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.bluetooth.le.BluetoothLeAdvertiser
import android.os.Build
import android.util.Log

class BleAdvertiser @TargetApi(Build.VERSION_CODES.M) constructor() {
    private val TAG = "BleAdvertiser"
    private val advertiser: BluetoothLeAdvertiser?

    init {
        advertiser = BluetoothAdapter.getDefaultAdapter().bluetoothLeAdvertiser
    }

    @TargetApi(Build.VERSION_CODES.M)
    fun advertiseData(data: AdvertiseData?, settings : AdvertiseSettings) {

        if (advertiser == null) {
            Log.w(TAG, "Device cannot advertise data");
            return;
        }

        val settings = settings

        advertiser.startAdvertising(settings, data, object : AdvertiseCallback() {
            override fun onStartSuccess(settingsInEffect: AdvertiseSettings) {
                super.onStartSuccess(settingsInEffect)
            }

            override fun onStartFailure(errorCode: Int) {
                super.onStartFailure(errorCode)
            }
        })
    }

    fun stopAdvertising (callback: AdvertiseCallback? = null) {
        advertiser?.stopAdvertising(callback)
    }
}