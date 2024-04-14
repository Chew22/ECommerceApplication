package com.example.ecommerceapplication.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.ecommerceapplication.R;

import java.util.ArrayList;
import java.util.List;

public class DetailedImageSliderAdapter extends PagerAdapter {

    private Context context;
    private List<SlideModel> slideModels;

    public DetailedImageSliderAdapter(Context context, List<String> imageUrls) {
        this.context = context;
        this.slideModels = new ArrayList<>();
        for (String imageUrl : imageUrls) {
            slideModels.add(new SlideModel(imageUrl, "", ScaleTypes.CENTER_CROP));
        }
    }

    @Override
    public int getCount() {
        return slideModels.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.image_slider_item, container, false);
        ImageView imageView = view.findViewById(R.id.imageView);
        Glide.with(context).load(slideModels.get(position).getImageUrl()).centerCrop().into(imageView);

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return slideModels.get(position).getTitle();
    }
}
