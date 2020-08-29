package com.covid.node.interfaces;

import android.bluetooth.BluetoothDevice;

import no.nordicsemi.android.support.v18.scanner.ScanRecord;

public interface BluetoothScanRecordReceived {
    void onBluetoothScanRecordReceived(BluetoothDevice device, ScanRecord scanRecord);
}
