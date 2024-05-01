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
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class OptionsActivity extends AppCompatActivity {

    private Switch dayNightSwitch;
    private static final String PREFS_NAME = "MyPrefs";
    private static final String THEME_KEY = "theme";
    private SharedPreferences sharedPreferences;
    FirebaseAuth auth;
    GoogleSignInClient mGoogleSignInClient;
    // Inside onCreate method
    TextView changePasswordBtn;
    TextView loginAsSeller;

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
        changePasswordBtn = findViewById(R.id.resetPassword);
        loginAsSeller = findViewById(R.id.loginAsSeller);


        // Initialize day/night switch
        dayNightSwitch = findViewById(R.id.dayNightSwitch);

        // Initialize FirebaseAuth
        auth = FirebaseAuth.getInstance();

        // Initialize GoogleSignInClient
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail().build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        changePasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangePasswordDialog(); // Show the dialog to change password
            }
        });

        dayNightSwitch.setChecked(savedTheme == AppCompatDelegate.MODE_NIGHT_YES);

        dayNightSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Determine the mode based on the switch state
                int mode = isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO;

                // Save the selected mode to SharedPreferences
                sharedPreferences.edit().putInt(THEME_KEY, mode).apply();

                // Apply the selected mode as the default night mode for the app
                AppCompatDelegate.setDefaultNightMode(mode);
                recreate(); // Restart the activity to apply the new theme
            }
        });

        loginAsSeller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show a confirmation dialog before logging out
                new AlertDialog.Builder(OptionsActivity.this)
                        .setTitle("Logout to Login As Seller")
                        .setMessage("Are you sure you want to log out?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Navigate to the seller login activity
                                Intent intent = new Intent(OptionsActivity.this, RegisterActivity.class);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("No", null) // Dismiss the dialog if user cancels
                        .show();
            }
        });


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show a confirmation dialog before logging out
                new AlertDialog.Builder(OptionsActivity.this)
                        .setTitle("Confirm Logout")
                        .setMessage("To login as seller, you need to logout first. Are you sure you want to log out?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // If user confirms, proceed with logout
                                performLogout();
                            }
                        })
                        .setNegativeButton("No", null) // Dismiss the dialog if user cancels
                        .show();
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

    private void showChangePasswordDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.change_password_pop, null); // Create a custom layout for the dialog

        final EditText currentPasswordEditText = view.findViewById(R.id.current_password);
        final EditText newPasswordEditText = view.findViewById(R.id.new_password);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setView(view)
                .setTitle("Change Password")
                .setPositiveButton("Change", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        changePassword(currentPasswordEditText, newPasswordEditText); // Call the function to change password
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void changePassword(EditText currentPasswordEditText, EditText newPasswordEditText) {
        String currentPassword = currentPasswordEditText.getText().toString().trim();
        String newPassword = newPasswordEditText.getText().toString().trim();

        if (currentPassword.isEmpty() || newPassword.isEmpty()) {
            Toast.makeText(this, "Both fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            // Reauthenticate the user
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);

            user.reauthenticate(credential)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // If reauthentication is successful, change the password
                                user.updatePassword(newPassword)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(OptionsActivity.this, "Password changed successfully", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(OptionsActivity.this, "Failed to change password: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            } else {
                                Toast.makeText(OptionsActivity.this, "Reauthentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void performLogout() {
        // Sign out from Firebase Authentication
        auth.signOut();

        // Sign out from Google
        mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(OptionsActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();

                // Redirect to login screen after logging out
                startActivity(new Intent(OptionsActivity.this, LoginActivity.class));
                finish(); // Finish the current activity to prevent back navigation
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
