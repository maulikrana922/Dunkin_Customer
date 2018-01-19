package com.dunkin.customer;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.dunkin.customer.Utils.AppUtils;
import com.dunkin.customer.constants.AppConstants;

import java.util.Locale;

// data

public class SettingActivity extends BaseActivity {

    private static Context context;
    private CheckBox cbLanguage;
    private ImageView imgSettingScreen;
    private SharedPreferences myPrefs;
    private boolean isFirstTime = true;
    private TextView txtLanguage;

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 10;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        inflateView(R.layout.activity_settings, getString(R.string.nav_settings));

        cbLanguage = (CheckBox) findViewById(R.id.cbLanguage);

        if (imgSettingScreen != null)
            ((BitmapDrawable) imgSettingScreen.getDrawable()).getBitmap().recycle();

        imgSettingScreen = (ImageView) findViewById(R.id.ivSettingScreen);
        try {
            //imgSettingScreen.setImageBitmap(decodeSampledBitmapFromResource(getResources(), R.drawable.setting_screen_img, 200, 200));
            imgSettingScreen.setImageResource(R.drawable.setting_screen_img);
        } catch (Exception e) {
            e.printStackTrace();
        }

        txtLanguage = (TextView) findViewById(R.id.smalltxt2);

        myPrefs = AppUtils.getAppPreference(SettingActivity.this);

        if (myPrefs.getString(AppConstants.USER_LANGUAGE, AppConstants.LANG_EN).equals(AppConstants.LANG_EN)) {
            cbLanguage.setChecked(true);
            txtLanguage.setText(getString(R.string.lang_en));
        } else {
            cbLanguage.setChecked(false);
            txtLanguage.setText(getString(R.string.lang_ar));
        }

        cbLanguage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String lang;
                if (isChecked) {
                    lang = AppConstants.LANG_EN;
                    isFirstTime = false;
                } else {
                    lang = AppConstants.LANG_AR;
                    isFirstTime = false;
                }

                if (!isFirstTime) {
                    SharedPreferences.Editor editor = AppUtils.getAppPreference(context).edit();
                    editor.putString(AppConstants.USER_LANGUAGE, lang);
                    editor.commit();
                    //  AppUtils.showToastMessage(context,getString(R.string.app_language_change_success));
                    CustomerApplication.setLocale(new Locale(lang));
                    setResult(RESULT_OK);
                    finish();
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        imgSettingScreen.setImageDrawable(null);
    }
}
