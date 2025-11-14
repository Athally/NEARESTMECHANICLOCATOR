package com.example.nearestmechaniclocator;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.annotations.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;

public class PlacesMapFragment extends Fragment implements OnMapReadyCallback {

    private static final String ARG_PLACE_TYPE = "arg_place_type";

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private String placeType;
    private LatLng currentLatLng;

    public PlacesMapFragment() {
        // Required empty constructor
    }

    public static PlacesMapFragment newInstance(String placeType) {
        PlacesMapFragment fragment = new PlacesMapFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PLACE_TYPE, placeType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            placeType = getArguments().getString(ARG_PLACE_TYPE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_places_map, container, false);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map_fragment);
        if (mapFragment != null) mapFragment.getMapAsync(this);

        return view;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 14f));
                mMap.addMarker(new MarkerOptions().position(currentLatLng).title("You are here"));
                findNearbyPlaces();
            }
        });
    }

    private void findNearbyPlaces() {
        String apiKey = "YOUR_API_KEY"; // replace with your key
        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json" +
                "?location=" + currentLatLng.latitude + "," + currentLatLng.longitude +
                "&radius=5000" +
                "&type=" + placeType +
                "&key=" + apiKey;

        OkHttpClient client = new OkHttpClient();
        okhttp3.Request request = new okhttp3.Request.Builder().url(url).build();
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull okhttp3.Call call, @NonNull okhttp3.Response response) throws IOException {
                String res = response.body().string();
                try {
                    JSONObject json = new JSONObject(res);
                    JSONArray results = json.getJSONArray("results");
                    for (int i = 0; i < results.length(); i++) {
                        JSONObject place = results.getJSONObject(i);
                        JSONObject loc = place.getJSONObject("geometry").getJSONObject("location");
                        double lat = loc.getDouble("lat");
                        double lng = loc.getDouble("lng");
                        String name = place.getString("name");

                        LatLng placeLatLng = new LatLng(lat, lng);
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() ->
                                    mMap.addMarker(new MarkerOptions().position(placeLatLng).title(name))
                            );
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
