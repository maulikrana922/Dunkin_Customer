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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dunkin.customer.DBAdaters.DBAdapter;
import com.dunkin.customer.ProductListActivity;
import com.dunkin.customer.R;
import com.dunkin.customer.RegisterActivity;
import com.dunkin.customer.Utils.AppUtils;
import com.dunkin.customer.Utils.Callback;
import com.dunkin.customer.adapters.CategoryAdapter;
import com.dunkin.customer.constants.AppConstants;
import com.dunkin.customer.controllers.AppController;
import com.dunkin.customer.models.CategoryModel;
import com.dunkin.customer.models.CountriesModel;
import com.fasterxml.jackson.core.type.TypeReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class CategoryFragment extends Fragment implements View.OnClickListener {

    private Context context;
    private List<CategoryModel> categoryList;
    private CategoryAdapter categoryAdapter;
    private View rootView;

    private GridView lvList;
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

        rootView = inflater.inflate(R.layout.cat_gridview, container, false);

        lvList = (GridView) rootView.findViewById(R.id.grid_view);

        progressLoading = (ProgressBar) rootView.findViewById(R.id.progressLoad);
        txtCountryName = (TextView) rootView.findViewById(R.id.spSelectCountry);
        categoryList = new ArrayList<>();

        txtCountryName.setOnClickListener(this);

        DBAdapter dbAdapter = new DBAdapter(context);
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
            getDataFromAPI(false);
        } catch (UnsupportedEncodingException | JSONException e) {
            e.printStackTrace();
        }

        lvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CategoryModel cm = (CategoryModel) parent.getAdapter().getItem(position);

                Intent i = new Intent(context, ProductListActivity.class);
                i.putExtra("category", cm);
                i.putExtra("country_id", country.getCountry_id());
                startActivity(i);
            }
        });
        return rootView;
    }

    private void getDataFromAPI(final boolean isLoading) throws UnsupportedEncodingException, JSONException {

        AppController.getCategoryList(context, country.getCountry_id(), new Callback() {
            @Override
            public void run(Object result) throws JSONException, IOException {
                JSONObject jsonResponse = new JSONObject((String) result);
                if (jsonResponse.getInt("success") == 1) {
                    List<CategoryModel> categoryModelList = AppUtils.getJsonMapper().readValue(jsonResponse.getJSONArray("categoryList").toString(), new TypeReference<List<CategoryModel>>() {
                    });
                    if (categoryModelList != null && categoryModelList.size() > 0) {
                        categoryList.addAll(categoryModelList);
                    }
                }
                else if (jsonResponse.getInt("success") == 100) {
                    AppUtils.showToastMessage(context, jsonResponse.getString("message"));
                }else if (jsonResponse.getInt("success") == 99) {
                    displayDialog(jsonResponse.getString("message"));
                }
                if (!isLoading) {
                    progressLoading.setVisibility(View.GONE);

                    categoryAdapter = new CategoryAdapter(context, categoryList);
                    lvList.setAdapter(categoryAdapter);
                    lvList.setEmptyView(rootView.findViewById(R.id.emptyElement));
                }
            }
        });
    }

    private void displayDialog(String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(context, RegisterActivity.class));
                        ((Activity) context).finish();
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
                    categoryList = new ArrayList<>();
                    try {
                        getDataFromAPI(false);
                    } catch (UnsupportedEncodingException | JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            countryDialog.create().show();
        }
    }
}
