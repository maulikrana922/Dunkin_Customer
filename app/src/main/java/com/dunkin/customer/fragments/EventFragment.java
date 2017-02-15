package com.dunkin.customer.fragments;

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

import com.dunkin.customer.DBAdaters.DBAdapter;
import com.dunkin.customer.EventDetailActivity;
import com.dunkin.customer.R;
import com.dunkin.customer.Utils.AppUtils;
import com.dunkin.customer.Utils.Callback;
import com.dunkin.customer.adapters.EventAdapter;
import com.dunkin.customer.constants.AppConstants;
import com.dunkin.customer.controllers.AppController;
import com.dunkin.customer.models.CountriesModel;
import com.dunkin.customer.models.EventModel;
import com.fasterxml.jackson.core.type.TypeReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class EventFragment extends Fragment implements View.OnClickListener {
    ListView lvList;
    ProgressBar progressLoading;
    private Context context;
    private View rootView;
    private List<EventModel> eventList;
    private EventAdapter eventAdapter;
    private TextView txtCountryName;
    private CountriesModel country;
    private ArrayAdapter<CountriesModel> countryAdapter;
    private int page = 1;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.loadlistview, null);
        lvList = (ListView) rootView.findViewById(R.id.lvLoadList);
        txtCountryName = (TextView) rootView.findViewById(R.id.spSelectCountry);
        progressLoading = (ProgressBar) rootView.findViewById(R.id.progressLoad);
        eventList = new ArrayList<>();
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
            getDataFromAPI();
        } catch (UnsupportedEncodingException | JSONException e) {
            e.printStackTrace();
        }

        lvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (country != null && !TextUtils.isEmpty("" + country.getCountry_id())) {
                    EventModel em = (EventModel) parent.getAdapter().getItem(position);
                    Intent i = new Intent(context, EventDetailActivity.class);
                    i.putExtra("eventId", em.getEventId());
                    i.putExtra("country_id", country.getCountry_id());
                    startActivity(i);
                }
            }
        });
        return rootView;
    }

    private void getDataFromAPI() throws UnsupportedEncodingException, JSONException {
        if (country != null && !TextUtils.isEmpty("" + country.getCountry_id())) {
            AppController.getEventsList(context, country.getCountry_id(), new Callback() {
                @Override
                public void run(Object result) throws JSONException, IOException {
                    JSONObject jsonResponse = new JSONObject((String) result);
                    if (jsonResponse.getInt("success") == 1) {
                        eventList = AppUtils.getJsonMapper().readValue(jsonResponse.getJSONArray("eventList").toString(), new TypeReference<List<EventModel>>() {
                        });
                    }else if (jsonResponse.getInt("success") == 100) {
                        AppUtils.showToastMessage(context, jsonResponse.getString("message"));
                    }
                    progressLoading.setVisibility(View.GONE);
                    eventAdapter = new EventAdapter(context, eventList);
                    lvList.setAdapter(eventAdapter);
                    lvList.setEmptyView(rootView.findViewById(R.id.emptyElement));
                }
            });
        }
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
                    eventList = new ArrayList<>();
                    page = 1;
                    try {
                        getDataFromAPI();
                    } catch (UnsupportedEncodingException | JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            countryDialog.create().show();
        }
    }
}
