package com.example.ecommerceapplication.utils;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.ecommerceapplication.models.UserModel;

public class AndroidUtil {

    public static void showToast(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static  void passUserModelAsIntent(Intent intent, UserModel model){
        intent.putExtra("username", model.getUsername());
        intent.putExtra("phone", model.getPhone());
        intent.putExtra("userId", model.getId());

    }

    public static  UserModel getUserModelFromIntent(Intent intent){
        UserModel userModel = new UserModel();
        userModel.setUsername(intent.getStringExtra("username"));
        userModel.setPhone(intent.getStringExtra("phone"));
        userModel.setId(intent.getStringExtra("userId"));
        return userModel;
    }
}