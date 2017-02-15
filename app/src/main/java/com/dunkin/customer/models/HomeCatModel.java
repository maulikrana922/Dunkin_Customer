package com.dunkin.customer.models;

import java.io.Serializable;

/**
 * Created by Admin on 10/16/2015.
 */
public class HomeCatModel implements Serializable {
    private int Id;
    private String Title;
    private String Image;

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }
}
