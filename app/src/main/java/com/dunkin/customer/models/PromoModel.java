package com.dunkin.customer.models;

import com.dunkin.customer.constants.AppConstants;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by qtm-c-android on 1/6/17.
 */

public class PromoModel implements Serializable {

    private String id;
    @JsonFormat(pattern = AppConstants.DD_MM_YYYY_DASH)
    private Date startDate;
    @JsonFormat(pattern = AppConstants.DD_MM_YYYY_DASH)
    private Date endDate;
    private String name;
    private String description;
    private String promoImage;
    private String ticketPoint;
    private String days;
    private int Qty;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPromoImage() {
        return promoImage;
    }

    public void setPromoImage(String promoImage) {
        this.promoImage = promoImage;
    }

    public String getTicketPoint() {
        return ticketPoint;
    }

    public void setTicketPoint(String ticketPoint) {
        this.ticketPoint = ticketPoint;
    }

    public int getQty() {
        return Qty;
    }

    public void setQty(int qty) {
        Qty = qty;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }
}
