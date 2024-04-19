package com.example.ecommerceapplication.models;

import java.util.ArrayList;

public class ItemsModel {
    private String itemId;
    private String productName;
    private ArrayList<String> productImage;
    private String productPrice;
    private String totalQuantity;
    private double totalPrice;

    public ItemsModel() {
    }

    public ItemsModel(String itemId, String productName, ArrayList<String> productImage, String productPrice, String totalQuantity, double totalPrice) {
        this.itemId = itemId;
        this.productName = productName;
        this.productImage = productImage;
        this.productPrice = productPrice;
        this.totalQuantity = totalQuantity;
        this.totalPrice = totalPrice;
    }

    // Method to get the first image URL
    public String getFirstProductImage() {
        if (productImage != null && !productImage.isEmpty()) {
            return productImage.get(0); // Return the first image URL
        } else {
            return null; // Or handle the case when there are no images
        }
    }

    public ArrayList<String> getProductImage() {
        return productImage;
    }

    public void setProductImage(ArrayList<String> productImage) {
        this.productImage = productImage;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(String totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
}

