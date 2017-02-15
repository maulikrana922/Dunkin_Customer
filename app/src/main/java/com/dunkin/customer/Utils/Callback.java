package com.dunkin.customer.Utils;

import org.json.JSONException;

import java.io.IOException;


public interface Callback {

    void run(Object result) throws JSONException, IOException;
}
