package com.trace.api.data;

import android.bluetooth.BluetoothDevice;
import android.os.Parcel;
import android.os.Parcelable;

import no.nordicsemi.android.support.v18.scanner.ScanResult;

public class ContactTraceResult implements Parcelable {

    private ScanResult scanResult;
    private BluetoothDevice device;

    private Creator creator;

    public ContactTraceResult(ScanResult result) {
        scanResult = result;
        retrieveCustomManufacturerSpecificData();
    }

    private ContactTraceResult(final Parcel in) {
        readFromParcel(in);
    }

    private void retrieveCustomManufacturerSpecificData() {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    private void readFromParcel(final Parcel in) {
        device = BluetoothDevice.CREATOR.createFromParcel(in);
//        if (in.readInt() == 1) {
//            scanRecord = ContactTraceResult.parseFromBytes(in.createByteArray());
//        }
//        rssi = in.readInt();
//        timestampNanos = in.readLong();
//        eventType = in.readInt();
//        primaryPhy = in.readInt();
//        secondaryPhy = in.readInt();
//        advertisingSid = in.readInt();
//        txPower = in.readInt();
//        periodicAdvertisingInterval = in.readInt();
    }
    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        device.writeToParcel(dest, flags);
//        if (scanRecord != null) {
//            dest.writeInt(1);
//            dest.writeByteArray(scanRecord.getBytes());
//        } else {
//            dest.writeInt(0);
//        }
//        dest.writeInt(rssi);
//        dest.writeLong(timestampNanos);
//        dest.writeInt(eventType);
//        dest.writeInt(primaryPhy);
//        dest.writeInt(secondaryPhy);
//        dest.writeInt(advertisingSid);
//        dest.writeInt(txPower);
//        dest.writeInt(periodicAdvertisingInterval);
    }

    public static final Parcelable.Creator<ContactTraceResult> CREATOR = new Creator<ContactTraceResult>() {
        @Override
        public ContactTraceResult createFromParcel(final Parcel source) {
            return new ContactTraceResult(source);
        }

        @Override
        public ContactTraceResult[] newArray(final int size) {
            return new ContactTraceResult[size];
        }
    };
}
