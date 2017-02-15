package com.dunkin.customer.models;

import java.io.Serializable;

/**
 * Created by Admin on 3/7/2016.
 */
public class NotificationModel implements Serializable {

    public NotificationResponseModel response;
    private int notification_id;
    private int is_read;
    private boolean isSelected;

    public int getIs_read() {
        return is_read;
    }

    public void setIs_read(int is_read) {
        this.is_read = is_read;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public int getNotification_id() {
        return notification_id;
    }

    public void setNotification_id(int notification_id) {
        this.notification_id = notification_id;
    }

    public NotificationResponseModel getResponse() {
        return response;
    }

    public void setResponse(NotificationResponseModel response) {
        this.response = response;
    }


}
