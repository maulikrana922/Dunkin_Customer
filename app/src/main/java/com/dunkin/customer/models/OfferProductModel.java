package com.dunkin.customer.models;

import java.io.Serializable;

/**
 * Created by Android2 on 9/11/2015.
 */
public class OfferProductModel implements Serializable {

    private int offerproductid;
    private OfferProductDetailModel offeronproduct;
    private OfferProductDetailModel offerfreeproduct;

    public int getOfferproductid() {
        return offerproductid;
    }

    public void setOfferproductid(int offerproductid) {
        this.offerproductid = offerproductid;
    }

    public OfferProductDetailModel getOfferonproduct() {
        return offeronproduct;
    }

    public void setOfferonproduct(OfferProductDetailModel offeronproduct) {
        this.offeronproduct = offeronproduct;
    }

    public OfferProductDetailModel getOfferfreeproduct() {
        return offerfreeproduct;
    }

    public void setOfferfreeproduct(OfferProductDetailModel offerfreeproduct) {
        this.offerfreeproduct = offerfreeproduct;
    }
}


