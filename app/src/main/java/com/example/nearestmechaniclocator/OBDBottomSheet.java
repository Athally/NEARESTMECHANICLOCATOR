package com.example.nearestmechaniclocator;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class OBDBottomSheet extends BottomSheetDialogFragment {

    private static final String TAG = "OBDBottomSheet";
    private static final int REQUEST_BLUETOOTH_PERMISSIONS = 1001;
    private static final String ARG_OBD = "arg_obd";

    private String obdJson;

    private OBDViewModel viewModel;
    private TextView tvSpeed, tvTemp, tvData, tvStatus;
    private Button btnConnectBluetooth, btnConnectWifi, btnRefresh, btnSave;
    private ProgressBar progressBar;

    private BluetoothAdapter bluetoothAdapter;
    private WifiManager wifiManager;
    public static OBDBottomSheet newInstance(String obdJson) {
        OBDBottomSheet fragment = new OBDBottomSheet();
        Bundle args = new Bundle();
        args.putString(ARG_OBD, obdJson);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            obdJson = getArguments().getString(ARG_OBD);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottomsheet_obd, container, false);

        // --- UI setup ---
        tvSpeed = view.findViewById(R.id.tvSpeed);
        tvTemp = view.findViewById(R.id.tvTemp);
        tvData = view.findViewById(R.id.tvData);
        tvStatus = view.findViewById(R.id.tvStatus);
        progressBar = view.findViewById(R.id.progressBar);
        btnConnectBluetooth = view.findViewById(R.id.btnConnectBluetooth);
        btnConnectWifi = view.findViewById(R.id.btnConnectWifi);
        btnRefresh = view.findViewById(R.id.btnRefresh);
        btnSave = view.findViewById(R.id.btnSave);

        viewModel = new ViewModelProvider(requireActivity()).get(OBDViewModel.class);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        wifiManager = (WifiManager) requireContext().getApplicationContext()
                .getSystemService(android.content.Context.WIFI_SERVICE);

        // --- Observe LiveData ---
        viewModel.getConnectionStatus().observe(getViewLifecycleOwner(), status -> {
            tvStatus.setText(status);
            progressBar.setVisibility((status != null && status.toLowerCase().contains("connect")) ? View.VISIBLE : View.GONE);

            // Start streaming automatically once connected
            if (status != null && status.toLowerCase().contains("connected")) {
                viewModel.startStreaming(1000);
            }
        });

        viewModel.getObdLiveData().observe(getViewLifecycleOwner(), data -> {
            if (data == null) return;
            tvSpeed.setText(data.getOrDefault("Speed", "N/A"));
            tvTemp.setText(data.getOrDefault("CoolantTemp", "N/A"));
            tvData.setText("RPM: " + data.getOrDefault("RPM", "N/A"));
        });

        viewModel.getError().observe(getViewLifecycleOwner(), err -> {
            if (err != null)
                Toast.makeText(getContext(), "Error: " + err, Toast.LENGTH_SHORT).show();
        });

        // --- Button actions ---
        btnConnectBluetooth.setOnClickListener(v -> connectBluetooth());
        btnConnectWifi.setOnClickListener(v -> connectWifi());
        btnRefresh.setOnClickListener(v -> viewModel.runDiagnostics());
        btnSave.setOnClickListener(v -> saveObdToFirestore());

        return view;
    }

    private void connectBluetooth() {
        if (bluetoothAdapter == null) {
            Toast.makeText(getContext(), "Bluetooth not supported", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!bluetoothAdapter.isEnabled()) {
            Toast.makeText(getContext(), "Enable Bluetooth first", Toast.LENGTH_SHORT).show();
            return;
        }

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_BLUETOOTH_PERMISSIONS);
            return;
        }

        new Thread(() -> {
            BluetoothDevice device = null;
            for (BluetoothDevice d : bluetoothAdapter.getBondedDevices()) {
                if (d.getName().toLowerCase().contains("obd")) {
                    device = d;
                    break;
                }
            }

            if (device == null) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "No paired OBD device found", Toast.LENGTH_SHORT).show());
                return;
            }

            // Call ViewModel to connect
            viewModel.connectBluetooth(device, requireContext());
        }).start();
    }

    private void connectWifi() {
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }

        // Call ViewModel to connect via Wi-Fi
        viewModel.connectWifi("192.168.0.10", 35000);
    }

    private void saveObdToFirestore() {
        // You can implement saving through ViewModel or Firestore here
        Toast.makeText(getContext(), "OBD data saved successfully (placeholder)", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (viewModel != null) viewModel.stopStreaming();
    }
}
