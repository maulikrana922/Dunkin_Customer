package com.dunkin.customer.models;

import java.io.Serializable;

/**
 * Created by Android2 on 9/11/2015.
 */
public class OfferProductDetailModel implements Serializable {

    private int productId;
    private int categoryId;
    private String productName;
    private String productPrice;
    private String productDescription;
    private String itemPoints;
    private String productImage;
    private String Buythisproduct;
    private String freethisproduct;
    private String Remainthisproduct;
    private String purchase_date;
    private String pay_option;
    private String usedQty;
    private String availQty;

    public String getAvailQty() {
        return availQty;
    }

    public void setAvailQty(String availQty) {
        this.availQty = availQty;
    }

    public String getUsedQty() {
        return usedQty;
    }

    public void setUsedQty(String usedQty) {
        this.usedQty = usedQty;
    }

    public String getPurchase_date() {
        return purchase_date;
    }

    public void setPurchase_date(String purchase_date) {
        this.purchase_date = purchase_date;
    }

    public String getPay_option() {
        return pay_option;
    }

    public void setPay_option(String pay_option) {
        this.pay_option = pay_option;
    }

    public String getRemainthisproduct() {
        return Remainthisproduct;
    }

    public void setRemainthisproduct(String remainthisproduct) {
        Remainthisproduct = remainthisproduct;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public String getItemPoints() {
        return itemPoints;
    }

    public void setItemPoints(String itemPoints) {
        this.itemPoints = itemPoints;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getBuythisproduct() {
        return Buythisproduct;
    }

    public void setBuythisproduct(String buythisproduct) {
        Buythisproduct = buythisproduct;
    }

    public String getFreethisproduct() {
        return freethisproduct;
    }

    public void setFreethisproduct(String freethisproduct) {
        this.freethisproduct = freethisproduct;
    }
}
