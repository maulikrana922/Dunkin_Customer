package com.dunkin.customer.dialogs;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dunkin.customer.HomeActivity;
import com.dunkin.customer.R;
import com.dunkin.customer.SimpleScannerActivity;
import com.dunkin.customer.Utils.AppUtils;
import com.dunkin.customer.constants.AppConstants;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import static com.dunkin.customer.constants.AppConstants.context;

/**
 * Created by qtm-android on 25/1/17.
 */

public class ScanAndWinDialog extends Dialog {
    private ImageView imgBag;
    private int rotationCount = 0;
    private TextView tvNo, tvYes;
    private static final int SCANNER_REQUEST_CODE = 0x11;
    private static ScanAndWinDialog f;
    private Context mContext;
    private String path;
    /**
     * Check if we load intro image or scan image
     * boolean for manage click event on image
     */
    private boolean isForScan = false;

    private static final int CAMERA_PERMISSION_REQUEST = 0x31;
    final String[] permsCamera = {"android.permission.CAMERA"};

    public ScanAndWinDialog(Context context, String path) {
        super(context);
        this.mContext = context;
        this.path = path;
    }

    public ScanAndWinDialog(Context context, int themeResId, String path, boolean isForScan) {
        super(context, themeResId);
        this.mContext = context;
        this.path = path;
        this.isForScan = isForScan;
    }

    protected ScanAndWinDialog(Context context, boolean cancelable, OnCancelListener cancelListener, String path) {
        super(context, cancelable, cancelListener);
        this.mContext = context;
        this.path = path;
    }

    public static ScanAndWinDialog newInstance(Context mContext, String imgPath, boolean isForScan) {
        if (f == null) {
            int theme = R.style.LoadingDialog;
            f = new ScanAndWinDialog(mContext, theme/*android.R.style.Theme_Translucent_NoTitleBar_Fullscreen*/, imgPath, isForScan);
        }
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_scan_and_win);
        imgBag = (ImageView) findViewById(R.id.imgBag);
        tvNo = (TextView) findViewById(R.id.tvNo);
        tvYes = (TextView) findViewById(R.id.tvYes);
        loadingAnimation();


        tvYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isForScan) {
                    if (mContext instanceof HomeActivity) {
                        ((HomeActivity) mContext).setScanImage();
                    }
                } else if (isForScan) {
                    if (ActivityCompat.checkSelfPermission(mContext,
                            android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions((HomeActivity) mContext, permsCamera, CAMERA_PERMISSION_REQUEST);
                    } else {
                        ((Activity) mContext).startActivityForResult(new Intent(AppConstants.context, SimpleScannerActivity.class), SCANNER_REQUEST_CODE);
                    }
                }
                ScanAndWinDialog.this.dismiss();
            }
        });

        tvNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScanAndWinDialog.this.dismiss();
            }
        });

        imgBag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isForScan) {
                    SharedPreferences.Editor editor = AppUtils.getAppPreference(context).edit();
                    editor.putBoolean(AppConstants.USER_SCAN_RESULT, true);
                    editor.apply();
                    if (mContext instanceof HomeActivity) {
                        ((HomeActivity) mContext).setScanImage();
                    }
                } else if (isForScan) {
                    if (ActivityCompat.checkSelfPermission(mContext,
                            android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions((HomeActivity) mContext, permsCamera, CAMERA_PERMISSION_REQUEST);
                    } else {
                        ((Activity) mContext).startActivityForResult(new Intent(AppConstants.context, SimpleScannerActivity.class), SCANNER_REQUEST_CODE);
                    }
                }
                ScanAndWinDialog.this.dismiss();
            }
        });
    }

    private void loadingAnimation() {
        if (!TextUtils.isEmpty(path)) {
            ImageLoader.getInstance().displayImage(path, imgBag, AppUtils.options, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    try {
                        imgBag.setImageBitmap(loadedImage);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {

                }
            });
        }

//        imgBag.setImageBitmap(BitmapFactory.decodeFile(path));
        imgBag.setVisibility(View.VISIBLE);

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
                    loading_out_in_1.setTarget(imgBag);
                    loading_out_in_1.start();
                    imgBag.refreshDrawableState();
                    imgBag.invalidate();
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
                loading_first.setTarget(imgBag);
                loading_first.start();
                imgBag.refreshDrawableState();
                imgBag.invalidate();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }
        });

        /**
         * Start animation if intro image load
         * if scan image load then display no animation
         *
         * Now 01/02/2017
         * As per client suggetion remove animation from intro file
         */

//        if (!isForScan) {
//            loading_first.setTarget(imgBag);
//            loading_first.start();
//        }

    }

    @Override
    public void dismiss() {
        // TODO Auto-generated method stub
        f = null;
        SharedPreferences.Editor editor = AppUtils.getAppPreference(context).edit();
        editor.putBoolean(AppConstants.USER_SCAN_RESULT, true);
        editor.apply();
        if (mContext instanceof HomeActivity) {
            ((HomeActivity) mContext).setScanImage();
        }
        super.dismiss();
        System.gc();
    }
}