
package com.example.nearestmechaniclocator;
public class ChatFragment extends Fragment {

    private RecyclerView recyclerView;
    private EditText messageInput;
    private ImageButton sendButton;
    private ChatAdapter adapter;
    private List<ChatMessage> messages;
    private DatabaseReference chatRef;
    private String currentUserId;
    private String chatId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        recyclerView = view.findViewById(R.id.chat_recycler);
        messageInput = view.findViewById(R.id.chat_input);
        sendButton = view.findViewById(R.id.chat_send_button);

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        messages = new ArrayList<>();
        adapter = new ChatAdapter(messages, currentUserId);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        if (getArguments() != null) chatId = getArguments().getString("chatId");
        chatRef = FirebaseDatabase.getInstance().getReference("chats").child(chatId).child("messages");

        listenForMessages();

        sendButton.setOnClickListener(v -> {
            String text = messageInput.getText().toString().trim();
            if (!text.isEmpty()) sendMessage(text);
        });

        return view;
    }

    private void listenForMessages() {
        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messages.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ChatMessage msg = ds.getValue(ChatMessage.class);
                    if (msg != null) messages.add(msg);
                }
                adapter.setMessages(messages);
                recyclerView.scrollToPosition(messages.size() - 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void sendMessage(String text) {
        String key = chatRef.push().getKey();
        ChatMessage message = new ChatMessage();
        if (key != null) chatRef.child(key).setValue(message);

        // Create notification for the other user
        DatabaseReference notifRef = FirebaseDatabase.getInstance().getReference("notifications").push();
        Map<String,Object> notif = new HashMap<>();
        notif.put("title", "New message");
        notif.put("message", text);
        notif.put("recipientId", getOtherUserId(chatId)); // implement method to get other UID
        notif.put("chatId", chatId);
        notif.put("timestamp", System.currentTimeMillis());
        notif.put("read", false);
        notifRef.setValue(notif);
    }

    private String getOtherUserId(String chatId) {
        // Example: chat_driver1_mechanic1
        String[] parts = chatId.split("_");
        return parts[1].equals(currentUserId) ? parts[2] : parts[1];
    }
}
