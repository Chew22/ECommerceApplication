package com.example.ecommerceapplication.models;

import java.io.Serializable;
import java.util.List;

public class PostModel implements Serializable {

    List<Integer> colors;
    double offerPercentage;
    double price;
    String productCategory;
    String productDescription;
    String productId;
    String productImages;
    String productName;
    List<String> sizes;
    String publisher;
    String rating;

    public PostModel() {
    }

    public PostModel(List<Integer> colors, double offerPercentage, double price, String productCategory, String productDescription, String productId, String productImages, String productName, List<String> sizes, String publisher, String rating) {
        this.colors = colors;
        this.offerPercentage = offerPercentage;
        this.price = price;
        this.productCategory = productCategory;
        this.productDescription = productDescription;
        this.productId = productId;
        this.productImages = productImages;
        this.productName = productName;
        this.sizes = sizes;
        this.publisher = publisher;
        this.rating = rating;
    }

    public List<Integer> getColors() {
        return colors;
    }

    public void setColors(List<Integer> colors) {
        this.colors = colors;
    }

    public double getOfferPercentage() {
        return offerPercentage;
    }

    public void setOfferPercentage(double offerPercentage) {
        this.offerPercentage = offerPercentage;
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

    public String getProductImages() {
        return productImages;
    }

    public void setProductImages(String productImages) {
        this.productImages = productImages;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public List<String> getSizes() {
        return sizes;
    }

    public void setSizes(List<String> sizes) {
        this.sizes = sizes;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }
}
