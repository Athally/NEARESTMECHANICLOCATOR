package com.example.nearestmechaniclocator;

import android.os.Bundle;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class DriverDashboardActivity extends AppCompatActivity {

    private ImageButton btnHome, btnServiceHistory, btnHistory, btnSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_dashboard);

        btnHome = findViewById(R.id.btnHome);
        btnServiceHistory = findViewById(R.id.btnServiceHistory);
        btnHistory = findViewById(R.id.btnHistory);
        btnSettings = findViewById(R.id.btnSettings);

        // Load default fragment
        loadFragment(new DriverHomeFragment());

        // Sidebar click listeners
        btnHome.setOnClickListener(v -> loadFragment(new DriverHomeFragment()));
        btnServiceHistory.setOnClickListener(v -> loadFragment(new DriverServicesFragment()));
        btnHistory.setOnClickListener(v -> loadFragment(new HistoryFragment()));
        btnSettings.setOnClickListener(v -> loadFragment(new DriverSettingsFragment()));
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.driver_fragment_container, fragment)
                .commit();
    }
}
