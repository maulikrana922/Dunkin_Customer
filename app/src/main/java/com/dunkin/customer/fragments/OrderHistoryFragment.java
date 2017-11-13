package com.dunkin.customer.fragments;

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

import com.dunkin.customer.OrderHistoryDetailActivity;
import com.dunkin.customer.R;
import com.dunkin.customer.Utils.AppUtils;
import com.dunkin.customer.Utils.Callback;
import com.dunkin.customer.adapters.OrderHistoryAdapter;
import com.dunkin.customer.controllers.AppController;
import com.dunkin.customer.models.OrderModel;
import com.fasterxml.jackson.core.type.TypeReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class OrderHistoryFragment extends Fragment {

    int page = 1;
    private Context context;
    private List<OrderModel> orderList;
    private View rootView;
    private OrderHistoryAdapter orderHistoryAdapter;
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

        orderList = new ArrayList<>();

        try {
            getDataFromAPI();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        lvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                OrderModel orderHistory = (OrderModel) parent.getAdapter().getItem(position);

                Intent i = new Intent(context, OrderHistoryDetailActivity.class);
                i.putExtra("orderId", orderHistory.getOrderId());
                startActivity(i);
            }
        });
        return rootView;
    }

    private void getDataFromAPI() throws UnsupportedEncodingException {
        AppController.getOrderList(context, new Callback() {
            @Override
            public void run(Object result) throws JSONException, IOException {

                JSONObject jsonResponse = new JSONObject((String) result);

                //Dunkin_Log.i("DataResponse", jsonResponse.toString());

                if (jsonResponse.getInt("success") == 1) {
                    orderList = AppUtils.getJsonMapper().readValue(jsonResponse.getJSONArray("orderList").toString(), new TypeReference<List<OrderModel>>() {
                    });
                }
                else if (jsonResponse.getInt("success") == 100) {
                    AppUtils.showToastMessage(context, jsonResponse.getString("message"));
                }
                progressLoading.setVisibility(View.GONE);

                orderHistoryAdapter = new OrderHistoryAdapter(context, orderList);
                lvList.setAdapter(orderHistoryAdapter);
                lvList.setEmptyView(rootView.findViewById(R.id.emptyElement));
            }
        });
    }
}
