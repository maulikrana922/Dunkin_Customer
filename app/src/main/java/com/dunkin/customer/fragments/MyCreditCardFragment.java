package com.dunkin.customer.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;

import com.dunkin.customer.NewCardActivity;
import com.dunkin.customer.R;
import com.dunkin.customer.Utils.AppUtils;
import com.dunkin.customer.Utils.Callback;
import com.dunkin.customer.adapters.CreditCardAdapter;
import com.dunkin.customer.controllers.AppController;
import com.dunkin.customer.models.CreditCardModel;
import com.fasterxml.jackson.core.type.TypeReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MyCreditCardFragment extends Fragment {
    MyCreditCardFragment fragment;
    View rootView;
    private Context context;
    private ListView lvCardList;
    private CreditCardAdapter creditCardAdapter;
    private List<CreditCardModel> creditCardList;
    private ProgressBar progressLoading;
    private LinearLayout cardContainer;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        setHasOptionsMenu(true);
        rootView = inflater.inflate(R.layout.fragment_my_credit_card, container, false);

        progressLoading = (ProgressBar) rootView.findViewById(R.id.progressLoad);
        cardContainer = (LinearLayout) rootView.findViewById(R.id.cardContainer);
        lvCardList = (ListView) rootView.findViewById(R.id.cardList);

        creditCardList = new ArrayList<>();

        getDataFromAPI();

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.credit_card_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_add_card) {
            startActivityForResult(new Intent(context, NewCardActivity.class), 110);
        }

        return super.onOptionsItemSelected(item);
    }

    private void getDataFromAPI() {
        AppController.getCardList(context, new Callback() {
            @Override
            public void run(Object result) throws JSONException, IOException {
                JSONObject jsonResponse = new JSONObject((String) result);
                progressLoading.setVisibility(View.GONE);
                cardContainer.setVisibility(View.VISIBLE);
                creditCardList = AppUtils.getJsonMapper().readValue(jsonResponse.getJSONArray("cardList").toString(), new TypeReference<List<CreditCardModel>>() {
                });
                creditCardAdapter = new CreditCardAdapter(context, creditCardList, MyCreditCardFragment.this);
                lvCardList.setAdapter(creditCardAdapter);
                lvCardList.setEmptyView(rootView.findViewById(R.id.emptyElement));
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 110 && resultCode == Activity.RESULT_OK) {
            getDataFromAPI();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
