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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ServiceDetailActivity extends AppCompatActivity {

    private TextView title, price, description, providerName, providerBio;
    private MaterialButton btnHire;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

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

        // Get data from intent
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

        btnHire.setOnClickListener(v -> {
            startChat(providerId, sProvider);
        });
    }

    private void startChat(String providerId, String providerName) {
        if (mAuth.getCurrentUser() == null) return;
        String currentUserId = mAuth.getCurrentUser().getUid();

        if (currentUserId.equals(providerId)) {
            Toast.makeText(this, "You cannot message yourself", Toast.LENGTH_SHORT).show();
            return;
        }

        // Generate a consistent Chat ID for these two users
        String[] ids = {currentUserId, providerId};
        Arrays.sort(ids);
        String chatId = ids[0] + "_" + ids[1];

        Map<String, Object> chat = new HashMap<>();
        chat.put("chatId", chatId);
        chat.put("userIds", Arrays.asList(currentUserId, providerId));
        chat.put("otherUserName", providerName); // Simplified: should ideally be dynamic

        db.collection("chats").document(chatId)
                .set(chat, com.google.firebase.firestore.SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    Intent intent = new Intent(this, ChatActivity.class);
                    intent.putExtra("chatId", chatId);
                    intent.putExtra("otherUserName", providerName);
                    startActivity(intent);
                });
    }
}