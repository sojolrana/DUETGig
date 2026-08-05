package com.sojolrana.duetgig;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

public class SignUpActivity extends AppCompatActivity {

    private TextInputLayout nameLayout, emailLayout, passwordLayout;
    private TextInputEditText nameEditText, emailEditText, passwordEditText;
    private RadioGroup roleGroup;
    private MaterialButton btnSignUp, btnLogin;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        nameLayout = findViewById(R.id.nameLayout);
        emailLayout = findViewById(R.id.emailLayout);
        passwordLayout = findViewById(R.id.passwordLayout);
        nameEditText = findViewById(R.id.name);
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        roleGroup = findViewById(R.id.roleGroup);
        btnSignUp = findViewById(R.id.btnSignUp);
        btnLogin = findViewById(R.id.btnLogin);

        btnSignUp.setOnClickListener(v -> {
            String name = nameEditText.getText() != null ? nameEditText.getText().toString().trim() : "";
            String email = emailEditText.getText() != null ? emailEditText.getText().toString().trim() : "";
            String password = passwordEditText.getText() != null ? passwordEditText.getText().toString().trim() : "";
            int selectedRoleId = roleGroup.getCheckedRadioButtonId();
            RadioButton selectedRoleButton = findViewById(selectedRoleId);
            String role = selectedRoleButton.getText().toString();

            if (validateInputs(name, email, password)) {
                registerUser(name, email, password, role);
            }
        });

        btnLogin.setOnClickListener(v -> {
            finish();
        });
    }

    private boolean validateInputs(String name, String email, String password) {
        boolean isValid = true;

        if (name.isEmpty()) {
            nameLayout.setError("Name is required");
            isValid = false;
        } else {
            nameLayout.setError(null);
        }

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

    private void registerUser(String name, String email, String password, String role) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful() && mAuth.getCurrentUser() != null) {
                        String userId = mAuth.getCurrentUser().getUid();
                        saveUserToFirestore(userId, name, email, role);
                    } else {
                        String error = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                        Toast.makeText(SignUpActivity.this, "Authentication failed: " + error,
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserToFirestore(String userId, String name, String email, String role) {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(fcmTask -> {
            String fcmToken = fcmTask.isSuccessful() ? fcmTask.getResult() : "";
            
            Map<String, Object> user = new HashMap<>();
            user.put("name", name);
            user.put("email", email);
            user.put("role", role);
            user.put("fcmToken", fcmToken);

            db.collection("users").document(userId)
                    .set(user)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(SignUpActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                        startActivity(intent);
                        finishAffinity();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(SignUpActivity.this, "Error saving data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
    }
}