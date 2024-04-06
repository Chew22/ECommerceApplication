package com.example.ecommerceapplication.activities;


import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.ecommerceapplication.R;
import com.example.ecommerceapplication.adapters.SliderAdapter;

public class OnBoardActivity extends AppCompatActivity {

    // Declare variables
    ViewPager viewPager;
    LinearLayout dotsLayout;
    Button btn, nextBtn;
    SliderAdapter sliderAdapter;
    TextView[] dots;
    Animation animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Hide status bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_on_board);

        // Hide toolbar
        //getSupportActionBar().hide();

        // Initialize views
        viewPager = findViewById(R.id.slider);
        dotsLayout = findViewById(R.id.dots);
        btn = findViewById(R.id.get_started_btn);
        nextBtn = findViewById(R.id.next_btn);

        // Add dots
        addDots(0);

        // Set up ViewPager listener
        viewPager.addOnPageChangeListener(changeListener);

        // Set up SliderAdapter
        sliderAdapter = new SliderAdapter(this);
        viewPager.setAdapter(sliderAdapter);

        // Set up OnClickListener for the Lets get Started button
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                // Start RegistrationActivity and finish current activity
                startActivity(new Intent(OnBoardActivity.this, MainActivity.class));
                finish();
            }
        });

        // Set up OnClickListener for the Next Arrow button
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Move to the next slide
                int nextSlideIndex = viewPager.getCurrentItem() + 1;
                viewPager.setCurrentItem(nextSlideIndex, true);
            }
        });
    }

    // Method to add dots
    private void addDots(int position){
        dots = new TextView[3];
        dotsLayout.removeAllViews();
        for (int i =0; i < dots.length; i++){
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226"));
            dots[i].setTextSize(35);
            dotsLayout.addView(dots[i]);
        }
        if(dots.length > 0){
            dots[position].setTextColor(getResources().getColor(R.color.green));
        }
    }

    // ViewPager listener
    ViewPager.OnPageChangeListener changeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            // Not used
        }

        @Override
        public void onPageSelected(int position) {
            // Update dots
            addDots(position);
            // Hide or show button based on position
            if (position == 0 || position == 1) {
                btn.setVisibility(View.INVISIBLE);
            } else {
                // Apply animation and show button
                animation = AnimationUtils.loadAnimation(OnBoardActivity.this, R.anim.slide_animation);
                btn.setAnimation(animation);
                btn.setVisibility(View.INVISIBLE);
                // Set listener to the animation
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        // Animation started
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        // Animation ended, make the button visible
                        btn.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                        // Animation repeated
                    }
                });
            }
        }



        @Override
        public void onPageScrollStateChanged(int state) {
            // Not used
        }
    };

}