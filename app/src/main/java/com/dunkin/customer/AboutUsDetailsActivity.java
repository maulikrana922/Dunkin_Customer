package com.dunkin.customer;

import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.widget.ImageView;
import android.widget.TextView;

import com.dunkin.customer.Utils.AppUtils;
import com.dunkin.customer.Utils.Callback;
import com.dunkin.customer.controllers.AppController;
import com.dunkin.customer.models.AboutUsModel;
import com.fasterxml.jackson.core.type.TypeReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by latitude on 1/8/17.
 */

public class AboutUsDetailsActivity extends BaseActivity {

    private TextView txtDesc;
    private ImageView img;
    private List<AboutUsModel> aboutUsModelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        inflateView(R.layout.activity_about_us_detail, "");

        String listId = getIntent().getStringExtra("listId");
        initializeViews();

        try {
            getDataFromAPI(listId);
        } catch (UnsupportedEncodingException | JSONException e) {
            e.printStackTrace();
        }

    }

    private void initializeViews() {
        txtDesc = (TextView) findViewById(R.id.txtDesc);
        img = (ImageView) findViewById(R.id.img);

        aboutUsModelList = new ArrayList<>();
    }

    private void getDataFromAPI(String listId) throws UnsupportedEncodingException, JSONException {
        AppController.getAboutUsDetail(AboutUsDetailsActivity.this, listId, new Callback() {
            @Override
            public void run(Object result) throws JSONException, IOException {
                JSONObject jsonResponse = new JSONObject((String) result);
                if (jsonResponse.getInt("success") == 1) {
                    aboutUsModelList = AppUtils.getJsonMapper().readValue(jsonResponse.getJSONArray("data").toString(), new TypeReference<List<AboutUsModel>>() {
                    });
                    if (aboutUsModelList != null) {
                        if(aboutUsModelList.size()>0) {
                            if (!TextUtils.isEmpty(aboutUsModelList.get(0).getListImage())) {
                                AppUtils.setImage(img, aboutUsModelList.get(0).getListImage());
                            }
                            String content = Html.fromHtml(aboutUsModelList.get(0).getContent()).toString();
                            txtDesc.setText(content);
                            txtDesc.setMovementMethod(LinkMovementMethod.getInstance());
                        }
                    }
                } else if (jsonResponse.getInt("success") == 0) {
                    AppUtils.showToastMessage(getApplicationContext(), jsonResponse.getString("message"));
                } else if (jsonResponse.getInt("success") == 100) {
                    AppUtils.showToastMessage(getApplicationContext(), jsonResponse.getString("message"));
                } else {
                    if(jsonResponse.getInt("success") != 99) {
                        AppUtils.showToastMessage(getApplicationContext(), getString(R.string.system_error));
                        finish();
                    }
                }
            }
        });
    }
}
