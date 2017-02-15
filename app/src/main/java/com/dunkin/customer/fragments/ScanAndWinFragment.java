package com.dunkin.customer.fragments;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dunkin.customer.HomeActivity;
import com.dunkin.customer.R;
import com.dunkin.customer.SimpleScannerActivity;
import com.dunkin.customer.Utils.Callback;
import com.dunkin.customer.constants.AppConstants;
import com.dunkin.customer.controllers.AppController;
import com.dunkin.customer.listener.FileDownloadListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ScanAndWinFragment extends Fragment {

    private Context mContext;
    private View rootView;

    private ImageView imgBag;
    private int rotationCount = 0;
    private TextView tvScan, tvSkip, tvNoScanAndWin;
    private LinearLayout rlMain;
    private ProgressBar progressLoading;

    private static final int SCANNER_REQUEST_CODE = 0x11;
    private String strUrl = "", strOfferUrl = "", scanImagePath = "", scanOfferImagePath = "",
            isScanWinEnable = "", isOfferEnable = "";
    final String[] permsReadWrite = {"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_EXTERNAL_STORAGE"};
    private static final int PERMISSION_STORAGE_READ_WRITE_REQUEST_CODE = 0x21;
    public static String TempPath = Environment.getExternalStorageDirectory()
            + File.separator + "Dunkin Leb" + File.separator + "Temp" + File.separator;
    private File mFileTemp;
    private static final int CAMERA_PERMISSION_REQUEST = 0x31;
    final String[] permsCamera = {"android.permission.CAMERA"};
    private boolean isOfferVisible = false;


    public ScanAndWinFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ScanAndWinFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ScanAndWinFragment newInstance(String param1, String param2) {
        ScanAndWinFragment fragment = new ScanAndWinFragment();
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
        rootView = inflater.inflate(R.layout.fragment_scan_and_win, container, false);

        imgBag = (ImageView) rootView.findViewById(R.id.imgBag);
        tvScan = (TextView) rootView.findViewById(R.id.tvScan);
        tvSkip = (TextView) rootView.findViewById(R.id.tvSkip);

        tvNoScanAndWin = (TextView) rootView.findViewById(R.id.tvNoScanAndWin);
        rlMain = (LinearLayout) rootView.findViewById(R.id.rlMain);
        progressLoading = (ProgressBar) rootView.findViewById(R.id.progressLoad);

        tvSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ScanAndWinDialog.this.dismiss();
                ((HomeActivity) mContext).navigate(AppConstants.MENU_HOME);
            }
        });

        imgBag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isOfferVisible) {
                    isOfferVisible = true;
                    loadingAnimation(scanOfferImagePath);
                } else if (isOfferVisible) {
                    isOfferVisible = false;
                    if (ActivityCompat.checkSelfPermission(mContext,
                            android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                        requestPermissions(permsCamera, CAMERA_PERMISSION_REQUEST);
                    } else {
                        ((Activity) mContext).startActivityForResult(new Intent(AppConstants.context, SimpleScannerActivity.class), SCANNER_REQUEST_CODE);
                    }
                }
            }
        });

        tvScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ActivityCompat.checkSelfPermission(mContext,
                        android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                    requestPermissions(permsCamera, CAMERA_PERMISSION_REQUEST);
                } else {
                    ((Activity) mContext).startActivityForResult(new Intent(AppConstants.context, SimpleScannerActivity.class), SCANNER_REQUEST_CODE);
                }
            }
        });

        checkScanAndWin();

        return rootView;
    }

    private void loadingAnimation(String str) {
        imgBag.setImageBitmap(BitmapFactory.decodeFile(str));

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

        loading_first.setTarget(imgBag);
        loading_first.start();

    }

