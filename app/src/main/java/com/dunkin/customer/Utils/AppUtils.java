package com.dunkin.customer.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dunkin.customer.CartActivity;
import com.dunkin.customer.DBAdaters.DBAdapter;
import com.dunkin.customer.HomeActivity;
import com.dunkin.customer.R;
import com.dunkin.customer.adapters.ToppingAdapter;
import com.dunkin.customer.constants.AppConstants;
import com.dunkin.customer.fragments.CartFragment;
import com.dunkin.customer.models.OrderItemModel;
import com.dunkin.customer.models.PaymentModel;
import com.dunkin.customer.models.ProductModel;
import com.dunkin.customer.models.ToppingModel;
import com.dunkin.customer.service.LogoutService;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.facebook.FacebookSdk.getApplicationContext;

@SuppressWarnings("unchecked")
public class AppUtils {
    //public static AsyncHttpClient syncHttpClient = new SyncHttpClient();
    // public static AsyncHttpClient asyncHttpClient = new AsyncHttpClient();

    private static Handler mHandler;
    //private static AsyncHttpClient client;
    private static ObjectMapper mapper;
    private static SharedPreferences appPreference;
    private static DisplayImageOptions options, optionScanDialog;
    private static SimpleDateFormat dateFormatter;
    private static SimpleDateFormat dateTimeFormatter;

    static {
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_loading)
                .showImageForEmptyUri(R.drawable.ic_loading)
                .showImageOnFail(R.drawable.ic_loading)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                .considerExifParams(true)
                .build();

