package com.dunkin.customer.Utils;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialog;
import android.view.Window;

import com.dunkin.customer.R;

public class LoadingDialog extends AppCompatDialog {
    public LoadingDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_loading);

        //getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        setCancelable(false);
    }
}
