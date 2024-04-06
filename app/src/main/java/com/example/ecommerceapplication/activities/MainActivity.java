package com.example.ecommerceapplication.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.ecommerceapplication.R;
import com.example.ecommerceapplication.fragments.ChatFragment;
import com.example.ecommerceapplication.fragments.HomeFragment;
import com.example.ecommerceapplication.fragments.NotificationFragment;
import com.example.ecommerceapplication.fragments.ProfileFragment;
import com.example.ecommerceapplication.fragments.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    Fragment homeFragment;
    Toolbar toolbar;
    BottomNavigationView bottomNavigationView;

    // Chat Page
    ChatFragment chatFragment;

    // Currently selected fragment
    Fragment selectedFragment = null;

    // Firebase
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initializing FirebaseAuth instance
        auth = FirebaseAuth.getInstance();

        // Setting up the toolbar
        toolbar = findViewById(R.id.home_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Loading the HomeFragment by default
        homeFragment = new HomeFragment();
        loadFragment(homeFragment);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        // Chat Fragement
        chatFragment = new ChatFragment();

        // Checking if there is any intent extras
        Bundle intent = getIntent().getExtras();
        if (intent != null){
            String publisher = intent.getString("publisher");
            // Saving the publisher ID to SharedPreferences
            SharedPreferences.Editor editor = getSharedPreferences("PREPS", MODE_PRIVATE).edit();
            editor.putString("profileid", publisher);
            editor.apply();
            // Replace the fragment container with a new instance of HomeFragment
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new HomeFragment()).commit();
        }else {
            // Replace the fragment container with a new instance of HomeFragment
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new HomeFragment()).commit();
        }
    }

    // Bottom navigation item selection listener
    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            // Handle each bottom navigation item separately
            if (item.getItemId() == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            }  else if (item.getItemId() == R.id.nav_chat) {
                selectedFragment = new ChatFragment();
            } else if (item.getItemId() == R.id.nav_search) {
                selectedFragment = new SearchFragment();
            } else if (item.getItemId() == R.id.nav_notification) {
                selectedFragment = new NotificationFragment();
                Log.d("Navigation", "Notification fragment selected");
            } else if (item.getItemId() == R.id.nav_profile) {
                // Saving the current user's profile ID to SharedPreferences
                SharedPreferences.Editor editor = getSharedPreferences("PREPS", MODE_PRIVATE).edit();
                editor.putString("profileid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                editor.apply();
                selectedFragment = new ProfileFragment();
            }
            // Load the selected fragment into the activity
            if (selectedFragment != null){
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
            }
            return true;
        }


    };

    // Method to load a fragment into the activity
    private void loadFragment(Fragment homeFragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.home_container, homeFragment);
        transaction.commit();
    }

    // Method to create options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    // Method to handle menu item selection
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_logout){
            // Signing out the user and redirecting to the registration activity
            auth.signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        } else if (id == R.id.menu_my_cart) {
            // Redirecting to the cart activity
            startActivity(new Intent(MainActivity.this, CartActivity.class));
        }
        return true;
    }


}

