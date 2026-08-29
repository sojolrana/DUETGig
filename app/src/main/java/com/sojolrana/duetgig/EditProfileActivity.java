package com.sojolrana.duetgig;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    private TextInputEditText etName, etStudentId, etPhone, etDob, etNid, etBirthCert, etFatherName, etMotherName, etPresentAddress, etPermanentAddress, etBloodGroup, etBio;
    private MaterialButton btnSave;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etName = findViewById(R.id.etEditName);
        etStudentId = findViewById(R.id.etEditStudentId);
        etPhone = findViewById(R.id.etEditPhone);
        etDob = findViewById(R.id.etEditDob);
        etNid = findViewById(R.id.etEditNid);
        etBirthCert = findViewById(R.id.etEditBirthCert);
        etFatherName = findViewById(R.id.etEditFatherName);
        etMotherName = findViewById(R.id.etEditMotherName);
        etPresentAddress = findViewById(R.id.etEditPresentAddress);
        etPermanentAddress = findViewById(R.id.etEditPermanentAddress);
        etBloodGroup = findViewById(R.id.etEditBloodGroup);
        etBio = findViewById(R.id.etEditBio);

        btnSave = findViewById(R.id.btnSaveProfile);

        loadCurrentData();

        btnSave.setOnClickListener(v -> saveProfile());
    }

    private void loadCurrentData() {
        if (mAuth.getCurrentUser() == null) return;
        String userId = mAuth.getCurrentUser().getUid();

        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        if (etName != null) etName.setText(documentSnapshot.getString("name"));
                        if (etStudentId != null) etStudentId.setText(documentSnapshot.getString("studentId"));
                        if (etPhone != null) etPhone.setText(documentSnapshot.getString("phone"));
                        if (etDob != null) etDob.setText(documentSnapshot.getString("dob"));
                        if (etNid != null) etNid.setText(documentSnapshot.getString("nid"));
                        if (etBirthCert != null) etBirthCert.setText(documentSnapshot.getString("birthCert"));
                        if (etFatherName != null) etFatherName.setText(documentSnapshot.getString("fatherName"));
                        if (etMotherName != null) etMotherName.setText(documentSnapshot.getString("motherName"));
                        if (etPresentAddress != null) etPresentAddress.setText(documentSnapshot.getString("presentAddress"));
                        if (etPermanentAddress != null) etPermanentAddress.setText(documentSnapshot.getString("permanentAddress"));
                        if (etBloodGroup != null) etBloodGroup.setText(documentSnapshot.getString("bloodGroup"));
                        if (etBio != null) etBio.setText(documentSnapshot.getString("bio"));
                    }
                });
    }

    private void saveProfile() {
        String name = etName.getText() != null ? etName.getText().toString().trim() : "";
        String studentId = etStudentId.getText() != null ? etStudentId.getText().toString().trim() : "";
        String phone = etPhone.getText() != null ? etPhone.getText().toString().trim() : "";
        String dob = etDob.getText() != null ? etDob.getText().toString().trim() : "";
        String nid = etNid.getText() != null ? etNid.getText().toString().trim() : "";
        String birthCert = etBirthCert.getText() != null ? etBirthCert.getText().toString().trim() : "";
        String fatherName = etFatherName.getText() != null ? etFatherName.getText().toString().trim() : "";
        String motherName = etMotherName.getText() != null ? etMotherName.getText().toString().trim() : "";
        String presentAddress = etPresentAddress.getText() != null ? etPresentAddress.getText().toString().trim() : "";
        String permanentAddress = etPermanentAddress.getText() != null ? etPermanentAddress.getText().toString().trim() : "";
        String bloodGroup = etBloodGroup.getText() != null ? etBloodGroup.getText().toString().trim() : "";
        String bio = etBio.getText() != null ? etBio.getText().toString().trim() : "";

        if (name.isEmpty()) {
            Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mAuth.getCurrentUser() == null) return;
        String userId = mAuth.getCurrentUser().getUid();

        Map<String, Object> updates = new HashMap<>();
        updates.put("name", name);
        updates.put("studentId", studentId);
        updates.put("phone", phone);
        updates.put("dob", dob);
        updates.put("nid", nid);
        updates.put("birthCert", birthCert);
        updates.put("fatherName", fatherName);
        updates.put("motherName", motherName);
        updates.put("presentAddress", presentAddress);
        updates.put("permanentAddress", permanentAddress);
        updates.put("bloodGroup", bloodGroup);
        updates.put("bio", bio);

        db.collection("users").document(userId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}