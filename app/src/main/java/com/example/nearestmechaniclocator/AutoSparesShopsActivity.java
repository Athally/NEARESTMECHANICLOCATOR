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

  class AutoSpareShopsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private RecyclerView recyclerView;
    private SpareShopAdapter adapter;
    private List<SpareShop> shopList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_spares_shops);

        recyclerView = findViewById(R.id.recyclerShops);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new SpareShopAdapter(this, shopList);
        recyclerView.setAdapter(adapter);

        adapter.setOnShopClickListener(shop -> {
            Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(shop.getName() + " " + shop.getAddress()));
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        });

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapContainerSpare);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        loadDummyShops();
    }

    private void loadDummyShops() {
        shopList.clear();
        shopList.add(new SpareShop("AutoSpare Hub", "Industrial Area, Nairobi", -1.31, 36.82, 0.8));
        shopList.add(new SpareShop("CarParts Express", "Ngara, Nairobi", -1.28, 36.83, 1.5));
        shopList.add(new SpareShop("Spare World", "Thika Road, Nairobi", -1.24, 36.88, 4.2));

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 200);
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

        // Add spare shop markers
        for (SpareShop shop : shopList) {
            LatLng pos = new LatLng(shop.getLat(), shop.getLng());
            mMap.addMarker(new MarkerOptions().position(pos).title(shop.getName()));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 200 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            onMapReady(mMap);
        } else {
            Toast.makeText(this, "Location permission is required", Toast.LENGTH_SHORT).show();
        }
    }
}
