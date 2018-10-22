package com.dunkin.customer.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dunkin.customer.BaseActivity;
import com.dunkin.customer.DBAdaters.DBAdapter;
import com.dunkin.customer.NewsDetailActivity;
import com.dunkin.customer.R;
import com.dunkin.customer.RegisterActivity;
import com.dunkin.customer.Utils.AppUtils;
import com.dunkin.customer.Utils.Callback;
import com.dunkin.customer.adapters.NewsAdapter;
import com.dunkin.customer.constants.AppConstants;
import com.dunkin.customer.controllers.AppController;
import com.dunkin.customer.models.CountriesModel;
import com.dunkin.customer.models.NewsModel;
import com.fasterxml.jackson.core.type.TypeReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class NewsFragment extends Fragment implements View.OnClickListener {
    DBAdapter dbAdapter;
    private Context context;
    private ListView lvList;
    private List<NewsModel> newsList;
    private NewsAdapter newsAdapter;
    private View rootView;
    private int page = 1;
    private TextView txtCountryName;
    private CountriesModel country;
    private ArrayAdapter<CountriesModel> countryAdapter;

    private ProgressBar progressLoading;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.loadlistview, container, false);
        txtCountryName = (TextView) rootView.findViewById(R.id.spSelectCountry);
        progressLoading = (ProgressBar) rootView.findViewById(R.id.progressLoad);
        lvList = (ListView) rootView.findViewById(R.id.lvLoadList);
        newsList = new ArrayList<>();
        txtCountryName.setOnClickListener(this);

        dbAdapter = new DBAdapter(context);
        dbAdapter.open();
        List<CountriesModel> countryList = dbAdapter.getAllCountries();
        dbAdapter.close();

        countryAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, countryList);

        for (CountriesModel cm : countryList) {
            if (cm.getCountry_id() == AppUtils.getAppPreference(context).getInt(AppConstants.USER_COUNTRY, -1)) {
                country = cm;
                txtCountryName.setText(cm.getName());
                break;
            }
        }

        try {
            getDatafromAPI();
        } catch (UnsupportedEncodingException | JSONException e) {
            e.printStackTrace();
        }

        lvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (country != null && !TextUtils.isEmpty("" + country.getCountry_id())) {
                    NewsModel nm = (NewsModel) parent.getAdapter().getItem(position);
                    Intent i = new Intent(context, NewsDetailActivity.class);
                    i.putExtra("newsId", nm.getNewsId());
                    i.putExtra("country_id", country.getCountry_id());
                    startActivity(i);
                }
            }
        });

        return rootView;
    }

    private void getDatafromAPI() throws UnsupportedEncodingException, JSONException {
        if (country != null && !TextUtils.isEmpty("" + country.getCountry_id())) {
            AppController.getNewsList(context, country.getCountry_id(), new Callback() {
                @Override
                public void run(Object result) throws JSONException, IOException {
                    JSONObject jsonResponse = new JSONObject((String) result);
                    if (jsonResponse.getInt("success") == 1) {
                        newsList = AppUtils.getJsonMapper().readValue(jsonResponse.getJSONArray("newsList").toString(), new TypeReference<List<NewsModel>>() {
                        });
                    }else if (jsonResponse.getInt("success") == 100) {
                        AppUtils.showToastMessage(context, jsonResponse.getString("message"));
                    }else if(jsonResponse.getInt("success") == 99) {
                        displayDialog(jsonResponse.getString("message"));
                    }
                    progressLoading.setVisibility(View.GONE);
                    newsAdapter = new NewsAdapter(context, newsList);
                    lvList.setAdapter(newsAdapter);
                    lvList.setEmptyView(rootView.findViewById(R.id.emptyElement));
                }
            });
        }
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

    @Override
    public void onClick(View v) {
        if (v == txtCountryName) {
            final AlertDialog.Builder countryDialog = new AlertDialog.Builder(context);
            countryDialog.setTitle(getString(R.string.txt_select_country));
            countryDialog.setAdapter(countryAdapter, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    country = countryAdapter.getItem(which);
                    txtCountryName.setText(country.getName());
                    newsList = new ArrayList<>();
                    page = 1;
                    try {
                        getDatafromAPI();
                    } catch (UnsupportedEncodingException | JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            countryDialog.create().show();
        }
    }
}