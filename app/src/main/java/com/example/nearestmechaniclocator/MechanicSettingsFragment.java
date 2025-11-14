package com.example.nearestmechaniclocator;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MechanicSettingsFragment extends Fragment {
    private Switch availabilitySwitch;
    private Switch liveLocationSwitch;
    private Switch themeSwitch;


    private EditText startTimeEditText, endTimeEditText;
    private Button saveAvailabilityButton;

    private SharedPreferences sharedPreferences;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private static final String PREFS_NAME = "MechanicPrefs";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mechanic_settings, container, false);

        // Initialize views
        availabilitySwitch = view.findViewById(R.id.availabilitySwitch);
        liveLocationSwitch = view.findViewById(R.id.liveLocationSwitch);
        themeSwitch = view.findViewById(R.id.themeSwitch);
        startTimeEditText = view.findViewById(R.id.startTimeEditText);
        endTimeEditText = view.findViewById(R.id.endTimeEditText);
        saveAvailabilityButton = view.findViewById(R.id.saveAvailabilityButton);

        sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        loadPreferences();

        // Switch listeners
        availabilitySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            savePreference("auto_accept", isChecked);
            updateFirestore("auto_accept", isChecked);
        });

        liveLocationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            savePreference("live_location", isChecked);
            updateFirestore("live_location", isChecked);
        });

        themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            savePreference("dark_theme", isChecked);
            AppCompatDelegate.setDefaultNightMode(
                    isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
            );
        });

        // Time pickers
        startTimeEditText.setOnClickListener(v -> showTimePicker(startTimeEditText));
        endTimeEditText.setOnClickListener(v -> showTimePicker(endTimeEditText));

        // Save availability times
        saveAvailabilityButton.setOnClickListener(v -> saveAvailabilityTimes());

        return view;
    }

    private void loadPreferences() {
        boolean autoAccept = sharedPreferences.getBoolean("auto_accept", false);
        boolean liveLocation = sharedPreferences.getBoolean("live_location", false);
        boolean darkTheme = sharedPreferences.getBoolean("dark_theme", true);

        String startTime = sharedPreferences.getString("start_time", "");
        String endTime = sharedPreferences.getString("end_time", "");

        availabilitySwitch.setChecked(autoAccept);
        liveLocationSwitch.setChecked(liveLocation);
        themeSwitch.setChecked(darkTheme);

        startTimeEditText.setText(startTime);
        endTimeEditText.setText(endTime);
    }

    private void savePreference(String key, boolean value) {
        sharedPreferences.edit().putBoolean(key, value).apply();
    }

    private void saveStringPreference(String key, String value) {
        sharedPreferences.edit().putString(key, value).apply();
    }

    private void updateFirestore(String key, boolean value) {
        String uid = getCurrentUserId();
        if (uid != null) {
            db.collection("mechanics").document(uid)
                    .update(key, value)
                    .addOnSuccessListener(unused -> Log.d("Settings", "Updated " + key + " in Firestore"))
                    .addOnFailureListener(e -> Log.e("Settings", "Failed to update Firestore", e));
        }
    }

    private void showTimePicker(EditText targetEditText) {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePicker = new TimePickerDialog(getContext(), (view, hourOfDay, minute1) -> {
            String formattedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute1);
            targetEditText.setText(formattedTime);
        }, hour, minute, DateFormat.is24HourFormat(getContext()));

        timePicker.show();
    }

    private void saveAvailabilityTimes() {
        String start = startTimeEditText.getText().toString().trim();
        String end = endTimeEditText.getText().toString().trim();

        if (start.isEmpty() || end.isEmpty()) {
            Toast.makeText(getContext(), "Please enter both start and end times", Toast.LENGTH_SHORT).show();
            return;
        }

        saveStringPreference("start_time", start);
        saveStringPreference("end_time", end);

        String uid = getCurrentUserId();
        if (uid != null) {
            Map<String, Object> times = new HashMap<>();
            times.put("available_start", start);
            times.put("available_end", end);

            db.collection("mechanics").document(uid)
                    .update(times)
                    .addOnSuccessListener(unused -> Toast.makeText(getContext(), "Availability updated", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to save times", Toast.LENGTH_SHORT).show());
        }
    }

    private String getCurrentUserId() {
        return auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;
    }
}
