package com.example.nearestmechaniclocator;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ServiceHistoryAdapter extends RecyclerView.Adapter<ServiceHistoryAdapter.HistoryViewHolder> {

    private Context context;
    private List<ServiceHistory> historyList;

    public ServiceHistoryAdapter(Context context, List<ServiceHistory> historyList) {
        this.context = context;
        this.historyList = historyList;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_service_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        ServiceHistory history = historyList.get(position);

        holder.tvMechanicName.setText("Mechanic: " + history.getMechanicName());
        holder.tvCarMake.setText(history.getCarMake());
        holder.tvRequestType.setText("Service: " + history.getRequestType());
        holder.tvIssue.setText("Resolved: " + history.getIssue());
        holder.tvLocation.setText("Location: " + history.getLocation());
        holder.tvStatus.setText("Status: " + history.getStatus());

        // Format timestamp → readable date
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
        String dateStr = sdf.format(new Date(history.getTimestamp()));
        holder.tvDate.setText("Completed on: " + dateStr);
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvMechanicName, tvCarMake, tvRequestType, tvIssue, tvDate, tvLocation, tvStatus;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMechanicName = itemView.findViewById(R.id.tvMechanicName);
            tvCarMake = itemView.findViewById(R.id.tvCarMake);
            tvRequestType = itemView.findViewById(R.id.tvRequestType);
            tvIssue = itemView.findViewById(R.id.tvIssue);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
}

