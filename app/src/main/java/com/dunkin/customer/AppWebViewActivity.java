package com.dunkin.customer;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by Admin on 10/1/2015.
 */
public class AppWebViewActivity extends BaseActivity {

    String URL;
    WebView wv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        inflateView(R.layout.fragment_about_us, null);


        wv = (WebView) findViewById(R.id.aboutUsView);
        URL = getIntent().getStringExtra("url");
        wv.getSettings().setJavaScriptEnabled(true);
        wv.setWebViewClient(new myWebClient());
        wv.loadUrl(URL);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        wv.loadUrl("");
        wv.stopLoading();
    }

    public class myWebClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // TODO Auto-generated method stub
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO Auto-generated method stub

            view.loadUrl(url);
            return true;

        }
    }
}
