package com.sojolrana.duetgig;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler(Looper.getMainLooper()).postDelayed(this::checkUserSession, 1500);
    }

    private void checkUserSession() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();
            FirebaseFirestore.getInstance().collection("users").document(userId).get()
                    .addOnSuccessListener(doc -> {
                        if (doc.exists()) {
                            String status = doc.getString("status");
                            Boolean isAdmin = doc.getBoolean("isAdmin");
                            if ("Pending".equals(status) && !Boolean.TRUE.equals(isAdmin)) {
                                mAuth.signOut();
                            } else if ("Blocked".equals(status)) {
                                mAuth.signOut();
                            }
                        }
                        navigateToMain();
                    })
                    .addOnFailureListener(e -> navigateToMain());
        } else {
            navigateToMain();
        }
    }

    private void navigateToMain() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
