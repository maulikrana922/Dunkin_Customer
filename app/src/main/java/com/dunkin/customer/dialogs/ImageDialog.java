package com.dunkin.customer.dialogs;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dunkin.customer.HomeActivity;
import com.dunkin.customer.R;
import com.dunkin.customer.RegisterActivity;
import com.dunkin.customer.Utils.AppUtils;
import com.dunkin.customer.fragments.HomeFragment;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * Created by qtm-c-android on 6/7/17.
 */

public class ImageDialog extends Dialog {
    private static ImageDialog f;
    private ImageView imgBag;
    private int rotationCount = 0;
    private TextView tvNo, tvYes;
    private Context mContext;
    private String path;
    /**
     * Check if we load intro image or scan image
     * boolean for manage click event on image
     */
    private boolean FromWhere = false;

    public ImageDialog(Context context, String path) {
        super(context);
        this.mContext = context;
        this.path = path;
    }

    public ImageDialog(Context context, int themeResId, String path, boolean FromWhere) {
        super(context, themeResId);
        this.mContext = context;
        this.path = path;
        this.FromWhere = FromWhere;
    }

    public static ImageDialog newInstance(Context mContext, String imgPath, boolean FromWhere) {
        if (f == null) {
            int theme = R.style.LoadingDialog;
            f = new ImageDialog(mContext, theme/*android.R.style.Theme_Translucent_NoTitleBar_Fullscreen*/, imgPath, FromWhere);
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
//        AppUtils.setImage(imgBag, path);

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
        else {
            if (FromWhere){
                imgBag.setImageResource(R.drawable.ic_loading);
        }
        }

        imgBag.setVisibility(View.VISIBLE);

        tvYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!FromWhere) {
                    if (mContext instanceof HomeActivity) {
                        ((HomeActivity) mContext).checkScanAndWin();
                    }
                } else if (FromWhere) {
                    ((Activity) mContext).startActivity(new Intent(mContext, RegisterActivity.class));
                    ((Activity) mContext).finish();
                }
                ImageDialog.this.dismiss();
            }
        });

        tvNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageDialog.this.dismiss();
            }
        });

        imgBag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!FromWhere) {
//                    if (mContext instanceof HomeActivity) {
//                        ((HomeActivity) mContext).checkScanAndWin();
//                    }
                    if ( ((HomeActivity) mContext).getSupportFragmentManager().findFragmentById(R.id.content) instanceof HomeFragment) {
                        ((HomeFragment)  ((HomeActivity) mContext).getSupportFragmentManager().findFragmentById(R.id.content)).checkScanAndWin();
                    }
                } else if (FromWhere) {
                    ((Activity) mContext).startActivity(new Intent(mContext, RegisterActivity.class));
                }
                ImageDialog.this.dismiss();
            }
        });
    }

    private void loadingAnimation() {
        imgBag.setImageBitmap(BitmapFactory.decodeFile(path));
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
        super.dismiss();
        System.gc();
    }
}