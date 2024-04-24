/*
This adapter is responsible for populating the RecyclerView with items in the user's shopping cart.
*/

package com.example.ecommerceapplication.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ecommerceapplication.R;
import com.example.ecommerceapplication.models.MyCartModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

/**
 * RecyclerView Adapter for displaying items in the user's cart.
 */
public class MyCartAdapter extends RecyclerView.Adapter<MyCartAdapter.ViewHolder>{

    Context context;
    List<MyCartModel> list;
    int totalAmount= 0;

    FirebaseFirestore firestore;
    FirebaseAuth auth;

    // View holder class
    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView name, price, size, color, date, time, totalQuantity, totalPrice;
        Button minusItem, plusItem;
        ImageView delete, image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.product_image);
            name = itemView.findViewById(R.id.product_name);
            size = itemView.findViewById(R.id.product_size);
            color = itemView.findViewById(R.id.product_color);
            price = itemView.findViewById(R.id.product_price);
            date = itemView.findViewById(R.id.current_date);
            time = itemView.findViewById(R.id.current_time);
            totalQuantity = itemView.findViewById(R.id.total_quantity);
            totalPrice = itemView.findViewById(R.id.total_price);
            minusItem = itemView.findViewById(R.id.minus_button);
            plusItem = itemView.findViewById(R.id.plus_button);
            delete = itemView.findViewById(R.id.delete);
        }
    }

    // Create view holder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.my_cart_item,parent,false));
    }

    private TotalAmountListener totalAmountListener;

    public interface TotalAmountListener {
        void onTotalAmountChanged(double totalAmount);
    }


    public MyCartAdapter(Context context, List<MyCartModel> list, TotalAmountListener listener) {
        this.context = context;
        this.list = list;
        this.totalAmountListener = listener;
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }



    // Bind data to the views
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        MyCartModel cartModel = list.get(position);

        holder.date.setText(list.get(position).getCurrentDate());
        holder.time.setText(list.get(position).getCurrentTime());
        holder.size.setText(list.get(position).getSelectedSize());
        holder.color.setText(list.get(position).getSelectedColor());
        holder.price.setText( "RM " + String.format("%.2f", Double.parseDouble(list.get(position).getProductPrice())));
        holder.name.setText(list.get(position).getProductName());
        holder.totalPrice.setText( "RM " + String.format("%.2f", (list.get(position).getTotalPrice())));
        holder.totalQuantity.setText(String.valueOf(list.get(position).getTotalQuantity()));

       Glide.with(context)
                    .load(cartModel.getProductImage())
                    .into(holder.image);

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Build the confirmation dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Confirm Delete");
                builder.setMessage("Are you sure you want to delete this item from the cart?");
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // User clicked the Delete button, proceed with deletion
                        deleteCartItem(position);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // User clicked the Cancel button, dismiss the dialog
                        dialog.dismiss();
                    }
                });

                // Show the dialog
                builder.create().show();
            }
        });


        // OnClickListener for the minus button
        holder.minusItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentQuantity = Integer.parseInt(list.get(position).getTotalQuantity());

                // Check if currentQuantity is greater than 0 before decrementing
                if (currentQuantity > 1) {
                    int newQuantity = currentQuantity - 1;

                    // Update totalQuantity in Firebase
                    firestore.collection("AddToCart").document(auth.getCurrentUser().getUid())
                            .collection("User")
                            .document(list.get(position).getDocumentId())
                            .update("totalQuantity", String.valueOf(newQuantity))
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        // Update the local list and notify adapter
                                        list.get(position).setTotalQuantity(String.valueOf(newQuantity));
                                        notifyItemChanged(position);
                                        calculateTotalAmountAndNotify();
                                        Toast.makeText(context, "Quantity decreased", Toast.LENGTH_SHORT).show();

                                    } else {
                                        Toast.makeText(context, "Error" + task.getException(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(context, "Minimum quantity reached", Toast.LENGTH_SHORT).show();
                }
            }
        });


        holder.plusItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentQuantity = Integer.parseInt(list.get(position).getTotalQuantity());
                int newQuantity = currentQuantity + 1;

                // Update totalQuantity in Firebase
                firestore.collection("AddToCart").document(auth.getCurrentUser().getUid())
                        .collection("User")
                        .document(list.get(position).getDocumentId())
                        .update("totalQuantity", String.valueOf(newQuantity))
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    // Update the local list and notify adapter
                                    list.get(position).setTotalQuantity(String.valueOf(newQuantity));
                                    notifyItemChanged(position);
                                    calculateTotalAmountAndNotify();
                                    Toast.makeText(context, "Quantity increased", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, "Error" + task.getException(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

    }

    private void deleteCartItem(int position) {
        firestore.collection("AddToCart").document(auth.getCurrentUser().getUid())
                .collection("User")
                .document(list.get(position).getDocumentId())
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Item successfully deleted from Firestore
                            // Remove item from the list and update the adapter
                            list.remove(position);
                            calculateTotalAmountAndNotify();
                            Toast.makeText(context, "Item deleted", Toast.LENGTH_SHORT).show();
                        } else {
                            // Error occurred while deleting item
                            Toast.makeText(context, "Error: " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    // Get item count
    @Override
    public int getItemCount() {
        return list.size();
    }

    private void calculateTotalAmountAndNotify() {
        double totalAmount = 0.0;
        for (MyCartModel myCartModel : list) {
            totalAmount += myCartModel.getTotalPrice();
        }
        if (totalAmountListener != null) {
            totalAmountListener.onTotalAmountChanged(totalAmount);
        }

    }


}
