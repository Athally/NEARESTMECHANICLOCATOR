package com.example.nearestmechaniclocator;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.LayoutInflater;


import com.bumptech.glide.Glide;

import java.util.List;

public class MechanicAdapter extends RecyclerView.Adapter<MechanicAdapter.ViewHolder> {

    private final List<Mechanic> mechanicList;
    private final Context context;

    public MechanicAdapter(Context context, List<Mechanic> list) {
        this.context = context;
        this.mechanicList = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_mechanic, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Mechanic mechanic = mechanicList.get(position);
        holder.name.setText(mechanic.getName());
        holder.specialization.setText(mechanic.getSpecialization());
        holder.distance.setText(String.format("%.1f km away", mechanic.getDistanceFromDriver()));
        Glide.with(context).load(mechanic.getPhotoUrl()).into(holder.image);
        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onMechanicClick(mechanic);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mechanicList.size();
    }

    public void setOnMechanicClickListener(Object mechanicDetails) {

    }
    public interface OnMechanicClickListener {
        void onMechanicClick(Mechanic mechanic);
    }
    private OnMechanicClickListener clickListener;

    public void setOnMechanicClickListener(OnMechanicClickListener listener) {
        this.clickListener = listener;
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, specialization, distance;
        ImageView image;

        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.mechanic_name);
            specialization = itemView.findViewById(R.id.mechanic_specialization);
            distance = itemView.findViewById(R.id.mechanic_distance);
            image = itemView.findViewById(R.id.mechanic_image);
        }
    }
}
