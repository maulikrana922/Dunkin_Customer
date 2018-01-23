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
import com.dunkin.customer.models.NewsModel;
import com.fasterxml.jackson.core.type.TypeReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class NewsDetailActivity extends BaseActivity {
    private TextView txtTitle, txtDate, txtPlace, txtDescription;
    private ImageView imgLogo;
    private ImageButton imbVideo;
    private NewsModel nm;
    private int country_id;
    private ScrollView scrollContainer;
    private ProgressBar progressLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        inflateView(R.layout.activity_news_detail, getString(R.string.nav_news));
        int newsId = getIntent().getIntExtra("newsId", -1);
        country_id = getIntent().getIntExtra("country_id", -1);
        initializeViews();
        try {
            getDataFromAPI(newsId);
        } catch (UnsupportedEncodingException | JSONException e) {
            e.printStackTrace();
        }
        imbVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NewsDetailActivity.this, AppWebViewActivity.class).putExtra("url", nm.getNewsVideo()));
            }
        });
    }

    private void initializeViews() {
        txtTitle = (TextView) findViewById(R.id.txtTitle);
        txtDate = (TextView) findViewById(R.id.txtDate);
        txtDescription = (TextView) findViewById(R.id.txtDescription);
        txtPlace = (TextView) findViewById(R.id.txtPlace);
        imgLogo = (ImageView) findViewById(R.id.imgLogo);
        imbVideo = (ImageButton) findViewById(R.id.imbVideo);
        progressLoading = (ProgressBar) findViewById(R.id.progressLoad);
        scrollContainer = (ScrollView) findViewById(R.id.scrollContainer);
    }

    private void getDataFromAPI(int newsId) throws UnsupportedEncodingException, JSONException {

        AppController.getnewsDetail(NewsDetailActivity.this, newsId, country_id, new Callback() {
            @Override
            public void run(Object result) throws JSONException, IOException {
                JSONObject jsonResponse = new JSONObject((String) result);
                progressLoading.setVisibility(View.GONE);
                if (jsonResponse.getInt("success") == 1) {
                    scrollContainer.setVisibility(View.VISIBLE);
                    nm = AppUtils.getJsonMapper().readValue(jsonResponse.getJSONObject("news").toString(), new TypeReference<NewsModel>() {
                    });
                    if (nm != null) {
                        if (nm.getNewsImage() != null) {
                            AppUtils.setImage(imgLogo, nm.getNewsImage());
                        } else {
                            imbVideo.setVisibility(View.VISIBLE);
                            AppUtils.setImage(imgLogo, nm.getNewsVideoThumb());
                        }

                        String newsTitle = Html.fromHtml(nm.getTitle()).toString();
                        txtTitle.setText(newsTitle);

                        String newsPlace = Html.fromHtml(nm.getPlace()).toString();
                        txtPlace.setText(newsPlace);

                        //String newsDescription = Html.fromHtml("<b> " + getString(R.string.txt_news_description) + " : </b><br/>" + nm.getDescription()).toString();
                        txtDescription.setText(Html.fromHtml(nm.getDescription()));
                        txtDescription.setMovementMethod(LinkMovementMethod.getInstance());
                        String newsDate = AppUtils.getFormattedDate(nm.getStartdate());
                        txtDate.setText(newsDate);
                    }
                } else if (jsonResponse.getInt("success") == 100) {
                    AppUtils.showToastMessage(getApplicationContext(), jsonResponse.getString("message"));
                }else {
                    if(jsonResponse.getInt("success") != 99) {
                        AppUtils.showToastMessage(NewsDetailActivity.this, getString(R.string.system_error));
                    }
                }
            }
        });
    }
}