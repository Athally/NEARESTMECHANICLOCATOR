package com.example.nearestmechaniclocator;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ChatDetailFragment extends Fragment {

    private static final String ARG_NAME = "name";
    private static final String ARG_LAST_MESSAGE = "lastMessage";

    private String chatName;
    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;
    private List<MessageItem> messageList;
    private EditText inputMessage;
    private ImageButton btnSend;

    public static ChatDetailFragment newInstance(String name, String lastMessage) {
        ChatDetailFragment fragment = new ChatDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_NAME, name);
        args.putString(ARG_LAST_MESSAGE, lastMessage);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            chatName = getArguments().getString(ARG_NAME);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_detail, container, false);

        recyclerView = view.findViewById(R.id.recyclerMessages);
        inputMessage = view.findViewById(R.id.inputMessage);
        btnSend = view.findViewById(R.id.btnSend);

        messageList = new ArrayList<>();
        messageList.add(new MessageItem(chatName, "Hello, I need help!"));
        messageList.add(new MessageItem("Me", "Sure, what seems to be the issue?"));

        messageAdapter = new MessageAdapter(messageList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(messageAdapter);

        btnSend.setOnClickListener(v -> {
            String text = inputMessage.getText().toString().trim();
            if (!text.isEmpty()) {
                messageList.add(new MessageItem("Me", text));
                messageAdapter.notifyItemInserted(messageList.size() - 1);
                recyclerView.scrollToPosition(messageList.size() - 1);
                inputMessage.setText("");
            }
        });

        return view;
    }
}

