package com.dunkin.customer.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.dunkin.customer.R;
import com.dunkin.customer.RegisterActivity;
import com.dunkin.customer.Utils.AppUtils;
import com.dunkin.customer.Utils.Callback;
import com.dunkin.customer.adapters.RedeemPointHistoryAdapter;
import com.dunkin.customer.controllers.AppController;
import com.dunkin.customer.models.RedeemHistoryModel;
import com.fasterxml.jackson.core.type.TypeReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


public class RedeemPointHistoryFragment extends Fragment {

    private Context context;
    private ListView lvList;
    private List<RedeemHistoryModel> redeemList;
    private RedeemPointHistoryAdapter redeemPointHistoryAdapter;
    private View rootView;
    private ProgressBar progressLoading;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.loadlistview, container, false);
        progressLoading = (ProgressBar) rootView.findViewById(R.id.progressLoad);
        lvList = (ListView) rootView.findViewById(R.id.lvLoadList);
        redeemList = new ArrayList<>();
        try {
            getDatafromAPI();
        } catch (UnsupportedEncodingException | JSONException e) {
            e.printStackTrace();
        }

        return rootView;
    }

    private void getDatafromAPI() throws UnsupportedEncodingException, JSONException {

        AppController.getRedeemPointHistory(context, new Callback() {
            @Override
            public void run(Object result) throws JSONException, IOException {

                JSONObject jsonResponse = new JSONObject((String) result);

                //Dunkin_Log.i("DataResponse", jsonResponse.toString());

                progressLoading.setVisibility(View.GONE);

                if (jsonResponse.getInt("success") == 1) {
                    redeemList = AppUtils.getJsonMapper().readValue(jsonResponse.getJSONArray("redeem_history").toString(), new TypeReference<List<RedeemHistoryModel>>() {
                    });
                }else if(jsonResponse.getInt("success") == 99) {
                    displayDialog(jsonResponse.getString("message"));
                }
                redeemPointHistoryAdapter = new RedeemPointHistoryAdapter(context, redeemList);
                lvList.setAdapter(redeemPointHistoryAdapter);
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
