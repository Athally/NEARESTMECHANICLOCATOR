package com.example.nearestmechaniclocator;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.Timestamp;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;

public class EarningsFragment extends Fragment {

    private FirebaseFirestore db;
    private String mechanicId = "12345"; // get from logged in user
    private TextView txtToday, txtWeek, txtMonth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_earnings, container, false);

        txtToday = view.findViewById(R.id.txtToday);
        txtWeek = view.findViewById(R.id.txtWeek);
        txtMonth = view.findViewById(R.id.txtMonth);

        db = FirebaseFirestore.getInstance();
        loadEarnings();

        return view;
    }

    private void loadEarnings() {
        db.collection("serviceRequests")
                .whereEqualTo("mechanicId", mechanicId)
                .whereEqualTo("status", "completed")
                .get()
                .addOnSuccessListener(query -> {
                    double todayTotal = 0, weekTotal = 0, monthTotal = 0;

                    Calendar now = Calendar.getInstance();
                    int thisMonth = now.get(Calendar.MONTH);
                    int thisWeek = now.get(Calendar.WEEK_OF_YEAR);
                    int todayDay = now.get(Calendar.DAY_OF_YEAR);

                    for (DocumentSnapshot doc : query) {
                        Double amount = doc.getDouble("amount");
                        Timestamp ts = doc.getTimestamp("dateCompleted");
                        if (amount == null || ts == null) continue;

                        Calendar c = Calendar.getInstance();
                        c.setTime(ts.toDate());

                        if (c.get(Calendar.DAY_OF_YEAR) == todayDay) todayTotal += amount;
                        if (c.get(Calendar.WEEK_OF_YEAR) == thisWeek) weekTotal += amount;
                        if (c.get(Calendar.MONTH) == thisMonth) monthTotal += amount;
                    }

                    txtToday.setText("Today: KSh " + todayTotal);
                    txtWeek.setText("This Week: KSh " + weekTotal);
                    txtMonth.setText("This Month: KSh " + monthTotal);
                });
    }
}
