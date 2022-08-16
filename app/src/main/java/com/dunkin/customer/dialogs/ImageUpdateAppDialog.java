package com.dunkin.customer.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dunkin.customer.NewHomeActivity;
import com.dunkin.customer.R;
import com.dunkin.customer.RegisterActivity;
import com.dunkin.customer.Utils.AppUtils;
import com.dunkin.customer.fragments.NewHomeFragment;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class ImageUpdateAppDialog extends Dialog {
    private static ImageUpdateAppDialog f;
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

    public ImageUpdateAppDialog(Context context, String path) {
        super(context);
        this.mContext = context;
        this.path = path;
    }

    public ImageUpdateAppDialog(Context context, int themeResId, String path) {
        super(context, themeResId);
        this.mContext = context;
        this.path = path;
    }

    public ImageUpdateAppDialog(Context context, int themeResId, String path, boolean FromWhere) {
        super(context, themeResId);
        this.mContext = context;
        this.path = path;
        this.FromWhere = FromWhere;
    }

    public static ImageUpdateAppDialog newInstance(Context mContext, String imgPath, boolean FromWhere) {
        if (f == null) {
            int theme = R.style.LoadingDialog;
            f = new ImageUpdateAppDialog(mContext, theme/*android.R.style.Theme_Translucent_NoTitleBar_Fullscreen*/, imgPath, FromWhere);
        }
        return f;
    }

    public static ImageUpdateAppDialog newInstance(Context mContext, String imgPath) {
        if (f == null) {
            int theme = R.style.LoadingDialog;
            f = new ImageUpdateAppDialog(mContext, theme/*android.R.style.Theme_Translucent_NoTitleBar_Fullscreen*/, imgPath);
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

        if (path != null) {
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
        } else {
            if (FromWhere) {
                imgBag.setImageResource(R.drawable.ic_loading);
            }
        }

        imgBag.setVisibility(View.VISIBLE);

        tvYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!FromWhere) {
                    if (mContext instanceof NewHomeActivity) {
                        ((NewHomeActivity) mContext).checkScanAndWin();
                    }
                } else if (FromWhere) {
                    ((Activity) mContext).startActivity(new Intent(mContext, RegisterActivity.class));
                    ((Activity) mContext).finish();
                }
                ImageUpdateAppDialog.this.dismiss();
            }
        });

        tvNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageUpdateAppDialog.this.dismiss();
            }
        });

        imgBag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!FromWhere) {
//                    if (mContext instanceof HomeActivity) {
//                        ((HomeActivity) mContext).checkScanAndWin();
//                    }
//                    if (((NewHomeActivity) mContext).getSupportFragmentManager().findFragmentById(R.id.content) instanceof NewHomeFragment) {
//                        ((NewHomeFragment) ((NewHomeActivity) mContext).getSupportFragmentManager().findFragmentById(R.id.content)).checkScanAndWin();
//                    }
                } else if (FromWhere) {
                    Intent intent = new Intent(mContext, RegisterActivity.class);
                    intent.putExtra("isRegister", true);
                    ((Activity) mContext).startActivity(intent);
//                    ((Activity) mContext).startActivity(new Intent(mContext, RegisterActivity.class));
                }
                ImageUpdateAppDialog.this.dismiss();
            }
        });
    }
}
