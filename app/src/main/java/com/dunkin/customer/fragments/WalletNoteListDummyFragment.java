package com.dunkin.customer.fragments;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.dunkin.customer.R;
import com.dunkin.customer.Utils.AppUtils;

/**
 * Created by Admin on 9/9/2015.
 */
public class WalletNoteListDummyFragment extends Fragment {

    private String tempRemainingPoints, remainingPoints;
    private FrameLayout flTotalAmount, flRemainingPoints;
    private TextView  tvNotePoints,txtAmountRemaining, txtAmountUsed;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        tempRemainingPoints = bundle.getString("points");
        if (tempRemainingPoints.contains(",")) {
            remainingPoints = tempRemainingPoints.replaceAll(",", "");
        } else
            remainingPoints = tempRemainingPoints;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_wallet_note_list, container, false);

        TextView txtTotalAmount = (TextView) rootView.findViewById(R.id.txtWalletAmount);
        TextView txtWalletAmountPoint = (TextView) rootView.findViewById(R.id.txtWalletAmountPoint);
        TextView emptyElement = (TextView) rootView.findViewById(R.id.emptyElement);
        flTotalAmount = (FrameLayout) rootView.findViewById(R.id.flTotalAmount);
        flRemainingPoints = (FrameLayout) rootView.findViewById(R.id.flRemainingPoints);
//        ListView lvWalletNoteList = (ListView) rootView.findViewById(R.id.walletNoteList);
//        String amount = getString(R.string.txt_my_wallet_amount, AppUtils.CurrencyFormat(0.00) + " LL") + "\n" +
//                getString(R.string.txt_my_wallet_remaining_amount) + " " + AppUtils.CurrencyFormat(Double.parseDouble(remainingPoints));
        String amount = AppUtils.CurrencyFormat(Double.parseDouble("0.0")) + " " + "LL";
        String points = AppUtils.CurrencyFormat(Double.parseDouble("0.0"));
        txtTotalAmount.setText(amount);
        txtWalletAmountPoint.setText(points);
        txtTotalAmount.setTextSize(10);
        txtWalletAmountPoint.setTextSize(10);
        tvNotePoints = rootView.findViewById(R.id.tvNotePoints);
        tvNotePoints.setTextSize(14);
        txtAmountRemaining = rootView.findViewById(R.id.txtAmountRemaining);
        txtAmountRemaining.setTextSize(10);
        txtAmountUsed = rootView.findViewById(R.id.txtAmountUsed);
        txtAmountUsed.setTextSize(10);
        loadAnimation(flTotalAmount);
        loadAnimation(flRemainingPoints);
        rootView.findViewById(R.id.progressLoad).setVisibility(View.GONE);

        emptyElement.setVisibility(View.VISIBLE);
//        lvWalletNoteList.setVisibility(View.GONE);

        return rootView;
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
