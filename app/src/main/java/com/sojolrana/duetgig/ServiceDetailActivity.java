package com.sojolrana.duetgig;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RatingBar;
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
import com.sojolrana.duetgig.adapters.ReviewAdapter;
import com.sojolrana.duetgig.models.Review;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ServiceDetailActivity extends AppCompatActivity {

    private TextView title, price, description, providerName, providerBio;
    private MaterialButton btnHire, btnWriteReview;
    private RecyclerView reviewsRecyclerView;
    private ReviewAdapter reviewAdapter;
    private List<Review> reviewList;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String serviceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_detail);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        title = findViewById(R.id.detailTitle);
        price = findViewById(R.id.detailPrice);
        description = findViewById(R.id.detailDescription);
        providerName = findViewById(R.id.detailProviderName);
        providerBio = findViewById(R.id.detailProviderBio);
        btnHire = findViewById(R.id.btnHire);
        btnWriteReview = findViewById(R.id.btnWriteReview);
        reviewsRecyclerView = findViewById(R.id.reviewsRecyclerView);

        // Get data from intent
        serviceId = getIntent().getStringExtra("serviceId");
        String sTitle = getIntent().getStringExtra("title");
        double sPrice = getIntent().getDoubleExtra("price", 0.0);
        String sDescription = getIntent().getStringExtra("description");
        String sProvider = getIntent().getStringExtra("provider");
        String sBio = getIntent().getStringExtra("bio");
        String providerId = getIntent().getStringExtra("providerId");

        title.setText(sTitle);
        price.setText("$" + sPrice);
        description.setText(sDescription);
        providerName.setText(sProvider);
        providerBio.setText(sBio);

        setupReviews();
        loadReviews();

        btnHire.setOnClickListener(v -> {
            startChat(providerId, sProvider);
        });

        btnWriteReview.setOnClickListener(v -> showAddReviewDialog());
        
        // Hide review button if current user is the provider
        if (mAuth.getCurrentUser() != null && mAuth.getCurrentUser().getUid().equals(providerId)) {
            btnWriteReview.setVisibility(View.GONE);
        }
    }

    private void setupReviews() {
        reviewList = new ArrayList<>();
        reviewAdapter = new ReviewAdapter(reviewList);
        reviewsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        reviewsRecyclerView.setAdapter(reviewAdapter);
    }

    private void loadReviews() {
        db.collection("services").document(serviceId).collection("reviews")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null || value == null) return;
                    reviewList.clear();
                    for (QueryDocumentSnapshot doc : value) {
                        reviewList.add(doc.toObject(Review.class));
                    }
                    reviewAdapter.notifyDataSetChanged();
                });
    }

    private void showAddReviewDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_review, null);
        RatingBar ratingBar = dialogView.findViewById(R.id.dialogRatingBar);
        TextInputEditText etComment = dialogView.findViewById(R.id.etReviewComment);

        new AlertDialog.Builder(this)
                .setView(dialogView)
                .setPositiveButton("Submit", (dialog, which) -> {
                    float rating = ratingBar.getRating();
                    String comment = etComment.getText() != null ? etComment.getText().toString().trim() : "";
                    if (rating > 0) {
                        submitReview(rating, comment);
                    } else {
                        Toast.makeText(this, "Please select a rating", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void submitReview(float rating, String comment) {
        if (mAuth.getCurrentUser() == null) return;
        String userId = mAuth.getCurrentUser().getUid();

        db.collection("users").document(userId).get().addOnSuccessListener(userDoc -> {
            String reviewerName = userDoc.getString("name");
            String reviewId = UUID.randomUUID().toString();
            Review review = new Review(reviewId, serviceId, reviewerName, rating, comment, Timestamp.now());

            db.collection("services").document(serviceId).collection("reviews")
                    .document(reviewId).set(review)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Review submitted", Toast.LENGTH_SHORT).show();
                    });
        });
    }

    private void startChat(String providerId, String providerName) {
        if (mAuth.getCurrentUser() == null) return;
        String currentUserId = mAuth.getCurrentUser().getUid();

        if (currentUserId.equals(providerId)) {
            Toast.makeText(this, "You cannot message yourself", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("users").document(currentUserId).get().addOnSuccessListener(userDoc -> {
            String currentUserName = userDoc.getString("name");
            if (currentUserName == null) currentUserName = "User";

            // Generate a consistent Chat ID for these two users
            String[] ids = {currentUserId, providerId};
            Arrays.sort(ids);
            String chatId = ids[0] + "_" + ids[1];

            Map<String, String> userNames = new HashMap<>();
            userNames.put(currentUserId, currentUserName);
            userNames.put(providerId, providerName);

            Map<String, Object> chat = new HashMap<>();
            chat.put("chatId", chatId);
            chat.put("userIds", Arrays.asList(currentUserId, providerId));
            chat.put("userNames", userNames);
            chat.put("lastMessage", "Conversation started");
            chat.put("lastTimestamp", com.google.firebase.Timestamp.now());

            db.collection("chats").document(chatId)
                    .set(chat, SetOptions.merge())
                    .addOnSuccessListener(aVoid -> {
                        Intent intent = new Intent(this, ChatActivity.class);
                        intent.putExtra("chatId", chatId);
                        intent.putExtra("otherUserName", providerName);
                        startActivity(intent);
                    });
        });
    }
}