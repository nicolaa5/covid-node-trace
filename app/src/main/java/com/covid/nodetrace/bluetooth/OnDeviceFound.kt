package com.covid.nodetrace.bluetooth

import android.bluetooth.BluetoothDevice

interface OnDeviceFound {
    /**
     * Node devices that are found in a bluetooth scan
     *
     * @param device
     * the device to that was detected
     *
     * @param data
     * the data included in the message
     */
    fun onDeviceFound(device: BluetoothDevice, data: ByteArray, timestamp : Long)
}