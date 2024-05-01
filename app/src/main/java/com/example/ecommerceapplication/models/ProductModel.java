package com.example.ecommerceapplication.models;

import java.util.List;

public interface ProductModel {
    List<String> getSizes();
    List<Integer> getColors();
    List<String> getProductImages();
    String getSellerID();
}
