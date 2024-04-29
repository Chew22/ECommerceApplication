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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm:ss a");

        firestore.collection("Order")
                .document(auth.getCurrentUser().getUid())
                .collection("Orders")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            orderList.clear(); // Clear any previous data

                            // Convert documents to OrderModel and add them to orderList
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                OrderModel order = document.toObject(OrderModel.class);
                                orderList.add(order);
                            }

                            Collections.sort(orderList, new Comparator<OrderModel>() {
                                @Override
                                public int compare(OrderModel o1, OrderModel o2) {
                                    try {
                                        String combinedDateTime1 = o1.getOrderDate() + " " + o1.getOrderTime(); // Combine date and time
                                        String combinedDateTime2 = o2.getOrderDate() + " " + o2.getOrderTime();

                                        LocalDateTime dateTime1 = LocalDateTime.parse(combinedDateTime1, formatter);
                                        LocalDateTime dateTime2 = LocalDateTime.parse(combinedDateTime2, formatter);

                                        return dateTime1.compareTo(dateTime2);
                                    } catch (DateTimeParseException e) {
                                        Log.e("OrderListActivity", "Error parsing date/time", e);
                                        return 0; // Return a neutral comparison if parsing fails
                                    }
                                }
                            });

                            Collections.reverse(orderList); // Optional, if you want descending order

                            // Set the adapter with the sorted and reversed list
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
