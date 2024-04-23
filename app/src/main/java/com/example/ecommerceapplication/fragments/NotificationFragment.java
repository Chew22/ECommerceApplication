package com.example.ecommerceapplication.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapplication.R;
import com.example.ecommerceapplication.adapters.NotificationAdapter;
import com.example.ecommerceapplication.models.NotificationModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NotificationFragment extends Fragment {

    private RecyclerView recyclerView;
    private NotificationAdapter notificationAdapter;
    private List<NotificationModel> notificationList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        notificationList = new ArrayList<>();
        notificationAdapter = new NotificationAdapter(getContext(), notificationList);
        recyclerView.setAdapter(notificationAdapter);

        readNotification();
        return view;
    }


    private void readNotification() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        notificationList.clear();

        CollectionReference notificationRef = FirebaseFirestore.getInstance().collection("Notifications").document(firebaseUser.getUid()).collection("notifications");
        notificationRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.e("readNotification", "Error fetching notifications", e);
                    return;
                }

                if (querySnapshot != null) {
                    notificationList.clear();
                    // Iterate through each document snapshot in the collection
                    for (DocumentSnapshot snapshot : querySnapshot.getDocuments()) {
                        // Convert the document snapshot to a NotificationModel object
                        NotificationModel notification = snapshot.toObject(NotificationModel.class);
                        // Add the notification to the notificationList
                        notificationList.add(notification);

                        // Log the data retrieved from Firestore
                        Log.d("FirestoreData", "ID: " + snapshot.getId());
                        Log.d("FirestoreData", "Data: " + snapshot.getData());
                    }

                    // Reverse the list to display the most recent notifications first
                    Collections.reverse(notificationList);
                    // Notify the adapter about the data change
                    notificationAdapter.notifyDataSetChanged();

                    // Show/hide empty view based on notificationList size
                    TextView emptyView = getView().findViewById(R.id.empty_view);
                    if (notificationList.isEmpty()) {
                        emptyView.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        emptyView.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                } else {
                    Log.d("readNotification", "Current data: null");
                }
            }

        });
    }
}