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
import com.example.ecommerceapplication.models.ItemsModel;

import java.util.List;
public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.OrderItemViewHolder> {

    private Context context;
    private List<ItemsModel> itemIds;

    public OrderItemAdapter(Context context, List<ItemsModel> itemIds) {
        this.context = context;
        this.itemIds = itemIds;
    }

    @NonNull
    @Override
    public OrderItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item, parent, false);
        return new OrderItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderItemViewHolder holder, int position) {
        ItemsModel item = itemIds.get(position);

        // Set product name, price, quantity, and total price
        holder.product_name.setText(item.getProductName());
        holder.product_size.setText(item.getSelectedSize());
        holder.product_color.setText(item.getSelectedColor());
        holder.product_price.setText(item.getProductPrice());
        holder.total_quantity.setText(item.getTotalQuantity());
        holder.total_price.setText(String.valueOf(item.getTotalPrice()));

        // Load the first image using Glide
        if (item.getProductImage() != null && !item.getProductImage().isEmpty()) {
            String firstImageUrl = item.getProductImage().get(0); // Get the first image URL
            Glide.with(context)
                    .load(firstImageUrl)
                    .placeholder(R.drawable.placeholder)
                    .into(holder.product_image);
        } else {
            // Handle the case when there are no images
            holder.product_image.setImageResource(R.drawable.placeholder); // or set a placeholder image
        }
    }

    @Override
    public int getItemCount() {
        return itemIds.size();
    }

    public static class OrderItemViewHolder extends RecyclerView.ViewHolder {
        TextView total_quantity, product_name, product_price, total_price, product_size, product_color;
        ImageView product_image;

        public OrderItemViewHolder(@NonNull View itemView) {
            super(itemView);
            total_quantity = itemView.findViewById(R.id.total_quantity);
            product_name = itemView.findViewById(R.id.product_name);
            product_size = itemView.findViewById(R.id.product_size);
            product_color = itemView.findViewById(R.id.product_color);
            product_price = itemView.findViewById(R.id.product_price);
            total_price = itemView.findViewById(R.id.total_price);
            product_image = itemView.findViewById(R.id.product_image);
        }
    }
}
