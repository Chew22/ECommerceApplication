package com.example.ecommerceapplication.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ecommerceapplication.R;
import com.example.ecommerceapplication.activities.PostActivity;
import com.example.ecommerceapplication.models.CategoryModel;

import java.util.List;

// Adapter class to bind data of Category to the RecyclerView
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private Context context;
    private List<CategoryModel> list;

    // Constructor to initialize the adapter with context and list of categories
    public CategoryAdapter(Context context, List<CategoryModel> list) {
        this.context = context;
        this.list = list;
    }

    // Create ViewHolder
    @NonNull
    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each item in the RecyclerView
        View view = (LayoutInflater.from(parent.getContext()).inflate(R.layout.category_list, parent, false));
        return new ViewHolder(view);
    }

    // Bind data to each ViewHolder
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Load image using Glide library
        Glide.with(context).load(list.get(position).getImg_url()).into(holder.catImg);
        // Set category name
        holder.catName.setText(list.get(position).getName());

        // Set click listener for item view
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start ShowAllActivity and pass type data as an extra
                Intent intent = new Intent(context, PostActivity.class);
                intent.putExtra("type",list.get(position).getType());
                context.startActivity(intent);
            }
        });
    }

    // Return the size of the data list
    @Override
    public int getItemCount() {
        return list.size();
    }

    // Hold references to views
    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView catImg;
        TextView catName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize views
            catImg = itemView.findViewById(R.id.cat_img);
            catName = itemView.findViewById(R.id.cat_name);

        }
    }
}
