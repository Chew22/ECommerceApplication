package com.example.ecommerceapplication.models;

public class UserModel implements Identifiable {

    private String id;
    private String profileImg;
    private String username;
    private String email;
    private String phone;
    private String bio;

    public UserModel() {
    }

    public UserModel(String id, String username, String email, String phone) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.phone = phone;
    }

    public UserModel(String id, String profileImg, String username, String email, String phone, String bio) {
        this.id = id;
        this.profileImg = profileImg;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.bio = bio;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProfileImg() {
        return profileImg;
    }

    public void setProfileImg(String profileImg) {
        this.profileImg = profileImg;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}
