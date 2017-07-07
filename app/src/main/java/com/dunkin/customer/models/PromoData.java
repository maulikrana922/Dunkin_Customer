package com.dunkin.customer.models;

import java.io.Serializable;

/**
 * Created by qtm-c-android on 7/7/17.
 */

public class PromoData implements Serializable {
    private String Image;
    private String type;
    private String promoStatus;

    public PromoData() {
    }

    public String getPromoStatus() {
        return promoStatus;
    }

    public void setPromoStatus(String promoStatus) {
        this.promoStatus = promoStatus;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
