package com.dunkin.customer.models;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Admin on 7/8/2015.
 */
public class RecurrentOrderItemModel implements Serializable {

    private int productId;
    private String productName;
    private int product_qty;
    private int unitPrice;
    private int subtotal;
    private List<ToppingModel> topping;
    private String productImage;

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
        return product_qty;
    }

    public void setQuantity(int quantity) {
        this.product_qty = quantity;
    }

    public int getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(int unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getSubTotal() {
        return subtotal;
    }

    public void setSubTotal(int subTotal) {
        this.subtotal = subTotal;
    }


}
