package com.dunkin.customer;

import android.content.Intent;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.dunkin.customer.Utils.Dunkin_Log;
import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Created by qtm-c-android on 29/6/17.
 */

public class SimpleScannerPromotionActivity extends BaseActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_scanner);

        setUpToolBar(getString(R.string.nav_scanner));

//        inflateView(R.layout.activity_simple_scanner, getString(R.string.nav_scanner));

        mScannerView = (ZXingScannerView) findViewById(R.id.scannerView);
        mScannerView.setAutoFocus(true);

//    Intent intent = new Intent();
//    intent.putExtra("scanner","6ebf08591eac10b5b33f4b20e6ee7188");
//    setResult(RESULT_OK,intent);
//    finish();

    }

    public void setUpToolBar(String title) {
        Toolbar toolBar = (Toolbar) findViewById(R.id.toolbar);
        if (toolBar != null) {
            toolBar.setTitleTextColor(ContextCompat.getColor(SimpleScannerPromotionActivity.this, android.R.color.white));
            setSupportActionBar(toolBar);
            if (title != null && title.length() > 0) {
                toolBar.findViewById(R.id.brandLogo).setVisibility(View.GONE);
                TextView tv = (TextView) toolBar.findViewById(R.id.toolbar_title);
                tv.setText(title);
                tv.setVisibility(View.VISIBLE);
            } else {
                toolBar.findViewById(R.id.brandLogo).setVisibility(View.VISIBLE);
                toolBar.findViewById(R.id.toolbar_title).setVisibility(View.GONE);
            }
        }

        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(ContextCompat.getDrawable(SimpleScannerPromotionActivity.this, R.drawable.ic_nav_back));
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause

//        ActivityManager activityManager = (ActivityManager) getApplicationContext()
//                .getSystemService(Context.ACTIVITY_SERVICE);
//
//        activityManager.moveTaskToFront(getTaskId(), 0);
    }

    @Override
    public void handleResult(Result rawResult) {

        // Do something with the result here
        Dunkin_Log.v("Scanner", rawResult.getText()); // Prints scan results
        Dunkin_Log.v("Scanner", rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode, pdf417 etc.)

        // If you would like to resume scanning, call this method below:
        mScannerView.resumeCameraPreview(this);

        Intent intent = new Intent();
        intent.putExtra("scanner", rawResult.getText());
        setResult(RESULT_OK, intent);
        finish();
    }

}

