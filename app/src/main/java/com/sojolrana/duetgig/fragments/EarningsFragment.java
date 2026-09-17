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
import java.util.Locale;

public class EarningsFragment extends Fragment {

    private TextView totalEarningsText;
    private TextView emptyStateText;
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
        emptyStateText = view.findViewById(R.id.earningsEmptyStateText);
        recyclerView = view.findViewById(R.id.transactionsRecyclerView);

        setupRecyclerView();
        loadEarnings();

        return view;
    }

    private void setupRecyclerView() {
        acceptedBids = new ArrayList<>();
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

        db.collection("users").document(userId).collection("earnings")
                .addSnapshotListener((value, error) -> {
                    if (error != null || value == null || value.isEmpty()) {
                        loadEarningsFromProjects(userId);
                        return;
                    }

                    double total = 0;
                    acceptedBids.clear();
                    for (QueryDocumentSnapshot doc : value) {
                        Bid bid = doc.toObject(Bid.class);
                        if ("Accepted".equals(bid.getStatus())) {
                            acceptedBids.add(bid);
                            total += bid.getAmount();
                        }
                    }
                    totalEarningsText.setText(String.format(Locale.US, "$%.2f", total));
                    adapter.notifyDataSetChanged();
                    updateUIState();
                });
    }

    private void loadEarningsFromProjects(String userId) {
        db.collection("projects").get().addOnSuccessListener(projectsSnapshot -> {
            if (projectsSnapshot == null || projectsSnapshot.isEmpty()) {
                totalEarningsText.setText("$0.00");
                acceptedBids.clear();
                adapter.notifyDataSetChanged();
                updateUIState();
                return;
            }

            acceptedBids.clear();
            final double[] totalSum = {0.0};
            final int[] remaining = {projectsSnapshot.size()};

            for (QueryDocumentSnapshot projectDoc : projectsSnapshot) {
                projectDoc.getReference().collection("bids")
                        .whereEqualTo("bidderId", userId)
                        .get()
                        .addOnSuccessListener(bidsSnapshot -> {
                            for (QueryDocumentSnapshot bidDoc : bidsSnapshot) {
                                Bid bid = bidDoc.toObject(Bid.class);
                                if ("Accepted".equals(bid.getStatus())) {
                                    acceptedBids.add(bid);
                                    totalSum[0] += bid.getAmount();

                                    // Auto-sync to users/{userId}/earnings
                                    db.collection("users").document(userId)
                                            .collection("earnings").document(bid.getBidId())
                                            .set(bid);
                                }
                            }
                            remaining[0]--;
                            if (remaining[0] <= 0) {
                                totalEarningsText.setText(String.format(Locale.US, "$%.2f", totalSum[0]));
                                adapter.notifyDataSetChanged();
                                updateUIState();
                            }
                        })
                        .addOnFailureListener(e -> {
                            remaining[0]--;
                            if (remaining[0] <= 0) {
                                totalEarningsText.setText(String.format(Locale.US, "$%.2f", totalSum[0]));
                                adapter.notifyDataSetChanged();
                                updateUIState();
                            }
                        });
            }
        });
    }

    private void updateUIState() {
        if (acceptedBids.isEmpty()) {
            if (emptyStateText != null) emptyStateText.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            if (emptyStateText != null) emptyStateText.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
}
