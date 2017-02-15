package com.dunkin.customer.models;

import com.dunkin.customer.Utils.AppUtils;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Admin on 3/5/2016.
 */
public class OfferHistoryModel implements Serializable {

    private int offerId;
    private String offerTitle;
    private String offerImage;
    @JsonFormat(pattern = "MM-dd-yyyy")
    private Date purchase_date;
    private String reference_id;
    private int pay_option;
    private String country_id;

    public String getReference_id() {
        return reference_id;
    }

    public void setReference_id(String reference_id) {
        this.reference_id = reference_id;
    }

    public int getOfferId() {
        return offerId;
    }

    public void setOfferId(int offerId) {
        this.offerId = offerId;
    }

    public String getOfferTitle() {
        return offerTitle;
    }

    public void setOfferTitle(String offerTitle) {
        this.offerTitle = offerTitle;
    }

    public String getOfferImage() {
        return offerImage;
    }

    public void setOfferImage(String offerImage) {
        this.offerImage = offerImage;
    }

    public Date getPurchase_date() {
        return purchase_date;
    }

    public void setPurchase_date(Date purchase_date) {
        this.purchase_date = purchase_date;
    }

    public String getPurchaseConverted_date() {
        return AppUtils.getFormattedDate((getPurchase_date()));
    }

    public int getPay_option() {
        return pay_option;
    }

    public void setPay_option(int pay_option) {
        this.pay_option = pay_option;
    }

    public String getCountry_id() {
        return country_id;
    }

    public void setCountry_id(String country_id) {
        this.country_id = country_id;
    }


}
