package com.example.ecommerceapplication.activities;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

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

    private OrderAdapter adapter;

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

        // Find TextViews in the layout
        TextView orderIdTextView = findViewById(R.id.order_id);
        TextView orderStatusTextView = findViewById(R.id.order_status);
        TextView orderPlacedDateTextView = findViewById(R.id.order_placed_date);
        TextView totalAmountTextView = findViewById(R.id.total_amount);

        // Set order details in TextViews
        orderIdTextView.setText("Order ID: " + order.getOrderId());
        orderStatusTextView.setText("Order Status: " + order.getOrderStatus());
        orderPlacedDateTextView.setText("Order Placed Date: " + order.getOrderDate());
        totalAmountTextView.setText("Total Amount: RM " + order.getOrderTotal());

        // Initialize the adapter
        adapter = new OrderAdapter(this, new ArrayList<>(), order.getOrderId()); // Pass orderId here
        recyclerView.setAdapter(adapter);

        // Fetch order item data
        fetchOrderItemData(order.getOrderId());
        fetchOrderData(order.getOrderId());
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


    private void fetchOrderData(String orderId) {
        firestore.collection("Order")
                .document(auth.getCurrentUser().getUid())
                .collection("Orders")
                .document(orderId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // Log the document data
                                Log.d(TAG, "Document ID: " + document.getId());
                                Log.d(TAG, "Document data: " + document.getData());

                                // Convert the document to OrderModel
                                OrderModel order = document.toObject(OrderModel.class);

                                // Update the adapter with the order details
                                adapter.addOrder(order);

                                // Now, fetch order items
                                fetchOrderItemData(orderId);
                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.e(TAG, "Error getting document: ", task.getException());
                        }
                    }
                });
    }
}
