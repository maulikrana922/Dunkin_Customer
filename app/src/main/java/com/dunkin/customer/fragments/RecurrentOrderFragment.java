package com.dunkin.customer.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.dunkin.customer.R;
import com.dunkin.customer.RecurrentOrderDetailActivity;
import com.dunkin.customer.Utils.AppUtils;
import com.dunkin.customer.Utils.Callback;
import com.dunkin.customer.adapters.RecurrentOrderAdapter;
import com.dunkin.customer.controllers.AppController;
import com.dunkin.customer.models.RecurrentOrderModel;
import com.fasterxml.jackson.core.type.TypeReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class RecurrentOrderFragment extends Fragment {

    private Context context;
    private List<RecurrentOrderModel> recurrentOrderList;
    private View rootView;
    private ListView lvList;
    private ProgressBar progressLoading;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.loadlistview, container, false);

        lvList = (ListView) rootView.findViewById(R.id.lvLoadList);
        LinearLayout spSelectCountry = (LinearLayout) rootView.findViewById(R.id.linear2);
        progressLoading = (ProgressBar) rootView.findViewById(R.id.progressLoad);
        spSelectCountry.setVisibility(View.GONE);

        recurrentOrderList = new ArrayList<>();

        try {
            getDataFromAPI();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        lvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                RecurrentOrderModel recurrentOrderModel = (RecurrentOrderModel) parent.getAdapter().getItem(position);

                Intent i = new Intent(context, RecurrentOrderDetailActivity.class);
                i.putExtra("recurrentOrderId", recurrentOrderModel.getReference_id());
                startActivityForResult(i, 101);
            }
        });
        return rootView;
    }

    private void getDataFromAPI() throws UnsupportedEncodingException {
        try {
            AppController.getRecurrentOrderList(context, new Callback() {
                @Override
                public void run(Object result) throws JSONException, IOException {

                    progressLoading.setVisibility(View.GONE);

                    JSONObject jsonResponse = new JSONObject((String) result);
                    //Dunkin_Log.i("DataResponse", jsonResponse.toString());
                    if (jsonResponse.getInt("success") == 1) {
                        recurrentOrderList = AppUtils.getJsonMapper().readValue(jsonResponse.getJSONArray("orderList").toString(), new TypeReference<List<RecurrentOrderModel>>() {
                        });

                        RecurrentOrderAdapter orderHistoryAdapter = new RecurrentOrderAdapter(context, recurrentOrderList);
                        lvList.setAdapter(orderHistoryAdapter);
                        lvList.setVisibility(View.VISIBLE);
                        lvList.setEmptyView(rootView.findViewById(R.id.emptyElement));
                        rootView.findViewById(R.id.emptyElement).setVisibility(View.GONE);
                    }else if (jsonResponse.getInt("success") == 100) {
                        AppUtils.showToastMessage(context, jsonResponse.getString("message"));
                    } else {
                        lvList.setVisibility(View.GONE);
                        rootView.findViewById(R.id.emptyElement).setVisibility(View.VISIBLE);
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101 && resultCode == Activity.RESULT_OK) {
            try {
                getDataFromAPI();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }
}
