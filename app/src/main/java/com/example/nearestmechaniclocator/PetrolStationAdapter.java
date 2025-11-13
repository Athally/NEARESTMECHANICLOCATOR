package com.example.nearestmechaniclocator;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PetrolStationAdapter extends RecyclerView.Adapter<PetrolStationAdapter.VH> {

    private final Context context;
    private final List<PetrolStation> stations;
    private OnStationClickListener listener;

    public interface OnStationClickListener {
        void onStationClick(PetrolStation station);
    }

    public void setOnStationClickListener(OnStationClickListener listener) {
        this.listener = listener;
    }

    public PetrolStationAdapter(Context context, List<PetrolStation> stations) {
        this.context = context;
        this.stations = stations;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_petrol_station, parent, false);
        return new VH(view);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        PetrolStation station = stations.get(position);
        holder.tvName.setText(station.getName());
        holder.tvAddress.setText(station.getAddress());
        holder.tvDistance.setText(String.format("%.1f km away", station.getDistance()));

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onStationClick(station);
        });
    }

    @Override
    public int getItemCount() {
        return stations.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvName, tvAddress, tvDistance;
        VH(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvStationName);
            tvAddress = itemView.findViewById(R.id.tvStationAddress);
            tvDistance = itemView.findViewById(R.id.tvStationDistance);
        }
    }
}
