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
import com.google.firebase.firestore.DocumentSnapshot;
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

    private List<OrderModel> orderList;

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

        // Fetch order items data from Firebase
        fetchOrderItemData();
        fetchOrderData();
    }

    private void fetchOrderItemData() {
        firestore.collection("Order")
                .document(auth.getCurrentUser().getUid())
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
        Log.d(TAG, "fetchOrderData: Start fetching order data");

        firestore.collection("Order")
                .document(auth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null) {
                                if (document.exists()) {
                                    // Log the document data
                                    Log.d(TAG, "Document data: " + document.getData());

                                    // Convert the document to OrderModel (assuming you have an OrderModel class)
                                    OrderModel order = document.toObject(OrderModel.class);
                                    Log.d(TAG, "Order data: " + order.toString());

                                    // Create the adapter and pass the order to it
                                    List<OrderModel> orderList = new ArrayList<>();
                                    orderList.add(order);
                                    OrderAdapter adapter = new OrderAdapter(OrderStatusActivity.this, orderList);
                                    recyclerView.setAdapter(adapter);
                                    Log.d(TAG, "Adapter set with order data");
                                } else {
                                    Log.d(TAG, "No such document");
                                }
                            } else {
                                Log.e(TAG, "Document is null");
                            }
                        } else {
                            Log.e(TAG, "Error getting document: ", task.getException());
                        }
                    }
                });
    }




}
