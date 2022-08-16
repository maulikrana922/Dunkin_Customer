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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dunkin.customer.NewHomeActivity;
import com.dunkin.customer.R;
import com.dunkin.customer.RegisterActivity;
import com.dunkin.customer.Utils.AppUtils;
import com.dunkin.customer.Utils.Callback;
import com.dunkin.customer.adapters.RatingAdapter;
import com.dunkin.customer.constants.AppConstants;
import com.dunkin.customer.controllers.AppController;
import com.dunkin.customer.listener.RatingListener;
import com.dunkin.customer.models.RatingModel;
import com.dunkin.customer.models.RestaurantModel;
import com.dunkin.customer.widget.RelativeLayoutButton;
import com.fasterxml.jackson.core.type.TypeReference;

import org.apache.commons.lang.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class FeedbackFragment extends Fragment implements View.OnClickListener, RatingListener {

    private Context context;
    private ListView lvRatings;
    private EditText edComments;
    private TextView spSelectRestaurant;
    private List<RatingModel> ratingModelList;
    private List<RestaurantModel> restaurantList;
    private ArrayAdapter<RestaurantModel> restaurantAdapter;
    private RestaurantModel restaurantModel;
    private RatingAdapter ratingAdapter;
    private String[] questions;
    private ProgressBar progressLoading;

    private RelativeLayoutButton btnSubmit;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_feedback, container, false);

        questions = context.getResources().getStringArray(R.array.feedback_question);

        lvRatings = (ListView) rootView.findViewById(R.id.lvRatings);
        edComments = (EditText) rootView.findViewById(R.id.edComments);
        progressLoading = (ProgressBar) rootView.findViewById(R.id.progressLoad);
        progressLoading.setVisibility(View.GONE);
        spSelectRestaurant = (TextView) rootView.findViewById(R.id.spSelectRestaurent);
        btnSubmit = (RelativeLayoutButton) rootView.findViewById(R.id.btnSubmit);
        btnSubmit.imgIcon.setImageResource(R.drawable.ic_submit);
        btnSubmit.btnText.setText(getString(R.string.btn_submit));
        btnSubmit.setVisibility(View.GONE);

        ratingModelList = new ArrayList<>();

        spSelectRestaurant.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        if (v == spSelectRestaurant) {
            try {
                AppController.getRestaurantList(context, true, new Callback() {
                    @Override
                    public void run(Object result) throws JSONException, IOException {

                        JSONObject jsonResponse = new JSONObject((String) result);

                        //Dunkin_Log.e("DataResponse", jsonResponse.toString());

                        if (jsonResponse.getInt("success") == 1) {

                            restaurantList = AppUtils.getJsonMapper().readValue(jsonResponse.getJSONArray("restaurantList").toString(), new TypeReference<List<RestaurantModel>>() {
                            });

                            restaurantAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, restaurantList);

                            openDialog(getString(R.string.txt_select_restaurant));
                        } else if (jsonResponse.getInt("success") == 100) {
                            AppUtils.showToastMessage(context, jsonResponse.getString("message"));
                        }else {
                            if(jsonResponse.getInt("success") == 99) {
                                displayDialog(jsonResponse.getString("message"));
                            }else{
                                AppUtils.showToastMessage(context, getString(R.string.system_error));
                            }
                        }
                    }
                });
            } catch (JSONException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        if (v == btnSubmit) {
            try {
                if (restaurantList != null) {
                    postRestaurantRatings();
                } else {
                    AppUtils.showToastMessage(context, getString(R.string.txt_select_restaurant));
                }
            } catch (UnsupportedEncodingException | JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void getDataFromAPI() throws UnsupportedEncodingException, JSONException {
        AppController.getRatingList(context, restaurantModel.getRestaurantId(), new Callback() {
            @Override
            public void run(Object result) throws JSONException, IOException {

                JSONObject jsonResponse = new JSONObject((String) result);

                //Dunkin_Log.e("DataResponse", jsonResponse.toString());

                progressLoading.setVisibility(View.GONE);

                if (jsonResponse.getInt("success") == 1) {

                    btnSubmit.setVisibility(View.VISIBLE);

                    JSONArray jsonArray = jsonResponse.getJSONArray("message");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        RatingModel rm = new RatingModel();
                        rm.setQuestion(questions[i]);
                        rm.setAverageRating(Double.parseDouble(String.valueOf(jsonArray.get(i))));
                        ratingModelList.add(rm);
                    }

                    ratingAdapter = new RatingAdapter(context, ratingModelList, FeedbackFragment.this);
                    lvRatings.setAdapter(ratingAdapter);

                    /*try {
                        AppUtils.setListViewHeightBasedOnChildren(ratingAdapter, lvRatings);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }*/
                }else if (jsonResponse.getInt("success") == 100) {
                    AppUtils.showToastMessage(context, jsonResponse.getString("message"));
                } else if (jsonResponse.getInt("success") == 99) {
                    displayDialog(jsonResponse.getString("message"));
                }else {
                    btnSubmit.setVisibility(View.GONE);
                }
            }
        });
    }

    private void displayDialog(String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(context, RegisterActivity.class));
                        ((Activity)context).finish();
                    }
                });

        AlertDialog alert = builder.create();
        alert.setTitle(getResources().getString(R.string.app_name));
        alert.show();
    }

    private void postRestaurantRatings() throws UnsupportedEncodingException, JSONException {
        List<Double> ratings = new ArrayList<>();

        List<RatingModel> updatedRatingList = ratingAdapter.getUpdatedList();

        for (RatingModel rm : updatedRatingList) {
            ratings.add(rm.getRates());
        }

        final JSONObject jsonRequest = new JSONObject();

        jsonRequest.put("email", AppUtils.getAppPreference(context).getString(AppConstants.USER_EMAIL_ADDRESS, ""));
        jsonRequest.put("country_id", AppUtils.getAppPreference(context).getInt(AppConstants.USER_COUNTRY, -1));
        jsonRequest.put("restaurantId", restaurantModel.getRestaurantId());
        jsonRequest.put("feedback", edComments.getText().toString());
        jsonRequest.put("lang_flag", AppUtils.getAppPreference(context).getString(AppConstants.USER_LANGUAGE, AppConstants.LANG_EN));

        if (ratings == null) {
            ratings.add(0.0);
            ratings.add(0.0);
            ratings.add(0.0);
            ratings.add(0.0);
            ratings.add(0.0);
            jsonRequest.put("rating", ratings);
        }
        jsonRequest.put("rating", ratings);

        AppController.postComments(context, jsonRequest.toString(), new Callback() {
            @Override
            public void run(Object result) throws JSONException, IOException {

                JSONObject jsonResponse = new JSONObject((String) result);

                //Dunkin_Log.e("DataResponse", jsonResponse.toString());

                if (jsonResponse.getInt("success") == 1) {
//                    AppUtils.showToastMessage(context, jsonResponse.getString("message"));
                    final AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    alert.setMessage(StringEscapeUtils.unescapeJava(jsonResponse.getString("message")));
                    alert.setPositiveButton(context.getString(R.string.al_ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            ((NewHomeActivity) getActivity()).addHomeFragment();
                        }
                    });
                    alert.show();
                    /*FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    FeedbackFragment feedbackFragment = new FeedbackFragment();
                    fragmentTransaction.replace(R.id.content, feedbackFragment);
                    fragmentTransaction.commit();*/
//                    ((HomeActivity) getActivity()).navigateAndCheckItem(AppConstants.MENU_HOME);
                }else if (jsonResponse.getInt("success") == 100) {
                    AppUtils.showToastMessage(context, jsonResponse.getString("message"));
                }else if (jsonResponse.getInt("success") == 99) {
                    displayDialog(jsonResponse.getString("message"));
                } else {
                    AppUtils.showToastMessage(context, getString(R.string.system_error));
                }
            }
        });
    }

    private void openDialog(String title) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle(title);

        alert.setAdapter(restaurantAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                restaurantModel = restaurantAdapter.getItem(which);
                spSelectRestaurant.setText(restaurantModel.getRestaurantName());
                ratingModelList = new ArrayList<>();
                try {
                    progressLoading.setVisibility(View.VISIBLE);

                    getDataFromAPI();
                } catch (UnsupportedEncodingException | JSONException e) {
                    e.printStackTrace();
                }
                spSelectRestaurant.setText(restaurantModel.getRestaurantName());
            }
        });
        alert.create().show();
    }

    @Override
    public void onRatingChanged(int position, RatingModel model) {
        ratingModelList.get(position).setSelected(model.isSelected());
        ratingModelList.get(position).setRates(model.getRates());
        ratingAdapter.notifyDataSetChanged();
    }
}