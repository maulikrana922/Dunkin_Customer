package com.dunkin.customer.controllers;

/**
 * Created by mital on 9/11/17.
 */

public class ApiParamloginUser {


    String email="email";
    String password="password";
    String is_device_android="is_device_android";
    String lang_flag="lang_flag";
    String facebook_id="facebook_id";

    public String getFacebook_id() {
        return facebook_id;
    }

    public void setFacebook_id(String facebook_id) {
        this.facebook_id = facebook_id;
    }

    public String getUdid() {
        return udid;
    }

    public void setUdid(String udid) {
        this.udid = udid;
    }

    String udid="udid";

    public String getIs_device_android() {
        return is_device_android;
    }

    public void setIs_device_android(String is_device_android) {
        this.is_device_android = is_device_android;
    }

    public String getLang_flag() {
        return lang_flag;
    }

    public void setLang_flag(String lang_flag) {
        this.lang_flag = lang_flag;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email.toString();
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
