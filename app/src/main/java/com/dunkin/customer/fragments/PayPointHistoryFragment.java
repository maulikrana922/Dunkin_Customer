package com.dunkin.customer.fragments;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dunkin.customer.R;
import com.dunkin.customer.RegisterActivity;
import com.dunkin.customer.Utils.AppUtils;
import com.dunkin.customer.Utils.Callback;
import com.dunkin.customer.adapters.NewRedeemPointHistoryAdapter;
import com.dunkin.customer.controllers.AppController;
import com.dunkin.customer.models.RedeemHistoryModel;
import com.dunkin.customer.models.WalletRedeemPoint;
import com.fasterxml.jackson.core.type.TypeReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class PayPointHistoryFragment extends Fragment {

    private Context context;
    private RecyclerView lvList;
    private TextView txtMyPoint, txtUsedPoint, emptyElement,tvNotePoints, txtMyPoints,txtUsedPoints;
    private List<RedeemHistoryModel> redeemList;
    private NewRedeemPointHistoryAdapter redeemPointHistoryAdapter;
    private View rootView;
    private ProgressBar progressLoading;
    private String usedPoints, totalPoints;
    private FrameLayout flMyPoints, flUsedPoints;
    private WalletRedeemPoint walletRedeemPoint;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.loadlistview2, container, false);
        progressLoading = (ProgressBar) rootView.findViewById(R.id.progressLoad);
        lvList = (RecyclerView) rootView.findViewById(R.id.lvLoadList);
        txtMyPoint = (TextView) rootView.findViewById(R.id.txtMyPoint);
        txtUsedPoint = (TextView) rootView.findViewById(R.id.txtUsedPoint);
        emptyElement = (TextView) rootView.findViewById(R.id.emptyElement);
        flMyPoints = (FrameLayout) rootView.findViewById(R.id.flMyPoints);
        flUsedPoints = (FrameLayout) rootView.findViewById(R.id.flUsedPoints);
        tvNotePoints = rootView.findViewById(R.id.tvNotePoints);
        tvNotePoints.setTextSize(14);
        txtMyPoints = rootView.findViewById(R.id.txtMyPoints);
        txtMyPoints.setTextSize(10);
        txtUsedPoints = rootView.findViewById(R.id.txtUsedPoints);
        txtUsedPoints.setTextSize(10);
        redeemList = new ArrayList<>();

        Bundle bundle = getArguments();
        walletRedeemPoint=bundle.getParcelable("walletredeempoints");

        txtMyPoint.setVisibility(View.VISIBLE);
        txtUsedPoint.setVisibility(View.VISIBLE);
        if (AppUtils.isNotNull(walletRedeemPoint.redeemPoint.totalUsedPoint))
            txtUsedPoint.setText(walletRedeemPoint.redeemPoint.totalUsedPoint);
        else
            txtUsedPoint.setText("0");
        if (AppUtils.isNotNull(walletRedeemPoint.redeemPoint.remainRedeemPoint))
            txtMyPoint.setText(walletRedeemPoint.redeemPoint.remainRedeemPoint);
        else
            txtMyPoint.setText("0");
        txtUsedPoint.setTextSize(10);
        txtMyPoint.setTextSize(10);
        loadAnimation(flMyPoints);
        loadAnimation(flUsedPoints);
        try {
            getDataFromAPI();
        } catch (UnsupportedEncodingException | JSONException e) {
            e.printStackTrace();
        }

        return rootView;
    }

    private void getDataFromAPI() throws UnsupportedEncodingException, JSONException {

//        try {
//            AppController.getRedeemPoints(context, AppUtils.getAppPreference(context).getString(AppConstants.USER_EMAIL_ADDRESS, ""), new Callback() {
//                @Override
//                public void run(Object result) throws JSONException, IOException {
//
//                    txtMyPoint.setVisibility(View.VISIBLE);
//                    txtUsedPoint.setVisibility(View.VISIBLE);
//
//                    JSONObject jsonResponse = new JSONObject((String) result);
//
//                    JSONObject json = jsonResponse.getJSONObject("points");
//                    usedPoints = json.getString("usedPoints");
//                    totalPoints = json.getString("totalPoints");
//
//                    // My Point
//                    String tempMyPoint;
//                    if (AppUtils.isNotNull(totalPoints)) {
//                        if (totalPoints.contains(","))
//                            tempMyPoint = totalPoints.replaceAll(",", "");
//                        else
//                            tempMyPoint = totalPoints;
//                    } else
//                        tempMyPoint = "0";
//
//                    if (AppUtils.isNotNull(tempMyPoint))
//                        txtMyPoint.setText(AppUtils.CurrencyFormat(Double.parseDouble(tempMyPoint)));
//                    else
//                        txtMyPoint.setText("0");
//
//                    // Used Point
//                    String tempUsedPoint;
//                    if (AppUtils.isNotNull(usedPoints)) {
//                        if (usedPoints.contains(","))
//                            tempUsedPoint = usedPoints.replaceAll(",", "");
//                        else
//                            tempUsedPoint = usedPoints;
//                    } else
//                        tempUsedPoint = "0";
//
//                    if (AppUtils.isNotNull(usedPoints))
//                        txtUsedPoint.setText(AppUtils.CurrencyFormat(Double.parseDouble(tempUsedPoint)));
//                    else
//                        txtUsedPoint.setText("0");
//
//
//                }
//            });
//        } catch (JSONException | UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }

        try {
            AppController.getRedeemPointHistory(context, new Callback() {
                @Override
                public void run(Object result) throws JSONException, IOException {
                    progressLoading.setVisibility(View.GONE);
                    JSONObject jsonResponse = new JSONObject((String) result);
                    if (jsonResponse.getInt("success") == 1) {
                        redeemList = AppUtils.getJsonMapper().readValue(jsonResponse.getJSONArray("redeem_history").toString(), new TypeReference<List<RedeemHistoryModel>>() {
                        });
                    } else if (jsonResponse.getInt("success") == 100) {
                        AppUtils.showToastMessage(context, jsonResponse.getString("message"));
                    } else if (jsonResponse.getInt("success") == 99) {
                        displayDialog(jsonResponse.getString("message"));
                    }
//                    redeemPointHistoryAdapter = new RedeemPointHistoryAdapter(context, redeemList);
//                    lvList.setAdapter(redeemPointHistoryAdapter);
//                    lvList.setEmptyView(rootView.findViewById(R.id.emptyElement));
                    if (redeemList != null && redeemList.size() > 0) {
                        emptyElement.setVisibility(View.GONE);
                        lvList.setVisibility(View.VISIBLE);
                        redeemPointHistoryAdapter = new NewRedeemPointHistoryAdapter(context, redeemList);
                        lvList.setAdapter(redeemPointHistoryAdapter);
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
                        lvList.setLayoutManager(layoutManager);
                    } else {
                        emptyElement.setVisibility(View.VISIBLE);
                        lvList.setVisibility(View.GONE);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public void loadAnimation(View view) {
        final AnimatorSet animatorSet = (AnimatorSet) AnimatorInflater.loadAnimator(getContext(), R.animator.flip_animation);
        animatorSet.setTarget(view);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(final Animator animator) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        animator.start();
                    }
                }, 2500);
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
        animatorSet.start();
    }
}
