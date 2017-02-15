package com.dunkin.customer;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.dunkin.customer.Utils.AppUtils;
import com.dunkin.customer.Utils.Callback;
import com.dunkin.customer.controllers.AppController;
import com.dunkin.customer.models.CelebrationModel;
import com.fasterxml.jackson.core.type.TypeReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class CelebrationDetailActivity extends BackActivity {
    private TextView txtName, txtPlace, txtCelebrationDescription, txtCelebrationDate;
    private ImageView imgLogo;
    private ImageButton imbVideo;
    private CelebrationModel cm;
    private int country_id;
    private ProgressBar progressLoading;
    private ScrollView scrollContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inflateView(R.layout.activity_celebration_detail, getString(R.string.nav_celebration));

        int celebrationId = getIntent().getIntExtra("celebrationId", -1);
        country_id = getIntent().getIntExtra("country_id", country_id);
        initializeViews();

        try {
            getDataFromAPI(celebrationId);
        } catch (UnsupportedEncodingException | JSONException e) {
            e.printStackTrace();
        }

        imbVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CelebrationDetailActivity.this, AppWebViewActivity.class).putExtra("url", cm.getCelebrationVideo()));
            }
        });
    }

    private void initializeViews() {
        txtName = (TextView) findViewById(R.id.txtName);
        txtPlace = (TextView) findViewById(R.id.txtPlace);
        txtCelebrationDescription = (TextView) findViewById(R.id.txtCelebrationDescription);
        //txtEndDate = (TextView) findViewById(R.id.txtEndDate);

        txtCelebrationDate = (TextView) findViewById(R.id.txtCelebrationDate);
        imgLogo = (ImageView) findViewById(R.id.imgLogo);
        imbVideo = (ImageButton) findViewById(R.id.imbVideo);
        progressLoading = (ProgressBar) findViewById(R.id.progressLoad);
        scrollContainer = (ScrollView) findViewById(R.id.scrollContainer);
    }

    private void getDataFromAPI(int celebrationId) throws UnsupportedEncodingException, JSONException {
        AppController.getCelebrationDetail(CelebrationDetailActivity.this, celebrationId, country_id, new Callback() {
            @Override
            public void run(Object result) throws JSONException, IOException {
                JSONObject jsonResponse = new JSONObject((String) result);
                progressLoading.setVisibility(View.GONE);
                if (jsonResponse.getInt("success") == 1) {
                    scrollContainer.setVisibility(View.VISIBLE);
                    cm = AppUtils.getJsonMapper().readValue(jsonResponse.getJSONObject("celebration").toString(), new TypeReference<CelebrationModel>() {
                    });
                    if (cm != null) {
                        if (cm.getCelebrationImage() != null) {
                            AppUtils.setImage(imgLogo, cm.getCelebrationImage());
                        } else {
                            imbVideo.setVisibility(View.VISIBLE);
                            AppUtils.setImage(imgLogo, cm.getCelebrationVideoThumb());
                        }
                        String celebrationName = Html.fromHtml(cm.getTitle()).toString();
                        txtName.setText(celebrationName);
                        String celebrationPlace = Html.fromHtml(cm.getPlace()).toString();
                        txtPlace.setText(celebrationPlace);

                        txtCelebrationDescription.setText(Html.fromHtml(cm.getDescription()));
                        txtCelebrationDescription.setMovementMethod(LinkMovementMethod.getInstance());
                        txtCelebrationDate.setText(getString(R.string.txt_to, AppUtils.getFormattedDate(cm.getStartdate()), AppUtils.getFormattedDate(cm.getEnddate())));
                    }
                }else if (jsonResponse.getInt("success") == 100) {
                    AppUtils.showToastMessage(getApplicationContext(), jsonResponse.getString("message"));
                } else {
                    AppUtils.showToastMessage(CelebrationDetailActivity.this, getString(R.string.system_error));
                }
            }
        });
    }
}
