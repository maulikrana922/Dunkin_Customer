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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.dunkin.customer.AboutUsDetailsActivity;
import com.dunkin.customer.R;
import com.dunkin.customer.RegisterActivity;
import com.dunkin.customer.Utils.AppUtils;
import com.dunkin.customer.Utils.Callback;
import com.dunkin.customer.adapters.AboutUsItemAdapter;
import com.dunkin.customer.controllers.AppController;
import com.dunkin.customer.models.AboutUsModel;
import com.fasterxml.jackson.core.type.TypeReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by latitude on 1/8/17.
 */

public class AboutUsFragmentNew extends Fragment {

    private Context context;
    private ProgressBar progressLoading;
    private List<AboutUsModel> aboutUsModelList;
    private ListView lvData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setReenterTransition(true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_about_us_new, container, false);

        progressLoading = (ProgressBar) rootView.findViewById(R.id.progressLoad);

        lvData = (ListView) rootView.findViewById(R.id.lvData);

        aboutUsModelList = new ArrayList<>();

        lvData.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(context, AboutUsDetailsActivity.class);
                i.putExtra("listId", aboutUsModelList.get(position).getListId());
                startActivity(i);
            }
        });

        try {
            AppController.aboutUSNew(context, new Callback() {
                @Override
                public void run(Object result) throws JSONException, IOException {
                    JSONObject jsonResponse = new JSONObject((String) result);
                    //Dunkin_Log.i("LINK", jsonResponse.getString("link"));
                    if (jsonResponse.getInt("success") == 1) {
                        progressLoading.setVisibility(View.GONE);

                        if (jsonResponse.getInt("success") == 1) {
                            aboutUsModelList = AppUtils.getJsonMapper().readValue(jsonResponse.getJSONArray("data").toString(), new TypeReference<List<AboutUsModel>>() {
                            });
                            AboutUsItemAdapter aboutUsItemAdapter = new AboutUsItemAdapter(context, aboutUsModelList);
                            lvData.setAdapter(aboutUsItemAdapter);
                        } else if (jsonResponse.getInt("success") == 0) {
                            AppUtils.showToastMessage(context, jsonResponse.getString("message"));
                        }else if (jsonResponse.getInt("success") == 100) {
                            AppUtils.showToastMessage(context, jsonResponse.getString("message"));
                        }else if (jsonResponse.getInt("success") == 99) {
                            displayDialog(jsonResponse.getString("message"));
                        }
                    }

                    else if (jsonResponse.getInt("success") == 100) {
                        AppUtils.showToastMessage(context, jsonResponse.getString("message"));
                    }else if (jsonResponse.getInt("success") == 99) {
                        displayDialog(jsonResponse.getString("message"));
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rootView;
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
}

