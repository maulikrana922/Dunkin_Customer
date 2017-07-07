package com.dunkin.customer.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.dunkin.customer.AddPromocodeActivity;
import com.dunkin.customer.PromoListActivity;
import com.dunkin.customer.R;
import com.dunkin.customer.SimpleScannerPromotionActivity;
import com.dunkin.customer.Utils.AppUtils;
import com.dunkin.customer.Utils.Callback;
import com.dunkin.customer.adapters.PromoAdapter;
import com.dunkin.customer.controllers.AppController;
import com.dunkin.customer.dialogs.ScanAndWinDialog;
import com.dunkin.customer.models.PromoData;
import com.dunkin.customer.models.PromoModel;
import com.fasterxml.jackson.core.type.TypeReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.dunkin.customer.HomeActivity.isOfferEnable;
import static com.dunkin.customer.HomeActivity.scanOfferImagePath;
import static com.dunkin.customer.constants.AppConstants.context;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by qtm-c-android on 1/6/17.
 */

public class ScanFragment extends Fragment implements Animation.AnimationListener {

    private Context mContext;
    private View rootView;
    private ImageView txtScan, txtPlay, txtPromo;
    private String playImage, scanImage, promoImage, promoStatus;
    private List<PromoModel> playModelList = new ArrayList<>();
    private static final int SCANNER_PROMOTION_REQUEST_CODE = 0x111;
    private LinearLayout learMain;
    Animation animFadein;
    private List<PromoData> promoDataList;
    private PromoAdapter promoAdapter;
    private View viewScan, viewPlay, viewPromo;
    private ProgressBar progressLoading;
    private ListView lvLoadList1;

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

        txtScan = (ImageView) rootView.findViewById(R.id.txtScan);
        txtPlay = (ImageView) rootView.findViewById(R.id.txtPlay);
        txtPromo = (ImageView) rootView.findViewById(R.id.txtPromo);
        learMain = (LinearLayout) rootView.findViewById(R.id.learMain);

        viewScan = (View) rootView.findViewById(R.id.viewScan);
        viewPlay = (View) rootView.findViewById(R.id.viewPlay);
        viewPromo = (View) rootView.findViewById(R.id.viewPromo);

        progressLoading = (ProgressBar) rootView.findViewById(R.id.progressLoad);
        lvLoadList1 = (ListView) rootView.findViewById(R.id.lvLoadList1);

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
                getDataFromAPI();
            }
        });

        txtPromo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(promoStatus.equals("1"))
                {
                    chooseOption();
                }
            }
        });

        animFadein = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.fade_in);
        animFadein.setAnimationListener(this);

//        animation();
        fetchPromoImage();

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    public void fetchPromoImage() {
        try {
            AppController.fetchPromoImage(mContext, new Callback() {
                @Override
                public void run(Object result) throws JSONException, IOException {
                    String res = (String) result;
                    JSONObject jsonResponse = new JSONObject(res);
                    progressLoading.setVisibility(View.GONE);
                    if (jsonResponse.getString("success").equalsIgnoreCase("1")) {
                        JSONObject object = jsonResponse.getJSONObject("data");
                        promoDataList = AppUtils.getJsonMapper().readValue
                                (object.getJSONArray("image").toString(),
                                        new TypeReference<List<PromoData>>() {
                        });

                        if(promoDataList.size()>0) {
                            promoAdapter = new PromoAdapter(context, promoDataList);
                            lvLoadList1.setAdapter(promoAdapter);
//                            lvLoadList1.setEmptyView(rootView.findViewById(R.id.emptyElement));
                            for(int i=0; i<promoDataList.size(); i++) {
                                if(promoDataList.get(i).getType().equals("playImage")) {
                                    playImage = promoDataList.get(i).getImage();
                                }
                                else if(promoDataList.get(i).getType().equals("scanImage")) {
                                    scanImage = promoDataList.get(i).getImage();
                                }
                                else if(promoDataList.get(i).getType().equals("promoImage")) {
                                    promoImage = promoDataList.get(i).getImage();
                                }
                                promoStatus = promoDataList.get(i).getPromoStatus();
                            }

                            if(!TextUtils.isEmpty(scanImage)) {
                                AppUtils.setImage(txtScan, scanImage);
                            }
                            else
                            {
                                txtScan.setVisibility(View.GONE);
                                viewScan.setVisibility(View.GONE);
                            }

                            if(!TextUtils.isEmpty(playImage)) {
                                AppUtils.setImage(txtPlay, playImage);
                            }
                            else
                            {
                                txtPlay.setVisibility(View.GONE);
                                viewPlay.setVisibility(View.GONE);
                            }

                            if(!TextUtils.isEmpty(promoImage)) {
                                AppUtils.setImage(txtPromo, promoImage);
                            }
                            else
                            {
                                txtPromo.setVisibility(View.GONE);
                                viewPromo.setVisibility(View.GONE);
                            }
                        }
                        learMain.startAnimation(animFadein);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getDataFromAPI() {

        try {
            AppController.getPlayList(mContext, new Callback() {
                @Override
                public void run(Object result) throws JSONException, IOException {
                    JSONObject jsonResponse = new JSONObject((String) result);

                    //Log.i("DataResponse", jsonResponse.toString());

                    if (jsonResponse.getInt("success") == 1) {
                        playModelList = AppUtils.getJsonMapper().readValue(jsonResponse.getJSONArray("promoList").toString(), new TypeReference<List<PromoModel>>() {
                        });
                    } else if (jsonResponse.getInt("success") == 0) {
                        AppUtils.showToastMessage(getApplicationContext(), jsonResponse.getString("message"));
                    }else if (jsonResponse.getInt("success") == 100) {
                        AppUtils.showToastMessage(getApplicationContext(), jsonResponse.getString("message"));
                    }
                    if(playModelList.size()>0) {
                        Intent i = new Intent(mContext, PromoListActivity.class);
                        startActivity(i);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void chooseOption() {
        final CharSequence[] items = { "Scan Promo Code", "Enter Promo Code",
                "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Select an Option");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Scan Promo Code")) {
                    ((Activity) mContext).startActivityForResult(new Intent(context, SimpleScannerPromotionActivity.class), SCANNER_PROMOTION_REQUEST_CODE);
                } else if (items[item].equals("Enter Promo Code")) {
                    ((Activity) mContext).startActivity(new Intent(context, AddPromocodeActivity.class));
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if (animation == animFadein) {
        }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}

