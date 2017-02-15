package com.dunkin.customer.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 7/8/2015.
 */
public class ProductModel implements Serializable {

    private int rowId;
    private int productId;
    private int categoryId;
    private String productName;
    private String categoryName;
    private int productPrice;
    private String productDescription;
    private double itemPoints;
    private String productImage;
    private int subTotal;
    private String currency;
    private int qty = 1;
    private List<ToppingModel> toppings = new ArrayList<>();
    private List<ToppingModel> productToppings = new ArrayList<>();

    public int getRowId() {
        return rowId;
    }

    public void setRowId(int rowId) {
        this.rowId = rowId;
    }

    public int getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(int subTotal) {
        this.subTotal = subTotal;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public List<ToppingModel> getToppings() {
        return toppings;
    }

    public void setToppings(List<ToppingModel> toppings) {
        this.toppings = toppings;
    }

    public List<ToppingModel> getProductToppings() {
        return productToppings;
    }

    public void setProductToppings(List<ToppingModel> productToppings) {
        this.productToppings = productToppings;
    }

    public int getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(int productPrice) {
        this.productPrice = productPrice;
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

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }


    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public double getItemPoints() {
        return itemPoints;
    }

    public void setItemPoints(double itemPoints) {
        this.itemPoints = itemPoints;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }
}
