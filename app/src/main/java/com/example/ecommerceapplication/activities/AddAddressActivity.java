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
 * This class represents an activity for adding a new address.
 */
public class AddAddressActivity extends AppCompatActivity {

    // UI elements
    EditText name, address, city, postalCode, phoneNumber;
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

        // Back Pressed On Toolbar
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        // Initialize UI elements
        name = findViewById(R.id.ad_name);
        address = findViewById(R.id.ad_address);
        city = findViewById(R.id.ad_city);
        phoneNumber = findViewById(R.id.ad_phone);
        postalCode = findViewById(R.id.ad_code);
        addAddressBtn = findViewById(R.id.ad_add_address);

        // Handle click on add address button
        addAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Get user input
                String userName = name.getText().toString();
                String userAddress = address.getText().toString();
                String userCity = city.getText().toString();
                String userNumber = phoneNumber.getText().toString();
                String userCode = postalCode.getText().toString();

                // Concatenate address fields
                String final_address = "";

                if (!userName.isEmpty()){
                    final_address += userName;
                }
                if (!userAddress.isEmpty()){
                    final_address += userAddress;
                }
                if (!userCity.isEmpty()){
                    final_address += userCity;
                }
                if (!userNumber.isEmpty()){
                    final_address += userNumber;
                }
                if (!userCode.isEmpty()){
                    final_address += userCode;
                }

                // Check if all fields are filled
                if (!userName.isEmpty() && !userAddress.isEmpty() && !userCity.isEmpty() && !userNumber.isEmpty() && !userCode.isEmpty() ){

                    // Create map to store address data
                    Map<String, String> map = new HashMap<>();
                    map.put("userAddress", final_address);

                    // Add address data to Firestore
                    firestore.collection("CurrentUser").document(auth.getCurrentUser().getUid())
                            .collection("Address").add(map).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    if (task.isSuccessful()){
                                        // Address added successfully, show toast and navigate to detailed activity
                                        Toast.makeText(AddAddressActivity.this, "Address Added", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(AddAddressActivity.this, DetailedActivity.class));
                                        finish();
                                    }
                                }
                            });

                }else {
                    // Show toast if any field is empty
                    Toast.makeText(AddAddressActivity.this, "Kindly Fill In All Field", Toast.LENGTH_SHORT).show();

                }

            }
        });

    }
}