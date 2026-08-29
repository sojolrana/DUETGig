package com.sojolrana.duetgig;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sojolrana.duetgig.models.Project;

import java.util.UUID;

public class PostProjectActivity extends AppCompatActivity {

    private TextInputLayout titleLayout, descLayout, budgetLayout, categoryLayout;
    private TextInputEditText etTitle, etDesc, etBudget, etCategory;
    private MaterialButton btnPost;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_project);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        titleLayout = findViewById(R.id.projectTitleLayout);
        descLayout = findViewById(R.id.projectDescLayout);
        budgetLayout = findViewById(R.id.projectBudgetLayout);
        categoryLayout = findViewById(R.id.projectCategoryLayout);
        etTitle = findViewById(R.id.etProjectTitle);
        etDesc = findViewById(R.id.etProjectDesc);
        etBudget = findViewById(R.id.etProjectBudget);
        etCategory = findViewById(R.id.etProjectCategory);
        btnPost = findViewById(R.id.btnPost);

        btnPost.setOnClickListener(v -> {
            String title = etTitle.getText() != null ? etTitle.getText().toString().trim() : "";
            String desc = etDesc.getText() != null ? etDesc.getText().toString().trim() : "";
            String budgetStr = etBudget.getText() != null ? etBudget.getText().toString().trim() : "";
            String category = etCategory.getText() != null ? etCategory.getText().toString().trim() : "";

            if (validateInputs(title, desc, budgetStr, category)) {
                double budget = Double.parseDouble(budgetStr);
                postProject(title, desc, budget, category);
            }
        });
    }

    private boolean validateInputs(String title, String desc, String budget, String category) {
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

        if (budget.isEmpty()) {
            budgetLayout.setError("Budget is required");
            isValid = false;
        } else {
            budgetLayout.setError(null);
        }

        if (category.isEmpty()) {
            categoryLayout.setError("Category is required");
            isValid = false;
        } else {
            categoryLayout.setError(null);
        }

        return isValid;
    }

    private void postProject(String title, String desc, double budget, String category) {
        if (mAuth.getCurrentUser() == null) return;

        String projectId = UUID.randomUUID().toString();
        String posterId = mAuth.getCurrentUser().getUid();
        String posterName = mAuth.getCurrentUser().getEmail(); // Simplified for now

        Project project = new Project(projectId, title, desc, budget, posterId, posterName, category, "Pending", Timestamp.now());

        db.collection("projects").document(projectId)
                .set(project)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Project posted successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error posting project: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}