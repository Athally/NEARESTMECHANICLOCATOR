package com.example.nearestmechaniclocator;

import static java.security.AccessController.getContext;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class NotificationFragment extends Fragment {

    private RecyclerView recyclerView;
    private NotificationAdapter adapter;
    private List<NotificationItem> notifications;
    private DatabaseReference notifRef;
    private String currentUserId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        recyclerView = view.findViewById(R.id.notifications_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        notifications = new ArrayList<>();
        adapter = new NotificationAdapter(notifications, currentUserId);
        recyclerView.setAdapter(adapter);

        notifRef = FirebaseDatabase.getInstance().getReference("notifications");
        loadNotifications();

        adapter.setOnItemClickListener(notification -> {
            // Open chat fragment
            Bundle bundle = new Bundle();
            bundle.putString("chatId", notification.getChatId());
            ChatFragment chatFragment = new ChatFragment();
            chatFragment.setArguments(bundle);

            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, chatFragment)
                    .addToBackStack(null)
                    .commit();

            // Mark as read
            notifRef.child(notification.getId()).child("read").setValue(true);
        });

        return view;
    }

    private void loadNotifications() {
        notifRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                notifications.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    NotificationItem n = ds.getValue(NotificationItem.class);
                    if (n != null && n.getRecipientId().equals(currentUserId)) {
                        n.setRead(n.isRead());
                        n.setRead(n.isRead());
                        n.setRead(n.isRead());
                        notifications.add(n);
                        n.setRead(n.isRead());
                        n.setRead(n.isRead());
                        n.setRead(n.isRead());
                        n.setRead(n.isRead());
                        n.setRead(n.isRead());
                    }
                }
                Collections.sort(notifications, (a, b) -> Long.compare(b.getTimestamp(), a.getTimestamp()));
                adapter.setNotifications(notifications);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}
