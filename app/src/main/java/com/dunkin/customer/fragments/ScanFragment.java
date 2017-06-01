package com.dunkin.customer.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dunkin.customer.PromoListActivity;
import com.dunkin.customer.R;
import com.dunkin.customer.dialogs.ScanAndWinDialog;

import static com.dunkin.customer.HomeActivity.isOfferEnable;
import static com.dunkin.customer.HomeActivity.scanOfferImagePath;

/**
 * Created by qtm-c-android on 1/6/17.
 */

public class ScanFragment extends Fragment {

    private Context mContext;
    private View rootView;
    private TextView txtScan, txtPlay;

    public ScanFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static ScanFragment newInstance(String param1, String param2) {
        ScanFragment fragment = new ScanFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_scan, container, false);

        txtScan = (TextView) rootView.findViewById(R.id.txtScan);
        txtPlay = (TextView) rootView.findViewById(R.id.txtPlay);

        txtScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOfferEnable.equalsIgnoreCase("1"))
                    ScanAndWinDialog.newInstance(mContext, scanOfferImagePath, true).show();
            }
        });

        txtPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, PromoListActivity.class);
                startActivity(i);
            }
        });

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }
}