//    @Override
//    public void dismiss() {
//        // TODO Auto-generated method stub
//        super.dismiss();
//        System.gc();
//    }

    public void checkScanAndWin() {
        try {
            AppController.getScan(mContext, new Callback() {
                @Override
                public void run(Object result) throws JSONException, IOException {
                    String res = (String) result;
                    JSONObject jsonResponse = new JSONObject(res);
                    if (jsonResponse.getString("isScanWinEnable").equalsIgnoreCase("1")) {
                        strUrl = jsonResponse.getString("scanwinImage");
                        strOfferUrl = jsonResponse.getString("offerImage");
                        isScanWinEnable = jsonResponse.getString("isScanWinEnable");
                        isOfferEnable = jsonResponse.getString("isOfferEnable");
                        if (ActivityCompat.checkSelfPermission(mContext,
                                android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                                &&
                                ActivityCompat.checkSelfPermission(mContext,
                                        android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                            requestPermissions(permsReadWrite, PERMISSION_STORAGE_READ_WRITE_REQUEST_CODE);
                        } else if (ActivityCompat.checkSelfPermission(mContext,
                                android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                                &&
                                ActivityCompat.checkSelfPermission(mContext,
                                        android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                            fileDownload();
                        }

                    } else {
                        hideScanAndWinView();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void hideScanAndWinView() {
        progressLoading.setVisibility(View.GONE);
        rlMain.setVisibility(View.GONE);
        tvNoScanAndWin.setVisibility(View.VISIBLE);
    }

    public void showScanAndWinView() {
        progressLoading.setVisibility(View.GONE);
        rlMain.setVisibility(View.VISIBLE);
        tvNoScanAndWin.setVisibility(View.GONE);
    }


    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(permsRequestCode, permissions, grantResults);
        switch (permsRequestCode) {

            case PERMISSION_STORAGE_READ_WRITE_REQUEST_CODE:

                boolean writeAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean readAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                if (writeAccepted && readAccepted) {
                    if (ActivityCompat.checkSelfPermission(mContext,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                            &&
                            ActivityCompat.checkSelfPermission(mContext,
                                    android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                        fileDownload();

                    }
                } else {
                    hideScanAndWinView();
                }
                break;

            case CAMERA_PERMISSION_REQUEST:

                boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (cameraAccepted) {
                    ((Activity) mContext).startActivityForResult(new Intent(AppConstants.context, SimpleScannerActivity.class), SCANNER_REQUEST_CODE);
                }
                break;
        }
    }

    private void fileDownload() {
        new GetFileTask(mContext, new FileDownloadListener() {
            @Override
            public void onFileDownload(String path) {
                showScanAndWinView();
                scanImagePath = path;
                loadingAnimation(scanImagePath);

                new GetFileTask(mContext, new FileDownloadListener() {
                    @Override
                    public void onFileDownload(String path) {
                        scanOfferImagePath = path;
                    }
                }).execute(strOfferUrl);
            }
        }).execute(strUrl);
    }

    public class GetFileTask extends AsyncTask<String, Void, String> {
        private FileDownloadListener downloadListner;

        public GetFileTask(Context mContext, FileDownloadListener downloadListner) {
            this.downloadListner = downloadListner;
        }

        @Override
        protected void onPostExecute(String path) {
            super.onPostExecute(path);
            if (path != null) {
                File f = new File(path);
                if (f.exists()) {
                    downloadListner.onFileDownload(path);
                }
            }
        }

        /*
         * param 0 : url
         */
        @Override
        protected String doInBackground(String... params) {
            try {
                File mediaStorageDir = new File(
                        Environment
                                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                        TempPath);

                // Create the storage directory if it does not exist
                if (!mediaStorageDir.exists()) {
                    boolean success = mediaStorageDir.mkdirs();
                    if (!success) {
                        Log.d("CreateJarFragmentThree", "Oops! Failed to create directory");
                    }
                }

                URL ulrn = new URL(params[0]);
                HttpURLConnection con = (HttpURLConnection) ulrn.openConnection();
                InputStream is = con.getInputStream();

                try {
                    OutputStream fOut = null;
                    String ImageName = params[0].substring(params[0].lastIndexOf("/") + 1);//, params[0].lastIndexOf("."));

                    mFileTemp = new File(
                            Environment
                                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                            TempPath + ImageName);

                    if (!mFileTemp.exists()) {

                        fOut = new FileOutputStream(mFileTemp);

                        int read = 0;
                        byte[] bytes = new byte[1024];

                        while ((read = is.read(bytes)) != -1) {
                            fOut.write(bytes, 0, read);
                        }
                        is.close();
                        fOut.flush();
                        fOut.close();
                    }
                    return mFileTemp.getAbsolutePath();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }
}
