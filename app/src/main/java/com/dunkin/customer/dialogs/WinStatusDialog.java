package com.dunkin.customer.dialogs;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dunkin.customer.HomeActivity;
import com.dunkin.customer.R;
import com.dunkin.customer.Utils.AppUtils;
import com.dunkin.customer.constants.AppConstants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by qtm-android on 26/1/17.
 */

public class WinStatusDialog extends DialogFragment {
    private ImageView imgStatus;
    private TextView tvOk;
    private LinearLayout llMain;
    private String strWinStatus, strStatusUrl,scanUrl;
    private static WinStatusDialog f;
    private Context mContext;
    private int rotationCount = 0;

    public static WinStatusDialog newInstance() {
        if (f == null) {
            f = new WinStatusDialog();
        }

        // Supply num input as an argument.
        Bundle args = new Bundle();
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        int style = DialogFragment.STYLE_NO_TITLE, theme = R.style.LoadingDialog;
        setStyle(style, theme/*android.R.style.Theme_Translucent_NoTitleBar_Fullscreen*/);

    }

    public static View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.layout_win_status, container, false);
        } catch (InflateException e) {
            /* map is already there, just return view as it is */
        }

        imgStatus = (ImageView) view.findViewById(R.id.imgStatus);
        tvOk = (TextView) view.findViewById(R.id.tvOk);
        llMain = (LinearLayout) view.findViewById(R.id.llMain);

        imgStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WinStatusDialog.this.dismiss();
                ((HomeActivity) mContext).navigate(AppConstants.MENU_HOME);
            }
        });

        llMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WinStatusDialog.this.dismiss();
                ((HomeActivity) mContext).navigate(AppConstants.MENU_HOME);
            }
        });

        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WinStatusDialog.this.dismiss();
//                ((HomeActivity) mContext).navigate(AppConstants.MENU_HOME);
            }
        });

        getAndSetData();

        return view;
    }

    @Override
    public void onDetach() {
        ((HomeActivity) mContext).navigate(AppConstants.MENU_HOME);
        super.onDetach();
    }

    private void getAndSetData() {
        if (getArguments().containsKey("res")) {
            if (!TextUtils.isEmpty(getArguments().getString("res"))) {
                try {
                    JSONObject jsonObject = new JSONObject(getArguments().getString("res"));
                    strWinStatus = jsonObject.getString("winStatus");
                    strStatusUrl = jsonObject.getString("scanImage");
                    scanUrl = jsonObject.getString("scanUrl");

                    if(!TextUtils.isEmpty(scanUrl))
                    {
                        if (!scanUrl.startsWith("http://") && !scanUrl.startsWith("https://"))
                            scanUrl = "http://" + scanUrl;

                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(scanUrl));
                        startActivity(browserIntent);
                    }
                    else {
                        try {
                            AppUtils.setScanImage(imgStatus, strStatusUrl);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    if (strWinStatus.equalsIgnoreCase("2")) {
//                        llMain.startAnimation(AnimationUtils.loadAnimation(AppConstants.context, R.anim.slide_down));
                        loadingAnimation();

                    } else {
//                        llMain.startAnimation(AnimationUtils.loadAnimation(AppConstants.context, R.anim.slide_up));
                        loadingAnimation();
                    }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void dismiss() {
        // TODO Auto-generated method stub
        ((HomeActivity) mContext).navigate(AppConstants.MENU_HOME);
        super.dismiss();
        System.gc();
    }


    private void loadingAnimation() {

        final AnimatorSet loading_first = (AnimatorSet) AnimatorInflater
                .loadAnimator(mContext, R.animator.myanimation_first);
        final AnimatorSet loading_out_in_1 = (AnimatorSet) AnimatorInflater
                .loadAnimator(mContext, R.animator.myanimation_two);

        loading_first.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (rotationCount < 1) {
                    loading_out_in_1.setTarget(imgStatus);
                    loading_out_in_1.start();
                    imgStatus.refreshDrawableState();
                    imgStatus.invalidate();
                    rotationCount++;
                } else {
                    rotationCount = 0;
                    //dismiss();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }
        });

        loading_out_in_1.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                loading_first.setTarget(imgStatus);
                loading_first.start();
                imgStatus.refreshDrawableState();
                imgStatus.invalidate();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }
        });

        loading_first.setTarget(imgStatus);
        loading_first.start();

    }

}
