package com.example.ecommerceapplication.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ecommerceapplication.R;

import java.util.List;

public class ProductDetailAdapter extends RecyclerView.Adapter<ProductDetailAdapter.ViewHolder> {
    private List<ProductDetailModel> productDetails; // List of product details
    private Context context; // Context for Glide and layout inflation

    public ProductDetailAdapter(Context context, List<ProductDetailModel> productDetails) {
        this.context = context;
        this.productDetails = productDetails;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_detailed, parent, false);
        return new ViewHolder(view); // Inflate layout for each item in the detailed page
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductDetailModel productDetail = productDetails.get(position); // Get the current product detail

        // Bind product name
        holder.productName.setText(productDetail.getProductName());

        // Bind product description
        holder.productDescription.setText(productDetail.getDescription());

        // Bind product image using Glide
        Glide.with(context).load(productDetail.getProductImage()).into(holder.productImage);

        // Check and display additional attributes, e.g., size and color
        if (productDetail.getSize() != null && !productDetail.getSize().trim().isEmpty() ) {
            holder.productSize.setVisibility(View.VISIBLE);
            holder.productSize.setText("Size: " + productDetail.getSize());
        } else {
            holder.productSize.setVisibility(View.GONE); // Hide if no size
        }

        if (productDetail.getColor() > 0) {
            holder.productColor.setVisibility(View.VISIBLE);
            holder.productColor.setText("Color: " + productDetail.getColor()); // Assuming color is an integer
        } else {
            holder.productColor.setVisibility(View.GONE); // Hide if color is 0
        }

        // Additional bindings for other product attributes...
    }

    @Override
    public int getItemCount() {
        return productDetails.size(); // Number of items to display
    }

    // ViewHolder class to bind views
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productDescription, productSize, productColor; // Product details
        ImageView productImage; // Product image

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // Initialize views
            productName = itemView.findViewById(R.id.product_name);
            productDescription = itemView.findViewById(R.id.description);
            productSize = itemView.findViewById(R.id.product_size);
            productColor = itemView.findViewById(R.id.product_color);
            productImage = itemView.findViewById(R.id.product_image);
        }
    }
}
