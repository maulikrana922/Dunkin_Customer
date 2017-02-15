package com.dunkin.customer.models;

import com.dunkin.customer.constants.AppConstants;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Admin on 7/7/2015.
 */
public class EventModel implements Serializable {

    private int eventId;
    private String eventName;
    private String eventImage;
    private String eventDescription;
    @JsonFormat(pattern = AppConstants.MM_DD_YYYY_SERVER)
    private Date startDate;
    @JsonFormat(pattern = AppConstants.MM_DD_YYYY_SERVER)
    private Date endDate;
    private String eventPlace;
    private String eventVideo;
    private String eventVideoThumb;

    public String getEventVideoThumb() {
        return eventVideoThumb;
    }

    public void setEventVideoThumb(String eventVideoThumb) {
        this.eventVideoThumb = eventVideoThumb;
    }

    public String getEventVideo() {
        return eventVideo;
    }

    public void setEventVideo(String eventVideo) {
        this.eventVideo = eventVideo;
    }

    public String getEventPlace() {
        return eventPlace;
    }

    public void setEventPlace(String eventPlace) {
        this.eventPlace = eventPlace;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventImage() {
        return eventImage;
    }

    public void setEventImage(String eventImage) {
        this.eventImage = eventImage;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
