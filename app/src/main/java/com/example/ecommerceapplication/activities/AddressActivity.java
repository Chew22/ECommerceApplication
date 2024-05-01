package com.example.ecommerceapplication.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapplication.R;
import com.example.ecommerceapplication.adapters.AddressAdapter;
import com.example.ecommerceapplication.models.AddressModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AddressActivity extends AppCompatActivity implements AddressAdapter.SelectAddress {

    // UI elements
    Button addAddressBtn;
    RecyclerView recyclerView;
    Button paymentBtn;
    Toolbar toolbar;

    // Firebase components
    FirebaseFirestore firestore;
    FirebaseAuth auth;

    // Address list
    private List<AddressModel> addressModelList;
    private AddressAdapter addressAdapter;

    // Address selected for payment
    String mAddress = "";
    String mPhoneNumber = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        // Set up toolbar
        toolbar = findViewById(R.id.address_toolbar);
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
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Initialize UI elements
        recyclerView = findViewById(R.id.address_recycler);
        paymentBtn = findViewById(R.id.payment_btn);
        addAddressBtn = findViewById(R.id.add_address_btn);

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        addressModelList = new ArrayList<>();
        addressAdapter = new AddressAdapter(getApplicationContext(), addressModelList, this);
        recyclerView.setAdapter(addressAdapter);

        // Retrieve addresses from Firestore
        firestore.collection("CurrentUser").document(auth.getCurrentUser().getUid())
                .collection("Address").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                                AddressModel addressModel = doc.toObject(AddressModel.class);
                                addressModelList.add(addressModel);
                                addressAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                });

        // Retrieve the totalAmount from the Intent
        Intent intent = getIntent();
        String orderId = intent.getStringExtra("orderId");
        double totalAmount = intent.getDoubleExtra("totalAmount", 0.0);

        paymentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAddress.isEmpty()) {
                    Toast.makeText(AddressActivity.this, "Please select an address before proceeding to payment", Toast.LENGTH_SHORT).show();
                } else {
                    if (mPhoneNumber.isEmpty()) {
                        Toast.makeText(AddressActivity.this, "Phone number is missing from the selected address", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    firestore.collection("Order")
                            .document(auth.getCurrentUser().getUid())
                            .collection("Orders")
                            .document(orderId)  // Use the orderId to find the correct document
                            .update("buyerAddress", mAddress, "buyerPhone", mPhoneNumber)  // Update both address and phone number
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        // Navigate to PaymentActivity
                                        Intent paymentIntent = new Intent(AddressActivity.this, PaymentActivity.class);
                                        paymentIntent.putExtra("totalAmount", totalAmount);
                                        startActivity(paymentIntent);
                                    } else {
                                        // Handle failure to update order
                                        Toast.makeText(AddressActivity.this, "Failed to save address and phone number", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });



        // Handle click on add address button
        addAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddressActivity.this, AddAddressActivity.class));
            }
        });
    }

    // Set the selected address for payment
    @Override
    public void setAddress(String address) {
        mAddress = address;
        // Find the phone number for the given address
        for (AddressModel addressModel : addressModelList) {
            if (addressModel.getFullAddress().equals(address)) {  // Adjust this comparison based on your data structure
                mPhoneNumber = addressModel.getPhoneNumber(); // Extract the phone number
                break;
            }
        }

    }
}
