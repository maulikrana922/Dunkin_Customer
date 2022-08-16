package com.dunkin.customer.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.text.TextUtils;
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
import com.dunkin.customer.adapters.StaffRatingAdapter;
import com.dunkin.customer.constants.AppConstants;
import com.dunkin.customer.controllers.AppController;
import com.dunkin.customer.listener.StaffRatingListener;
import com.dunkin.customer.models.CatalogQuestionModel;
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

public class RateStaffFragment extends Fragment implements View.OnClickListener, StaffRatingListener {

    private Context context;
    private ListView lvRatings;
    private EditText etEmail,etFeedback;
    private TextView spSelectRestaurant, tvTotalCount;
    private List<CatalogQuestionModel> catalogQuestionModelList;
    private List<RestaurantModel> restaurantList;
    private ArrayAdapter<RestaurantModel> restaurantAdapter;
    private RestaurantModel restaurantModel;
    private StaffRatingAdapter staffRatingAdapter;
    private String[] questions;
    private ProgressBar progressLoading;

    private RelativeLayoutButton btnSubmit;
    private int totalRate;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_rate_staff, container, false);

        questions = context.getResources().getStringArray(R.array.feedback_question);

        lvRatings = (ListView) rootView.findViewById(R.id.lvRatings);
        etEmail = (EditText) rootView.findViewById(R.id.etEmail);
        etFeedback=(EditText)rootView.findViewById(R.id.etFeedback);
        tvTotalCount = (TextView) rootView.findViewById(R.id.tvTotalCount);
        tvTotalCount.setText(totalRate + " / " + 100);
        progressLoading = (ProgressBar) rootView.findViewById(R.id.progressLoad);
        progressLoading.setVisibility(View.GONE);
        spSelectRestaurant = (TextView) rootView.findViewById(R.id.spSelectRestaurent);
        btnSubmit = (RelativeLayoutButton) rootView.findViewById(R.id.btnSubmit);
        btnSubmit.imgIcon.setImageResource(R.drawable.ic_submit);
        btnSubmit.btnText.setText(getString(R.string.btn_submit));
        btnSubmit.setVisibility(View.GONE);

        catalogQuestionModelList = new ArrayList<>();

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
                        } else {
                            if (jsonResponse.getInt("success") == 99) {
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
                    putStaffAssessment();
                } else {
                    AppUtils.showToastMessage(context, getString(R.string.txt_select_restaurant));
                }
            } catch (UnsupportedEncodingException | JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void getDataFromAPI() throws UnsupportedEncodingException, JSONException {
        AppController.getCatalogQuestionList(context, restaurantModel.getRestaurantId(), new Callback() {
            @Override
            public void run(Object result) throws JSONException, IOException {

                JSONObject jsonResponse = new JSONObject((String) result);

                //Dunkin_Log.e("DataResponse", jsonResponse.toString());

                progressLoading.setVisibility(View.GONE);

                if (jsonResponse.getInt("success") == 1) {

                    btnSubmit.setVisibility(View.VISIBLE);

                    JSONArray jsonArray = jsonResponse.getJSONArray("QuestionList");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        CatalogQuestionModel questionModel = new CatalogQuestionModel();
                        questionModel.setQueId(jsonObject.getString("queId"));
                        questionModel.setQueTitle(jsonObject.getString("queTitle"));
                        catalogQuestionModelList.add(questionModel);
                    }

                    staffRatingAdapter = new StaffRatingAdapter(context, catalogQuestionModelList, RateStaffFragment.this);
                    lvRatings.setAdapter(staffRatingAdapter);

                    /*try {
                        AppUtils.setListViewHeightBasedOnChildren(staffRatingAdapter, lvRatings);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }*/
                } else if (jsonResponse.getInt("success") == 100) {
                    btnSubmit.setVisibility(View.GONE);
                    AppUtils.showToastMessage(context, jsonResponse.getString("message"));
                }else if (jsonResponse.getInt("success") == 99) {
                    displayDialog(jsonResponse.getString("message"));
                } else {
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

    private void putStaffAssessment() throws UnsupportedEncodingException, JSONException {
        if (TextUtils.isEmpty(etEmail.getText().toString().trim())) {
            AppUtils.showError(etEmail, getString(R.string.empty_email_phone));
        } else if (etEmail.getText().toString().trim().contains("@") && !android.util.Patterns.EMAIL_ADDRESS.matcher(etEmail.getText().toString().trim()).matches()) {
            AppUtils.showError(etEmail, getString(R.string.enter_valid_email));
        } else if (!etEmail.getText().toString().trim().contains("@") && !android.util.Patterns.PHONE.matcher(etEmail.getText().toString().trim()).matches()) {
            AppUtils.showError(etEmail, getString(R.string.enter_valid_phone));
        } else if (catalogQuestionModelList.get(0).getRating() == 0) {
            AppUtils.showError(etEmail, getString(R.string.give_rate_question));
        } else if (catalogQuestionModelList.get(1).getRating() == 0) {
            AppUtils.showError(etEmail, getString(R.string.give_rate_question));
        } else if (catalogQuestionModelList.get(2).getRating() == 0) {
            AppUtils.showError(etEmail, getString(R.string.give_rate_question));
        } else if (catalogQuestionModelList.get(3).getRating() == 0) {
            AppUtils.showError(etEmail, getString(R.string.give_rate_question));
        } else if (catalogQuestionModelList.get(4).getRating() == 0) {
            AppUtils.showError(etEmail, getString(R.string.give_rate_question));
        } else {

            List<CatalogQuestionModel> updatedRatingList = staffRatingAdapter.getUpdatedList();

            final JSONObject jsonRequest = new JSONObject();

            jsonRequest.put("email", AppUtils.getAppPreference(context).getString(AppConstants.USER_EMAIL_ADDRESS, ""));
            jsonRequest.put("country_id", String.valueOf(AppUtils.getAppPreference(context).getInt(AppConstants.USER_COUNTRY, -1)));
            jsonRequest.put("restaurant_id", String.valueOf(restaurantModel.getRestaurantId()));
            jsonRequest.put("lang_flag", AppUtils.getAppPreference(context).getString(AppConstants.USER_LANGUAGE, AppConstants.LANG_EN));
            jsonRequest.put("userscan", etEmail.getText().toString().trim());
            jsonRequest.put("message", etFeedback.getText().toString().trim());
            int j = 1;
            for (int i = 0; i < updatedRatingList.size(); i++) {
                jsonRequest.put("title" + j, updatedRatingList.get(i).getQueTitle());
                jsonRequest.put("rate" + j, String.valueOf(updatedRatingList.get(i).getRating()));
                j++;
            }
            jsonRequest.put("totalRate", String.valueOf(totalRate));

            AppController.putStaffAssessment(context, jsonRequest.toString(), new Callback() {
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
                    } else if (jsonResponse.getInt("success") == 100 || jsonResponse.getInt("success") == 0) {
                        AppUtils.showToastMessage(context, jsonResponse.getString("message"));
                    }else if (jsonResponse.getInt("success") == 99) {
                        displayDialog(jsonResponse.getString("message"));
                    } else {
                        AppUtils.showToastMessage(context, getString(R.string.system_error));
                    }
                }
            });
        }
    }

    private void openDialog(String title) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle(title);

        alert.setAdapter(restaurantAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                restaurantModel = restaurantAdapter.getItem(which);
                spSelectRestaurant.setText(restaurantModel.getRestaurantName());
                catalogQuestionModelList = new ArrayList<>();
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
    public void onRatingChanged(int position, int rat) {
        if(catalogQuestionModelList.size()>0) {
            catalogQuestionModelList.get(position).setRating(rat);
//        staffRatingAdapter.notifyDataSetChanged();

            totalRate = 0;
            for (CatalogQuestionModel catalogQuestionModel : catalogQuestionModelList) {
                totalRate += catalogQuestionModel.getRating();

            }
            tvTotalCount.setText(totalRate + " / " + 100);
        }
    }
}