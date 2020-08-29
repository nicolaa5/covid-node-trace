package com.covid.node;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.covid.node.data.ContactTraceResult;
import com.covid.node.interfaces.ContactTraceResultReceived;

import static com.covid.node.ContactTraceService.BROADCAST_SCAN_RESULT;
import static com.covid.node.ContactTraceService.EXTRA_DEVICE;
import static com.covid.node.ContactTraceService.EXTRA_SCAN_RESULT;


public class ContractTrace implements ContactTraceResultReceived {

    private Context context;
    private ContactTraceService mService;

    public ContractTrace(Context context) {
        this.context = context;
    }


    public void startService(){
        Intent mServiceIntent = new Intent(context, ContactTraceService.class);

        if (Build.VERSION.SDK_INT >= 26) {
            context.startForegroundService(mServiceIntent);
            context.bindService(mServiceIntent, mServiceConnection, 0);
        } else {
            // Pre-O behavior.
            context.startService(mServiceIntent);
            context.bindService(mServiceIntent, mServiceConnection, 0);
        }

        LocalBroadcastManager.getInstance(context).unregisterReceiver(mDataBroadcastReceiver);
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(final ComponentName name, final IBinder service) {
            final ContactTraceService bleService = mService = (ContactTraceService)  service;

            onServiceBound(bleService);
        }

        @Override
        public void onServiceDisconnected(final ComponentName name) {

        }
    };

    private final BroadcastReceiver mDataBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            final String action = intent.getAction();
            switch (action) {
                case BROADCAST_SCAN_RESULT:
                    BluetoothDevice device = intent.getParcelableExtra(EXTRA_DEVICE);
                    ContactTraceResult result = intent.getParcelableExtra(EXTRA_SCAN_RESULT);
                    onContactTraceResultReceived(device, result);
                    break;
            }
        }
    };

    @SuppressWarnings("WeakerAccess")
    public void onServiceBound (ContactTraceService service) {

    }

    @Override
    public void onContactTraceResultReceived(BluetoothDevice device, ContactTraceResult scanResult) {

    }
}
