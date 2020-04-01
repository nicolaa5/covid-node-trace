package com.trace.api.data;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.os.Build;

import java.util.UUID;

public class BluetoothAdvertisement {

    BluetoothLeAdvertiser advertiser;

    @TargetApi(Build.VERSION_CODES.M)
    public BluetoothAdvertisement () {
        advertiser = BluetoothAdapter.getDefaultAdapter().getBluetoothLeAdvertiser();

        AdvertiseData testAdvertisement = new AdvertiseData.Builder()
                .addManufacturerData(0,
                        new byte[]{0x43, 0x6f, 0x6e, 0x74, 0x61, 0x63, 0x74, 0x20, 0x54, 0x72,0x61, 0x63, 0x69, 0x6e, 0x67})
                //.addServiceData()
                //.addServiceUuid()
                .build();

        advertiseData(testAdvertisement);
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void advertiseData (AdvertiseData data) {
        AdvertiseSettings settings = new AdvertiseSettings.Builder()
                .setAdvertiseMode(1)
                .setConnectable(false)
                //.setTimeout(5)
                //.setTxPowerLevel()
                .build();

        advertiser.startAdvertising(settings, data, new AdvertiseCallback() {
            @Override
            public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                super.onStartSuccess(settingsInEffect);
            }

            @Override
            public void onStartFailure(int errorCode) {
                super.onStartFailure(errorCode);
            }
        });
    }
}
