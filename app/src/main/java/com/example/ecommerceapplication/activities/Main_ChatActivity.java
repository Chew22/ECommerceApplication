//package com.example.ecommerceapplication.activities;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.widget.ImageButton;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.example.ecommerceapplication.R;
//
//public class Main_ChatActivity extends AppCompatActivity {
//
//    // Chat Page
//    ImageButton searchButton;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main_chat);
//
//        searchButton = findViewById(R.id.main_search_btn);
//        searchButton.setOnClickListener((v)->{
//            startActivity(new Intent(Main_ChatActivity.this, Chew_SearchUserActivity.class));
//
//        });
//    }
//}

//
//<?xml version="1.0" encoding="utf-8"?>
//<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
//        xmlns:app="http://schemas.android.com/apk/res-auto"
//        xmlns:tools="http://schemas.android.com/tools"
//        android:layout_width="match_parent"
//        android:layout_height="match_parent"
//        android:layout_marginTop="60dp"
//        tools:context=".activities.Main_ChatActivity">
//
//<RelativeLayout
//        android:layout_width="match_parent"
//                android:layout_height="wrap_content"
//                android:background="@color/theme_orange"
//                android:padding="10dp"
//                android:id="@+id/toolbar">
//
//
//<TextView
//            android:layout_width="wrap_content"
//                    android:layout_height="wrap_content"
//                    android:text="Shoploca"
//                    android:textSize="22sp"
//                    android:textStyle="bold"
//                    android:textColor="@color/white"
//                    android:layout_marginStart="10dp"
//                    android:layout_centerVertical="true"
//                    android:layout_toRightOf="@id/back_btn"/>
//
//<ImageButton
//            android:layout_width="32dp"
//                    android:layout_height="32dp"
//                    android:src="@drawable/ic_search"
//                    android:background="?attr/selectableItemBackgroundBorderless"
//                    app:tint="@color/white"
//                    android:layout_alignParentEnd="true"
//                    android:layout_marginEnd="10dp"
//                    android:id="@+id/main_search_btn"/>
//
//
//<ImageButton
//            android:layout_width="32dp"
//                    android:layout_height="32dp"
//                    android:src="@drawable/ic_back"
//                    android:background="?attr/selectableItemBackgroundBorderless"
//                    app:tint="@color/white"
//                    android:layout_centerVertical="true"
//                    android:layout_marginEnd="10dp"
//                    android:id="@+id/back_btn"/>
//
//</RelativeLayout>
//
//<FrameLayout
//        android:layout_width="match_parent"
//                android:layout_height="match_parent"
//                android:layout_below="@+id/toolbar"
//                android:id="@+id/main_frame_layout"/>
//
//
//
//
//
//</RelativeLayout>