package com.example.ecommerceapplication.models;


public class SellerModel implements Identifiable{

    private String shopName;
    private String halalCertificatePath;
    private String businessreg;
    private String address;
    private String email;
    private String imagePath;
    private String sellerID;

    public SellerModel() {
    }

    public SellerModel(String shopName, String halalCertificatePath, String businessreg, String address, String email, String imagePath, String sellerID) {
        this.shopName = shopName;
        this.halalCertificatePath = halalCertificatePath;
        this.businessreg = businessreg;
        this.address = address;
        this.email = email;
        this.imagePath = imagePath;
        this.sellerID = sellerID;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getHalalCertificatePath() {
        return halalCertificatePath;
    }

    public void setHalalCertificatePath(String halalCertificatePath) {
        this.halalCertificatePath = halalCertificatePath;
    }

    public String getBusinessreg() {
        return businessreg;
    }

    public void setBusinessreg(String businessreg) {
        this.businessreg = businessreg;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getSellerID() {
        return sellerID;
    }

    public void setSellerID(String sellerID) {
        this.sellerID = sellerID;
    }

    @Override
    public String getId() {
        return sellerID;
    }
}
