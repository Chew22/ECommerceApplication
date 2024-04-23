package com.example.ecommerceapplication.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.ecommerceapplication.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CardPaymentActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    EditText card_number_input, expiry_date_input, cvv_input, name_input;
    Button save_card_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_payment);

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Find the toolbar by ID
        Toolbar toolbar = findViewById(R.id.toolbar);

        // Set the toolbar as the action bar
        setSupportActionBar(toolbar);

        // Enable the back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Handle back button click
        toolbar.setNavigationOnClickListener(v -> {
            finish();
        });

        // Bind with XML
        name_input = findViewById(R.id.name_input);
        card_number_input = findViewById(R.id.card_number_input);
        expiry_date_input = findViewById(R.id.expiry_date_input);
        cvv_input = findViewById(R.id.cvv_input);
        save_card_button = findViewById(R.id.save_card_button);

        // Set a Click Listener
        save_card_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCardData();
            }
        });

    }

    private void saveCardData() {
        // Collect user input
        String name = name_input.getText().toString();
        String cardNumber = card_number_input.getText().toString();
        String expiryDate = expiry_date_input.getText().toString();
        String cvv = cvv_input.getText().toString();

        // Create a data map to store in Firestore
        Map<String, String> cardData = new HashMap<>();
        cardData.put("name", name);
        cardData.put("cardNumber", cardNumber);
        cardData.put("expiryDate", expiryDate);
        cardData.put("cvv", cvv);

        // Save data to Firestore
        db.collection("cardPayments")
                .add(cardData)
                .addOnSuccessListener(documentReference -> {
                    // Show success message
                    Toast.makeText(this, "Card saved successfully!", Toast.LENGTH_SHORT).show();

                    // Go back to the previous page
                    finish();
                })
                .addOnFailureListener(e -> {
                    // Handle failure with a message
                    Toast.makeText(this, "Failed to save card. Please try again.", Toast.LENGTH_SHORT).show();
                });
    }
}
