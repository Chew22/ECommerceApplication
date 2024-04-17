package com.example.ecommerceapplication.utils;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.ecommerceapplication.models.SellerModel;
import com.google.firebase.firestore.FirebaseFirestore;

public class AndroidUtil {

    public static void showToast(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static  void passUserModelAsIntent(Intent intent, SellerModel model){
        intent.putExtra("shopName", model.getShopName());
        intent.putExtra("address", model.getAddress());
        // Retrieve the document ID of the SellerModel
        FirebaseFirestore.getInstance().collection("seller")
                .whereEqualTo("email", model.getEmail())  // Assuming 'email' is a unique field to identify sellers
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().getDocuments().isEmpty()) {
                        String sellerId = task.getResult().getDocuments().get(0).getId();
                        intent.putExtra("sellerID", sellerId);
                    }
                });

    }



    public static SellerModel getUserModelFromIntent(Intent intent){
        SellerModel sellerModel = new SellerModel();

        sellerModel.setShopName(intent.getStringExtra("shopName"));
        sellerModel.setSellerID(intent.getStringExtra("sellerID"));
        sellerModel.setEmail(intent.getStringExtra("email"));
        sellerModel.setAddress(intent.getStringExtra("address"));
        sellerModel.setImagePath(intent.getStringExtra("imagePath"));
        return sellerModel;
    }
}
