package com.dunkin.customer.models;

import java.io.Serializable;

/**
 * Created by latitude on 1/8/17.
 */

public class AboutUsModel implements Serializable {
    private String listImage;
    private String listId;
    private String content;

    public String getListId() {
        return listId;
    }

    public void setListId(String listId) {
        this.listId = listId;
    }

    public String getListImage() {
        return listImage;
    }

    public void setListImage(String listImage) {
        this.listImage = listImage;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
