package com.dunkin.customer.models;

import java.io.Serializable;

/**
 * Created by Admin on 10/30/2015.
 */
public class ToppingModel implements Serializable {

    private int id;
    private int product_id;
    private int toppingId;
    private String toppingTitle;
    private String toppingPrice;
    private String toppingImage;
    private int relationId;
    private boolean selected = false;

    public int getProduct_id() {
        return product_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRelationId() {
        return relationId;
    }

    public void setRelationId(int relationId) {
        this.relationId = relationId;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public int getToppingId() {
        return toppingId;
    }

    public void setToppingId(int toppingId) {
        this.toppingId = toppingId;
    }

    public String getToppingTitle() {
        return toppingTitle;
    }

    public void setToppingTitle(String toppingTitle) {
        this.toppingTitle = toppingTitle;
    }

    public String getToppingPrice() {
        return toppingPrice;
    }

    public void setToppingPrice(String toppingPrice) {
        this.toppingPrice = toppingPrice;
    }

    public String getToppingImage() {
        return toppingImage;
    }

    public void setToppingImage(String toppingImage) {
        this.toppingImage = toppingImage;
    }
}
