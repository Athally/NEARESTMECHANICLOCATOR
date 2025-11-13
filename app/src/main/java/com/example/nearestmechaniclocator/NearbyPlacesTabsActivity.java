package com.example.nearestmechaniclocator;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayout;

public class NearbyPlacesTabsActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private Fragment currentFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_places_tabs);

        tabLayout = findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Motels"));
        tabLayout.addTab(tabLayout.newTab().setText("Auto Spares"));
        tabLayout.addTab(tabLayout.newTab().setText("Petrol Stations"));

        loadMapFragment("lodging");

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0: loadMapFragment("lodging"); break;
                    case 1: loadMapFragment("car_repair"); break;
                    case 2: loadMapFragment("gas_station"); break;
                }
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void loadMapFragment(String placeType) {
        currentFragment = NearbyMapFragment.newInstance(placeType);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.map_container, currentFragment)
                .commit();
    }
}
