package com.dunkin.customer.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dunkin.customer.R;
import com.dunkin.customer.Utils.AppUtils;
import com.dunkin.customer.Utils.Callback;
import com.dunkin.customer.controllers.AppController;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class GetBillFragment extends Fragment {


    private Context context;
    private TextView txtBillText;
    private View rootView;
    private ProgressBar progressLoading;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_get_bill, container, false);

        txtBillText = (TextView) rootView.findViewById(R.id.txtBillText);
        progressLoading = (ProgressBar) rootView.findViewById(R.id.progressLoad);

        try {
            AppController.getBill(context, new Callback() {
                @Override
                public void run(Object result) throws JSONException, IOException {
                    JSONObject jsonResponse = new JSONObject((String) result);
                    progressLoading.setVisibility(View.GONE);
                    //Dunkin_Log.i("DataResponse", jsonResponse.toString());

                    if (jsonResponse != null) {
                        if (jsonResponse.getInt("success") == 1) {
                            txtBillText.setText(getString(R.string.txt_get_bill_success));
                        } else if (jsonResponse.getInt("success") == 2) {
                            txtBillText.setText(getString(R.string.txt_get_bill_linked));
                        } else if (jsonResponse.getInt("success") == 0) {
                            txtBillText.setText(getString(R.string.txt_get_bill_failure));
                        }else if (jsonResponse.getInt("success") == 100) {
                            AppUtils.showToastMessage(context, jsonResponse.getString("message"));
                        }
                    }
                }
            });
        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return rootView;
    }
}
