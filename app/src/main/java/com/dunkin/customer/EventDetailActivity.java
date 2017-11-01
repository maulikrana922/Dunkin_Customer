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
import com.dunkin.customer.models.EventModel;
import com.fasterxml.jackson.core.type.TypeReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class EventDetailActivity extends BackActivity {
    private TextView txtEventName, txtEventPlace, txtEventDate, txtEventDescription;
    private ImageView imgLogo;
    private ImageButton imbVideo;
    private EventModel em;
    private int countryId;
    private ProgressBar progressLoading;
    private ScrollView scrollContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        inflateView(R.layout.activity_event_detail, getString(R.string.nav_event));

        int eventId = getIntent().getIntExtra("eventId", -1);
        countryId = getIntent().getIntExtra("country_id", -1);
        initializeViews();

        try {
            getDataFromAPI(eventId);
        } catch (UnsupportedEncodingException | JSONException e) {
            e.printStackTrace();
        }

        imbVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EventDetailActivity.this, AppWebViewActivity.class).putExtra("url", em.getEventVideo()));
            }
        });
    }

    private void initializeViews() {
        txtEventName = (TextView) findViewById(R.id.txtTitle);
        txtEventDescription = (TextView) findViewById(R.id.txtEventDescription);
        //   txtEventEndDate = (TextView) findViewById(R.id.txtEndDate);
        txtEventPlace = (TextView) findViewById(R.id.txtPlace);
        txtEventDate = (TextView) findViewById(R.id.txtEventDate);
        imgLogo = (ImageView) findViewById(R.id.imgLogo);
        imbVideo = (ImageButton) findViewById(R.id.imbVideo);
        scrollContainer = (ScrollView) findViewById(R.id.scrollContainer);
        progressLoading = (ProgressBar) findViewById(R.id.progressLoad);
    }

    private void getDataFromAPI(int eventId) throws UnsupportedEncodingException, JSONException {
        AppController.getEventDetail(EventDetailActivity.this, eventId, countryId, new Callback() {
            @Override
            public void run(Object result) throws JSONException, IOException {
                JSONObject jsonResponse = new JSONObject((String) result);
                progressLoading.setVisibility(View.GONE);
                if (jsonResponse.getInt("success") == 1) {
                    scrollContainer.setVisibility(View.VISIBLE);
                    em = AppUtils.getJsonMapper().readValue(jsonResponse.getJSONObject("event").toString(), new TypeReference<EventModel>() {
                    });

                    if (em != null) {
                        if (em.getEventImage() != null) {
                            AppUtils.setImage(imgLogo, em.getEventImage());
                        } else {
                            imbVideo.setVisibility(View.VISIBLE);
                            AppUtils.setImage(imgLogo, em.getEventVideoThumb());
                        }
                        String eventName = Html.fromHtml(em.getEventName()).toString();
                        txtEventName.setText(eventName);

                        txtEventDescription.setText(Html.fromHtml(em.getEventDescription()));
                        txtEventDescription.setMovementMethod(LinkMovementMethod.getInstance());

                        String eventPlace = Html.fromHtml(em.getEventPlace()).toString();
                        txtEventPlace.setText(eventPlace);
                        txtEventDate.setText(getString(R.string.txt_to, AppUtils.getFormattedDate(em.getStartDate()), AppUtils.getFormattedDate(em.getEndDate())));
                    }
                } else if (jsonResponse.getInt("success") == 100) {
                    AppUtils.showToastMessage(getApplicationContext(), jsonResponse.getString("message"));
                }else {
                    if(jsonResponse.getInt("success") != 99) {
                        AppUtils.showToastMessage(EventDetailActivity.this, getString(R.string.system_error));
                        finish();
                    }
                }
            }
        });
    }
}
