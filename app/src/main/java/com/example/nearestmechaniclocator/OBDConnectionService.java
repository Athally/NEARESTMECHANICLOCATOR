package com.example.nearestmechaniclocator;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Small service that connects to either Bluetooth SPP (ELM327) or a Wi-Fi host (ELM327 over TCP).
 * Exposes getInputStream()/getOutputStream() for the OBD command runner.
 */
public class OBDConnectionService extends Service {
    private static final String TAG = "OBDConnectionSvc";
    private final IBinder binder = new LocalBinder();

    // SPP UUID for most ELM327 devices
    private static final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private BluetoothSocket btSocket;
    private Socket wifiSocket;
    private InputStream input;
    private OutputStream output;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public class LocalBinder extends Binder {
        public OBDConnectionService getService() { return OBDConnectionService.this; }
    }

    @Override
    public IBinder onBind(Intent intent) { return binder; }

    public void connectBluetooth(final BluetoothDevice device, final ConnectionCallback cb) {
        executor.execute(() -> {
            try {
                if (device == null) throw new IllegalArgumentException("device==null");
                btSocket = device.createRfcommSocketToServiceRecord(SPP_UUID);
                btSocket.connect();
                input = btSocket.getInputStream();
                output = btSocket.getOutputStream();
                Log.i(TAG, "Bluetooth OBD connected");
                if (cb != null) cb.onConnected();
            } catch (Exception e) {
                Log.e(TAG, "BT connect error", e);
                if (cb != null) cb.onError(e);
                closeAll();
            }
        });
    }

    public void connectWifi(final String host, final int port, final ConnectionCallback cb) {
        executor.execute(() -> {
            try {
                wifiSocket = new Socket();
                wifiSocket.connect(new InetSocketAddress(host, port), 5000);
                input = wifiSocket.getInputStream();
                output = wifiSocket.getOutputStream();
                Log.i(TAG, "WiFi OBD connected");
                if (cb != null) cb.onConnected();
            } catch (Exception e) {
                Log.e(TAG, "WiFi connect error", e);
                if (cb != null) cb.onError(e);
                closeAll();
            }
        });
    }

    public InputStream getInputStream() { return input; }
    public OutputStream getOutputStream() { return output; }

    public boolean isConnected() {
        try {
            return (btSocket != null && btSocket.isConnected()) ||
                    (wifiSocket != null && wifiSocket.isConnected());
        } catch (Exception e) { return false; }
    }

    public void disconnect() {
        executor.execute(this::closeAll);
    }

    private void closeAll() {
        try { if (input != null) input.close(); } catch (Exception ignored) {}
        try { if (output != null) output.close(); } catch (Exception ignored) {}
        try { if (btSocket != null) btSocket.close(); } catch (Exception ignored) {}
        try { if (wifiSocket != null) wifiSocket.close(); } catch (Exception ignored) {}
        input = null; output = null; btSocket = null; wifiSocket = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        closeAll();
        executor.shutdownNow();
    }

    public interface ConnectionCallback {
        void onConnected();
        void onError(Exception e);
    }
}
