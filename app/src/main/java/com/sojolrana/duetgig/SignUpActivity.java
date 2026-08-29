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

    private TextInputLayout nameLayout, emailLayout, passwordLayout, studentIdLayout, phoneLayout, dobLayout;
    private TextInputLayout nidLayout, birthCertLayout, fatherNameLayout, motherNameLayout, presentAddressLayout, permanentAddressLayout, bloodGroupLayout;
    private TextInputEditText nameEditText, emailEditText, passwordEditText, studentIdEditText, phoneEditText, dobEditText;
    private TextInputEditText nidEditText, birthCertEditText, fatherNameEditText, motherNameEditText, presentAddressEditText, permanentAddressEditText, bloodGroupEditText;
    private RadioGroup roleGroup, genderGroup;
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

        // Initialize layouts and inputs
        nameLayout = findViewById(R.id.nameLayout);
        emailLayout = findViewById(R.id.emailLayout);
        passwordLayout = findViewById(R.id.passwordLayout);
        studentIdLayout = findViewById(R.id.studentIdLayout);
        phoneLayout = findViewById(R.id.phoneLayout);
        dobLayout = findViewById(R.id.dobLayout);

        nidLayout = findViewById(R.id.nidLayout);
        birthCertLayout = findViewById(R.id.birthCertLayout);
        fatherNameLayout = findViewById(R.id.fatherNameLayout);
        motherNameLayout = findViewById(R.id.motherNameLayout);
        presentAddressLayout = findViewById(R.id.presentAddressLayout);
        permanentAddressLayout = findViewById(R.id.permanentAddressLayout);
        bloodGroupLayout = findViewById(R.id.bloodGroupLayout);

        nameEditText = findViewById(R.id.name);
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        studentIdEditText = findViewById(R.id.etStudentId);
        phoneEditText = findViewById(R.id.etPhone);
        dobEditText = findViewById(R.id.etDob);

        nidEditText = findViewById(R.id.etNid);
        birthCertEditText = findViewById(R.id.etBirthCert);
        fatherNameEditText = findViewById(R.id.etFatherName);
        motherNameEditText = findViewById(R.id.etMotherName);
        presentAddressEditText = findViewById(R.id.etPresentAddress);
        permanentAddressEditText = findViewById(R.id.etPermanentAddress);
        bloodGroupEditText = findViewById(R.id.etBloodGroup);

        roleGroup = findViewById(R.id.roleGroup);
        genderGroup = findViewById(R.id.genderGroup);
        btnSignUp = findViewById(R.id.btnSignUp);
        btnLogin = findViewById(R.id.btnLogin);

        btnSignUp.setOnClickListener(v -> {
            String name = nameEditText.getText() != null ? nameEditText.getText().toString().trim() : "";
            String email = emailEditText.getText() != null ? emailEditText.getText().toString().trim() : "";
            String password = passwordEditText.getText() != null ? passwordEditText.getText().toString().trim() : "";
            String studentId = studentIdEditText.getText() != null ? studentIdEditText.getText().toString().trim() : "";
            String phone = phoneEditText.getText() != null ? phoneEditText.getText().toString().trim() : "";
            String dob = dobEditText.getText() != null ? dobEditText.getText().toString().trim() : "";

            String nid = nidEditText.getText() != null ? nidEditText.getText().toString().trim() : "";
            String birthCert = birthCertEditText.getText() != null ? birthCertEditText.getText().toString().trim() : "";
            String fatherName = fatherNameEditText.getText() != null ? fatherNameEditText.getText().toString().trim() : "";
            String motherName = motherNameEditText.getText() != null ? motherNameEditText.getText().toString().trim() : "";
            String presentAddress = presentAddressEditText.getText() != null ? presentAddressEditText.getText().toString().trim() : "";
            String permanentAddress = permanentAddressEditText.getText() != null ? permanentAddressEditText.getText().toString().trim() : "";
            String bloodGroup = bloodGroupEditText.getText() != null ? bloodGroupEditText.getText().toString().trim() : "";

            int selectedGenderId = genderGroup.getCheckedRadioButtonId();
            RadioButton selectedGenderButton = findViewById(selectedGenderId);
            String gender = selectedGenderButton != null ? selectedGenderButton.getText().toString() : "Male";

            int selectedRoleId = roleGroup.getCheckedRadioButtonId();
            RadioButton selectedRoleButton = findViewById(selectedRoleId);
            String role = selectedRoleButton != null ? selectedRoleButton.getText().toString() : "Student";

            if (validateInputs(name, email, password, studentId, phone, dob)) {
                registerUser(name, email, password, studentId, phone, dob, nid, birthCert, fatherName, motherName, presentAddress, permanentAddress, bloodGroup, gender, role);
            }
        });

        btnLogin.setOnClickListener(v -> finish());
    }

    private boolean validateInputs(String name, String email, String password, String studentId, String phone, String dob) {
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

        if (studentId.isEmpty()) {
            studentIdLayout.setError("Student ID is required");
            isValid = false;
        } else {
            studentIdLayout.setError(null);
        }

        if (phone.isEmpty()) {
            phoneLayout.setError("Phone number is required");
            isValid = false;
        } else {
            phoneLayout.setError(null);
        }

        if (dob.isEmpty()) {
            dobLayout.setError("Date of Birth is required");
            isValid = false;
        } else {
            dobLayout.setError(null);
        }

        return isValid;
    }

    private void registerUser(String name, String email, String password, String studentId, String phone, String dob,
                              String nid, String birthCert, String fatherName, String motherName, String presentAddress,
                              String permanentAddress, String bloodGroup, String gender, String role) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful() && mAuth.getCurrentUser() != null) {
                        String userId = mAuth.getCurrentUser().getUid();
                        saveUserToFirestore(userId, name, email, studentId, phone, dob, nid, birthCert, fatherName, motherName, presentAddress, permanentAddress, bloodGroup, gender, role);
                    } else {
                        String error = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                        Toast.makeText(SignUpActivity.this, "Authentication failed: " + error,
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserToFirestore(String userId, String name, String email, String studentId, String phone, String dob,
                                     String nid, String birthCert, String fatherName, String motherName, String presentAddress,
                                     String permanentAddress, String bloodGroup, String gender, String role) {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(fcmTask -> {
            String fcmToken = fcmTask.isSuccessful() ? fcmTask.getResult() : "";
            
            Map<String, Object> user = new HashMap<>();
            user.put("name", name);
            user.put("email", email);
            user.put("studentId", studentId);
            user.put("phone", phone);
            user.put("dob", dob);
            user.put("nid", nid);
            user.put("birthCert", birthCert);
            user.put("fatherName", fatherName);
            user.put("motherName", motherName);
            user.put("presentAddress", presentAddress);
            user.put("permanentAddress", permanentAddress);
            user.put("bloodGroup", bloodGroup);
            user.put("gender", gender);
            user.put("role", role);
            user.put("status", "Pending");
            user.put("isAdmin", false);
            user.put("fcmToken", fcmToken);

            db.collection("users").document(userId)
                    .set(user)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(SignUpActivity.this, "Registration pending admin approval", Toast.LENGTH_LONG).show();
                        mAuth.signOut();
                        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finishAffinity();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(SignUpActivity.this, "Error saving data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
    }
}