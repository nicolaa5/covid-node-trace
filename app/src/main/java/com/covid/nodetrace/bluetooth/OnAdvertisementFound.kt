package com.covid.nodetrace.bluetooth

import android.bluetooth.BluetoothDevice
import no.nordicsemi.android.support.v18.scanner.ScanResult

interface OnAdvertisementFound {
    /**
     * Scan result that are found in a bluetooth scan
     *
     * @param scanResult
     * The scan result that includes
     * - The found device
     * - The advertisement data
     * - The timestamp of the scan
     */
    fun onAdvertisementFound(scanResult: ScanResult)
}