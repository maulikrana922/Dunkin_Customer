package com.dunkin.customer.models;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Admin on 7/8/2015.
 */
public class OrderItemModel implements Serializable {

    private int productId;
    private String productName;
    private int quantity;
    private int unitPrice;
    private String itemPoints;
    private int subTotal;
    private List<ToppingModel> topping;
    private String productImage;

    public String getItemPoints() {
        return itemPoints;
    }

    public void setItemPoints(String itemPoints) {
        this.itemPoints = itemPoints;
    }

    public List<ToppingModel> getTopping() {
        return topping;
    }

    public void setTopping(List<ToppingModel> topping) {
        this.topping = topping;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(int unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(int subTotal) {
        this.subTotal = subTotal;
    }


}
