package com.example.nearestmechaniclocator;

import static java.security.AccessController.getContext;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Objects;

class RequestsAdapter extends ListAdapter<Request, RequestsAdapter.RequestVH> {

    interface OnRequestActionListener {
        void onAccept(@NonNull Request request);
        void onReject(@NonNull Request request);
        void onTrack(@NonNull Request request);
        void onViewOBD(@NonNull Request request);
        void onChat(@NonNull Request request);   // ✅ Added chat
    }

    private final OnRequestActionListener listener;

    public RequestsAdapter(OnRequestActionListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    @NonNull
    @Override
    public RequestVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_request, parent, false);
        return new RequestVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestVH holder, int position) {
        Request r = getItem(position);
        holder.bind(r, listener);
    }

    static class RequestVH extends RecyclerView.ViewHolder {
        TextView txtOwner, txtStatus;
        Button btnAccept, btnReject, btnTrack, btnOBD, btnChat;

        public RequestVH(@NonNull View itemView) {
            super(itemView);
            txtOwner = itemView.findViewById(R.id.tvOwnerName);
            txtStatus = itemView.findViewById(R.id.tvCarMake);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnReject = itemView.findViewById(R.id.btnReject);
            btnTrack  = itemView.findViewById(R.id.btnTrack);
            btnOBD    = itemView.findViewById(R.id.btnOBD);
            btnChat   = itemView.findViewById(R.id.btnChat);
        }

        void bind(Request r, OnRequestActionListener listener) {
            txtOwner.setText(r.getOwnerName());
            txtStatus.setText(r.getStatus());

            btnAccept.setOnClickListener(v -> listener.onAccept(r));
            btnReject.setOnClickListener(v -> listener.onReject(r));
            btnTrack.setOnClickListener(v -> listener.onTrack(r));
            btnOBD.setOnClickListener(v -> listener.onViewOBD(r));
            btnChat.setOnClickListener(v -> listener.onChat(r));
        }
    }

    private static final DiffUtil.ItemCallback<Request> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<>() {
                @Override
                public boolean areItemsTheSame(@NonNull Request oldItem, @NonNull Request newItem) {
                    return Objects.equals(oldItem.getId(), newItem.getId());
                }

                @Override
                public boolean areContentsTheSame(@NonNull Request oldItem, @NonNull Request newItem) {
                    return Objects.equals(oldItem, newItem);
                }



            };
}
