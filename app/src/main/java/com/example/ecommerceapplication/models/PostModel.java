package com.example.ecommerceapplication.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PostModel implements ProductModel, Serializable {

    List<Integer> colors;
    List<String> sizes;
    String rating = null;
    Double offerPercentage;
    private long timestamp;
    double price;
    String productCategory;
    String productDescription;
    String productId;
    List<String> productImages;
    String productName;
    String sellerID;
    String sellerName;
    String shopName;


    public PostModel() {
    }

    public PostModel(List<Integer> colors, int timestamp, Double offerPercentage, double price, String productCategory, String productDescription, String productId, List<String> productImages, String productName, String sellerID, String sellerName, String shopName, List<String> sizes, String rating) {
        this.colors = colors;
        this.timestamp = timestamp;
        this.offerPercentage = offerPercentage;
        this.price = price;
        this.productCategory = productCategory;
        this.productDescription = productDescription;
        this.productId = productId;
        this.productImages = productImages;
        this.productName = productName;
        this.sellerID = sellerID;
        this.sellerName = sellerName;
        this.shopName = shopName;
        this.sizes = sizes;
        this.rating = rating;
    }

    public String getFirstProductImage() {
        if (productImages != null && !productImages.isEmpty()) {
            return productImages.get(0); // Return the first image URL
        }
        return null;
    }

    public void setFirstProductImages(List<String> productImages) {
        this.productImages = productImages;
    }

    // Getter and Setter for timestamp
    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public List<String> getProductImages() {
        return productImages;
    }

    public void setProductImages(List<String> productImages) {
        this.productImages = productImages;
    }

    public String getSellerID() {
        return sellerID;
    }

    public void setSellerID(String sellerID) {
        this.sellerID = sellerID;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }


    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public List<String> getSizes() {
        return sizes != null ? sizes : new ArrayList<>(); // Returns empty list if null
    }

    public void setSizes(List<String> sizes) {
        this.sizes = sizes;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public List<Integer> getColors() {
        return colors != null ? colors : new ArrayList<>(); // Returns empty list if null
    }

    public void setColors(List<Integer> colors) {
        this.colors = colors;
    }

    public Double getOfferPercentage() {
        return offerPercentage;
    }

    public void setOfferPercentage(Double offerPercentage) {
        this.offerPercentage = offerPercentage;
    }
}
