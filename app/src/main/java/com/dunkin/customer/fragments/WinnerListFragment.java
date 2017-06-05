package com.dunkin.customer.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.dunkin.customer.R;
import com.dunkin.customer.Utils.AppUtils;

/**
 * Created by qtm-c-android on 5/6/17.
 */

public class WinnerListFragment extends Fragment {

    private String winnerImage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        winnerImage = bundle.getString("winnerImage");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_winner, container, false);
        ImageView imgWinner = (ImageView) rootView.findViewById(R.id.imgWinner);

        AppUtils.setImage(imgWinner, winnerImage);

        return rootView;
    }
}


