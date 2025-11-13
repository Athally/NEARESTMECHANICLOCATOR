package com.example.nearestmechaniclocator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private List<NotificationItem> notifications;
    private String currentUserId;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(NotificationItem notification);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public NotificationAdapter(List<NotificationItem> notifications, String currentUserId) {
        this.notifications = notifications;
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        NotificationItem n = notifications.get(position);
        holder.title.setText(n.getTitle());
        holder.message.setText(n.getMessage());
        holder.itemView.setAlpha(n.isRead() ? 0.5f : 1f);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null && n.getRecipientId().equals(currentUserId)) {
                listener.onItemClick(n);
            }
        });
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView title, message;
        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.notification_title);
            message = itemView.findViewById(R.id.notification_message);
        }
    }

    public void setNotifications(List<NotificationItem> notifications) {
        this.notifications = notifications;
        notifyDataSetChanged();
    }
}
