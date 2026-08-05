package com.sojolrana.duetgig;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class ServiceDetailActivity extends AppCompatActivity {

    private TextView title, price, description, providerName, providerBio;
    private MaterialButton btnHire;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_detail);

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

        title.setText(sTitle);
        price.setText("$" + sPrice);
        description.setText(sDescription);
        providerName.setText(sProvider);
        providerBio.setText(sBio);

        btnHire.setOnClickListener(v -> {
            Toast.makeText(this, "Hiring process coming soon!", Toast.LENGTH_SHORT).show();
        });
    }
}