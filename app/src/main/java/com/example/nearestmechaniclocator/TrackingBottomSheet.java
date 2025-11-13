package com.example.nearestmechaniclocator;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TrackingBottomSheet extends BottomSheetDialogFragment implements OnMapReadyCallback {

    private static final String ARG_REQ_ID = "req_id";
    private static final String ARG_NAME   = "name";
    private static final String ARG_LAT    = "lat";
    private static final String ARG_LNG    = "lng";

    public static TrackingBottomSheet newInstance(String requestId, String ownerName, double lat, double lng) {
        Bundle b = new Bundle();
        b.putString(ARG_REQ_ID, requestId);
        b.putString(ARG_NAME, ownerName);
        b.putDouble(ARG_LAT, lat);
        b.putDouble(ARG_LNG, lng);
        TrackingBottomSheet f = new TrackingBottomSheet();
        f.setArguments(b);
        return f;
    }

    private GoogleMap map;
    private Marker marker;
    private DatabaseReference locRef;
    private ValueEventListener locListener;

    @Nullable
    @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                                       @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_sheet_mechanic_tracking, container, false);

        SupportMapFragment mapFragment = new SupportMapFragment();
        getChildFragmentManager().beginTransaction()
                .replace(R.id.mapContainer, mapFragment)
                .commit();
        mapFragment.getMapAsync(this);

        return v;
    }

    @Override public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;

        String name = getArguments() != null ? getArguments().getString(ARG_NAME) : "Client";
        double lat = getArguments() != null ? getArguments().getDouble(ARG_LAT) : 0;
        double lng = getArguments() != null ? getArguments().getDouble(ARG_LNG) : 0;
        LatLng pos = new LatLng(lat, lng);

        marker = map.addMarker(new MarkerOptions().position(pos).title(name));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 15f));

        // OPTIONAL: live updates if the owner's app writes new lat/lng to /requests/{id}/location
        String reqId = getArguments() != null ? getArguments().getString(ARG_REQ_ID) : null;
        if (reqId != null) {
            locRef = FirebaseDatabase.getInstance()
                    .getReference("requests").child(reqId).child("location");
            locListener = new ValueEventListener() {
                @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Double nLat = snapshot.child("lat").getValue(Double.class);
                    Double nLng = snapshot.child("lng").getValue(Double.class);
                    if (nLat != null && nLng != null && marker != null && map != null) {
                        LatLng np = new LatLng(nLat, nLng);
                        marker.setPosition(np);
                        map.animateCamera(CameraUpdateFactory.newLatLng(np));
                    }
                }
                @Override public void onCancelled(@NonNull DatabaseError error) {}
            };
            locRef.addValueEventListener(locListener);
        }
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        if (locRef != null && locListener != null) {
            locRef.removeEventListener(locListener);
        }
    }
}
