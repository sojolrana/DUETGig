package com.sojolrana.duetgig;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import com.sojolrana.duetgig.adapters.BidAdapter;
import com.sojolrana.duetgig.models.Bid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ProjectDetailActivity extends AppCompatActivity {

    private TextView title, budget, description, posterName, bidsTitle;
    private MaterialButton btnApply;
    private RecyclerView bidsRecyclerView;
    private BidAdapter bidAdapter;
    private List<Bid> bidList;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String projectId, posterId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_detail);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        title = findViewById(R.id.projectDetailTitle);
        budget = findViewById(R.id.projectDetailBudget);
        description = findViewById(R.id.projectDetailDescription);
        posterName = findViewById(R.id.projectDetailPosterName);
        bidsTitle = findViewById(R.id.bidsTitle);
        btnApply = findViewById(R.id.btnApply);
        bidsRecyclerView = findViewById(R.id.bidsRecyclerView);

        // Get data from intent
        projectId = getIntent().getStringExtra("projectId");
        String pTitle = getIntent().getStringExtra("title");
        double pBudget = getIntent().getDoubleExtra("budget", 0.0);
        String pDescription = getIntent().getStringExtra("description");
        String pPoster = getIntent().getStringExtra("posterName");
        posterId = getIntent().getStringExtra("posterId");

        title.setText(pTitle);
        budget.setText("Budget: $" + pBudget);
        description.setText(pDescription);
        posterName.setText(pPoster);

        setupBidding();
    }

    private void setupBidding() {
        String currentUserId = mAuth.getCurrentUser().getUid();

        if (currentUserId.equals(posterId)) {
            // User is the poster: Show bids, hide apply button
            btnApply.setVisibility(View.GONE);
            bidsTitle.setVisibility(View.VISIBLE);
            bidsRecyclerView.setVisibility(View.VISIBLE);
            loadBids();
        } else {
            // User is NOT the poster: Hide bids, show apply button
            btnApply.setVisibility(View.VISIBLE);
            bidsTitle.setVisibility(View.GONE);
            bidsRecyclerView.setVisibility(View.GONE);
            btnApply.setOnClickListener(v -> showAddBidDialog());
        }
    }

    private void loadBids() {
        bidList = new ArrayList<>();
        bidAdapter = new BidAdapter(bidList, new BidAdapter.OnBidActionListener() {
            @Override
            public void onAccept(Bid bid) {
                updateBidStatus(bid, "Accepted");
            }

            @Override
            public void onDecline(Bid bid) {
                updateBidStatus(bid, "Rejected");
            }
        });

        bidsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        bidsRecyclerView.setAdapter(bidAdapter);

        db.collection("projects").document(projectId).collection("bids")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null || value == null) return;
                    bidList.clear();
                    for (QueryDocumentSnapshot doc : value) {
                        bidList.add(doc.toObject(Bid.class));
                    }
                    bidAdapter.notifyDataSetChanged();
                });
    }

    private void updateBidStatus(Bid bid, String status) {
        db.collection("projects").document(projectId).collection("bids")
                .document(bid.getBidId()).update("status", status)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Bid " + status, Toast.LENGTH_SHORT).show();
                    if (status.equals("Accepted")) {
                        startChat(bid.getBidderId(), bid.getBidderName());
                    }
                });
    }

    private void showAddBidDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_bid, null);
        TextInputEditText etAmount = dialogView.findViewById(R.id.etBidAmount);
        TextInputEditText etProposal = dialogView.findViewById(R.id.etBidProposal);

        new AlertDialog.Builder(this)
                .setView(dialogView)
                .setPositiveButton("Submit", (dialog, which) -> {
                    String amountStr = etAmount.getText() != null ? etAmount.getText().toString().trim() : "";
                    String proposal = etProposal.getText() != null ? etProposal.getText().toString().trim() : "";
                    if (!amountStr.isEmpty() && !proposal.isEmpty()) {
                        submitBid(Double.parseDouble(amountStr), proposal);
                    } else {
                        Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void submitBid(double amount, String proposal) {
        String currentUserId = mAuth.getCurrentUser().getUid();
        
        db.collection("users").document(currentUserId).get().addOnSuccessListener(userDoc -> {
            String bidderName = userDoc.getString("name");
            String bidId = UUID.randomUUID().toString();
            Bid bid = new Bid(bidId, projectId, currentUserId, bidderName, amount, proposal, "Pending", Timestamp.now());

            db.collection("projects").document(projectId).collection("bids")
                    .document(bidId).set(bid)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Bid submitted successfully", Toast.LENGTH_SHORT).show();
                    });
        });
    }

    private void startChat(String providerId, String providerName) {
        if (mAuth.getCurrentUser() == null) return;
        String currentUserId = mAuth.getCurrentUser().getUid();

        if (currentUserId.equals(providerId)) {
            Toast.makeText(this, "You cannot apply to your own project", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] ids = {currentUserId, providerId};
        Arrays.sort(ids);
        String chatId = ids[0] + "_" + ids[1];

        Map<String, Object> chat = new HashMap<>();
        chat.put("chatId", chatId);
        chat.put("userIds", Arrays.asList(currentUserId, providerId));
        chat.put("otherUserName", providerName);

        db.collection("chats").document(chatId)
                .set(chat, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    Intent intent = new Intent(this, ChatActivity.class);
                    intent.putExtra("chatId", chatId);
                    intent.putExtra("otherUserName", providerName);
                    startActivity(intent);
                });
    }
}