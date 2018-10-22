package com.dunkin.customer.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.dunkin.customer.ProductDetailActivity;
import com.dunkin.customer.R;
import com.dunkin.customer.RegisterActivity;
import com.dunkin.customer.Utils.AppUtils;
import com.dunkin.customer.Utils.Callback;
import com.dunkin.customer.adapters.ProductAdapter;
import com.dunkin.customer.controllers.AppController;
import com.dunkin.customer.models.ChildCatModel;
import com.dunkin.customer.models.ProductModel;
import com.fasterxml.jackson.core.type.TypeReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


public class ProductListFragment extends Fragment {
    private static final String ARG_POSITION = "position";
    View rootView;
    int country_id;
    private Context context;
    private int page = 1;
    private ListView lvList;
    private ChildCatModel childCatModel;
    private ProductAdapter productAdapter;
    private List<ProductModel> productModelListl;
    private String currency_code = null;
    private int updatedQuantity;
    private ProgressBar progressLoading;

    public static ProductListFragment newInstance(int position, ChildCatModel category, int country_id) {
        ProductListFragment f = new ProductListFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        b.putSerializable("category", category);
        b.putInt("country_id", country_id);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        childCatModel = (ChildCatModel) getArguments().getSerializable("category");
        country_id = getArguments().getInt("country_id", 0);
        setHasOptionsMenu(true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.loadlistview, container, false);

        lvList = (ListView) rootView.findViewById(R.id.lvLoadList);

        progressLoading = (ProgressBar) rootView.findViewById(R.id.progressLoad);
        rootView.findViewById(R.id.linear2).setVisibility(View.GONE);
        productModelListl = new ArrayList<>();

        getDataFromAPI();

        lvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ProductModel pm = (ProductModel) parent.getAdapter().getItem(position);

                Intent i = new Intent(context, ProductDetailActivity.class);
                pm.setCurrency(currency_code);

                i.putExtra("product", productModelListl.get(position));
                i.putExtra("country_id", country_id);

                startActivity(i);
            }
        });
        return rootView;
    }

    private void getDataFromAPI() {

        try {
            AppController.getProductsFromCatagory(context, childCatModel.getCategoryParentid(), country_id, childCatModel.getCategoryId(), new Callback() {
                @Override
                public void run(Object result) throws JSONException, IOException {
                    JSONObject jsonResponse = new JSONObject((String) result);

                    progressLoading.setVisibility(View.GONE);

                    if (jsonResponse.getInt("success") == 1) {
                        rootView.findViewById(R.id.emptyElement).setVisibility(View.GONE);
                        lvList.setVisibility(View.VISIBLE);

                        //try {
                        productModelListl = AppUtils.getJsonMapper().readValue(jsonResponse.getJSONArray("productList").toString(), new TypeReference<List<ProductModel>>() {
                        });
                        currency_code = jsonResponse.getString("currency_code");

                            /*getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {*/
                        productAdapter = new ProductAdapter(context, productModelListl, country_id, currency_code != null ? currency_code : "");
                        lvList.setAdapter(productAdapter);
                        lvList.setEmptyView(rootView.findViewById(R.id.emptyElement));
                                    /*} catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });*/
                       /* } catch (Exception e) {
                            e.printStackTrace();
                        }*/
                    } else if (jsonResponse.getInt("success") == 100) {
                        AppUtils.showToastMessage(context, jsonResponse.getString("message"));
                    }else if (jsonResponse.getInt("success") == 99) {
                        displayDialog(jsonResponse.getString("message"));
                    }else {
                        rootView.findViewById(R.id.emptyElement).setVisibility(View.VISIBLE);
                        lvList.setVisibility(View.GONE);
                    }
                }
            });
        } catch (JSONException | UnsupportedEncodingException e) {
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
}
