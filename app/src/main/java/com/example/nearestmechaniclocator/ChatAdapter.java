package com.example.nearestmechaniclocator;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nearestmechaniclocator.ChatMessage;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private List<ChatMessage> messages;
    private String currentUserId;

    public ChatAdapter(List<ChatMessage> messages, String currentUserId) {
        this.messages = messages;
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_message, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatMessage msg = messages.get(position);
        holder.messageText.setText(msg.getText());
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.messageText.getLayoutParams();
        if (msg.getSenderId().equals(currentUserId)) {
            holder.messageText.setBackgroundResource(R.drawable.bg_message_sent);
            params.gravity = Gravity.END;
        } else {
            holder.messageText.setBackgroundResource(R.drawable.bg_message_received);
            params.gravity = Gravity.START;
        }
        holder.messageText.setLayoutParams(params);
    }

    @Override
    public int getItemCount() { return messages.size(); }

    public class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;
        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.chat_message_text);
        }
    }

    public void setMessages(List<ChatMessage> messages) {
        this.messages = messages;
        notifyDataSetChanged();
    }
}
