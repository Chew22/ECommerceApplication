package com.example.ecommerceapplication.models;

import java.util.ArrayList;

public class MyCartModel {

    String productId;
    String currentTime;
    String currentDate;
    String productName;
    String productPrice;
    String totalQuantity;
    double totalPrice;
    String documentId;
    ArrayList<String> productImage;
    String sellerID;
    String selectedColor;
    String selectedSize;
    String rating = null;
    Double offerPercentage;

    public MyCartModel(){

    }

    public MyCartModel(String productId, String currentTime, String currentDate, String productName, String productPrice, String totalQuantity, double totalPrice, String documentId, ArrayList<String> productImage, String sellerID, String selectedColor, String selectedSize, String rating, Double offerPercentage) {
        this.productId = productId;
        this.currentTime = currentTime;
        this.currentDate = currentDate;
        this.productName = productName;
        this.productPrice = productPrice;
        this.totalQuantity = totalQuantity;
        this.totalPrice = totalPrice;
        this.documentId = documentId;
        this.productImage = productImage;
        this.sellerID = sellerID;
        this.selectedColor = selectedColor;
        this.selectedSize = selectedSize;
        this.rating = rating;
        this.offerPercentage = offerPercentage;
    }

    public MyCartModel(String productId, String currentTime, String currentDate, String productName, String productPrice, String totalQuantity, double totalPrice, String documentId, ArrayList<String> productImage) {
        this.productId = productId;
        this.currentTime = currentTime;
        this.currentDate = currentDate;
        this.productName = productName;
        this.productPrice = productPrice;
        this.totalQuantity = totalQuantity;
        this.totalPrice = totalPrice;
        this.documentId = documentId;
        this.productImage = productImage;
    }


    public String getSelectedSize() {
        return selectedSize;
    }

    public void setSelectedSize(String selectedSize) {
        this.selectedSize = selectedSize;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getSelectedColor() {
        return selectedColor;
    }

    public void setSelectedColor(String selectedColor) {
        this.selectedColor = selectedColor;
    }

    public Double getOfferPercentage() {
        return offerPercentage;
    }

    public void setOfferPercentage(Double offerPercentage) {
        this.offerPercentage = offerPercentage;
    }

    public String getSellerID() {
        return sellerID;
    }

    public void setSellerID(String sellerID) {
        this.sellerID = sellerID;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getFirstProductImage() {
        if (productImage != null && !productImage.isEmpty()) {
            return productImage.get(0); // Return the first image URL
        }
        return null;
    }

    public ArrayList<String> getProductImage() {
        return productImage;
    }

    public void setProductImage(ArrayList<String> productImage) {
        this.productImage = productImage;
    }


    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
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
        // Parse productPrice and totalQuantity to double
        double price = Double.parseDouble(getProductPrice());
        double quantity = Double.parseDouble(getTotalQuantity());

        // Calculate total price by multiplying productPrice with totalQuantity
        return price * quantity; }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }


}
