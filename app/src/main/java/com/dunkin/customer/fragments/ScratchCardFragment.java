package com.dunkin.customer.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dunkin.customer.R;
import com.dunkin.customer.Utils.AppUtils;
import com.dunkin.customer.Utils.Callback;
import com.dunkin.customer.adapters.ScratchCardAdapter;
import com.dunkin.customer.controllers.AppController;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ScratchCardFragment extends Fragment {

    ScratchCardAdapter scratchCardAdapter;
    private Context context;
    private GridView gridView;
    private List<String> digitsList;
    private TextView emptyElement;

    private ProgressBar progressLoading;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_scratch_card, container, false);

        gridView = (GridView) rootView.findViewById(R.id.grid_view);
        emptyElement = (TextView) rootView.findViewById(R.id.emptyElement);
        progressLoading = (ProgressBar) rootView.findViewById(R.id.progressLoad);
        digitsList = new ArrayList<>();

        getDataFromAPI();
        return rootView;
    }

    private void getDataFromAPI() {
        try {
            AppController.getScratchCard(context, new Callback() {
                @Override
                public void run(Object result) throws JSONException, IOException {
                    JSONObject jsonResponse = new JSONObject((String) result);

                    progressLoading.setVisibility(View.GONE);
                    if (jsonResponse.getInt("success") == 1) {
                        digitsList = Arrays.asList(jsonResponse.getString("message").split(","));
                    } else if (jsonResponse.getInt("success") == 100) {
                        AppUtils.showToastMessage(context, jsonResponse.getString("message"));
                    }else {
                        emptyElement.setText(getString(R.string.msg_scratch_card_error));
                    }

                    scratchCardAdapter = new ScratchCardAdapter(context, digitsList);
                    gridView.setAdapter(scratchCardAdapter);
                    gridView.setEmptyView(emptyElement);
                }
            });
        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
