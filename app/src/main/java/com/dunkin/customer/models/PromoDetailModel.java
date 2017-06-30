package com.dunkin.customer.models;

import java.io.Serializable;

/**
 * Created by qtm-c-android on 30/6/17.
 */

public class PromoDetailModel implements Serializable {
    private String promoCode;
    private String promoQRCode;
    private String startTime;
    private String endTime;
    private String maxUse;
    private String userPoint;
    private String myPoint;

    public String getMyPoint() {
        return myPoint;
    }

    public void setMyPoint(String myPoint) {
        this.myPoint = myPoint;
    }

    public String getUserPoint() {
        return userPoint;
    }

    public void setUserPoint(String userPoint) {
        this.userPoint = userPoint;
    }

    public String getMaxUse() {
        return maxUse;
    }

    public void setMaxUse(String maxUse) {
        this.maxUse = maxUse;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getPromoQRCode() {
        return promoQRCode;
    }

    public void setPromoQRCode(String promoQRCode) {
        this.promoQRCode = promoQRCode;
    }

    public String getPromoCode() {
        return promoCode;
    }

    public void setPromoCode(String promoCode) {
        this.promoCode = promoCode;
    }
}
