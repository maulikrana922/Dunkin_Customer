package com.dunkin.customer.models;

import java.io.Serializable;

/**
 * Created by Android2 on 10/5/2015.
 */
public class SocialLinks implements Serializable {

    private String title;
    private String url;
    private String icon;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

}
