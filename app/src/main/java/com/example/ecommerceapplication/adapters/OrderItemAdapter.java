package com.example.ecommerceapplication.adapters;

import static androidx.fragment.app.FragmentManager.TAG;

import android.content.Context;
import android.util.Log;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.List;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.OrderItemViewHolder> {

    private Context context;
    private List<ItemsModel> itemIds;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();

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
        // Reset views
        holder.product_name.setText("");
        holder.product_price.setText("");
        holder.total_quantity.setText("");
        holder.total_price.setText("");
        holder.product_image.setImageResource(0); // or set a placeholder image

        // Bind new data to views
        ItemsModel item = itemIds.get(position);
        holder.product_name.setText(item.getProductName());
        holder.product_price.setText(item.getProductPrice());
        holder.total_quantity.setText(item.getTotalQuantity());
        holder.total_price.setText(String.valueOf(item.getTotalPrice()));

        // Load image using Glide
        Glide.with(context)
                .load(item.getProductImage())
                .placeholder(R.drawable.placeholder)
                .into(holder.product_image);
    }



    @Override
    public int getItemCount() {
        Log.d(TAG, "Item count: " + itemIds.size()); // Log the size of the list
        return itemIds.size();
    }

    public static class OrderItemViewHolder extends RecyclerView.ViewHolder {
        TextView total_quantity, product_name, product_price, total_price;
        ImageView product_image;

        public OrderItemViewHolder(@NonNull View itemView) {
            super(itemView);
            total_quantity = itemView.findViewById(R.id.total_quantity);
            product_name = itemView.findViewById(R.id.product_name);
            product_price = itemView.findViewById(R.id.product_price);
            total_price = itemView.findViewById(R.id.total_price);
            product_image = itemView.findViewById(R.id.product_image);
        }
    }
}
