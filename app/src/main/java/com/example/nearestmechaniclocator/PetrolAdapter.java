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

public class PetrolStationAdapter extends RecyclerView.Adapter<PetrolStationAdapter.StationViewHolder> {

    public interface OnStationClickListener {
        void onStationClick(PetrolStation station);
    }

    private final Context context;
    private List<PetrolStation> stationList;
    private OnStationClickListener listener;

    public PetrolStationAdapter(Context context, List<PetrolStation> stationList) {
        this.context = context;
        this.stationList = stationList;
    }

    public void setOnStationClickListener(OnStationClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public StationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_petrol_station, parent, false);
        return new StationViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull StationViewHolder holder, int position) {
        PetrolStation station = stationList.get(position);

        holder.tvName.setText(station.getName());
        holder.tvAddress.setText(station.getAddress());
        holder.tvDistance.setText(String.format("%.2f km away", station.getDistance()));
        holder.ivIcon.setImageResource(R.drawable.ic_petrol);

        holder.cardView.setOnClickListener(v -> {
            if (listener != null) listener.onStationClick(station);
        });
    }

    @Override
    public int getItemCount() {
        return stationList.size();
    }

    static class StationViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvAddress, tvDistance;
        ImageView ivIcon;
        CardView cardView;

        StationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvStationName);
            tvAddress = itemView.findViewById(R.id.tvStationAddress);
            tvDistance = itemView.findViewById(R.id.tvStationDistance);
            ivIcon = itemView.findViewById(R.id.ivStationIcon);
            cardView = itemView.findViewById(R.id.stationCard);
        }
    }
}
