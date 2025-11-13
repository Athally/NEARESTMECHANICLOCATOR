package com.example.nearestmechaniclocator;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OBDViewModel extends ViewModel {

    private static final String TAG = "OBDViewModel";
    private static final UUID OBD_UUID =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private final MutableLiveData<Map<String, String>> obdLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> connectionStatus = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();

    private OBDRepository repository;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private boolean isStreaming = false;

    // --- Initialize Repository ---
    public void initRepository(InputStream in, OutputStream out) {
        repository = new OBDRepository(in, out);
    }

    // --- LiveData Getters ---
    public LiveData<Map<String, String>> getObdLiveData() {
        return obdLiveData;
    }

    public LiveData<String> getConnectionStatus() {
        return connectionStatus;
    }

    public LiveData<String> getError() {
        return error;
    }

    // --- Connect to Bluetooth ELM327 ---
    public void connectBluetooth(BluetoothDevice device, Context context) {
        new Thread(() -> {
            try {
                if (ContextCompat.checkSelfPermission(
                        context, android.Manifest.permission.BLUETOOTH_CONNECT)
                        != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    postError("Missing BLUETOOTH_CONNECT permission");
                    return;
                }

                postStatus("Connecting via Bluetooth...");

                var socket = device.createRfcommSocketToServiceRecord(OBD_UUID);
                socket.connect();

                InputStream in = socket.getInputStream();
                OutputStream out = socket.getOutputStream();
                initRepository(in, out);

                postStatus("Connected to " + device.getName());
                fetchSnapshot();

            } catch (SecurityException e) {
                postError("Bluetooth permission denied: " + e.getMessage());
            } catch (IOException e) {
                postError("Bluetooth connection failed: " + e.getMessage());
            }
        }).start();
    }

    // --- Connect to Wi-Fi ELM327 (TCP Socket) ---
    public void connectWifi(String host, int port) {
        new Thread(() -> {
            try {
                postStatus("Connecting via Wi-Fi...");
                Socket socket = new Socket();
                socket.connect(new InetSocketAddress(host, port), 5000);

                InputStream in = socket.getInputStream();
                OutputStream out = socket.getOutputStream();
                initRepository(in, out);

                postStatus("Connected via Wi-Fi");
                fetchSnapshot();

            } catch (IOException e) {
                postError("Wi-Fi connection failed: " + e.getMessage());
            }
        }).start();
    }

    // --- Fetch one-time OBD snapshot ---
    public void fetchSnapshot() {
        if (repository == null) {
            postError("Repository not initialized");
            return;
        }

        executor.execute(() -> {
            try {
                Map<String, String> data = repository.readSnapshot();
                obdLiveData.postValue(data);
            } catch (Exception e) {
                postError("Snapshot read failed: " + e.getMessage());
            }
        });
    }

    // --- Run full diagnostics manually (for Refresh button) ---
    public void runDiagnostics() {
        if (repository == null) {
            postError("Not connected to OBD");
            return;
        }

        postStatus("Running diagnostics...");
        executor.execute(() -> {
            try {
                Map<String, String> data = repository.readSnapshot();
                obdLiveData.postValue(data);
                postStatus("Diagnostics complete ✅");
            } catch (Exception e) {
                postError("Diagnostics failed: " + e.getMessage());
            }
        });
    }

    // --- Stream continuously ---
    public void startStreaming(int intervalMs) {
        if (repository == null || isStreaming) return;

        isStreaming = true;
        executor.execute(() -> {
            while (isStreaming) {
                try {
                    Map<String, String> data = repository.readSnapshot();
                    obdLiveData.postValue(data);
                    Thread.sleep(intervalMs);
                } catch (Exception e) {
                    Log.e(TAG, "Streaming error", e);
                    postError("Streaming stopped: " + e.getMessage());
                    break;
                }
            }
        });
    }

    public void stopStreaming() {
        isStreaming = false;
    }

    private void postStatus(String status) {
        connectionStatus.postValue(status);
    }

    private void postError(String msg) {
        error.postValue(msg);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        stopStreaming();
        executor.shutdownNow();
    }
}
