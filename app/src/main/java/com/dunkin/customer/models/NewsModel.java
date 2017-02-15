package com.dunkin.customer.models;

import com.dunkin.customer.constants.AppConstants;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Android2 on 7/8/2015.
 */
public class NewsModel implements Serializable {

    private int newsId;
    private String newsImage;
    private String title;
    @JsonFormat(pattern = AppConstants.MM_DD_YYYY_SERVER)
    private Date newsdate;
    private String description;
    private String place;
    private String newsVideo;
    private String newsVideoThumb;

    public String getNewsVideo() {
        return newsVideo;
    }

    public void setNewsVideo(String newsVideo) {
        this.newsVideo = newsVideo;
    }

    public String getNewsVideoThumb() {
        return newsVideoThumb;
    }

    public void setNewsVideoThumb(String newsVideoThumb) {
        this.newsVideoThumb = newsVideoThumb;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getNewsId() {
        return newsId;
    }

    public void setNewsId(int newsId) {
        this.newsId = newsId;
    }

    public String getNewsImage() {
        return newsImage;
    }

    public void setNewsImage(String newsImage) {
        this.newsImage = newsImage;
    }


    public Date getNewsdate() {
        return newsdate;
    }

    public void setNewsdate(Date newsdate) {
        this.newsdate = newsdate;
    }


}
