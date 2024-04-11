package com.example.ecommerceapplication.models;

import java.io.Serializable;

public class OrderModel implements Serializable {
    private String orderId;
    private String orderDate;
    private String orderStatus;
    private String orderTime;
    private double orderTotal;

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
