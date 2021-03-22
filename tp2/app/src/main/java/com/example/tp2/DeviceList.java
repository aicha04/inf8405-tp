package com.example.tp2;
import android.content.Context;
import android.view.LayoutInflater;
import android.bluetooth.BluetoothDevice;
import java.util.ArrayList;
import android.view.View;
import android.widget.TextView;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class DeviceList extends ArrayAdapter<BluetoothDevice>{

    private final LayoutInflater layoutInflater;
    private final ArrayList<BluetoothDevice> pairedDevices;
    private final int viewResourceId;
    private int deviceIndex;

    public DeviceList(Context context, int deviceUuid, ArrayList<BluetoothDevice> devices) {
        super(context, deviceUuid, devices);
        this.pairedDevices = devices;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        viewResourceId = deviceUuid;
        this.deviceIndex = 1;
    }
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = layoutInflater.inflate(viewResourceId, null);
        BluetoothDevice device = pairedDevices.get(position);
        if (device != null) {
            TextView deviceIndex = (TextView) convertView.findViewById(R.id.deviceIndex);
            TextView deviceAddress = (TextView) convertView.findViewById(R.id.deviceAddress);
            // TextView deviceAlias = (TextView) convertView.findViewById(R.id.deviceAlias);
            TextView deviceClass = (TextView) convertView.findViewById(R.id.deviceClass);
            TextView deviceMajorClass = (TextView) convertView.findViewById(R.id.deviceMajorClass);
            // TextView deviceBondState = (TextView) convertView.findViewById(R.id.deviceBondState); // Décrit si l'appareil est connecté au telephone
            TextView deviceName = (TextView) convertView.findViewById(R.id.deviceName);
            TextView deviceType = (TextView) convertView.findViewById(R.id.deviceType);
            // TextView deviceUuid = (TextView) convertView.findViewById(R.id.deviceUuid);

            if (deviceIndex != null) {
                String strDeviceIndex = "Device n° " + String.valueOf(this.deviceIndex); // TODO: Ca incrémente à l'infini
                deviceIndex.setText(strDeviceIndex);
                this.deviceIndex++;
            }
            else
                deviceIndex.setText(R.string.deviceIndexError);

            String address = device.getAddress();
            if (address != null)
                deviceAddress.setText(address);
            else
                deviceAddress.setText(R.string.deviceAddressError);
//            if (deviceAlias != null)
//                deviceAlias.setText(device.getAlias());
            if (deviceClass != null)
                deviceClass.setText(device.getBluetoothClass().toString()); // deviceClass.setText(String.valueOf(device.getBluetoothClass().getDeviceClass()));
            else
                deviceAddress.setText(R.string.deviceClassError);
            if (deviceMajorClass != null)
                deviceMajorClass.setText(String.valueOf(device.getBluetoothClass().getMajorDeviceClass()));
            else
                deviceAddress.setText(R.string.deviceMajorClassError);
//            if (deviceBondState != null)
//                deviceBondState.setText(String.valueOf(device.getBondState()));


            String name = device.getName();
            if (deviceName != null) {
                deviceName.setText(name);
                System.out.println(deviceName);
            }
            else {
                deviceAddress.setText(R.string.deviceNameError);
            }
            if (deviceType != null)
                deviceType.setText(String.valueOf(device.getType()));
            else
                deviceAddress.setText(R.string.deviceTypeError);
//            if (deviceUuid != null)
//                deviceUuid.setText(device.getUuids().toString());
        }
        return convertView;
    }
}
