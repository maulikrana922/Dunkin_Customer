package com.dunkin.customer.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.dunkin.customer.OfferPaymentActivity;
import com.dunkin.customer.R;
import com.dunkin.customer.Utils.AppUtils;
import com.dunkin.customer.Utils.Callback;
import com.dunkin.customer.adapters.OfferHistoryAdapter;
import com.dunkin.customer.controllers.AppController;
import com.dunkin.customer.models.OfferHistoryModel;
import com.fasterxml.jackson.core.type.TypeReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 3/5/2016.
 */
public class OfferHistoryFragment extends Fragment {

    private Context context;
    private View rootView;
    private ListView lvList;
    private OfferHistoryAdapter offerHistoryAdapter;
    private List<OfferHistoryModel> offerHistoryList;
    private ProgressBar progressLoading;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.loadlistview, container, false);
        lvList = (ListView) rootView.findViewById(R.id.lvLoadList);
        progressLoading = (ProgressBar) rootView.findViewById(R.id.progressLoad);

        offerHistoryList = new ArrayList<>();
        try {
            AppController.getOfferHistory(context, new Callback() {
                @Override
                public void run(Object result) throws JSONException, IOException {
                    JSONObject jsonResponse = new JSONObject((String) result);
                    //Log.i("DataResponse", jsonResponse.toString());

                    if (jsonResponse.getInt("success") == 1) {
                        offerHistoryList = AppUtils.getJsonMapper().readValue(jsonResponse.getJSONArray("offerDetail").toString(), new TypeReference<List<OfferHistoryModel>>() {
                        });
                    }
                    else if (jsonResponse.getInt("success") == 100) {
                        AppUtils.showToastMessage(context, jsonResponse.getString("message"));
                    }
                    progressLoading.setVisibility(View.GONE);
                    offerHistoryAdapter = new OfferHistoryAdapter(context, offerHistoryList);
                    lvList.setAdapter(offerHistoryAdapter);
                    lvList.setEmptyView(rootView.findViewById(R.id.emptyElement));
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

        lvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                OfferHistoryModel of = (OfferHistoryModel) parent.getAdapter().getItem(position);

                Intent intent = new Intent(context, OfferPaymentActivity.class);
                intent.putExtra("reference_id", of.getReference_id());
                intent.putExtra("offerId", String.valueOf(of.getOfferId()));
                intent.putExtra("country_id", of.getCountry_id());
                intent.putExtra("isFromHistory", true);
                startActivity(intent);
            }
        });
        return rootView;
    }
}
