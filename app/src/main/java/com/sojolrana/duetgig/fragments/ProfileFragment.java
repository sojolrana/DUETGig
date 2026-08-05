package com.sojolrana.duetgig.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sojolrana.duetgig.EditProfileActivity;
import com.sojolrana.duetgig.LoginActivity;
import com.sojolrana.duetgig.R;

public class ProfileFragment extends Fragment {

    private TextView name, email, bio;
    private Chip roleChip;
    private MaterialButton btnEdit, btnSignOut;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        name = view.findViewById(R.id.profileName);
        email = view.findViewById(R.id.profileEmail);
        bio = view.findViewById(R.id.profileBio);
        roleChip = view.findViewById(R.id.profileRoleChip);
        btnEdit = view.findViewById(R.id.btnEditProfile);
        btnSignOut = view.findViewById(R.id.btnSignOut);

        loadUserProfile();

        btnEdit.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), EditProfileActivity.class));
        });

        btnSignOut.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(getContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        return view;
    }

    private void loadUserProfile() {
        if (mAuth.getCurrentUser() == null) return;
        String userId = mAuth.getCurrentUser().getUid();

        db.collection("users").document(userId)
                .addSnapshotListener((documentSnapshot, error) -> {
                    if (error != null || documentSnapshot == null) return;
                    if (documentSnapshot.exists()) {
                        name.setText(documentSnapshot.getString("name"));
                        email.setText(documentSnapshot.getString("email"));
                        bio.setText(documentSnapshot.getString("bio") != null ? documentSnapshot.getString("bio") : "No bio added yet.");
                        roleChip.setText(documentSnapshot.getString("role"));
                    }
                });
    }
}