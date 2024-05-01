package com.example.ecommerceapplication.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.ecommerceapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * This class reprfgjesents an activity for adding a new address.
 */
public class AddAddressActivity extends AppCompatActivity {

    // UI elements
    EditText name, addressLane, city, postalCode, phoneNumber;
    Toolbar toolbar;
    Button addAddressBtn;

    // Firebase components
    FirebaseFirestore firestore;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);

        // Initialize toolbar
        toolbar = findViewById(R.id.add_address_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Handle back navigation
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Initialize Firebase components
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Initialize UI elements
        name = findViewById(R.id.ad_name);
        addressLane = findViewById(R.id.ad_address);
        city = findViewById(R.id.ad_city);
        phoneNumber = findViewById(R.id.ad_phone);
        postalCode = findViewById(R.id.ad_code);
        addAddressBtn = findViewById(R.id.ad_add_address);

        // Set click listener for adding the address
        addAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get user input
                String userName = name.getText().toString();
                String userAddressLane = addressLane.getText().toString();
                String userCity = city.getText().toString();
                String userPostalCode = postalCode.getText().toString();
                String userPhoneNumber = phoneNumber.getText().toString();

                // Concatenate to form full address
                String fullAddress = userAddressLane + ", " + userCity + ", " + userPostalCode;

                // Check if all required fields are filled
                if (!userName.isEmpty() && !userAddressLane.isEmpty() && !userCity.isEmpty() &&
                        !userPostalCode.isEmpty() && !userPhoneNumber.isEmpty()) {
                    // Create a map to store the user data
                    Map<String, Object> addressData = new HashMap<>();
                    addressData.put("name", userName);
                    addressData.put("addressLane", userAddressLane);
                    addressData.put("city", userCity);
                    addressData.put("postalCode", userPostalCode);
                    addressData.put("phoneNumber", userPhoneNumber);
                    addressData.put("fullAddress", fullAddress); // Storing the concatenated full address

                    // Add address data to Firestore
                    firestore.collection("CurrentUser").document(auth.getCurrentUser().getUid())
                            .collection("Address").add(addressData).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    if (task.isSuccessful()) {
                                        // Address added successfully
                                        Toast.makeText(AddAddressActivity.this, "Address Added", Toast.LENGTH_SHORT).show();
                                        // Redirect to AddressActivity
                                        startActivity(new Intent(AddAddressActivity.this, AddressActivity.class));
                                        finish();
                                    } else {
                                        // Display error message
                                        Toast.makeText(AddAddressActivity.this, "Failed to Add Address", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    // Display error message if any field is empty
                    Toast.makeText(AddAddressActivity.this, "Please Fill in All Fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
