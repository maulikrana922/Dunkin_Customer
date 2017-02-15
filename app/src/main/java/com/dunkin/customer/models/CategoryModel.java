package com.dunkin.customer.models;

import java.io.Serializable;
import java.util.List;


public class CategoryModel implements Serializable {

    private int categoryId;
    private String categoryName;
    private int categoryParentId;
    private String categoryImage;
    private List<ChildCatModel> ChildCategory;

    public String getCategoryImage() {
        return categoryImage;
    }

    public void setCategoryImage(String categoryImage) {
        this.categoryImage = categoryImage;
    }

    public int getCategoryParentId() {
        return categoryParentId;
    }

    public void setCategoryParentId(int categoryParentId) {
        this.categoryParentId = categoryParentId;
    }

    public List<ChildCatModel> getChildCategory() {
        return ChildCategory;
    }

    public void setChildCategory(List<ChildCatModel> childCategory) {
        ChildCategory = childCategory;
    }

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

}
