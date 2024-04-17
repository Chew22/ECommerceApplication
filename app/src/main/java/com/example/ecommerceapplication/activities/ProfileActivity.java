package com.example.ecommerceapplication.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ecommerceapplication.R;
import com.example.ecommerceapplication.fragments.ProfileFragment;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Get the seller ID from the intent
        String sellerId = getIntent().getStringExtra("sellerId");

        // Create a new instance of ProfileFragment and pass the seller ID
        ProfileFragment profileFragment = new ProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putString("sellerId", sellerId);
        profileFragment.setArguments(bundle);

        // Start the ProfileFragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.profile_container, profileFragment)
                .commit();
    }
}
