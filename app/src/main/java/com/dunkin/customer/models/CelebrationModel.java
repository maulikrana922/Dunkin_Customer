package com.dunkin.customer.models;

import com.dunkin.customer.constants.AppConstants;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Android2 on 7/8/2015.
 */
public class CelebrationModel implements Serializable {

    private int celebrationId;
    private String celebrationImage;
    private String title;
    private String place;
    @JsonFormat(pattern = AppConstants.MM_DD_YYYY_SERVER)
    private Date startdate;
    @JsonFormat(pattern = AppConstants.MM_DD_YYYY_SERVER)
    private Date enddate;
    private String description;
    private String celebrationVideo;
    private String celebrationVideoThumb;

    public int getCelebrationId() {
        return celebrationId;
    }

    public void setCelebrationId(int celebrationId) {
        this.celebrationId = celebrationId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public Date getStartdate() {
        return startdate;
    }

    public void setStartdate(Date startdate) {
        this.startdate = startdate;
    }

    public Date getEnddate() {
        return enddate;
    }

    public void setEnddate(Date enddate) {
        this.enddate = enddate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public String getCelebrationImage() {
        return celebrationImage;
    }

    public void setCelebrationImage(String celebrationImage) {
        this.celebrationImage = celebrationImage;
    }


    public String getCelebrationVideoThumb() {
        return celebrationVideoThumb;
    }

    public void setCelebrationVideoThumb(String celebrationVideoThumb) {
        this.celebrationVideoThumb = celebrationVideoThumb;
    }

    public String getCelebrationVideo() {
        return celebrationVideo;
    }

    public void setCelebrationVideo(String celebrationVideo) {
        this.celebrationVideo = celebrationVideo;
    }
}