        optionScanDialog = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.img_scan_ic_loading)
                .showImageForEmptyUri(R.drawable.img_scan_ic_loading)
                .showImageOnFail(R.drawable.img_scan_ic_loading)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                .considerExifParams(true)
                .build();

        dateFormatter = new SimpleDateFormat(AppConstants.DD_MM_YYYY_SLASH);
        dateTimeFormatter = new SimpleDateFormat(AppConstants.DD_MM_YYYY_HH_MM_SS);
    }

    /*public static AsyncHttpClient getClient() {
        if (client == null) {
            client = new AsyncHttpClient();
            client.setTimeout(AppConstants.REQUEST_TIME_OUT);
        }
        return client;
    }*/

    /*public static AsyncHttpClient getClient() {
        *//*if (client == null) {
            client = new AsyncHttpClient();
            client.setTimeout(AppConstants.REQUEST_TIME_OUT);
        }
        return client;*//*

        // Return the synchronous HTTP client when the thread is not prepared
        if (Looper.myLooper() == null)
            return syncHttpClient;
        return asyncHttpClient;
    }*/

    public static Handler getHandler() {
        return mHandler;
    }

    public static void setHandler(Handler handler) {
        mHandler = handler;
    }

    public static SharedPreferences getAppPreference(Context context) {
        if (appPreference == null) {
            appPreference = context.getSharedPreferences("customer_semsom.xml", Context.MODE_PRIVATE);
        }
        return appPreference;
    }


    public static ObjectMapper getJsonMapper() {
        if (mapper == null) {
            mapper = new ObjectMapper();
            // SerializationFeature for changing how JSON is written
            mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
            // to enable standard indentation ("pretty-printing"):
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            // to allow serialization of "empty" POJOs (no properties to
            // serialize)
            // (without this setting, an exception is thrown in those cases)
            mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
            // to write java.util.Date, Calendar as number (timestamp):
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            //mapper.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            mapper.enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS);
            // DeserializationFeature for changing how JSON is read as POJOs:
            mapper.enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS);
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            // to prevent exception when encountering unknown property:
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            // to allow coercion of JSON empty String ("") to null Object value:
            mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
            // mapper.enableDefaultTyping();
        }
        return mapper;
    }

    public static int checkNetwork(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return 1;

            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return 1;
        }
        return 0;
    }

    public static boolean isNotNull(String str) {
        return !(str == null ||
                str.equalsIgnoreCase("null") ||
                str.equalsIgnoreCase("") ||
                str.equalsIgnoreCase(" ") ||
                str.equalsIgnoreCase(null) ||
                str.trim().length() == 0);
    }

    //MAKE REQUEST CALL
    /*public static void MakeRequestCall(final Context context, final String URL, final String Method, final StringEntity se, final boolean isLoading, final Callback callback) {

        final ProgressDialog pDialog = new ProgressDialog(context);
        if (checkNetwork(context) != 0) {

            getClient();

            if (Method.equals(AppConstants.POST)) {
                if (Build.VERSION.SDK_INT >= 11) {
                    getClient().setThreadPool((ExecutorService) AsyncTask.THREAD_POOL_EXECUTOR);
                }
                getClient().post(context, URL, se, AppConstants.contentType, new AsyncHttpResponseHandler() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        if (isLoading) {
                            pDialog.setMessage(context.getString(R.string.processing));
                            pDialog.setCancelable(false);
                            pDialog.show();
                        }
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        if (isLoading)
                            pDialog.dismiss();
                        try {
                            callback.run(new String(responseBody));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        if (isLoading)
                            pDialog.dismiss();
                        error.printStackTrace();
                        AppUtils.showErrorDialog(context, context.getString(R.string.system_error));
                    }
                });

            } else if (Method.equals(AppConstants.GET)) {

                getClient().get(context, URL, se, AppConstants.contentType, new AsyncHttpResponseHandler() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        if (isLoading) {
                            pDialog.setMessage(context.getString(R.string.processing));
                            pDialog.setCancelable(false);
                            pDialog.show();
                        }
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        if (isLoading)
                            pDialog.dismiss();
                        try {
                            callback.run(new String(responseBody));
                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        if (isLoading)
                            pDialog.dismiss();
                        try {
                            AppUtils.showErrorDialog(context, error.getMessage());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        } else {
            AlertDialog.Builder alert = new AlertDialog.Builder(context);
            alert.setTitle(context.getString(R.string.al_warning));
            alert.setMessage(context.getString(R.string.network_error));
            alert.setPositiveButton(context.getString(R.string.al_ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            alert.setNegativeButton(context.getString(R.string.al_try_again), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    MakeRequestCall(context, URL, Method, se, isLoading, callback);
                }
            });
            alert.create().show();
        }
    }*/

    @SuppressWarnings("deprecation")
    private static String makeRequest(String URL, org.apache.http.entity.StringEntity se) {
        try {
            //url with the post data
            HttpPost httpPost = new HttpPost(AppConstants.getReverseCase(AppConstants.context) + URL);

            httpPost.setEntity(se);
            //sets a request header so the page receiving the request
            //will know what to do with it
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            String authorizationToken = AppUtils.getAppPreference(AppConstants.context).getString(AppConstants.USER_CASEID, "");
            String data = AppConstants.getCase(AppConstants.context, authorizationToken);
            httpPost.setHeader("caseId", data);

            HttpParams httpParameters = new BasicHttpParams();

            // Set the timeout in milliseconds until a connection is established.
            // The default value is zero, that means the timeout is not used.
            int timeoutConnection = 20000;
            HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);

            // Set the default socket timeout (SO_TIMEOUT)
            // in milliseconds which is the timeout for waiting for data.
            int timeoutSocket = 20000;
            HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

            DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);

            //Handles what is returned from the page
            String res = httpClient.execute(httpPost, new BasicResponseHandler());
            Log.e("URL", URL);
            Log.e("responce", res);
            checkCaseIdEr(res);
            return res;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void checkCaseIdEr(String res) {
        try {
            JSONObject objRes = new JSONObject(res);
            if (objRes.getString(AppConstants.context.getString(R.string.success))
                    .equals(AppConstants.context.getString(R.string.case_er))) {
                Intent intent = new Intent(getApplicationContext(), LogoutService.class);
                intent.putExtra("isDirectExit", 1);
                AppConstants.context.startService(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //MAKE REQUEST CALL
    public static void requestCallAsyncTask(final Context context, final String URL, @SuppressWarnings("deprecation") final org.apache.http.entity.StringEntity se, final boolean isLoading, final Callback callback) {

        final LoadingDialog pDialog;
        final boolean[] isTimeOut = {false};
        final String[] strResponse = {""};

        if (checkNetwork(context) != 0) {
            pDialog = new LoadingDialog(context);

            new AsyncTask<Void, Void, String>() {

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    if (!((Activity) context).isFinishing() && isLoading) {
                        pDialog.show();
                    }
                }

                @Override
                protected String doInBackground(Void... params) {
                    try {
                        strResponse[0] = makeRequest(URL, se);
                        if (TextUtils.isEmpty(strResponse[0])) {
                            isTimeOut[0] = true;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        isTimeOut[0] = true;
                    }

                    return strResponse[0];
                }

                @Override
                protected void onPostExecute(String s) {
                    super.onPostExecute(s);

                    if (!isTimeOut[0]) {
                        try {
                            JSONObject jsonResponse = new JSONObject(s);
                            Log.e("DataResponse", jsonResponse.toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (pDialog.isShowing() && isLoading)
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                                if (((Activity) context).isDestroyed()) {
                                    return;
                                }
                            } else {
                                if (((Activity) context).isFinishing()) {
                                    return;
                                }
                            }

                        if (!((Activity) context).isFinishing() && pDialog.isShowing()) {
                            pDialog.dismiss();
                        }

                        try {
                            if (s != null && AppUtils.isNotNull(s)) {
                                callback.run(s);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        isTimeOut[0] = false;

                        if (pDialog.isShowing() && isLoading)
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                                if (((Activity) context).isDestroyed()) {
                                    return;
                                }
                            } else {
                                if (((Activity) context).isFinishing()) {
                                    return;
                                }
                            }

                        if (!((Activity) context).isFinishing() && pDialog.isShowing()) {
                            pDialog.dismiss();
                        }

                        final AlertDialog.Builder alert = new AlertDialog.Builder(context);
                        alert.setTitle(context.getString(R.string.al_warning));
                        alert.setMessage(context.getString(R.string.msg_failed_to_get_response));
                        alert.setCancelable(false);
                        alert.setPositiveButton(context.getString(R.string.al_ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                                    if (((Activity) context).isDestroyed()) {
                                        return;
                                    }
                                } else {
                                    if (((Activity) context).isFinishing()) {
                                        return;
                                    }
                                }

                                if ((dialog != null) && !((Activity) context).isFinishing() && alert.create().isShowing()) {
                                    dialog.dismiss();
                                }
                                try {
                                    callback.run(AppConstants.TIME_OUT);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                        alert.setNegativeButton(context.getString(R.string.al_try_again), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                                    if (((Activity) context).isDestroyed()) {
                                        return;
                                    }
                                } else {
                                    if (((Activity) context).isFinishing()) {
                                        return;
                                    }
                                }

                                if ((dialog != null) && !((Activity) context).isFinishing() && alert.create().isShowing()) {
                                    dialog.dismiss();
                                }
                                requestCallAsyncTask(context, URL, se, isLoading, callback);
                            }
                        });
                        if (!((Activity) context).isFinishing()) {
                            if (!alert.create().isShowing())
                                alert.create().show();
                        }
                    }
                }
            }.execute();
        } else {
            final AlertDialog.Builder alert = new AlertDialog.Builder(context);
            alert.setTitle(context.getString(R.string.al_warning));
            alert.setMessage(context.getString(R.string.network_error));
            alert.setPositiveButton(context.getString(R.string.al_ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        if (((Activity) context).isDestroyed()) {
                            return;
                        }
                    } else {
                        if (((Activity) context).isFinishing()) {
                            return;
                        }
                    }

                    if ((dialog != null) && !((Activity) context).isFinishing() && alert.create().isShowing()) {
                        dialog.dismiss();
                    }
                }
            });

            alert.setNegativeButton(context.getString(R.string.al_try_again), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        if (((Activity) context).isDestroyed()) {
                            return;
                        }
                    } else {
                        if (((Activity) context).isFinishing()) {
                            return;
                        }
                    }

                    if ((dialog != null) && !((Activity) context).isFinishing() && alert.create().isShowing()) {
                        dialog.dismiss();
                    }
                    requestCallAsyncTask(context, URL, se, isLoading, callback);
                }
            });
            if (!((Activity) context).isFinishing()) {
                if (!alert.create().isShowing())
                    alert.create().show();
            }
        }
    }

    //MAKE REQUEST CALL
    public static void requestCallAsyncTask(final String URL, @SuppressWarnings("deprecation") final org.apache.http.entity.StringEntity se, final Callback callback) {
        final String[] strResponse = {""};
        new AsyncTask<Void, Void, String>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(Void... params) {

                try {
                    strResponse[0] = makeRequest(URL, se);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return strResponse[0];
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                try {
                    JSONObject jsonResponse = new JSONObject(s);
                    Log.e("DataResponse", jsonResponse.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    if (s != null && AppUtils.isNotNull(s)) {
                        callback.run(s);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    //MAKE REQUEST CALL
    public static void requestCallAsyncTask(final int id, final Context context, final String URL, @SuppressWarnings("deprecation") final org.apache.http.entity.StringEntity se, final boolean isLoading, final Callback callback) {

        final LoadingDialog pDialog;
        final String[] strResponse = {""};
        final JSONObject[] jsonResponse = new JSONObject[1];

        if (AppUtils.checkNetwork(context) != 0) {
            pDialog = new LoadingDialog(context);

            new AsyncTask<Void, Void, String>() {

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    if (!((Activity) context).isFinishing() && isLoading) {
                        pDialog.show();
                    }
                }

                @Override
                protected String doInBackground(Void... params) {
                    try {
                        strResponse[0] = AppUtils.makeRequest(URL, se);
                        if (TextUtils.isEmpty(strResponse[0])) {
                            if (id == AppConstants.OF_PROFILE) {
                                DBAdapter dbAdapter = new DBAdapter(context);
                                dbAdapter.open();
                                String result = dbAdapter.getOfflineData(AppConstants.OF_PROFILE);
                                try {
                                    strResponse[0] = result;
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (id == AppConstants.OF_PROFILE) {
                            DBAdapter dbAdapter = new DBAdapter(context);
                            dbAdapter.open();
                            String result = dbAdapter.getOfflineData(AppConstants.OF_PROFILE);
                            try {
                                strResponse[0] = result;
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                        }
                    }

                    return strResponse[0];
                }

                @Override
                protected void onPostExecute(String s) {
                    super.onPostExecute(s);

                    try {
                        jsonResponse[0] = new JSONObject(s);
                        Log.e("DataResponse", jsonResponse[0].toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (pDialog.isShowing() && isLoading)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            if (((Activity) context).isDestroyed()) {
                                return;
                            }
                        } else {
                            if (((Activity) context).isFinishing()) {
                                return;
                            }
                        }

                    if (!((Activity) context).isFinishing() && pDialog.isShowing()) {
                        pDialog.dismiss();
                    }

                    try {
                        if (s != null && AppUtils.isNotNull(s)) {
                            callback.run(s);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.execute();
        } else {
            try {
                if (id == AppConstants.OF_PROFILE) {
                    DBAdapter dbAdapter = new DBAdapter(context);
                    dbAdapter.open();
                    String result = dbAdapter.getOfflineData(AppConstants.OF_PROFILE);
                    try {
                        callback.run(result);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void showErrorDialog(final Context context, String message) {
        if (context != null) {
            final AlertDialog.Builder alert = new AlertDialog.Builder(context);
            alert.setMessage(message);
            alert.setPositiveButton(context.getString(R.string.al_ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        if (((Activity) context).isDestroyed()) {
                            return;
                        }
                    } else {
                        if (((Activity) context).isFinishing()) {
                            return;
                        }
                    }

                    if ((dialog != null) && !((Activity) context).isFinishing() && alert.create().isShowing()) {
                        dialog.dismiss();
                    }
                }
            });
            if (!((Activity) context).isFinishing() && !alert.create().isShowing()) {
                alert.create().show();
            }
        }
    }

    public static void showError(View view, String message) {
        showToastMessage(view.getContext(), message);
    }

    public static void showToastMessage(Context ctx, String message) {
        Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show();
    }

    public static boolean validateStringForAlpha(Context context, String message) {
        String lang = getAppPreference(context).getString(AppConstants.USER_LANGUAGE, AppConstants.LANG_EN);
        if (lang.equals(AppConstants.LANG_EN)) {

            Pattern mPattern = Pattern.compile(AppConstants.USER_NAME_VALIDATION);

            Matcher matcher = mPattern.matcher(message);
            if (!matcher.matches()) {
                return false;
            }
            return true;
        }
        return true;
    }

    public static void setImage(final ImageView imgView, String URL) {
        if (!URL.isEmpty()) {
            ImageLoader.getInstance().displayImage(URL, imgView, options, new ImageLoadingListener() {
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
            imgView.setImageResource(R.drawable.ic_loading);
        }
    }

    public static void setScanImage(final ImageView imgView, String URL) {
        if (!URL.isEmpty()) {
            ImageLoader.getInstance().displayImage(URL, imgView, optionScanDialog, new ImageLoadingListener() {
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
            imgView.setImageResource(R.drawable.ic_loading);
        }
    }


    public static int isValidDate(String date, String pattern) throws ParseException {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);

        Date date1 = sdf.parse(sdf.format(cal.getTime()));
        Date date2 = sdf.parse(date);

        if (date1.compareTo(date2) > 0) {
            return 0;
        }
        return 1;
    }

    public static boolean isDaysBetweenDates(String startDate, String endDate, JSONArray daysList) throws ParseException, JSONException {

        Set<Integer> data = new HashSet<>();
        for (int i = 0; i < daysList.length(); i++) {
            data.add((Integer) daysList.get(i));
        }
        java.util.LinkedList hitList = searchBetweenDates(
                new java.text.SimpleDateFormat(AppConstants.DD_MM_YYYY_SLASH).parse(startDate),
                new java.text.SimpleDateFormat(AppConstants.DD_MM_YYYY_SLASH).parse(endDate));
        Integer[] comboDates = new Integer[hitList.size()];
        for (int i = 0; i < hitList.size(); i++) {
            comboDates[i] = ((java.util.Date) hitList.get(i)).getDay();
        }

        for (int i = 0; i < comboDates.length; i++) {

            if (data.contains(comboDates[i])) {
                return true;
            }
        }
        return false;
    }

    private static java.util.LinkedList searchBetweenDates(java.util.Date startDate, java.util.Date endDate) {

        java.util.Date begin = new Date(startDate.getTime());
        java.util.LinkedList list = new java.util.LinkedList();
        list.add(new Date(begin.getTime()));

        while (begin.compareTo(endDate) < 0) {
            begin = new Date(begin.getTime() + 86400000);
            list.add(new Date(begin.getTime()));
        }

        return list;
    }


    public static String maskCardNumber(String cardNumber, String mask) {

        // format the number
        int index = 0;
        StringBuilder maskedNumber = new StringBuilder();
        for (int i = 0; i < mask.length(); i++) {
            char c = mask.charAt(i);
            if (c == '#') {
                maskedNumber.append(cardNumber.charAt(index));
                index++;
            } else if (c == 'x') {
                maskedNumber.append(c);
                index++;
            } else {
                maskedNumber.append(c);
            }
        }

        // return the masked number
        return maskedNumber.toString();
    }

    public static void setListViewHeightBasedOnChildren(BaseAdapter listAdapter, ListView listView) {

        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    // FORMATTING WITH US
    public static String CurrencyFormat(Object currency) {
        NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.US);
        DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) nf).getDecimalFormatSymbols();
        decimalFormatSymbols.setCurrencySymbol("");
        ((DecimalFormat) nf).setDecimalFormatSymbols(decimalFormatSymbols);
        return nf.format(currency).trim();

        //return new DecimalFormat("##,##,##0").format(currency);

        // return NumberFormat.getCurrencyInstance(Locale.US)..format(currency).toString();
    }

    // FORMATTING THE PRICE
    public static String currencyFormat(Object currency) {
        return new DecimalFormat("##,##,##0").format(currency);

        // return NumberFormat.getCurrencyInstance(Locale.US)..format(currency).toString();
    }

    public static boolean verifyPermissions(int[] grantResults) {
        // At least one result must be checked.
        if (grantResults.length < 1) {
            return false;
        }

        // Verify that each required permission has been granted, otherwise return false.
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public static void openToppingActivity(final Context context, final ProductModel product, final boolean fromAddOnOrCart) {
        final DBAdapter dbAdapter = new DBAdapter(context);
        final AlertDialog.Builder alert = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.topping_dialog_layout, null);
        alert.setView(view);
        ListView toppingList = (ListView) view.findViewById(R.id.toppingList);
        Button btnDone = (Button) view.findViewById(R.id.btnDone);
        TextView txtProductName = (TextView) view.findViewById(R.id.productName);
        txtProductName.setText(product.getProductName());
        if (product.getProductToppings() != null) {
            if (product.getProductToppings().size() == 0) {
                product.setProductToppings(product.getToppings());
            }
        }

        List<ToppingModel> toppingModelList = new ArrayList<>();
        for (ToppingModel tm : product.getProductToppings()) {
            tm.setSelected(false);

            for (ToppingModel tm1 : product.getToppings()) {
                if (tm.getToppingId() == tm1.getToppingId() && product.getRowId() != 0) {
                    tm.setSelected(true);
                }
            }
            toppingModelList.add(tm);
        }

        final ToppingAdapter toppingAdapter = new ToppingAdapter(context, toppingModelList, product.getCurrency());
        toppingList.setAdapter(toppingAdapter);
        final AlertDialog alertDialog = alert.create();

        final boolean[] isAdded = {false};
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<ToppingModel> addToppingList = new ArrayList<>();

                List<ToppingModel> toppingModels = toppingAdapter.getUpdatedToppingList();
                if (toppingModels != null && toppingModels.size() > 0) {
                    for (ToppingModel tm : toppingModels) {
                        if (product.getRowId() != 0 && !tm.isSelected()) {
                            dbAdapter.open();
                            dbAdapter.removeToppingsFromCart(product, tm);
                            dbAdapter.close();
                        } else if (product.getRowId() != 0 && tm.isSelected()) {
                            dbAdapter.open();
                            dbAdapter.addToppingOnExistingProduct(product, tm);
                            dbAdapter.close();
                        } else if (product.getRowId() == 0 && tm.isSelected()) {
                            addToppingList.add(tm);
                        }
                    }
                }

                if (fromAddOnOrCart) {
                    if (addToppingList.size() > 0) {
                        dbAdapter.open();
                        dbAdapter.addProductInCart(product, addToppingList);
                        dbAdapter.close();
                        isAdded[0] = true;
                    }
                } else {
                    if (addToppingList.size() > 0) {
                        dbAdapter.open();
                        dbAdapter.addProductInCart(product, addToppingList);
                        dbAdapter.close();
                        isAdded[0] = true;
                    } else {
                        dbAdapter.open();
                        dbAdapter.addProductInCart(product, null);
                        dbAdapter.close();
                        isAdded[0] = true;
                    }
                }


                // TO update the cart list
                if (context instanceof CartActivity) {
                    ((CartActivity) context).getDataFromAPI();
                } else if (context instanceof HomeActivity) {
                    List<Fragment> fragments = ((HomeActivity) context).getSupportFragmentManager().getFragments();
                    for (Fragment fg : fragments) {
                        if (fg instanceof CartFragment && fg.isVisible()) {
                            ((CartFragment) fg).getDataFromAPI();
                            break;
                        }
                    }
                }
                alertDialog.dismiss();
            }
        });

        /*alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if(!isAdded[0]) {
                    List<ToppingModel> addToppingList = new ArrayList<>();

                    List<ToppingModel> toppingModels = toppingAdapter.getUpdatedToppingList();
                    if (toppingModels != null && toppingModels.size() > 0) {
                        for (ToppingModel tm : toppingModels) {
                            if (product.getRowId() != 0 && !tm.isSelected()) {
                                dbAdapter.open();
                                dbAdapter.removeToppingsFromCart(product, tm);
                                dbAdapter.close();
                            } else if (product.getRowId() != 0 && tm.isSelected()) {
                                dbAdapter.open();
                                dbAdapter.addToppingOnExistingProduct(product, tm);
                                dbAdapter.close();
                            } else if (product.getRowId() == 0 && tm.isSelected()) {
                                addToppingList.add(tm);
                            }
                        }
                    }

                    *//*if (addToppingList.size() > 0) {
                        dbAdapter.open();
                        dbAdapter.addProductInCart(product, addToppingList);
                        dbAdapter.close();
                    }*//*

                    if (fromAddOnOrCart) {
                        if (addToppingList.size() > 0) {
                            dbAdapter.open();
                            dbAdapter.addProductInCart(product, addToppingList);
                            dbAdapter.close();
                        }
                    } else {
                        if (addToppingList.size() > 0) {
                            dbAdapter.open();
                            dbAdapter.addProductInCart(product, addToppingList);
                            dbAdapter.close();
                        } else {
                            dbAdapter.open();
                            dbAdapter.addProductInCart(product, null);
                            dbAdapter.close();
                        }
                    }


                    // TO update the cart list
                    if (context instanceof CartActivity) {
                        ((CartActivity) context).getDataFromAPI();
                    } else if (context instanceof HomeActivity) {
                        List<Fragment> fragments = ((HomeActivity) context).getSupportFragmentManager().getFragments();
                        for (Fragment fg : fragments) {
                            if (fg instanceof CartFragment && fg.isVisible()) {
                                ((CartFragment) fg).getDataFromAPI();
                                break;
                            }
                        }
                    }
                    alertDialog.dismiss();
                }
            }
        });*/

        if (product.getProductToppings() != null && product.getProductToppings().size() > 0)
            alertDialog.show();
    }

    public static void openOrderToppings(Context context, String currency, OrderItemModel orderItemModel) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.topping_dialog_layout, null);
        alert.setView(view);

        TextView txtProductName = (TextView) view.findViewById(R.id.productName);
        ListView toppingList = (ListView) view.findViewById(R.id.toppingList);
        Button btnDone = (Button) view.findViewById(R.id.btnDone);
        TextView txt1 = (TextView) view.findViewById(R.id.txt1);

        btnDone.setText(context.getString(R.string.al_close));

        txt1.setText(context.getString(R.string.lbl_order_toppings));
        txtProductName.setText(orderItemModel.getProductName());
        List<ToppingModel> toppingModelList = orderItemModel.getTopping();
        ToppingAdapter toppingAdapter = new ToppingAdapter(context, toppingModelList, currency);
        toppingList.setAdapter(toppingAdapter);
        final AlertDialog alertDialog = alert.create();
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    // GET PAYMENT TYPES
    private static List<PaymentModel> getPaymentList(Context context) {
        List<PaymentModel> paymentList = new ArrayList<>();
        paymentList.add(new PaymentModel(1, context.getString(R.string.pay_by_cash)));
        paymentList.add(new PaymentModel(2, context.getString(R.string.pay_by_wallet)));
        paymentList.add(new PaymentModel(3, context.getString(R.string.pay_by_points)));

        return paymentList;
    }

    public static String getPaymentMode(Context context, int paymentType) {
        List<PaymentModel> paymentModels = getPaymentList(context);
        for (PaymentModel pm : paymentModels) {
            if (pm.getPaymentId() == paymentType) {
                return pm.getPaymentType();
            }
        }
        return "";
    }

    public static String getFormattedDate(Date date) {
        return dateFormatter.format(date);
    }

    public static String getFormattedDateTime(String date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        Date noteDate = null;
        try {
            noteDate = dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateTimeFormatter.format(noteDate);
    }

    public static String getFormattedDateTimeRedeemHistory(String date) {
        DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss", Locale.US);
        //DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.US);
        DateFormat dateFormat2 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.US);
        Date noteDate = null;
        try {
            noteDate = dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateFormat2.format(noteDate);
    }

    public static String getFormattedDate(String date) {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        //DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.US);
        DateFormat dateFormat2 = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        Date noteDate = null;
        try {
            noteDate = dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateFormat2.format(noteDate);
    }
}
