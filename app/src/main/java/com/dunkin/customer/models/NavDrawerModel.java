package com.dunkin.customer.models;

import java.io.Serializable;

/**
 * Created by Admin on 9/12/2015.
 */
public class NavDrawerModel implements Serializable {

    private String navTitle;
    private int navId;
    private int imageId;
    private boolean isSection;

    public NavDrawerModel(int navId, int imageId, String navTitle, boolean isSection) {
        this.navId = navId;
        this.navTitle = navTitle;
        this.imageId = imageId;
        this.isSection = isSection;
    }

    public NavDrawerModel() {

    }

    @Override
    public String toString() {
        return navTitle;
    }

    public String getNavTitle() {
        return navTitle;
    }

    public void setNavTitle(String navTitle) {
        this.navTitle = navTitle;
    }

    public int getNavId() {
        return navId;
    }

    public void setNavId(int navId) {
        this.navId = navId;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public boolean isSection() {
        return isSection;
    }

    public void setIsSection(boolean isSection) {
        this.isSection = isSection;
    }

    @Override
    public boolean equals(Object object) {
        boolean result = false;
        if (object == null || object.getClass() != getClass()) {
            result = false;
        } else {
            NavDrawerModel employee = (NavDrawerModel) object;
            if (this.navId == employee.getNavId()) {
                result = true;
            }
        }
        return result;
    }
}
