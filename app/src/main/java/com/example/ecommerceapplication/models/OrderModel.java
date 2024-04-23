package com.example.ecommerceapplication.models;

import java.io.Serializable;

public class OrderModel implements Serializable {
    private String orderId;
    private String orderDate;
    private String orderStatus;
    private String orderTime;
    private double orderTotal;
    String colors;
    String sizes;
    String rating = null;
    Double offerPercentage;

    // Default constructor (required for Firebase)
    public OrderModel() {
    }

    public OrderModel(String orderId, String orderDate, String orderStatus, String orderTime, double orderTotal) {
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.orderTime = orderTime;
        this.orderTotal = orderTotal;
    }



    public String getSizes() {
        return sizes != null ? sizes : "none"; // Returns "none" if null
    }

    public void setSizes(String sizes) {
        this.sizes = sizes;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getColors() {
        return colors != null ? colors : "0"; // Returns "none" if null
    }

    public void setColors(String colors) {
        this.colors = colors;
    }

    public Double getOfferPercentage() {
        return offerPercentage;
    }

    public void setOfferPercentage(Double offerPercentage) {
        this.offerPercentage = offerPercentage;
    }


    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public double getOrderTotal() {
        return orderTotal;
    }

    public void setOrderTotal(double orderTotal) {
        this.orderTotal = orderTotal;
    }
}
