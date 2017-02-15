package com.dunkin.customer.models;

import java.io.Serializable;

/**
 * Created by Android2 on 8/4/2015.
 */
public class CountriesModel implements Serializable {


    public int country_id;
    public String name;

    public int getCountry_id() {
        return country_id;
    }

    public void setCountry_id(int country_id) {
        this.country_id = country_id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
