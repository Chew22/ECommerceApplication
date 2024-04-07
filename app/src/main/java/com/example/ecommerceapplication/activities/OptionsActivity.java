package com.example.ecommerceapplication.activities;


import static androidx.fragment.app.FragmentManager.TAG;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import com.example.ecommerceapplication.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class OptionsActivity extends AppCompatActivity {

    private Switch dayNightSwitch;
    private static final String PREFS_NAME = "MyPrefs";
    private static final String THEME_KEY = "theme";
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int savedTheme = sharedPreferences.getInt(THEME_KEY, AppCompatDelegate.MODE_NIGHT_NO);
        AppCompatDelegate.setDefaultNightMode(savedTheme);

        setContentView(R.layout.activity_options);

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Options");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        // Initialize views
        TextView logout = findViewById(R.id.logout);
        TextView updateEmailBtn = findViewById(R.id.updateEmailMenu);
        TextView deleteAccountBtn = findViewById(R.id.delete_account_menu);

        // Initialize day/night switch
        dayNightSwitch = findViewById(R.id.dayNightSwitch);

        dayNightSwitch.setChecked(savedTheme == AppCompatDelegate.MODE_NIGHT_YES);

        dayNightSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int mode = isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO;
                sharedPreferences.edit().putInt(THEME_KEY, mode).apply();
                AppCompatDelegate.setDefaultNightMode(mode);
                recreate(); // Restart the activity to apply the new theme
            }
        });


        // Set click listener for logout option
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Sign out the user and navigate to the login activity
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(OptionsActivity.this, LoginActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        // Set click listener for updating email option
        updateEmailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Display a dialog to enter a new email address
                showUpdateEmailDialog();
            }
        });

        // Set click listener for deleting account option
        deleteAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Display a confirmation dialog for deleting the account
                showDeleteAccountConfirmationDialog();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    // Method to show a dialog for updating the email address
    private void showUpdateEmailDialog() {
        // Inflate the dialog layout
        View view = LayoutInflater.from(this).inflate(R.layout.reset_pop, null);

        // Create an AlertDialog
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setView(view)
                .setTitle("Update Email")
                .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Handle the email update process
                        handleEmailUpdate(view);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // Method to handle updating the email address
    private void handleEmailUpdate(View view) {
        EditText emailEditText = view.findViewById(R.id.reset_email_pop);
        String newEmail = emailEditText.getText().toString().trim();

        if (newEmail.isEmpty()) {
            emailEditText.setError("Email is required");
            return;
        }

        // Get the current user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Build action code settings for updating email
            ActionCodeSettings actionCodeSettings = ActionCodeSettings.newBuilder()
                    .setUrl("https://www.example.com/?email=" + newEmail)
                    .setHandleCodeInApp(true)
                    .setIOSBundleId("com.example.ios")
                    .setAndroidPackageName("com.example.android", true, "12")
                    .build();

            // Send verification email for updating email address
            user.verifyBeforeUpdateEmail(newEmail, actionCodeSettings)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            // Verification email sent successfully
                            Toast.makeText(OptionsActivity.this, "Verification email sent to new address", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Failed to send verification email
                            Toast.makeText(OptionsActivity.this, "Failed to send verification email: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    // Method to show a confirmation dialog for deleting the account
    private void showDeleteAccountConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Account Deletion")
                .setMessage("Are you sure you want to delete your account?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Call method to delete the user account
                        deleteAccount();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    // Method to delete the user account
    private void deleteAccount() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            // Account deletion successful
                            Log.d(TAG, "User account deleted.");
                            Toast.makeText(OptionsActivity.this, "Account deleted successfully", Toast.LENGTH_SHORT).show();
                            // Navigate to login activity
                            startActivity(new Intent(OptionsActivity.this, LoginActivity.class)
                                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Failed to delete user account
                            Log.w(TAG, "Failed to delete user account.", e);
                            Toast.makeText(OptionsActivity.this, "Failed to delete account: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
