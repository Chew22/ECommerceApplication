package com.example.ecommerceapplication.models;

public class ItemsModel {
    private String productName;
    private String productImage;
    private String productPrice;
    private String totalQuantity;
    private double totalPrice;

    // Default constructor (required for Firebase)
    public ItemsModel() {
    }

    // Parameterized constructor
    public ItemsModel(String productName, String productImage, String productPrice, String totalQuantity, double totalPrice) {
        this.productName = productName;
        this.productImage = productImage;
        this.productPrice = productPrice;
        this.totalQuantity = totalQuantity;
        this.totalPrice = totalPrice;
    }

    // Getters and setters
    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
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

