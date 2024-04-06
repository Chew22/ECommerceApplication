package com.example.ecommerceapplication.activities;

import static androidx.fragment.app.FragmentManager.TAG;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapplication.R;
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

    Context context;

    private List<OrderModel> orderModelList;
    private RecyclerView recyclerView;

    private FirebaseFirestore firestore;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.order_rec);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize orderModelList
        orderModelList = new ArrayList<>();

        // Fetch order data from Firebase
        fetchOrderData();
    }

    private void fetchOrderData() {
        firestore.collection("Order").document(auth.getCurrentUser().getUid())
                .collection("Items").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot doc : task.getResult().getDocuments()) {

                        OrderModel orderModel = doc.toObject(OrderModel.class);
                        orderModelList.add(orderModel);
                    }
                }
            }
        });

        firestore.collection("Order").document(auth.getCurrentUser().getUid())
                .collection("Items").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<ItemsModel> orderItemList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ItemsModel orderItem = document.toObject(ItemsModel.class);
                                orderItemList.add(orderItem);
                            }

                            // Create the adapter and pass the orderItemList
                            OrderItemAdapter adapter = new OrderItemAdapter(context, orderItemList);
                            recyclerView.setAdapter(adapter);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

}
