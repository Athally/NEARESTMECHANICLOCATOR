package com.example.nearestmechaniclocator;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ServiceHistoryFragment extends Fragment {

    private RecyclerView recyclerHistory;
    private ProgressBar progressHistory;
    private TextView emptyHistory;

    private ServiceHistoryAdapter adapter;
    private List<ServiceHistory> historyList;
    private DatabaseReference requestsRef;
    private String currentUserId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_service_history, container, false);

        recyclerHistory = view.findViewById(R.id.recyclerHistory);
        progressHistory = view.findViewById(R.id.progressHistory);
        emptyHistory = view.findViewById(R.id.emptyHistory);

        recyclerHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        historyList = new ArrayList<>();
        adapter = new ServiceHistoryAdapter(getContext(), historyList);
        recyclerHistory.setAdapter(adapter);

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        requestsRef = FirebaseDatabase.getInstance().getReference("requests");

        loadServiceHistory();

        return view;
    }

    private void loadServiceHistory() {
        progressHistory.setVisibility(View.VISIBLE);
        requestsRef.orderByChild("ownerId").equalTo(currentUserId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        historyList.clear();
                        for (DataSnapshot reqSnap : snapshot.getChildren()) {
                            String status = reqSnap.child("status").getValue(String.class);
                            if (status != null && status.equals("Completed")) {
                                String mechanicName = reqSnap.child("mechanicName").getValue(String.class);
                                String carMake = reqSnap.child("carMake").getValue(String.class);
                                String requestType = reqSnap.child("requestType").getValue(String.class);
                                String issue = reqSnap.child("issue").getValue(String.class);
                                Long timestamp = reqSnap.child("timestamp").getValue(Long.class);
                                String location = reqSnap.child("location/address").getValue(String.class);

                                ServiceHistory history = new ServiceHistory(
                                        mechanicName != null ? mechanicName : "Unknown Mechanic",
                                        carMake != null ? carMake : "Unknown Car",
                                        requestType != null ? requestType : "Unknown",
                                        issue != null ? issue : "N/A",
                                        timestamp != null ? timestamp : 0L,
                                        location != null ? location : "N/A",
                                        status
                                );
                                historyList.add(history);
                            }
                        }
                        progressHistory.setVisibility(View.GONE);

                        if (historyList.isEmpty()) {
                            emptyHistory.setVisibility(View.VISIBLE);
                        } else {
                            emptyHistory.setVisibility(View.GONE);
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressHistory.setVisibility(View.GONE);
                        emptyHistory.setText("Failed to load history.");
                        emptyHistory.setVisibility(View.VISIBLE);
                    }
                });
    }
}
