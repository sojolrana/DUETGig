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

public class ProjectDetailActivity extends AppCompatActivity {

    private TextView title, budget, description, posterName;
    private MaterialButton btnApply;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

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
        btnApply = findViewById(R.id.btnApply);

        // Get data from intent
        String pTitle = getIntent().getStringExtra("title");
        double pBudget = getIntent().getDoubleExtra("budget", 0.0);
        String pDescription = getIntent().getStringExtra("description");
        String pPoster = getIntent().getStringExtra("posterName");
        String posterId = getIntent().getStringExtra("posterId");

        title.setText(pTitle);
        budget.setText("Budget: $" + pBudget);
        description.setText(pDescription);
        posterName.setText(pPoster);

        btnApply.setOnClickListener(v -> startChat(posterId, pPoster));
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