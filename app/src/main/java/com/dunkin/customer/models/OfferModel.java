package com.dunkin.customer.models;

import com.dunkin.customer.constants.AppConstants;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by Admin on 7/7/2015.
 */
public class OfferModel implements Serializable {

    private String offerHappy;

    public String getOfferHappy() {
        return offerHappy;
    }

    public void setOfferHappy(String offerHappy) {
        this.offerHappy = offerHappy;
    }

    private String currency;
    private String currency_id;
    private int offerId;
    private String offerTitle;
    @JsonFormat(pattern = AppConstants.MM_DD_YYYY_SERVER)
    private Date offerStartDate;
    @JsonFormat(pattern = AppConstants.MM_DD_YYYY_SERVER)
    private Date offerEndDate;
    private String offerCode;
    private String offerqrCode;
    private String offerDescription;
    private String offerNoofdaysvalidate;
    private String offerItemperday;
    private String offerFree;
    private String offerPublicprivatestatus;
    private String offerImage;
    private String offer_video_url;
    private String totalRedeem;
    private String timesRedeem;
    private String remainingtimesRedeem;
    private String pay_option;
    private List<OfferProductModel> offerproduct;

    public String getTotalRedeem() {
        return totalRedeem;
    }

    public void setTotalRedeem(String totalRedeem) {
        this.totalRedeem = totalRedeem;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCurrency_id() {
        return currency_id;
    }

    public void setCurrency_id(String currency_id) {
        this.currency_id = currency_id;
    }

    public String getPay_option() {
        return pay_option;
    }

    public void setPay_option(String pay_option) {
        this.pay_option = pay_option;
    }

    public String getTimesRedeem() {
        return timesRedeem;
    }

    public void setTimesRedeem(String timesRedeem) {
        this.timesRedeem = timesRedeem;
    }

    public String getRemainingtimesRedeem() {
        return remainingtimesRedeem;
    }

    public void setRemainingtimesRedeem(String remainingtimesRedeem) {
        this.remainingtimesRedeem = remainingtimesRedeem;
    }

    public String getOffer_video_url() {
        return offer_video_url;
    }

    public void setOffer_video_url(String offer_video_url) {
        this.offer_video_url = offer_video_url;
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

    public Date getOfferStartDate() {
        return offerStartDate;
    }

    public void setOfferStartDate(Date offerStartDate) {
        this.offerStartDate = offerStartDate;
    }

    public Date getOfferEndDate() {
        return offerEndDate;
    }

    public void setOfferEndDate(Date offerEndDate) {
        this.offerEndDate = offerEndDate;
    }

    public String getOfferCode() {
        return offerCode;
    }

    public void setOfferCode(String offerCode) {
        this.offerCode = offerCode;
    }

    public String getOfferqrCode() {
        return offerqrCode;
    }

    public void setOfferqrCode(String offerqrCode) {
        this.offerqrCode = offerqrCode;
    }

    public String getOfferDescription() {
        return offerDescription;
    }

    public void setOfferDescription(String offerDescription) {
        this.offerDescription = offerDescription;
    }

    public String getOfferNoofdaysvalidate() {
        return offerNoofdaysvalidate;
    }

    public void setOfferNoofdaysvalidate(String offerNoofdaysvalidate) {
        this.offerNoofdaysvalidate = offerNoofdaysvalidate;
    }

    public String getOfferItemperday() {
        return offerItemperday;
    }

    public void setOfferItemperday(String offerItemperday) {
        this.offerItemperday = offerItemperday;
    }

    public String getOfferFree() {
        return offerFree;
    }

    public void setOfferFree(String offerFree) {
        this.offerFree = offerFree;
    }

    public String getOfferPublicprivatestatus() {
        return offerPublicprivatestatus;
    }

    public void setOfferPublicprivatestatus(String offerPublicprivatestatus) {
        this.offerPublicprivatestatus = offerPublicprivatestatus;
    }

    public String getOfferImage() {
        return offerImage;
    }

    public void setOfferImage(String offerImage) {
        this.offerImage = offerImage;
    }

    public List<OfferProductModel> getOfferproduct() {
        return offerproduct;
    }

    public void setOfferproduct(List<OfferProductModel> offerproduct) {
        this.offerproduct = offerproduct;
    }
}
