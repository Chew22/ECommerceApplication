/*
This adapter is responsible for populating the RecyclerView with items in the "Show All" section.
*/

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
import com.example.ecommerceapplication.activities.DetailedActivity;
import com.example.ecommerceapplication.models.PostModel;

import java.util.List;

public class ShowAllAdapter extends RecyclerView.Adapter<ShowAllAdapter.ViewHolder> {

    private Context context;
    private List<PostModel> list;

    public ShowAllAdapter(Context context, List<PostModel> list) {
        this.context = context;
        this.list = list;
    }

    // Creating view holder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.show_all_item,parent,false));
    }

    // Binding data to the views
    @Override
    public void onBindViewHolder(@NonNull ShowAllAdapter.ViewHolder holder, int position) {
        // Loading item image using Glide
        Glide.with(context).load(list.get(position).getFirstProductImage()).into(holder.mItemImage);
        holder.mCost.setText("RM " + list.get(position).getPrice());
        holder.mName.setText(list.get(position).getProductName());

        // Handling item click to navigate to detailed activity
        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(context, DetailedActivity.class);
                intent.putExtra("detailed",list.get(position));
                context.startActivity(intent);
            }
        });
    }

    // Getting item count
    @Override
    public int getItemCount() {
        return list.size();
    }

    // View holder class
    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView mItemImage;
        private TextView mCost;
        private TextView mName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mItemImage = itemView.findViewById(R.id.item_image);
            mCost = itemView.findViewById(R.id.item_cost);
            mName = itemView.findViewById(R.id.item_name);
        }
    }
}
