package com.example.ecommerceapplication.models;

public class AddressModel {

    boolean isSelected;
    String addressLane;
    String city;
    String fullAddress;
    String name;
    String phoneNumber;
    String postalCode;

    public AddressModel() {
    }

    public AddressModel(boolean isSelected, String addressLane, String city, String fullAddress, String name, String phoneNumber, String postalCode) {
        this.isSelected = isSelected;
        this.addressLane = addressLane;
        this.city = city;
        this.fullAddress = fullAddress;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.postalCode = postalCode;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getAddressLane() {
        return addressLane;
    }

    public void setAddressLane(String addressLane) {
        this.addressLane = addressLane;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
}




