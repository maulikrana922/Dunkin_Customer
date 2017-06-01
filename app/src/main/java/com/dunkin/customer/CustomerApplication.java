package com.dunkin.customer;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.AsyncTask;

import com.dunkin.customer.Utils.AppUtils;
import com.dunkin.customer.constants.AppConstants;
import com.facebook.FacebookSdk;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;
import java.io.File;
import java.util.Locale;


public class CustomerApplication extends Application {

    private static Context context;

    public static Context getContext() {
        return context;
    }

    public static void setLocale(Locale locale) {
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.locale = locale;
        Resources resources = context.getResources();
        resources.updateConfiguration(config, resources.getDisplayMetrics());
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        Fabric.with(this, new Crashlytics());

        context = getApplicationContext();

//        Mint.initAndStartSession(context, "30f479ec");
        FacebookSdk.sdkInitialize(context);

        SharedPreferences myPrefs = AppUtils.getAppPreference(context);
        SharedPreferences.Editor editor = myPrefs.edit();
        editor.putString(AppConstants.USER_LANGUAGE, myPrefs.getString(AppConstants.USER_LANGUAGE, AppConstants.LANG_EN));
        editor.apply();

        try {
            setLocale(new Locale(myPrefs.getString(AppConstants.USER_LANGUAGE, AppConstants.LANG_EN)));
        } catch (Exception e) {
            e.printStackTrace();
        }

        File cacheDir = StorageUtils.getCacheDirectory(context);
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .memoryCacheExtraOptions(480, 800) // default = device screen dimensions
                .diskCacheExtraOptions(480, 800, null)
                .taskExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
                .taskExecutorForCachedImages(AsyncTask.THREAD_POOL_EXECUTOR)

                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new UsingFreqLimitedMemoryCache(4 * 1024 * 1024)) // default
                .memoryCacheSize(4 * 1024 * 1024)
                .diskCache(new UnlimitedDiskCache(cacheDir)) // default
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileCount(1500)
                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator()) // default
                .imageDownloader(new BaseImageDownloader(context)) // default
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple()) // default
                .build();

        ImageLoader.getInstance().init(config);
    }
}
