package com.dunkin.customer.models;

import java.io.Serializable;

/**
 * Created by Android2 on 9/3/2015.
 */
public class PaymentModel implements Serializable {

    private int paymentId;
    private String paymentType;

    public PaymentModel(int paymentId, String paymentType) {
        this.paymentId = paymentId;
        this.paymentType = paymentType;
    }

    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    @Override
    public String toString() {
        return paymentType;
    }
}
