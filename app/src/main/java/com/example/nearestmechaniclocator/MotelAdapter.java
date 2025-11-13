package com.example.nearestmechaniclocator;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MotelAdapter extends RecyclerView.Adapter<MotelAdapter.MotelViewHolder> {

    public interface OnMotelClickListener {
        void onMotelClick(Motel motel);
    }

    private Context context;
    private List<Motel> motelList;
    private OnMotelClickListener listener;

    public MotelAdapter(Context context, List<Motel> motelList) {
        this.context = context;
        this.motelList = motelList;
    }

    public void setOnMotelClickListener(OnMotelClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public MotelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_motel, parent, false);
        return new MotelViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MotelViewHolder holder, int position) {
        Motel motel = motelList.get(position);

        holder.tvName.setText(motel.getName());
        holder.tvAddress.setText(motel.getAddress());
        holder.tvDistance.setText(String.format("%.2f km away", motel.getDistance()));

        // Placeholder image (can integrate Glide/Picasso later for real motel images)
        holder.ivIcon.setImageResource(R.drawable.ic_motel);

        holder.cardView.setOnClickListener(v -> {
            if (listener != null) listener.onMotelClick(motel);
        });
    }

    @Override
    public int getItemCount() {
        return motelList.size();
    }

    static class MotelViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvAddress, tvDistance;
        ImageView ivIcon;
        CardView cardView;

        MotelViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvMotelName);
            tvAddress = itemView.findViewById(R.id.tvMotelAddress);
            tvDistance = itemView.findViewById(R.id.tvMotelDistance);
            ivIcon = itemView.findViewById(R.id.ivMotelIcon);
            cardView = itemView.findViewById(R.id.motelCard);
        }
    }
}
