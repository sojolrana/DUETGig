package com.sojolrana.duetgig;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.sojolrana.duetgig.models.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PostServiceActivity extends AppCompatActivity {

    private TextInputLayout titleLayout, descLayout, priceLayout, categoryLayout;
    private TextInputEditText etTitle, etDesc, etPrice;
    private AutoCompleteTextView categoryDropdown;
    private MaterialButton btnPost;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private List<String> categories = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_service);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        titleLayout = findViewById(R.id.serviceTitleLayout);
        descLayout = findViewById(R.id.serviceDescLayout);
        priceLayout = findViewById(R.id.servicePriceLayout);
        categoryLayout = findViewById(R.id.serviceCategoryLayout);
        etTitle = findViewById(R.id.etServiceTitle);
        etDesc = findViewById(R.id.etServiceDesc);
        etPrice = findViewById(R.id.etServicePrice);
        categoryDropdown = findViewById(R.id.categoryDropdown);
        btnPost = findViewById(R.id.btnPostService);

        loadCategories();

        btnPost.setOnClickListener(v -> {
            String title = etTitle.getText() != null ? etTitle.getText().toString().trim() : "";
            String desc = etDesc.getText() != null ? etDesc.getText().toString().trim() : "";
            String priceStr = etPrice.getText() != null ? etPrice.getText().toString().trim() : "";
            String category = categoryDropdown.getText().toString();

            if (validateInputs(title, desc, priceStr, category)) {
                postService(title, desc, priceStr, category);
            }
        });
    }

    private boolean validateInputs(String title, String desc, String price, String category) {
        boolean isValid = true;

        if (title.isEmpty()) {
            titleLayout.setError("Title is required");
            isValid = false;
        } else {
            titleLayout.setError(null);
        }

        if (desc.isEmpty()) {
            descLayout.setError("Description is required");
            isValid = false;
        } else {
            descLayout.setError(null);
        }

        if (price.isEmpty()) {
            priceLayout.setError("Price is required");
            isValid = false;
        } else {
            priceLayout.setError(null);
        }

        if (category.isEmpty()) {
            categoryLayout.setError("Category is required");
            isValid = false;
        } else {
            categoryLayout.setError(null);
        }

        return isValid;
    }

    private void loadCategories() {
        db.collection("categories").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                categories.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    categories.add(document.getString("name"));
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, categories);
                categoryDropdown.setAdapter(adapter);
            }
        });
    }

    private void postService(String title, String desc, String priceStr, String category) {
        double price = Double.parseDouble(priceStr);
        String userId = mAuth.getCurrentUser().getUid();

        // Fetch provider info first
        db.collection("users").document(userId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String providerName = documentSnapshot.getString("name");
                String providerBio = documentSnapshot.getString("bio");
                
                String serviceId = UUID.randomUUID().toString();
                Service service = new Service(serviceId, title, desc, price, category, userId, providerName, providerBio, 5.0f, "Pending");

                db.collection("services").document(serviceId).set(service).addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Service submitted for admin approval", Toast.LENGTH_LONG).show();
                    finish();
                });
            }
        });
    }
}