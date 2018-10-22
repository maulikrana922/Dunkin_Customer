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

import com.dunkin.customer.AppPaymentActivity;
import com.dunkin.customer.CounterOrderPaymentActivity;
import com.dunkin.customer.GiftDetailActivity;
import com.dunkin.customer.OfferDetailActivity;
import com.dunkin.customer.OfferPaymentActivity;
import com.dunkin.customer.OrderHistoryDetailActivity;
import com.dunkin.customer.PromoCodeDetailActivity;
import com.dunkin.customer.R;
import com.dunkin.customer.RegisterActivity;
import com.dunkin.customer.Utils.AppUtils;
import com.dunkin.customer.Utils.Callback;
import com.dunkin.customer.Utils.Dunkin_Log;
import com.dunkin.customer.adapters.NotificationAdapter;
import com.dunkin.customer.constants.AppConstants;
import com.dunkin.customer.controllers.AppController;
import com.dunkin.customer.models.NotificationModel;
import com.dunkin.customer.models.NotificationResponseModel;
import com.dunkin.customer.widget.RelativeLayoutButton;
import com.fasterxml.jackson.core.type.TypeReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class NotificationFragment extends Fragment {

    private View rootView;
    private Context context;
    private ProgressBar progressLoading;
    private ListView lvList;
    private RelativeLayoutButton btnRemove, btnRemoveSelected;
    private List<NotificationModel> notificationList;
    private NotificationAdapter notificationAdapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_notification, container, false);
        btnRemove = (RelativeLayoutButton) rootView.findViewById(R.id.btnRemove);
        btnRemove.imgIcon.setImageResource(R.drawable.ic_white_submit);
        btnRemove.btnText.setText(getString(R.string.btn_remove_notification));
        btnRemove.setVisibility(View.GONE);

        btnRemoveSelected = (RelativeLayoutButton) rootView.findViewById(R.id.btnRemoveSelected);
        btnRemoveSelected.imgIcon.setImageResource(R.drawable.ic_white_submit);
        btnRemoveSelected.btnText.setText(getString(R.string.btn_remove_selected_notification));
        btnRemoveSelected.setVisibility(View.GONE);

        notificationList = new ArrayList<>();

        progressLoading = (ProgressBar) rootView.findViewById(R.id.progressLoad);
        lvList = (ListView) rootView.findViewById(R.id.lvLoadList);

        try {
            getDataFromAPI();
        } catch (Exception e) {
            e.printStackTrace();
        }

        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final JSONArray jsonArray = new JSONArray();
                final List<NotificationModel> list = new ArrayList<>();
                for (NotificationModel nm : notificationList) {
                    jsonArray.put(nm.getNotification_id());
                    list.add(nm);
                }

                if (list.size() > 0) {
                    final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                    dialog.setMessage(R.string.msg_remove_all_notification);
                    dialog.setTitle(R.string.app_name);
                    dialog.setPositiveButton(getString(R.string.al_yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();
                            try {
                                JSONObject jsonRequest = new JSONObject();
                                try {
                                    jsonRequest.put("email", AppUtils.getAppPreference(context).getString(AppConstants.USER_EMAIL_ADDRESS, ""));
                                    jsonRequest.put("notification_id", jsonArray);

                                    AppController.removeAllNotifications(context, jsonRequest, new Callback() {
                                        @Override
                                        public void run(Object result) throws JSONException, IOException {
                                            JSONObject jsonResponse = new JSONObject((String) result);

                                            if (jsonResponse.getInt("success") == 1) {
                                                notificationList = new ArrayList<>();
                                                notificationAdapter = new NotificationAdapter(context, notificationList);
                                                lvList.setAdapter(notificationAdapter);
                                                lvList.setEmptyView(rootView.findViewById(R.id.emptyElement));
                                                removeFromList(list);
                                            } else if (jsonResponse.getInt("success") == 100) {
                                                AppUtils.showToastMessage(context, jsonResponse.getString("message"));
                                            }else if(jsonResponse.getInt("success") == 99) {
                                                displayDialog(jsonResponse.getString("message"));
                                            }
                                        }
                                    });
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
//                                AppUtils.showToastMessage(context, getString(R.string.system_error));
                            }
                        }
                    });

                    dialog.setNegativeButton(getString(R.string.al_no), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    dialog.create().show();
                }
            }
        });

        btnRemoveSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final JSONArray jsonArray = new JSONArray();
                final List<NotificationModel> list = new ArrayList<>();
                for (NotificationModel nm : notificationList) {
                    if (nm.isSelected()) {
                        jsonArray.put(nm.getNotification_id());
                        list.add(nm);
                    }
                }

                if (list != null && list.size() > 0) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                    dialog.setMessage(R.string.txt_al_remove_message);
                    dialog.setTitle(R.string.app_name);
                    dialog.setPositiveButton(getString(R.string.al_yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();
                            try {
                                JSONObject jsonRequest = new JSONObject();
                                try {
                                    jsonRequest.put("email", AppUtils.getAppPreference(context).getString(AppConstants.USER_EMAIL_ADDRESS, ""));
                                    jsonRequest.put("notification_id", jsonArray);

                                    AppController.removeAllNotifications(context, jsonRequest, new Callback() {
                                        @Override
                                        public void run(Object result) throws JSONException, IOException {
                                            JSONObject jsonResponse = new JSONObject((String) result);

                                            if (jsonResponse.getInt("success") == 1) {
                                                removeFromList(list);
                                            } else if (jsonResponse.getInt("success") == 100) {
                                                AppUtils.showToastMessage(context, jsonResponse.getString("message"));
                                            }else if(jsonResponse.getInt("success") == 99) {
                                                displayDialog(jsonResponse.getString("message"));
                                            }
                                        }
                                    });
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
//                                AppUtils.showToastMessage(context, getString(R.string.system_error));
                            }
                        }
                    });

                    dialog.setNegativeButton(getString(R.string.al_no), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    dialog.create().show();
                }
            }
        });

        lvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                NotificationModel nm = (NotificationModel) parent.getAdapter().getItem(position);

                try {
                    isNotificationRead(nm.getNotification_id(), parent, position);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        return rootView;
    }

    private void removeFromList(List<NotificationModel> list) {
        if (list != null && list.size() > 0)
            notificationList.removeAll(list);

        list.clear();
        notificationAdapter.notifyDataSetChanged();
        if (notificationList != null && notificationList.size() > 0) {
            btnRemove.setVisibility(View.VISIBLE);
            btnRemoveSelected.setVisibility(View.VISIBLE);
        } else {
            btnRemove.setVisibility(View.GONE);
            btnRemoveSelected.setVisibility(View.GONE);
        }
    }

    private void getDataFromAPI() throws JSONException {
        AppController.getNotifications(context, new Callback() {
            @Override
            public void run(Object result) throws JSONException, IOException {
                JSONObject jsonResponse = new JSONObject((String) result);

                if (jsonResponse.getInt("success") == 1) {
                    notificationList = AppUtils.getJsonMapper().readValue(jsonResponse.getJSONArray("notificationList").toString(), new TypeReference<List<NotificationModel>>() {
                    });
                } else if (jsonResponse.getInt("success") == 100) {
                    AppUtils.showToastMessage(context, jsonResponse.getString("message"));
                }else if(jsonResponse.getInt("success") == 99) {
                    displayDialog(jsonResponse.getString("message"));
                }
                progressLoading.setVisibility(View.GONE);

                notificationAdapter = new NotificationAdapter(context, notificationList);
                lvList.setAdapter(notificationAdapter);
                lvList.setEmptyView(rootView.findViewById(R.id.emptyElement));

                if (notificationList != null && notificationList.size() > 0) {
                    btnRemove.setVisibility(View.VISIBLE);
                    btnRemoveSelected.setVisibility(View.VISIBLE);
                } else {
                    btnRemove.setVisibility(View.GONE);
                    btnRemoveSelected.setVisibility(View.GONE);
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

    private void isNotificationRead(int notificationId, final AdapterView<?> parent, final int position) throws JSONException {
        try {
            if (notificationList.get(position).getIs_read() == 0) {
                AppController.updateIsReadNotification(context, notificationId, new Callback() {
                    @Override
                    public void run(Object result) throws JSONException, IOException {
                        JSONObject jsonResponse = new JSONObject((String) result);

                        if (jsonResponse.getInt("success") == 1) {
                            redirectToSpecificActivity(parent, position, true);
                        } else if (jsonResponse.getInt("success") == 100) {
                            AppUtils.showToastMessage(context, jsonResponse.getString("message"));
                        }else if(jsonResponse.getInt("success") == 99) {
                            displayDialog(jsonResponse.getString("message"));
                        }
                    }
                });
            } else if (notificationList.get(position).getIs_read() == 1) {
                redirectToSpecificActivity(parent, position, false);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void redirectToSpecificActivity(final AdapterView<?> parent, final int position, boolean isNotificationUpdate) {
        NotificationModel nm = (NotificationModel) parent.getAdapter().getItem(position);
        NotificationResponseModel nmResponse = nm.getResponse();
        Intent intent;

        if (isNotificationUpdate) {
            notificationList.get(position).setIs_read(1);
            notificationAdapter.notifyDataSetChanged();
        }

        if (nmResponse.getMsgtype() == 1) {

            Dunkin_Log.i("MSG Type", "" + nmResponse.getMsgtype());

            intent = new Intent(context, AppPaymentActivity.class);
            intent.putExtra("order_id", nmResponse.getOrderId());
            intent.putExtra("table_id", nmResponse.getTable_id());
            intent.putExtra("restaurant_id", nmResponse.getRestaurant_id());

            startActivity(intent);
        } else if (nmResponse.getMsgtype() == 3) {

            Dunkin_Log.i("MSG Type", "" + nmResponse.getMsgtype());

            intent = new Intent(context, OrderHistoryDetailActivity.class);
            intent.putExtra("orderId", nmResponse.getOrderId());

            startActivity(intent);
        } else if (nmResponse.getMsgtype() == 4) {

            Dunkin_Log.i("MSG Type", "" + nmResponse.getMsgtype());

            intent = new Intent(context, CounterOrderPaymentActivity.class);
            intent.putExtra("orderId", nmResponse.getOrderId());
            intent.putExtra("restaurant_name", nmResponse.getRestaurant_name());

            startActivity(intent);
        } else if (nmResponse.getMsgtype() == 5) {

            Dunkin_Log.i("MSG Type", "" + nmResponse.getMsgtype());

            intent = new Intent(context, OfferPaymentActivity.class);
            intent.putExtra("offerId", nmResponse.getOfferId());
            intent.putExtra("country_id", nmResponse.getCountry_id());
            intent.putExtra("reference_id", nmResponse.getReference_id());

            startActivity(intent);
        } else if (nmResponse.getMsgtype() == 6) {

            Dunkin_Log.i("MSG Type", "" + nmResponse.getMsgtype());

            intent = new Intent(context, OfferDetailActivity.class);
            intent.putExtra("offerId", nmResponse.getOfferId());

            startActivity(intent);
        } else if (nmResponse.getMsgtype() == 8) {

            Dunkin_Log.i("MSG Type", "" + nmResponse.getMsgtype());

            //((HomeActivity) getActivity()).navigateAndCheckItem(AppConstants.MENU_WALLET);

            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frFragmentContainer, new MyWalletFragment())
                    //.addToBackStack(null)
                    .commit();

        } else if (nmResponse.getMsgtype() == 9) {

            Dunkin_Log.i("MSG Type", "" + nmResponse.getMsgtype());

            intent = new Intent(context, GiftDetailActivity.class);
            intent.putExtra("giftOrderId", nmResponse.getPurchaseId());

            startActivity(intent);
        } else if (nmResponse.getMsgtype() == 13) {

            Dunkin_Log.i("MSG Type", "" + nmResponse.getMsgtype());

            intent = new Intent(context, PromoCodeDetailActivity.class);
            intent.putExtra("promoId", nmResponse.getPromoId());

            startActivity(intent);
        } else if (nmResponse.getMsgtype() == 14) {

            if (nmResponse.getPromoType() != null && nmResponse.getPromoType().equals("1")) {
                Dunkin_Log.i("MSG Type", "" + nmResponse.getMsgtype());

                //((HomeActivity) getActivity()).navigateAndCheckItem(AppConstants.MENU_WALLET);

                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frFragmentContainer, new MyWalletFragment())
                        //.addToBackStack(null)
                        .commit();
            } else if (nmResponse.getPromoType() != null && nmResponse.getPromoType().equals("2")) {
                Dunkin_Log.i("MSG Type", "" + nmResponse.getMsgtype());

                intent = new Intent(context, OfferDetailActivity.class);
                intent.putExtra("offerId", nmResponse.getOfferId());

                startActivity(intent);
            } else if (nmResponse.getPromoType() != null && nmResponse.getPromoType().equals("3")) {
                Dunkin_Log.i("MSG Type", "" + nmResponse.getMsgtype());

                intent = new Intent(context, OfferDetailActivity.class);
                intent.putExtra("offerId", nmResponse.getOfferId());

                startActivity(intent);
            } else {
                Dunkin_Log.i("MSG Type", "" + nmResponse.getMsgtype());

                //((HomeActivity) getActivity()).navigateAndCheckItem(AppConstants.MENU_WALLET);

                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frFragmentContainer, new MyWalletFragment())
                        //.addToBackStack(null)
                        .commit();
            }
        } else {

            Dunkin_Log.i("MSG Type", "" + nmResponse.getMsgtype());

            //getActivity().finish();
            //intent = new Intent(context, HomeActivity.class);
        }
    }
}
