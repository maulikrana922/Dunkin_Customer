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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dunkin.customer.DBAdaters.DBAdapter;
import com.dunkin.customer.R;
import com.dunkin.customer.RegisterActivity;
import com.dunkin.customer.Utils.AppUtils;
import com.dunkin.customer.Utils.Callback;
import com.dunkin.customer.constants.AppConstants;
import com.dunkin.customer.controllers.AppController;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;


public class RedeemPointDataFragment extends Fragment {

    private TextView txtUsedPoint, txtTotalPoint, txtPercentage;
    private Context context;
    private String usedPoints, totalPoints;
    private DBAdapter dbAdapter;
    private ImageView imgQR;
    private View rootView;
    private LinearLayout llMyPoints;

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

        dbAdapter = new DBAdapter(context);

        rootView = inflater.inflate(R.layout.fragment_redeem_point, container, false);

        txtTotalPoint = (TextView) rootView.findViewById(R.id.txtMyPoint);
        txtUsedPoint = (TextView) rootView.findViewById(R.id.txtUsedPoint);
        txtPercentage = (TextView) rootView.findViewById(R.id.txtPercentage);
        llMyPoints = (LinearLayout) rootView.findViewById(R.id.llMyPoints);

        txtPercentage.setTextSize(22);

        imgQR = (ImageView) rootView.findViewById(R.id.imgProfileQR);
        AppUtils.setImage(imgQR, AppUtils.getAppPreference(context).getString(AppConstants.USER_PROFILE_QR, ""));

