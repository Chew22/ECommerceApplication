package com.example.ecommerceapplication.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapplication.R;
import com.example.ecommerceapplication.adapters.AddressAdapter;
import com.example.ecommerceapplication.models.AddressModel;
import com.example.ecommerceapplication.models.NewProductsModel;
import com.example.ecommerceapplication.models.PostModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
/**
 * This activity displays the list of addresses and allows users to select one for payment.
 */
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

        // Get data from DetailedActivity
        Object obj = getIntent().getSerializableExtra("item");

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
        addressAdapter = new AddressAdapter(getApplicationContext(),addressModelList, this);
        recyclerView.setAdapter(addressAdapter);

        // Retrieve addresses from Firestore
        firestore.collection("CurrentUser").document(auth.getCurrentUser().getUid())
                        .collection("Address").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()){
                            for (DocumentSnapshot doc :task.getResult().getDocuments()){

                                AddressModel addressModel = doc.toObject(AddressModel.class);
                                addressModelList.add(addressModel);
                                addressAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                });

        // Handle click on payment button
        paymentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                double amount = 0.0;
                if (obj instanceof NewProductsModel){
                    NewProductsModel newProductsModel = (NewProductsModel) obj;
                    amount = newProductsModel.getPrice();
                    Log.d("AddressActivity", "Price of NewProductsModel: " + amount);
                }
                if (obj instanceof PostModel){
                    PostModel postModel = (PostModel) obj;
                    amount = postModel.getPrice();
                    Log.d("AddressActivity", "Price of PostModel: " + amount);
                }
                // Create an intent to navigate from the AddressActivity to the PaymentActivity
                Intent intent = new Intent(AddressActivity.this, PaymentActivity.class);
                // Attach the amount to be paid as an extra to the intent
                intent.putExtra("amount", amount);
                // Start the PaymentActivity
                startActivity(intent);
            }
        });

        // Handle click on add address button
        addAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent( AddressActivity.this, AddAddressActivity.class));
            }
        });

    }

    // Set the selected address for payment
    @Override
    public void setAddress(String address) {
        mAddress = address;
    }
}