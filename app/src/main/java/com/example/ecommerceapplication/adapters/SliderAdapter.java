package com.example.ecommerceapplication.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;

import com.example.ecommerceapplication.R;

public class SliderAdapter extends PagerAdapter {

    // Context and LayoutInflater for inflating views
    Context context;
    LayoutInflater layoutInflater;

    // Arrays containing resource IDs for images, headings, and descriptions
    int imagesArray[] = {
            R.drawable.onboardscreen1,
            R.drawable.onboardscreen2,
            R.drawable.onboardscreen3
    };

    int headingArray[] ={
            R.string.first_slide,
            R.string.second_slide,
            R.string.third_slide
    };


    int descriptionArray[] ={
            R.string.description,
            R.string.description,
            R.string.description
    };

    // Constructor to initialize the SliderAdapter with a context
    public SliderAdapter(Context context){
        this.context = context;
    }

    // Get the total number of slides
    @Override
    public int getCount() {
        return headingArray.length;
    }

    /**
     * Determine whether the current view is associated with the object returned by instantiateItem.
     * @param view The view to be associated.
     * @param object The object returned by instantiateItem.
     * @return True if the view is associated with the object, false otherwise.
     */
    // Determine whether the current view is associated with the object returned by instantiateItem
    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (ConstraintLayout) object;
    }

    /**
     * Inflate and add the view to the ViewPager.
     * @param container The parent view that the slide view will be attached to.
     * @param position The position of the view in the ViewPager.
     * @return The instantiated view.
     */
    // Inflate and add the view to the ViewPager
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.sliding_layout,container,false);

        ImageView imageView = view.findViewById(R.id.slider_img);
        TextView heading = view.findViewById(R.id.heading);
        TextView description = view.findViewById(R.id.description);

        imageView.setImageResource(imagesArray[position]);
        heading.setText(headingArray[position]);
        description.setText(descriptionArray[position]);

        container.addView(view);

        return view;
    }

    /**
     * Remove the view from the ViewPager.
     * @param container The parent view from which the view will be removed.
     * @param position The position of the view to be removed.
     * @param object The instantiated view to be removed.
     */
    // Remove the view from the ViewPager
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ConstraintLayout)object);
    }
}
