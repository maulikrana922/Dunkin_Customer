package com.dunkin.customer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.dunkin.customer.Utils.AppUtils;
import com.dunkin.customer.Utils.Callback;
import com.dunkin.customer.adapters.PlayItemAdapter;
import com.dunkin.customer.controllers.AppController;
import com.dunkin.customer.models.PromoModel;
import com.fasterxml.jackson.core.type.TypeReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by qtm-c-android on 1/6/17.
 */

public class PromoListActivity extends BackActivity {

    private ListView lvPlayData;
    private ProgressBar progressLoading;
    private String user_point;
    private List<PromoModel> playModelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        inflateView(R.layout.activity_promo_list, "");

        lvPlayData = (ListView) findViewById(R.id.lvPlayData);

        progressLoading = (ProgressBar) findViewById(R.id.progressLoad);

        playModelList = new ArrayList<>();

        lvPlayData.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PromoModel playModel = (PromoModel) parent.getAdapter().getItem(position);

                Intent i = new Intent(PromoListActivity.this, PromationDetailsActivity.class);
                i.putExtra("promo_data", playModel);
                i.putExtra("user_point", user_point);
                startActivity(i);
            }
        });
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        getDataFromAPI();
    }
    private void getDataFromAPI() {

        try {
            AppController.getPlayList(PromoListActivity.this, new Callback() {
                @Override
                public void run(Object result) throws JSONException, IOException {
                    JSONObject jsonResponse = new JSONObject((String) result);

                    //Log.i("DataResponse", jsonResponse.toString());

                    progressLoading.setVisibility(View.GONE);

                    if (jsonResponse.getInt("success") == 1) {
                        user_point = jsonResponse.getString("userpoints");
                        playModelList = AppUtils.getJsonMapper().readValue(jsonResponse.getJSONArray("promoList").toString(), new TypeReference<List<PromoModel>>() {
                        });
                        PlayItemAdapter orderItemAdapter = new PlayItemAdapter(PromoListActivity.this, playModelList);
                        lvPlayData.setAdapter(orderItemAdapter);
                    } else if (jsonResponse.getInt("success") == 0) {
                        AppUtils.showToastMessage(getApplicationContext(), jsonResponse.getString("message"));
                    }else if (jsonResponse.getInt("success") == 100) {
                        AppUtils.showToastMessage(getApplicationContext(), jsonResponse.getString("message"));
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}