package com.example.nearestmechaniclocator;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DriverDashboardActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_dashboard);

        bottomNavigationView = findViewById(R.id.driverBottomNav);

        // Load default fragment
        loadFragment(new DriverHomeFragment());

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                fragment = new DriverHomeFragment();
            } else if (id == R.id.nav_service_history) {
                fragment = new DriverServicesFragment();
            } else if (id == R.id.nav_history) {
                fragment = new DriverHistoryFragment();
            } else if (id == R.id.nav_settings) {
                fragment = new DriverSettingsFragment();
            }

            if (fragment != null) {
                loadFragment(fragment);
                return true;
            }

            return false;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.driver_fragment_container, fragment)
                .commit();
    }
}
