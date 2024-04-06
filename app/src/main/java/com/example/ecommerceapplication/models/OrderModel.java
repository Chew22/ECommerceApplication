package com.example.ecommerceapplication.models;

public class OrderModel {
    private String orderDate;
    private String orderStatus;
    private String orderTime;
    private double orderTotal;

    // Default constructor (required for Firebase)
    public OrderModel() {
    }

    // Parameterized constructor
    public OrderModel(String orderDate, String orderStatus, String orderTime, double orderTotal) {
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.orderTime = orderTime;
        this.orderTotal = orderTotal;
    }

    // Getters and setters
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
