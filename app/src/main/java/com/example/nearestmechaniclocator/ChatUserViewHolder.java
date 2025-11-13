package com.example.nearestmechaniclocator;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ChatUserViewHolder extends RecyclerView.ViewHolder {

    private final TextView nameTextView;

    public ChatUserViewHolder(@NonNull View itemView) {
        super(itemView);
        nameTextView = itemView.findViewById(R.id.chatUserName);
    }

    public void bind(ChatUser chatUser) {
        nameTextView.setText(chatUser.getDriverName());
    }
}
