package com.dunkin.customer.models;

import java.io.Serializable;

/**
 * Created by Admin on 8/5/2015.
 */
public class ChildCatModel implements Serializable {

    private int categoryId;
    private String categoryName;
    private int categoryParentid;

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public int getCategoryParentid() {
        return categoryParentid;
    }

    public void setCategoryParentid(int categoryParentid) {
        this.categoryParentid = categoryParentid;
    }
}
