package com.example.ecommerceapplication.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ecommerceapplication.R;
import com.shuhart.stepview.StepView;

import java.util.Arrays;

public class TrackingActivity extends AppCompatActivity {

    int currentStep = 2; // Change this to the step number you want to move to

    private StepView stepView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);

        // Get the total amount passed from PaymentActivity
        double totalAmount = getIntent().getDoubleExtra("amount", 0.0);

        stepView = findViewById(R.id.step_view);
        setupStepView();

        // Define your steps (e.g., order statuses)
        String[] orderStatuses = {"Order Placed", "Packing", "Delivering", "Delivered"};

        // Set the steps in the StepView
        stepView.setSteps(Arrays.asList(orderStatuses));
        stepView.go(currentStep, true); // true for animation

        // Click listener to handle step clicks
        stepView.setOnStepClickListener(new StepView.OnStepClickListener() {
            @Override
            public void onStepClick(int step) {
                // Handle step click event
            }
        });
    }

    private void setupStepView() {
        // Set the number of steps in your order process
        stepView.setStepsNumber(4); // Example: 4 steps

        // Set the current step in the order process
        stepView.go(2, true); // Example: Current step is 2 (0-based index)

        // Customize step titles (optional)
        stepView.getState()
                .animationType(StepView.ANIMATION_CIRCLE)
                .animationDuration(getResources().getInteger(android.R.integer.config_shortAnimTime))
                .commit();
    }

}
