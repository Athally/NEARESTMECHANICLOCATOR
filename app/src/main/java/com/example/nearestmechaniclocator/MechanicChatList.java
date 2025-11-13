package com.example.nearestmechaniclocator;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class MechanicChatListFragment extends Fragment {

    private RecyclerView recyclerView;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private FirestoreRecyclerAdapter<ChatUser, ChatUserViewHolder> adapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mechanic_chat_list, container, false);

        recyclerView = view.findViewById(R.id.chatRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        loadChatList();

        return view;
    }

    private void loadChatList() {
        String mechanicId = auth.getCurrentUser().getUid();

        Query query = db.collection("chats")
                .whereEqualTo("mechanicId", mechanicId);

        FirestoreRecyclerOptions<ChatUser> options = new FirestoreRecyclerOptions.Builder<ChatUser>()
                .setQuery(query, ChatUser.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<ChatUser, ChatUserViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ChatUserViewHolder holder, int position, @NonNull ChatUser model) {
                holder.bind(model);

                holder.itemView.setOnClickListener(v -> {
                    DocumentSnapshot snapshot = getSnapshots().getSnapshot(holder.getBindingAdapterPosition());
                    String chatId = snapshot.getId();

                    Intent intent = new Intent(getContext(), ChatActivity.class);
                    intent.putExtra("chatId", chatId);
                    intent.putExtra("otherUserId", model.getDriverId());
                    intent.putExtra("otherUserName", model.getDriverName());
                    startActivity(intent);
                });
            }

            @NonNull
            @Override
            public ChatUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_chat_user, parent, false);
                return new ChatUserViewHolder(view);
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null) adapter.stopListening();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (adapter != null) adapter.startListening();
    }
}
