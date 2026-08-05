package com.sojolrana.duetgig.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.sojolrana.duetgig.R;
import com.sojolrana.duetgig.adapters.BidAdapter;
import com.sojolrana.duetgig.models.Bid;

import java.util.ArrayList;
import java.util.List;

public class EarningsFragment extends Fragment {

    private TextView totalEarningsText;
    private RecyclerView recyclerView;
    private BidAdapter adapter;
    private List<Bid> acceptedBids;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_earnings, container, false);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        totalEarningsText = view.findViewById(R.id.totalEarnings);
        recyclerView = view.findViewById(R.id.transactionsRecyclerView);

        setupRecyclerView();
        loadEarnings();

        return view;
    }

    private void setupRecyclerView() {
        acceptedBids = new ArrayList<>();
        // Reusing BidAdapter for transactions list (simplified)
        adapter = new BidAdapter(acceptedBids, new BidAdapter.OnBidActionListener() {
            @Override
            public void onAccept(Bid bid) {}
            @Override
            public void onDecline(Bid bid) {}
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void loadEarnings() {
        if (mAuth.getCurrentUser() == null) return;
        String userId = mAuth.getCurrentUser().getUid();

        db.collectionGroup("bids")
                .whereEqualTo("bidderId", userId)
                .whereEqualTo("status", "Accepted")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        double total = 0;
                        acceptedBids.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Bid bid = document.toObject(Bid.class);
                            acceptedBids.add(bid);
                            total += bid.getAmount();
                        }
                        totalEarningsText.setText("$" + String.format("%.2f", total));
                        adapter.notifyDataSetChanged();
                    }
                });
    }
}