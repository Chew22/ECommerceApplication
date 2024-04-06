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

        String itemId = String.valueOf(itemIds.get(position));
        // Retrieve item data from Firestore based on item ID
        db.collection("Order")
                .document(auth.getCurrentUser().getUid())
                .collection("Items")
                .document(itemId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        ItemsModel item = documentSnapshot.toObject(ItemsModel.class);
                        if (item != null) {
                            holder.product_name.setText("Product Name: " + item.getProductName());
                            holder.product_price.setText("Product Price: " + item.getProductPrice());
                            holder.total_quantity.setText("Total Quantity: " + item.getTotalQuantity());
                            holder.total_price.setText("Total Price: " + item.getTotalPrice());

                            Glide.with(context)
                                    .load(item.getProductImage())
                                    .placeholder(R.drawable.placeholder)
                                    .into(holder.product_image);
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error getting item document", e));
    }

    @Override
    public int getItemCount() {
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
