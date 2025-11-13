package com.example.nearestmechaniclocator;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.Map;

public class DiagnosticsFragment extends Fragment {

    private OBDViewModel viewModel;
    private TextView tvSpeed, tvTemp;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_diagnostics, container, false);

        tvSpeed = view.findViewById(R.id.tvSpeed);
        tvTemp = view.findViewById(R.id.tvTemp);

        viewModel = new ViewModelProvider(requireActivity()).get(OBDViewModel.class);

        viewModel.getObdLiveData().observe(getViewLifecycleOwner(), this::updateUI);

        return view;
    }

    private void updateUI(Map<String, String> data) {
        if (data == null) return;

        tvSpeed.setText("Speed: " + data.getOrDefault("Speed", "N/A"));
        tvTemp.setText("Ambient Temp: " + data.getOrDefault("AmbientTemp", "N/A"));
    }
}
