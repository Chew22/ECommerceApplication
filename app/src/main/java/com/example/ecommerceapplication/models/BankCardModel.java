package com.example.ecommerceapplication.models;

// Bank card model representing a typical credit/debit card
public class BankCardModel {

    // Attributes of a bank card
    private String cardholderName;
    private String cardNumber;
    private String expiryDate; // e.g., MM/YY format
    private String cvv;

    // Default constructor (required for Firebase serialization)
    public BankCardModel() {}

    // Constructor with parameters
    public BankCardModel(String cardholderName, String cardNumber, String expiryDate, String cvv) {
        this.cardholderName = cardholderName;
        this.cardNumber = cardNumber;
        this.expiryDate = expiryDate;
        this.cvv = cvv;
    }

    // Getters and Setters for the attributes
    public String getCardholderName() {
        return cardholderName;
    }

    public void setCardholderName(String cardholderName) {
        this.cardholderName = cardholderName;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

}
