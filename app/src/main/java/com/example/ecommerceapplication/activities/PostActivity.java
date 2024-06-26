package com.example.ecommerceapplication.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapplication.R;
import com.example.ecommerceapplication.adapters.ShowAllAdapter;
import com.example.ecommerceapplication.models.PostModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class PostActivity extends AppCompatActivity {

    // Declare RecyclerView, adapter, model list, and toolbar variables
   RecyclerView recyclerView_post;
   ShowAllAdapter showAllAdapter;
   List<PostModel> PostModelList;
   Toolbar toolbar;

    // Declare Firestore instance
   FirebaseFirestore firestore;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all);

        // Initialize toolbar and set support action bar
        toolbar = findViewById(R.id.show_all_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Back Pressed On Toolbar
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        // Initialize Firestore instance and RecyclerView
        firestore = FirebaseFirestore.getInstance();
        recyclerView_post = findViewById(R.id.show_all_rec);
        recyclerView_post.setLayoutManager(new GridLayoutManager(this, 2));
        PostModelList = new ArrayList<>();
        showAllAdapter = new ShowAllAdapter(this, PostModelList);
        recyclerView_post.setAdapter(showAllAdapter);



        // Safe retrieval of intent extras
        Intent intent = getIntent();
        if (intent != null) {
            boolean isNewProducts = intent.getBooleanExtra("isNewProducts", true);

            if (isNewProducts) {
                firestore.collection("NewProduct")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                                        PostModel postModel = doc.toObject(PostModel.class);
                                        PostModelList.add(postModel);
                                    }
                                    showAllAdapter.notifyDataSetChanged();  // Notify adapter of changes
                                }
                            }
                        });
            }

        } else {
            // Handle the case when there's no intent or required extras
            Log.e("PostActivity", "Intent is null or missing expected extras");
            finish();
        }

        // Retrieve productCategory from intent extra
        String productCategory = getIntent().getStringExtra("productCategory");


        // Fetch data based on the selected productCategory
        if(productCategory == null || productCategory.isEmpty()) {
            // All documents from the "ShowAll" collection
            firestore.collection("Products")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                            if (task.isSuccessful()) {
                                // Iterate through documents and add them to the list
                                for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                                    PostModel PostModel = doc.toObject(PostModel.class);
                                    PostModelList.add(PostModel);
                                    showAllAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    });
        }

        // Query Firestore based on the productCategory
        if(productCategory != null && productCategory.equalsIgnoreCase("Handmade Crafts")){
            // Log the category passed to PostActivity
            Log.d("PostActivity", "Product Category: " + productCategory);

            // Query documents where "productCategory" field matches the 'antique''
            firestore.collection("Products").whereEqualTo("productCategory", "Handmade Crafts")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                            if (task.isSuccessful()) {
                                Log.d("PostActivity", "Products fetched for category: " + productCategory);

                                for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                                    // Iterate through documents and add them to the list
                                    PostModel PostModel = doc.toObject(PostModel.class);
                                    PostModelList.add(PostModel);
                                    showAllAdapter.notifyDataSetChanged();
                                }
                            }else {
                                Log.e("PostActivity", "Failed to fetch products for category: " + productCategory, task.getException());
                            }
                        }
                    });
        }   if(productCategory != null && productCategory.equalsIgnoreCase("Artwork")){

            firestore.collection("Products").whereEqualTo("productCategory", "Artwork")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                            if (task.isSuccessful()) {
                                for (DocumentSnapshot doc : task.getResult().getDocuments()) {

                                    PostModel PostModel = doc.toObject(PostModel.class);
                                    PostModelList.add(PostModel);
                                    showAllAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    });
        }   if(productCategory != null && productCategory.equalsIgnoreCase("Fashion and Accessories")){

            firestore.collection("Products").whereEqualTo("productCategory", "Fashion and Accessories")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                            if (task.isSuccessful()) {
                                for (DocumentSnapshot doc : task.getResult().getDocuments()) {

                                    PostModel PostModel = doc.toObject(PostModel.class);
                                    PostModelList.add(PostModel);
                                    showAllAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    });
        }   if(productCategory != null && productCategory.equalsIgnoreCase("Home Decor")){

            firestore.collection("Products").whereEqualTo("productCategory", "Home Decor")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                            if (task.isSuccessful()) {
                                for (DocumentSnapshot doc : task.getResult().getDocuments()) {

                                    PostModel PostModel = doc.toObject(PostModel.class);
                                    PostModelList.add(PostModel);
                                    showAllAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    });
        }   if(productCategory != null && productCategory.equalsIgnoreCase("Gifts and Souvenirs")){

            firestore.collection("Products").whereEqualTo("productCategory", "Gifts and Souvenirs")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                            if (task.isSuccessful()) {
                                for (DocumentSnapshot doc : task.getResult().getDocuments()) {

                                    PostModel PostModel = doc.toObject(PostModel.class);
                                    PostModelList.add(PostModel);
                                    showAllAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    });
        }   if(productCategory != null && productCategory.equalsIgnoreCase("Food and Beverages")) {

            firestore.collection("Products").whereEqualTo("productCategory", "Food and Beverages")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                            if (task.isSuccessful()) {
                                for (DocumentSnapshot doc : task.getResult().getDocuments()) {

                                    PostModel PostModel = doc.toObject(PostModel.class);
                                    PostModelList.add(PostModel);
                                    showAllAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    });
        }if(productCategory != null && productCategory.equalsIgnoreCase("Personal Care Products")) {

            firestore.collection("Products").whereEqualTo("productCategory", "Personal Care Products")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                            if (task.isSuccessful()) {
                                for (DocumentSnapshot doc : task.getResult().getDocuments()) {

                                    PostModel PostModel = doc.toObject(PostModel.class);
                                    PostModelList.add(PostModel);
                                    showAllAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    });
        }
    }
}

