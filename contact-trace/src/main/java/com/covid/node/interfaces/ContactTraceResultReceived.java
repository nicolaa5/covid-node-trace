package com.covid.node.interfaces;

import android.bluetooth.BluetoothDevice;

import com.covid.node.data.ContactTraceResult;

/**
 * Interface for retrieving contact trace results from processed Bluetooth scan records
 */
public interface ContactTraceResultReceived {
    void onContactTraceResultReceived(BluetoothDevice device, ContactTraceResult scanResult);
}
