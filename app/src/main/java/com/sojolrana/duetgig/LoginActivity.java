package com.sojolrana.duetgig;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout emailLayout, passwordLayout;
    private TextInputEditText emailEditText, passwordEditText;
    private MaterialButton btnLogin, btnSignUp;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        emailLayout = findViewById(R.id.emailLayout);
        passwordLayout = findViewById(R.id.passwordLayout);
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        btnLogin = findViewById(R.id.btnLogin);
        btnSignUp = findViewById(R.id.btnSignUp);

        btnLogin.setOnClickListener(v -> {
            String email = emailEditText.getText() != null ? emailEditText.getText().toString().trim() : "";
            String password = passwordEditText.getText() != null ? passwordEditText.getText().toString().trim() : "";

            if (validateInputs(email, password)) {
                loginUser(email, password);
            }
        });

        btnSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
        });
    }

    private boolean validateInputs(String email, String password) {
        boolean isValid = true;

        if (email.isEmpty()) {
            emailLayout.setError("Email is required");
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailLayout.setError("Enter a valid email address");
            isValid = false;
        } else {
            emailLayout.setError(null);
        }

        if (password.isEmpty()) {
            passwordLayout.setError("Password is required");
            isValid = false;
        } else if (password.length() < 6) {
            passwordLayout.setError("Password must be at least 6 characters");
            isValid = false;
        } else {
            passwordLayout.setError(null);
        }

        return isValid;
    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful() && mAuth.getCurrentUser() != null) {
                        String userId = mAuth.getCurrentUser().getUid();
                        FirebaseFirestore.getInstance().collection("users").document(userId).get()
                                .addOnSuccessListener(doc -> {
                                    if (doc.exists()) {
                                        String status = doc.getString("status");
                                        Boolean isAdmin = doc.getBoolean("isAdmin");
                                        if (status != null && "Pending".equals(status) && !Boolean.TRUE.equals(isAdmin)) {
                                            mAuth.signOut();
                                            Toast.makeText(LoginActivity.this, "Your account is pending admin approval.", Toast.LENGTH_LONG).show();
                                            return;
                                        }
                                        if (status != null && "Blocked".equals(status)) {
                                            mAuth.signOut();
                                            Toast.makeText(LoginActivity.this, "Your account has been blocked by admin.", Toast.LENGTH_LONG).show();
                                            return;
                                        }
                                    }
                                    saveFcmToken();
                                    Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(LoginActivity.this, "Error checking account status", Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        String error = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                        Toast.makeText(LoginActivity.this, "Authentication failed: " + error,
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveFcmToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful() && mAuth.getCurrentUser() != null) {
                String token = task.getResult();
                String userId = mAuth.getCurrentUser().getUid();
                Map<String, Object> update = new HashMap<>();
                update.put("fcmToken", token);
                FirebaseFirestore.getInstance().collection("users").document(userId).update(update);
            }
        });
    }
}