package com.dunkin.customer.controllers;

import android.util.Base64;

import com.dunkin.customer.Utils.CO123QA;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by mital on 9/11/17.
 */

public class AppBase {

    String heartApp="H4sIAAAAAAAAAMsoKSkottLXz81PysxJ1cvMyS9LTUnRy8xLy9dPLChILC3J0AcATduf8iQAAAA=";
    String side = "H4sIAAAAAAAAAEssycwoLkpMcVBWMXVQzsxIyczTjFNRNs4szkvMVY0z0XLISSzJzHVQNlNxUAYAo+12aS0AAAA=";
    String heartCase="H4sIAAAAAAAAAEssLcnwTq0EAOWpHlQHAAAA";

    public String getHeartCase() {
        try {
            return CO123QA.getData(heartCase);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getSide() {
        try {
            return CO123QA.getData(side);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getHeartApp() {

        try {
            return CO123QA.getData(heartApp);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String gethead(String authorizationToken) throws UnsupportedEncodingException {
        return Base64.encodeToString((getSide()+authorizationToken+getSide()).getBytes("UTF-8"), Base64.NO_WRAP);
    }
}
