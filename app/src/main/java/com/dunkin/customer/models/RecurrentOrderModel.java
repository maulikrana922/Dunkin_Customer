package com.dunkin.customer.models;

import com.dunkin.customer.constants.AppConstants;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class RecurrentOrderModel implements Serializable {

    private String reference_id;

    @JsonFormat(pattern = AppConstants.MM_DD_YYYY_SERVER)
    private Date recurrent_startdate;
    @JsonFormat(pattern = AppConstants.MM_DD_YYYY_SERVER)
    private Date recurrent_enddate;

    @JsonFormat(pattern = AppConstants.MM_DD_YYYY_SERVER)
    private Date order_start_date;
    @JsonFormat(pattern = AppConstants.MM_DD_YYYY_SERVER)
    private Date order_end_date;

    @JsonFormat(pattern = AppConstants.MM_DD_YYYY_SERVER)
    private Date created_at;

    private String selected_day;

    @JsonFormat(pattern = AppConstants.YYYY_MM_DD_HH_MM_SS_SERVER)
    private Date orderDate;

    private String Amount;
    private String currency_code;
    private String shipping_address;
    private String billing_address;
    private List<RecurrentOrderItemModel> products;

    public Date getOrder_start_date() {
        return order_start_date;
    }

    public void setOrder_start_date(Date order_start_date) {
        this.order_start_date = order_start_date;
    }

    public Date getOrder_end_date() {
        return order_end_date;
    }

    public void setOrder_end_date(Date order_end_date) {
        this.order_end_date = order_end_date;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public String getSelected_day() {
        return selected_day;
    }

    public void setSelected_day(String selected_day) {
        this.selected_day = selected_day;
    }

    public Date getRecurrent_enddate() {
        return recurrent_enddate;
    }

    public void setRecurrent_enddate(Date recurrent_enddate) {
        this.recurrent_enddate = recurrent_enddate;
    }

    public Date getRecurrent_startdate() {
        return recurrent_startdate;
    }

    public void setRecurrent_startdate(Date recurrent_startdate) {
        this.recurrent_startdate = recurrent_startdate;
    }

    public String getReference_id() {
        return reference_id;
    }

    public void setReference_id(String reference_id) {
        this.reference_id = reference_id;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public String getOrderAmount() {
        return Amount;
    }

    public void setOrderAmount(String orderAmount) {
        this.Amount = orderAmount;
    }

    public String getCurrency() {
        return currency_code;
    }

    public void setCurrency(String currency) {
        this.currency_code = currency;
    }

    public String getShippingAddress() {
        return shipping_address;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shipping_address = shippingAddress;
    }

    public String getBillingAddress() {
        return billing_address;
    }

    public void setBillingAddress(String billingAddress) {
        this.billing_address = billingAddress;
    }

    public List<RecurrentOrderItemModel> getOrderItems() {
        return products;
    }

    public void setOrderItems(List<RecurrentOrderItemModel> orderItems) {
        this.products = orderItems;
    }


}


