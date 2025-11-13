package com.example.nearestmechaniclocator;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.UUID;

public class OBDManager {

    private BluetoothSocket bluetoothSocket;
    private Socket wifiSocket;
    private OutputStream outputStream;
    private InputStream inputStream;
    private boolean isBluetooth = true;

    public boolean connectBluetooth(String deviceAddress) {
        try {
            BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
            BluetoothDevice device = adapter.getRemoteDevice(deviceAddress);
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return null;
            }
            bluetoothSocket = device.createRfcommSocketToServiceRecord(
                    UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
            bluetoothSocket.connect();
            outputStream = bluetoothSocket.getOutputStream();
            inputStream = bluetoothSocket.getInputStream();
            isBluetooth = true;
            return true;
        } catch (Exception e) {
            Log.e("OBD", "Bluetooth connect failed: " + e.getMessage());
            return false;
        }
    }

    public boolean connectWiFi(String ip, int port) {
        try {
            wifiSocket = new Socket(ip, port);
            outputStream = wifiSocket.getOutputStream();
            inputStream = wifiSocket.getInputStream();
            isBluetooth = false;
            return true;
        } catch (Exception e) {
            Log.e("OBD", "WiFi connect failed: " + e.getMessage());
            return false;
        }
    }

    public String sendCommand(String cmd) {
        try {
            outputStream.write((cmd + "\r").getBytes());
            Thread.sleep(200);
            byte[] buffer = new byte[1024];
            int len = inputStream.read(buffer);
            return new String(buffer, 0, len);
        } catch (Exception e) {
            Log.e("OBD", "Command failed: " + e.getMessage());
            return "Error reading OBD";
        }
    }

    public void disconnect() {
        try {
            if (isBluetooth && bluetoothSocket != null) bluetoothSocket.close();
            if (!isBluetooth && wifiSocket != null) wifiSocket.close();
        } catch (Exception ignored) {}
    }
}
