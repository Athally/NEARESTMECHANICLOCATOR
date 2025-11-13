package com.example.nearestmechaniclocator;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class PetrolStationsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private RecyclerView recyclerView;
    private PetrolStationAdapter adapter;
    private List<PetrolStation> stationList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_petrol_stations);

        recyclerView = findViewById(R.id.recyclerStations);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new PetrolStationAdapter(this, stationList);
        recyclerView.setAdapter(adapter);

        adapter.setOnStationClickListener(station -> {
            Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(station.getName() + " " + station.getAddress()));
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        });

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapContainerPetrol);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        loadDummyStations();
    }

    private void loadDummyStations() {
        stationList.clear();
        stationList.add(new PetrolStation("Shell Station", "Highway Road, Nairobi", -1.286389, 36.817223, 1.2));
        stationList.add(new PetrolStation("Total Energies", "Westlands, Nairobi", -1.264, 36.804, 2.4));
        stationList.add(new PetrolStation("Kobil", "Ngong Road, Nairobi", -1.3, 36.8, 3.1));

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
            return;
        }

        mMap.setMyLocationEnabled(true);

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                LatLng userLoc = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLoc, 14f));
                mMap.addMarker(new MarkerOptions().position(userLoc).title("You are here"));
            }
        });

        // Add petrol station markers
        for (PetrolStation station : stationList) {
            LatLng pos = new LatLng(station.getLat(), station.getLng());
            mMap.addMarker(new MarkerOptions().position(pos).title(station.getName()));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            onMapReady(mMap);
        } else {
            Toast.makeText(this, "Location permission is required", Toast.LENGTH_SHORT).show();
        }
    }
}
