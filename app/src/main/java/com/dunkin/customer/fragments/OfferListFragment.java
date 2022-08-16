package com.dunkin.customer.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.dunkin.customer.OfferDetailActivity;
import com.dunkin.customer.R;
import com.dunkin.customer.RegisterActivity;
import com.dunkin.customer.Utils.AppUtils;
import com.dunkin.customer.Utils.Callback;
import com.dunkin.customer.adapters.OfferAdapter;
import com.dunkin.customer.constants.AppConstants;
import com.dunkin.customer.controllers.AppController;
import com.dunkin.customer.models.OfferModel;
import com.fasterxml.jackson.core.type.TypeReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;


public class OfferListFragment extends Fragment {

    private Context context;
    private ListView lvList;
    private List<OfferModel> offerList;
    private OfferAdapter offerAdapter;
    private View rootView;
    private ProgressBar progressLoading;
    private int country_id;
    private LinearLayout mainLayout;
    Animation animFadein;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.loadlistview, container, false);
        lvList = (ListView) rootView.findViewById(R.id.lvLoadList);

        mainLayout = (LinearLayout) rootView.findViewById(R.id.mainLayout);

        animFadein = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.fade_in);

        progressLoading = (ProgressBar) rootView.findViewById(R.id.progressLoad);
        //  spSelectCountry.setOnClickListener(this);

        offerList = new ArrayList<>();
        country_id = AppUtils.getAppPreference(context).getInt(AppConstants.USER_COUNTRY, -1);

        try {
            getDataFromAPI();
        } catch (UnsupportedEncodingException | JSONException e) {
            e.printStackTrace();
        }

        lvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                OfferModel of = (OfferModel) parent.getAdapter().getItem(position);

                Intent i = new Intent(context, OfferDetailActivity.class);
                i.putExtra("offerId", "" + of.getOfferId());
                i.putExtra("country_id", country_id);
                startActivity(i);
            }
        });
        return rootView;
    }

    private void getDataFromAPI() throws UnsupportedEncodingException, JSONException {

        AppController.getOffersList(context, country_id, new Callback() {
            @Override
            public void run(Object result) throws JSONException, IOException {
                JSONObject jsonResponse = new JSONObject((String) result);
                mainLayout.startAnimation(animFadein);
                if (jsonResponse.getInt("success") == 1) {
                    offerList = AppUtils.getJsonMapper().readValue(jsonResponse.getJSONArray("offerList").toString(), new TypeReference<List<OfferModel>>() {
                    });
                }
                else if (jsonResponse.getInt("success") == 100) {
                    AppUtils.showToastMessage(context, jsonResponse.getString("message"));
                }else if (jsonResponse.getInt("success") == 99) {
                    displayDialog(jsonResponse.getString("message"));
                }
                progressLoading.setVisibility(View.GONE);
                offerAdapter = new OfferAdapter(context, offerList);
                lvList.setAdapter(offerAdapter);
                lvList.setEmptyView(rootView.findViewById(R.id.emptyElement));
            }
        });
    }

    private void displayDialog(String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(context, RegisterActivity.class));
                        ((Activity)context).finish();
                    }
                });

        AlertDialog alert = builder.create();
        alert.setTitle(getResources().getString(R.string.app_name));
        alert.show();
    }
}
