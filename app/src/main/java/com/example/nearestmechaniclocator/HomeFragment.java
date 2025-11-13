package com.example.nearestmechaniclocator;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class DriverHomeFragment extends Fragment {

    private TextView tvWelcome;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true); // Enable 3-dot menu
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        tvWelcome = view.findViewById(R.id.tvWelcome);

        // Retrieve username passed from registration/login
        String username = getActivity().getIntent().getStringExtra("username");
        if (username != null) {
            tvWelcome.setText("Welcome " + username);
        }

        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_home, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share_obd:
                Toast.makeText(getContext(), "Sharing OBD data...", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.action_upload_credentials:
                Toast.makeText(getContext(), "Opening credentials uploader...", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.action_open_drawer:
                FrameLayout drawer = getView().findViewById(R.id.drawerContainer);
                if (drawer.getVisibility() == View.GONE) {
                    drawer.setVisibility(View.VISIBLE);
                    Toast.makeText(getContext(), "Drawer Opened", Toast.LENGTH_SHORT).show();
                } else {
                    drawer.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Drawer Closed", Toast.LENGTH_SHORT).show();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
