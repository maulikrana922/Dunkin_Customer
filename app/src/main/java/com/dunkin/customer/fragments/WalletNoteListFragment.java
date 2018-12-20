package com.dunkin.customer.fragments;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.dunkin.customer.R;
import com.dunkin.customer.Utils.AppUtils;
import com.dunkin.customer.adapters.NewWalletNoteListAdapter;
import com.dunkin.customer.models.WalletModel;

/**
 * Created by Admin on 9/9/2015.
 */
public class WalletNoteListFragment extends Fragment {

    private WalletModel wallet;
    private String tempRemainingPoints, remainingPoints;
    private FrameLayout flTotalAmount, flRemainingPoints;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        wallet = (WalletModel) bundle.getSerializable("notes");
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
//        ListView lvWalletNoteList = (ListView) rootView.findViewById(R.id.walletNoteList);
        RecyclerView lvWalletNoteList = (RecyclerView) rootView.findViewById(R.id.walletNoteList);
        flTotalAmount = (FrameLayout) rootView.findViewById(R.id.flTotalAmount);
        flRemainingPoints = (FrameLayout) rootView.findViewById(R.id.flRemainingPoints);
//        String amount = getString(R.string.txt_my_wallet_amount, AppUtils.CurrencyFormat(Double.parseDouble(wallet.getTotal())) + " " + wallet.getCurrency()) + "\n" +
//                getString(R.string.txt_my_wallet_remaining_amount) + " " + AppUtils.CurrencyFormat(Double.parseDouble(remainingPoints));

        String amount = AppUtils.CurrencyFormat(Double.parseDouble(wallet.getTotal())) + " " + wallet.getCurrency();
        String points = AppUtils.CurrencyFormat(Double.parseDouble(remainingPoints));
        txtTotalAmount.setText(amount);
        txtWalletAmountPoint.setText(points);
        loadAnimation(flTotalAmount);
        loadAnimation(flRemainingPoints);
        rootView.findViewById(R.id.progressLoad).setVisibility(View.GONE);
        lvWalletNoteList.setNestedScrollingEnabled(true);
        if (wallet.getNotes() != null && wallet.getNotes().size() > 0) {
            emptyElement.setVisibility(View.GONE);
            lvWalletNoteList.setVisibility(View.VISIBLE);

//            WalletNoteListAdapter walletNoteListAdapter = new WalletNoteListAdapter(getActivity(), wallet.getNotes());
//            lvWalletNoteList.setAdapter(walletNoteListAdapter);
//            lvWalletNoteList.setEmptyView(rootView.findViewById(R.id.emptyElement));
            NewWalletNoteListAdapter walletNoteListAdapter = new NewWalletNoteListAdapter(getActivity(), wallet.getNotes());
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
            lvWalletNoteList.setLayoutManager(layoutManager);
            lvWalletNoteList.setAdapter(walletNoteListAdapter);
//            lvWalletNoteList.setEmptyView(rootView.findViewById(R.id.emptyElement));
        } else {
            emptyElement.setVisibility(View.VISIBLE);
            lvWalletNoteList.setVisibility(View.GONE);
        }
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
