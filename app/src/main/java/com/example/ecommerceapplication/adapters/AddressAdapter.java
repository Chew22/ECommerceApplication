package com.example.ecommerceapplication.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapplication.R;
import com.example.ecommerceapplication.models.AddressModel;

import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {

    // Declare variables
    Context context;
    List<AddressModel> addressModelList;
    SelectAddress selectAddress;
    RadioButton selectedRadioBtn;

    // Constructor
    public AddressAdapter(Context context, List<AddressModel> addressModelList, SelectAddress selectAddress) {
        this.context = context;
        this.addressModelList = addressModelList;
        this.selectAddress = selectAddress;
    }

    // Create view holder
    @NonNull
    @Override
    public AddressAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout for each item
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.address_item, parent, false));
    }

    // Bind data to views
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Set address text
        holder.address.setText(addressModelList.get(position).getUserAddress());
        // Set click listener for radio button
        holder.radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Unselect all addresses
                for (AddressModel address:addressModelList){
                    address.setSelected(false);
                }
                // Select clicked address
                addressModelList.get(position).setSelected(true);

                // Uncheck previously selected radio button
                if (selectedRadioBtn != null){
                    selectedRadioBtn.setChecked(false);
                }
                // Check the clicked radio button
                selectedRadioBtn = (RadioButton) v;
                selectedRadioBtn.setChecked(true);
                // Notify activity of selected address
                selectAddress.setAddress(addressModelList.get(position).getUserAddress());
            }
        });

    }

    // Return total number of items
    @Override
    public int getItemCount() {
        return addressModelList.size();
    }

    // ViewHolder class to hold inflated views
    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView address;
        RadioButton radioButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize views
            address = itemView.findViewById(R.id.address_add);
            radioButton = itemView.findViewById(R.id.select_address);

        }
    }

    // Interface to communicate selected address to activity
    public interface SelectAddress {
        void setAddress(String address);
    }
}
