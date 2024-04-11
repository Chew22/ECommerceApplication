package com.example.ecommerceapplication.models;

public class MyCartModel {

    String productId;
    String currentTime;
    String currentDate;
    String productName;
    String productPrice;
    String totalQuantity;
    double totalPrice;
    String documentId;
    String productImage;

    public MyCartModel(){

    }

    public MyCartModel(String productId, String currentTime, String currentDate, String productName, String productPrice, String totalQuantity, double totalPrice, String documentId, String productImage) {
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

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
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
