package com.covid.node;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.AdvertisingSet;
import android.bluetooth.le.AdvertisingSetCallback;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.covid.node.data.ContactTraceResult;
import com.covid.node.interfaces.BluetoothScanRecordReceived;

import no.nordicsemi.android.support.v18.scanner.ScanRecord;

public class ContactTraceService extends Service implements BluetoothScanRecordReceived {
    private final static String TAG = ContactTraceService.class.getSimpleName();

    public static final String BROADCAST_SCAN_RESULT = "com.trace.api.BROADCAST_SCAN_RESULT";
    public static final String EXTRA_SCAN_RESULT = "com.trace.api.EXTRA_SCAN_RESULT";
    public static final String EXTRA_DEVICE = "com.trace.api.EXTRA_DEVICE";




    private final LocalBinder mBinder = new LocalBinder();

    public LocalBinder getBinder() {
        return mBinder;
    }


    /**
     * The interface for retrieving information from the service
     */
    public class LocalBinder extends Binder {

        /**
         * Initiate advertisement of contact trace data
         */
        public void startAdvertising() {

            if (Build.VERSION.SDK_INT >= 26) {
                BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
                BluetoothLeAdvertiser advertiser = BluetoothAdapter.getDefaultAdapter().getBluetoothLeAdvertiser();

                AdvertisingSetCallback callback = new AdvertisingSetCallback() {
                    @Override
                    public void onAdvertisingSetStarted(AdvertisingSet advertisingSet, int txPower, int status) {
                        Log.i(TAG, "onAdvertisingSetStarted(): txPower:" + txPower + " , status: "
                                + status);
                    }

                    @Override
                    public void onAdvertisingDataSet(AdvertisingSet advertisingSet, int status) {
                        Log.i(TAG, "onAdvertisingDataSet() :status:" + status);
                    }

                    @Override
                    public void onScanResponseDataSet(AdvertisingSet advertisingSet, int status) {
                        Log.i(TAG, "onScanResponseDataSet(): status:" + status);
                    }

                    @Override
                    public void onAdvertisingSetStopped(AdvertisingSet advertisingSet) {
                        Log.i(TAG, "onAdvertisingSetStopped():");
                    }
                };
            }

        }

        /**
         * Stop advertisement of contact trace data
         */
        public void stopAdvertising() {

        }

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return getBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBluetoothScanRecordReceived(BluetoothDevice device, ScanRecord scanRecord) {

    }

    public void onContactTraceResultReceived (BluetoothDevice device, ContactTraceResult contactTraceResult) {
        final Intent broadcast = new Intent(BROADCAST_SCAN_RESULT)
                .putExtra(EXTRA_DEVICE, device)
                .putExtra(EXTRA_SCAN_RESULT, contactTraceResult);

            LocalBroadcastManager.getInstance(this).sendBroadcast(broadcast);
    }
}
