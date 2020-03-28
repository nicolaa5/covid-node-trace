package com.trace.api.interfaces;

import android.bluetooth.BluetoothDevice;

import com.trace.api.data.ContactTraceResult;

/**
 * Interface for retrieving contact trace results from processed Bluetooth scan records
 */
public interface ContactTraceResultReceived {
    void onContactTraceResultReceived(BluetoothDevice device, ContactTraceResult scanResult);
}
