package com.example.ecommerceapplication.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapplication.R;
import com.example.ecommerceapplication.models.OrderModel;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private Context context;
    private List<OrderModel> list;

    public OrderAdapter(Context context, List<OrderModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public OrderAdapter.OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderModel order = list.get(position);

        holder.order_placed_date.setText("Order Placed Date: " + order.getOrderDate());
        holder.order_status.setText("Order Placed Date: " + order.getOrderStatus());
        holder.total_amount.setText("Total Amount: RM " + order.getOrderTotal());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        
        TextView order_placed_date;
        TextView order_status;
        TextView total_amount;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);

            order_placed_date = itemView.findViewById(R.id.order_placed_date);
            order_status = itemView.findViewById(R.id.order_status);
            total_amount = itemView.findViewById(R.id.total_amount);
        }
        
    }
}
