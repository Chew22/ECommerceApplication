package com.example.ecommerceapplication.adapters;

import android.content.Context;
import android.util.Log;
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

    private static final String TAG = "OrderAdapter";

    private Context context;
    private List<OrderModel> list;
    private String orderId;

    public interface OnOrderItemClickListener {
        void onOrderItemClick(OrderModel order);
    }

    private OnOrderItemClickListener clickListener;

    public void setOnOrderItemClickListener(OnOrderItemClickListener listener) {
        this.clickListener = listener;
    }

    public OrderAdapter(Context context, List<OrderModel> list, String orderId) {
        this.context = context;
        this.list = list;
        this.orderId = orderId;
    }


    // Constructor with orderId parameter
    public OrderAdapter(Context context, List<OrderModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_list_item, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        // Bind new data to views
        OrderModel order = list.get(position);

            holder.order_id.setText(order.getOrderId());
            holder.order_placed_date.setText(order.getOrderDate());
            holder.order_status.setText(order.getOrderStatus());
            holder.total_amount.setText("RM " + order.getOrderTotal());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (clickListener != null) {
                        clickListener.onOrderItemClick(order);
                    }
                }
            });

    }


    public void addOrder(OrderModel order) {
        list.add(order);
        notifyItemInserted(list.size() - 1);
    }



    @Override
    public int getItemCount() {
        Log.d(TAG, "Item count: " + list.size()); // Log the size of the list
        return list.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView order_id, order_placed_date, order_status, total_amount;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            order_id = itemView.findViewById(R.id.order_id);
            order_placed_date = itemView.findViewById(R.id.order_placed_date);
            order_status = itemView.findViewById(R.id.order_status);
            total_amount = itemView.findViewById(R.id.total_amount);
        }
    }
}
