package com.dunkin.customer.models;

import java.io.Serializable;

/**
 * Created by Android2 on 9/23/2015.
 */
public class RestaurantsModel implements Serializable {

    public int restaurantId;
    public String latitude;
    public String resturantName;
    public String longitude;
    public String address;
    public String telephone;
    public String email;
    public String distance;

    public String getResturantName() {
        return resturantName;
    }

    public void setResturantName(String resturantName) {
        this.resturantName = resturantName;
    }

    public int getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(int restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }
}


/*
"restaurantId": "10",
        "resturantName": "AAA Restaurant",
        "latitude": "25.1527971",
        "longitude": "75.7500491",
        "address": "2156 Hidden Isle,<br />\r\nWiseman,ND,<br />\r\n58880-1762,<br />\r\n(701) 186-1872),<br />\r\nUS.",
        "telephone": "7011861872",
        "email": "test1@gmail.com",
        "distance": "0.19 KM"
        },*/
