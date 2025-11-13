package com.example.nearestmechaniclocator;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SpareShopAdapter extends RecyclerView.Adapter<SpareShopAdapter.VH> {

    private final Context context;
    private final List<SpareShop> shops;
    private OnShopClickListener listener;

    public interface OnShopClickListener {
        void onShopClick(SpareShop shop);
    }

    public void setOnShopClickListener(OnShopClickListener listener) {
        this.listener = listener;
    }

    public SpareShopAdapter(Context context, List<SpareShop> shops) {
        this.context = context;
        this.shops = shops;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_spare_shop, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        SpareShop shop = shops.get(position);
        holder.tvName.setText(shop.getName());
        holder.tvAddress.setText(shop.getAddress());
        holder.tvDistance.setText(String.format("%.1f km away", shop.getDistance()));

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onShopClick(shop);
        });
    }

    @Override
    public int getItemCount() {
        return shops.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvName, tvAddress, tvDistance;
        VH(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvShopName);
            tvAddress = itemView.findViewById(R.id.tvShopAddress);
            tvDistance = itemView.findViewById(R.id.tvShopDistance);
        }
    }
}
