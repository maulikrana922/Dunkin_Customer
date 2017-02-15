package com.dunkin.customer.models;

import java.io.Serializable;

/**
 * Created by Admin on 7/13/2015.
 */
public class CreditCardModel implements Serializable {

    private int id;
    private String cardNumber;
    private String expiry;
    private String cardHolderName;


    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getExpiry() {
        return expiry;
    }

    public void setExpiry(String expiry) {
        this.expiry = expiry;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
