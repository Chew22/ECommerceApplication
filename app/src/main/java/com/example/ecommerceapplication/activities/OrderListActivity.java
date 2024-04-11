package com.example.ecommerceapplication.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapplication.R;
import com.example.ecommerceapplication.adapters.OrderAdapter;
import com.example.ecommerceapplication.models.OrderModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
public class OrderListActivity extends AppCompatActivity implements OrderAdapter.OnOrderItemClickListener {

    private static final String TAG = "OrderListActivity";

    private Context context;

    private RecyclerView recyclerView;
    private List<OrderModel> orderList; // Declare orderList variable

    private FirebaseFirestore firestore;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);

        // Initialize Firestore and FirebaseAuth
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.order_list_rec);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize orderList
        orderList = new ArrayList<>();

        // Fetch order list data from Firebase
        fetchOrderData();
    }

    private void fetchOrderData() {

        firestore.collection("Order")
                .document(auth.getCurrentUser().getUid())
                .collection("Orders")  // Reference the "Orders" subcollection
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Log the document data
                                Log.d(TAG, "Document ID: " + document.getId());
                                Log.d(TAG, "Document data: " + document.getData());

                                // Convert each document to OrderModel and add it to the list
                                OrderModel order = document.toObject(OrderModel.class);
                                orderList.add(order);
                            }

                            // Create the adapter and pass the order list to it
                            OrderAdapter adapter = new OrderAdapter(OrderListActivity.this, orderList);
                            adapter.setOnOrderItemClickListener(OrderListActivity.this);
                            recyclerView.setAdapter(adapter);

                        } else {
                            Log.e(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    @Override
    public void onOrderItemClick(OrderModel order) {
        // Start OrderStatusActivity and pass the order details
        Intent intent = new Intent(OrderListActivity.this, OrderStatusActivity.class);
        intent.putExtra("order_model", order);
        startActivity(intent);
    }
}