        try {
            AppController.getRedeemPoints(context, AppUtils.getAppPreference(context).getString(AppConstants.USER_EMAIL_ADDRESS, ""), new Callback() {
                @Override
                public void run(Object result) throws JSONException, IOException {
                    rootView.findViewById(R.id.progressLoad).setVisibility(View.GONE);

                    JSONObject jsonResponse = new JSONObject((String) result);
                    //Dunkin_Log.i("DataResponse", jsonResponse.toString());
                    if (jsonResponse.getInt("success") == 1) {
                        rootView.findViewById(R.id.emptyElement).setVisibility(View.GONE);
                        imgQR.setVisibility(View.VISIBLE);
                        llMyPoints.setVisibility(View.VISIBLE);
                        txtTotalPoint.setVisibility(View.VISIBLE);
                        txtUsedPoint.setVisibility(View.VISIBLE);
                        txtPercentage.setVisibility(View.VISIBLE);

                        dbAdapter.open();
                        dbAdapter.addOfflineData(AppConstants.OF_POINTS, (String) result);
                        dbAdapter.close();
                        JSONObject json = jsonResponse.getJSONObject("points");
                        usedPoints = json.getString("usedPoints");
                        totalPoints = json.getString("totalPoints");

                        int myPointPercentage = json.getInt("percentage");

                        // My Point
                        String tempMyPoint;
                        if (AppUtils.isNotNull(totalPoints)) {
                            if (totalPoints.contains(","))
                                tempMyPoint = totalPoints.replaceAll(",", "");
                            else
                                tempMyPoint = totalPoints;
                        } else
                            tempMyPoint = "0";

                        if (AppUtils.isNotNull(tempMyPoint))
                            txtTotalPoint.setText(AppUtils.CurrencyFormat(Double.parseDouble(tempMyPoint)));
                        else
                            txtTotalPoint.setText("0");

                        // Used Point
                        String tempUsedPoint;
                        if (AppUtils.isNotNull(usedPoints)) {
                            if (usedPoints.contains(","))
                                tempUsedPoint = usedPoints.replaceAll(",", "");
                            else
                                tempUsedPoint = usedPoints;
                        } else
                            tempUsedPoint = "0";

                        if (AppUtils.isNotNull(usedPoints))
                            txtUsedPoint.setText(AppUtils.CurrencyFormat(Double.parseDouble(tempUsedPoint)));
                        else
                            txtUsedPoint.setText("0");

                        /*if(myPointPercentage == 1) {
                            Dunkin_Log.i("Percentage", "1: " + myPointPercentage);
                            txtPercentage.setText(String.format(" %s %%", String.valueOf(myPointPercentage)));
                            txtPercentage.setBackgroundResource(R.drawable.ic_cup_1_512);
                        } else if (myPointPercentage > 1 && myPointPercentage <= 10) {
                            Dunkin_Log.i("Percentage", "2: " + myPointPercentage);
                            txtPercentage.setText(String.format(" %s %%", String.valueOf(myPointPercentage)));
                            txtPercentage.setBackgroundResource(R.drawable.ic_cup_10_512);
                        } else if (myPointPercentage > 10 && myPointPercentage <= 20) {
                            Dunkin_Log.i("Percentage", "3: " + myPointPercentage);
                            txtPercentage.setText(String.format(" %s %%", String.valueOf(myPointPercentage)));
                            txtPercentage.setBackgroundResource(R.drawable.ic_cup_20_512);
                        } else if (myPointPercentage > 20 && myPointPercentage <= 30) {
                            Dunkin_Log.i("Percentage", "4: " + myPointPercentage);
                            txtPercentage.setText(String.format(" %s %%", String.valueOf(myPointPercentage)));
                            txtPercentage.setBackgroundResource(R.drawable.ic_cup_30_512);
                        } else if (myPointPercentage > 30 && myPointPercentage <= 40) {
                            Dunkin_Log.i("Percentage", "5: " + myPointPercentage);
                            txtPercentage.setText(String.format(" %s %%", String.valueOf(myPointPercentage)));
                            txtPercentage.setBackgroundResource(R.drawable.ic_cup_40_512);
                        } else if (myPointPercentage > 40 && myPointPercentage <= 50) {
                            Dunkin_Log.i("Percentage", "6: " + myPointPercentage);
                            txtPercentage.setText(String.format(" %s %%", String.valueOf(myPointPercentage)));
                            txtPercentage.setBackgroundResource(R.drawable.ic_cup_50_512);
                        } else if (myPointPercentage > 50 && myPointPercentage <= 60) {
                            Dunkin_Log.i("Percentage", "7: " + myPointPercentage);
                            txtPercentage.setText(String.format(" %s %%", String.valueOf(myPointPercentage)));
                            txtPercentage.setBackgroundResource(R.drawable.ic_cup_60_512);
                        } else if (myPointPercentage > 60 && myPointPercentage <= 70) {
                            Dunkin_Log.i("Percentage", "8: " + myPointPercentage);
                            txtPercentage.setText(String.format(" %s %%", String.valueOf(myPointPercentage)));
                            txtPercentage.setBackgroundResource(R.drawable.ic_cup_70_512);
                        } else if (myPointPercentage > 70 && myPointPercentage <= 80) {
                            Dunkin_Log.i("Percentage", "9: " + myPointPercentage);
                            txtPercentage.setText(String.format(" %s %%", String.valueOf(myPointPercentage)));
                            txtPercentage.setBackgroundResource(R.drawable.ic_cup_80_512);
                        } else if (myPointPercentage > 80 && myPointPercentage <= 90) {
                            Dunkin_Log.i("Percentage", "10: " + myPointPercentage);
                            txtPercentage.setText(String.format(" %s %%", String.valueOf(myPointPercentage)));
                            txtPercentage.setBackgroundResource(R.drawable.ic_cup_90_512);
                        } else if (myPointPercentage > 90 && myPointPercentage <= 100) {
                            Dunkin_Log.i("Percentage", "11: " + myPointPercentage);
                            txtPercentage.setText(String.format(" %s %%", String.valueOf(myPointPercentage)));
                            txtPercentage.setBackgroundResource(R.drawable.ic_cup_100_512);
                        } else {
                            Dunkin_Log.i("Percentage", "0 or Below 0: " + myPointPercentage);
                            txtPercentage.setText("0%");
                            txtPercentage.setBackgroundResource(R.drawable.ic_cup_1_512);
                        }*/
                    } else if (jsonResponse.getInt("success") == 99) {
                        displayDialog(jsonResponse.getString("message"));
                    }else {
                        rootView.findViewById(R.id.emptyElement).setVisibility(View.VISIBLE);
                        imgQR.setVisibility(View.GONE);
                        llMyPoints.setVisibility(View.GONE);
                        txtTotalPoint.setVisibility(View.GONE);
                        txtUsedPoint.setVisibility(View.GONE);
                        txtPercentage.setVisibility(View.GONE);
                    }
                }
            });
        } catch (JSONException | UnsupportedEncodingException e) {
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
