package com.dunkin.customer.models;

import java.io.Serializable;

/**
 * Created by qtm-c-android on 2/6/17.
 */

public class TokenModel implements Serializable {

    private String id;
//    @JsonFormat(pattern = AppConstants.MM_DD_YYYY_SERVER)
    private String purchaseDate;
    private String promoId;
    private String ticketId;
    private String ticketPoint;
    private String userName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTicketPoint() {
        return ticketPoint;
    }

    public void setTicketPoint(String ticketPoint) {
        this.ticketPoint = ticketPoint;
    }

    public String getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(String purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public String getPromoId() {
        return promoId;
    }

    public void setPromoId(String promoId) {
        this.promoId = promoId;
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}

