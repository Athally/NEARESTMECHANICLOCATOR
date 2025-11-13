package com.example.nearestmechaniclocator;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class RequestsFragment extends Fragment implements RequestsAdapter.OnRequestActionListener {

    private RecyclerView recycler;
    private ProgressBar progress;
    private TextView empty;
    private RequestsAdapter adapter;
    private DatabaseReference reqRef;
    private ValueEventListener reqListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_requests, container, false);

        recycler = v.findViewById(R.id.recyclerRequests);
        progress = v.findViewById(R.id.progressRequests);
        empty    = v.findViewById(R.id.emptyRequests);

        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RequestsAdapter(this);
        recycler.setAdapter(adapter);

        reqRef = FirebaseDatabase.getInstance().getReference("requests");

        loadRequestsRealtime();

        return v;
    }

    private void loadRequestsRealtime() {
        showLoading(true);
        reqListener = new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Request> list = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Request r = child.getValue(Request.class);
                    if (r != null) {
                        r.setId(child.getKey()); // keep firebase key
                        list.add(r);
                    }
                }
                adapter.submitList(list);
                showLoading(false);
                showEmpty(list.isEmpty());
            }

            @Override public void onCancelled(@NonNull DatabaseError error) {
                showLoading(false);
                Toast.makeText(getContext(), "Failed to load: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
        reqRef.addValueEventListener(reqListener);
    }

    private void showLoading(boolean show) {
        if (progress != null) progress.setVisibility(show ? View.VISIBLE : View.GONE);
        if (recycler != null) recycler.setAlpha(show ? 0.4f : 1f);
    }

    private void showEmpty(boolean show) {
        if (empty != null) empty.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (reqListener != null) reqRef.removeEventListener(reqListener);
    }

    // ===== Adapter callbacks =====
    @Override
    public void onAccept(@NonNull Request request) {
        if (request.getId() == null) return;
        reqRef.child(request.getId()).child("status").setValue("Accepted")
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onReject(@NonNull Request request) {
        if (request.getId() == null) return;
        reqRef.child(request.getId()).child("status").setValue("Rejected")
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onTrack(@NonNull Request request) {
        if (request.getLocation() == null) {
            Toast.makeText(getContext(), "No location provided", Toast.LENGTH_SHORT).show();
            return;
        }
        TrackingBottomSheet.newInstance(
                request.getId(),
                request.getOwnerName(),
                request.getLocation().getLat(),
                request.getLocation().getLng()
        ).show(getChildFragmentManager(), "track_bs");
    }

    @Override
    public void onViewOBD(@NonNull Request request) {
        if (request.getId() == null) {
            Toast.makeText(getContext(), "Invalid request", Toast.LENGTH_SHORT).show();
            return;
        }

        reqRef.child(request.getId()).child("obd").get()
                .addOnSuccessListener(snapshot -> {
                    if (!snapshot.exists()) {
                        Toast.makeText(getContext(), "No OBD data available", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String obdJson = snapshot.getValue(String.class);
                    OBDBottomSheet.newInstance(obdJson)
                            .show(getChildFragmentManager(), "obd_bs");
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to load OBD: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onChat(@NonNull Request request) {
        startChatWithDriver(request);
    }

    private void startChatWithDriver(@NonNull Request request) {
        if (getContext() == null) return;

        String mechanicId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        String driverId = request.getOwnerId();  // ✅ Make sure Request has this
        if (driverId == null) {
            Toast.makeText(getContext(), "Driver info missing", Toast.LENGTH_SHORT).show();
            return;
        }

        // Generate unique chatId
        String chatId = mechanicId.compareTo(driverId) < 0
                ? mechanicId + "_" + driverId
                : driverId + "_" + mechanicId;

        Intent chatIntent = new Intent(getContext(), ChatFragment.class);
        chatIntent.putExtra("chatId", chatId);
        chatIntent.putExtra("otherUserId", driverId);
        chatIntent.putExtra("otherUserName", request.getOwnerName());
        startActivity(chatIntent);
    }
}
