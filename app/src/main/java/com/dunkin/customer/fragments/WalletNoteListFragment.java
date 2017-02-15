package com.dunkin.customer.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.dunkin.customer.R;
import com.dunkin.customer.Utils.AppUtils;
import com.dunkin.customer.adapters.WalletNoteListAdapter;
import com.dunkin.customer.models.WalletModel;

/**
 * Created by Admin on 9/9/2015.
 */
public class WalletNoteListFragment extends Fragment {

    private WalletModel wallet;
    private String tempRemainingPoints, remainingPoints;

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
        TextView emptyElement = (TextView) rootView.findViewById(R.id.emptyElement);
        ListView lvWalletNoteList = (ListView) rootView.findViewById(R.id.walletNoteList);

        String amount = getString(R.string.txt_my_wallet_amount, AppUtils.CurrencyFormat(Double.parseDouble(wallet.getTotal())) + " " + wallet.getCurrency()) + "\n" +
                getString(R.string.txt_my_wallet_remaining_amount) + " " + AppUtils.CurrencyFormat(Double.parseDouble(remainingPoints));

        txtTotalAmount.setText(amount);

        rootView.findViewById(R.id.progressLoad).setVisibility(View.GONE);

        if (wallet.getNotes() != null && wallet.getNotes().size() > 0) {
            emptyElement.setVisibility(View.GONE);
            lvWalletNoteList.setVisibility(View.VISIBLE);

            WalletNoteListAdapter walletNoteListAdapter = new WalletNoteListAdapter(getActivity(), wallet.getNotes());
            lvWalletNoteList.setAdapter(walletNoteListAdapter);
            lvWalletNoteList.setEmptyView(rootView.findViewById(R.id.emptyElement));
        } else {
            emptyElement.setVisibility(View.VISIBLE);
            lvWalletNoteList.setVisibility(View.GONE);
        }

        return rootView;
    }
}
