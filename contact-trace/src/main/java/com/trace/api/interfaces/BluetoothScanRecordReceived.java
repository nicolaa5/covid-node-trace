package com.trace.api.interfaces;

import android.bluetooth.BluetoothDevice;

import no.nordicsemi.android.support.v18.scanner.ScanRecord;
import no.nordicsemi.android.support.v18.scanner.ScanResult;

public interface BluetoothScanRecordReceived {
    void onBluetoothScanRecordReceived(BluetoothDevice device, ScanRecord scanRecord);
}
