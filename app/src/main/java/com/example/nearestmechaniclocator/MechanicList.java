package com.example.nearestmechaniclocator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

class MechanicListFragment extends Fragment {

    private MechanicViewModel viewModel;
    private MechanicAdapter adapter;
    private Spinner specializationSpinner;
    private SeekBar radiusSeekBar, ratingSeekBar;
    private TextView radiusText;

    private final double currentLat = 12.9; // Replace with actual location
    private final double currentLng = 77.6;

    private final List<Mechanic> mechanicList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mechanic_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        RecyclerView recyclerView = view.findViewById(R.id.mechanicRecyclerView);
        specializationSpinner = view.findViewById(R.id.specializationSpinner);
        radiusSeekBar = view.findViewById(R.id.radiusSeekBar);
        ratingSeekBar = view.findViewById(R.id.ratingSeekBar);
        radiusText = view.findViewById(R.id.radiusText);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new MechanicAdapter(requireContext(), mechanicList);
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(MechanicViewModel.class);

        setupFilters();

        viewModel.getFilteredMechanics().observe(getViewLifecycleOwner(), mechanics -> {
            mechanicList.clear();
            mechanicList.addAll(mechanics);
            adapter.notifyDataSetChanged();
        });
    }

    private void setupFilters() {
        String[] specializations = {"All", "Engine", "Brakes", "Electrical", "Transmission"};
        specializationSpinner.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, specializations));

        specializationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                applyFilter();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        radiusSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                radiusText.setText(getString(R.string.radius) + progress + "km");
                applyFilter();
            }

            public void onStartTrackingTouch(SeekBar seekBar) {}

            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        ratingSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                applyFilter();
            }

            public void onStartTrackingTouch(SeekBar seekBar) {}

            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void applyFilter() {
        String selectedSpecialization = specializationSpinner.getSelectedItem().toString();
        int selectedRadius = radiusSeekBar.getProgress();
        float selectedRating = ratingSeekBar.getProgress();

        viewModel.filterMechanics(currentLat, currentLng, selectedRadius, selectedSpecialization, selectedRating);
    }
}
