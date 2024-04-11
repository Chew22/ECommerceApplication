package com.example.ecommerceapplication.activities;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapplication.R;
import com.example.ecommerceapplication.adapters.OrderAdapter;
import com.example.ecommerceapplication.adapters.OrderItemAdapter;
import com.example.ecommerceapplication.models.ItemsModel;
import com.example.ecommerceapplication.models.OrderModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class OrderStatusActivity extends AppCompatActivity {

    private static final String TAG = "OrderStatusActivity";

    private Context context;

    private List<ItemsModel> orderItemList;
    private RecyclerView recyclerView;

    private FirebaseFirestore firestore;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);

        // Initialize Firestore and FirebaseAuth
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.order_rec);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize orderItemList
        orderItemList = new ArrayList<>();

        // Get the order model from intent
        OrderModel order = (OrderModel) getIntent().getSerializableExtra("order_model");
        if (order != null) {
            // Fetch order items data from Firebase using the order ID
            fetchOrderItemData(order.getOrderId());
        } else {
            Log.e(TAG, "No order model received");
        }

        fetchOrderData();
    }

    private void fetchOrderItemData(String orderId) {
        firestore.collection("Order")
                .document(auth.getCurrentUser().getUid())
                .collection("Orders")
                .document(orderId)  // Use the order ID passed from OrderListActivity
                .collection("Items")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Convert the document to ItemsModel
                                ItemsModel orderItem = document.toObject(ItemsModel.class);
                                orderItemList.add(orderItem);
                            }
                            // Create the adapter and pass the orderItemList
                            OrderItemAdapter adapter = new OrderItemAdapter(OrderStatusActivity.this, orderItemList);
                            recyclerView.setAdapter(adapter);
                        } else {
                            Log.e(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
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
                            List<OrderModel> orderList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Log the document data
                                Log.d(TAG, "Document ID: " + document.getId());
                                Log.d(TAG, "Document data: " + document.getData());

                                // Convert each document to OrderModel and add it to the list
                                OrderModel order = document.toObject(OrderModel.class);
                                orderList.add(order);
                            }

                            // Create the adapter and pass the order list to it
                            OrderAdapter adapter = new OrderAdapter(OrderStatusActivity.this, orderList);
                            recyclerView.setAdapter(adapter);
                        } else {
                            Log.e(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

}
