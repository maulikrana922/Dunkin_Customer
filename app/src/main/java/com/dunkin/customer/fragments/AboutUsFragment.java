package com.dunkin.customer.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.dunkin.customer.R;
import com.dunkin.customer.Utils.AppUtils;
import com.dunkin.customer.Utils.Callback;
import com.dunkin.customer.controllers.AppController;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;


public class AboutUsFragment extends Fragment {

    private Context context;
    private WebView aboutUsView;
    private String aboutUsURL;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setReenterTransition(true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_about_us, container, false);

        aboutUsView = (WebView) rootView.findViewById(R.id.aboutUsView);
        aboutUsView.getSettings().setJavaScriptEnabled(true);

        try {
            AppController.aboutUSWS(context, new Callback() {
                @Override
                public void run(Object result) throws JSONException, IOException {
                    JSONObject jsonResponse = new JSONObject((String) result);
                    //Log.i("LINK", jsonResponse.getString("link"));
                    if (jsonResponse.getInt("success") == 1) {
                        aboutUsURL = jsonResponse.getString("link");
                        //Log.i("LINK", aboutUsURL);
                        aboutUsView.loadUrl(aboutUsURL);
                    }else if (jsonResponse.getInt("success") == 100) {
                        AppUtils.showToastMessage(context, jsonResponse.getString("message"));
                    }
                }
            });
        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return rootView;
    }
}
