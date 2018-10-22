package com.dunkin.customer.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.dunkin.customer.NewHomeActivity;
import com.dunkin.customer.R;
import com.dunkin.customer.RegisterActivity;
import com.dunkin.customer.Utils.AppUtils;
import com.dunkin.customer.Utils.Callback;
import com.dunkin.customer.constants.AppConstants;
import com.dunkin.customer.controllers.AppController;
import com.dunkin.customer.models.RotatingBanner;
import com.fasterxml.jackson.core.type.TypeReference;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


public class RotatingBannerFragment extends Fragment {
    public static CountDownTimer timer = null;
    public int random = 0;
    private Context context;
    private ImageView imgView;
    private List<RotatingBanner> bannerList;
    private DisplayImageOptions displayImageOptions;

    public static RotatingBannerFragment newInstance() {
        RotatingBannerFragment banner = new RotatingBannerFragment();
        return banner;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bannerList = new ArrayList<>();
        setRetainInstance(true);

        displayImageOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_banner_loading)
                .showImageForEmptyUri(R.drawable.ic_banner_loading)
                .showImageOnFail(R.drawable.ic_banner_loading)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_rotating_banner, null);
        imgView = (ImageView) rootView.findViewById(R.id.imgBanner);

        try {
            final FrameLayout advertisement_banner = (FrameLayout) ((NewHomeActivity)context).findViewById(R.id.advertisement_banner);

            AppController.getRotatingBanner(context, AppUtils.getAppPreference(context).getInt(AppConstants.USER_COUNTRY, -1), new Callback() {
                @Override
                public void run(Object result) throws JSONException, IOException {
                    //Dunkin_Log.i("DataResponse ", (String) result);
                    JSONObject jsonResponse = new JSONObject((String) result);
                    if (jsonResponse.getInt("success") == 1) {

                        advertisement_banner.setVisibility(View.VISIBLE);

                        bannerList = AppUtils.getJsonMapper().readValue(jsonResponse.getJSONArray("rotatingbannerList").toString(), new TypeReference<List<RotatingBanner>>() {
                        });
                        displayBanner();
                    }
                    else {
                        advertisement_banner.setVisibility(View.GONE);
                    }

                    if (jsonResponse.getInt("success") == 0) {
                        getView().setVisibility(View.GONE);
                        imgView.setVisibility(View.GONE);
                    } else if (jsonResponse.getInt("success") == 100) {
                        AppUtils.showToastMessage(context, jsonResponse.getString("message"));
                    }else if (jsonResponse.getInt("success") == 99) {
                        displayDialog(jsonResponse.getString("message"));
                    }else if(advertisement_banner != null) {
                                advertisement_banner.setVisibility(View.GONE);
                            }
                }
            });
        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return rootView;
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

    @Override
    public void onStart() {
        super.onStart();
    }

    private void displayBanner() {
        if (bannerList != null && bannerList.size() > 0) {
            if (random > bannerList.size() - 1) {
                random = 0;
            }

            final RotatingBanner banner = bannerList.get(random);
            setImage(imgView, banner.getBannerImage());
            timer = new CountDownTimer(banner.getDisplay_time() * 1000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                }

                @Override
                public void onFinish() {
                    try {
                        timer.cancel();
                        random += 1;
                        displayBanner();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        } else {
            getView().setVisibility(View.GONE);
            imgView.setVisibility(View.GONE);
        }
    }


    private void setImage(final ImageView imgView, String bannerURL) {
        if (!bannerURL.isEmpty()) {
            ImageLoader.getInstance().displayImage(bannerURL, imgView, displayImageOptions, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    try {
                        imgView.setImageBitmap(loadedImage);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {
                }
            });
        } else {
            imgView.setImageResource(R.drawable.ic_banner_loading);
        }
    }
}
