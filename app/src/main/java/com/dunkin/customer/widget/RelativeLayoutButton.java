package com.dunkin.customer.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dunkin.customer.R;


public class RelativeLayoutButton extends LinearLayout {

    public ImageView imgIcon;
    public TextView btnText;


    public RelativeLayoutButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    public RelativeLayoutButton(Context context, AttributeSet attrs) {
        super(context, attrs);


    }

    public RelativeLayoutButton(Context context) {
        super(context);

    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.btnText = (TextView) findViewById(R.id.btnText);
        this.imgIcon = (ImageView) findViewById(R.id.imgIcon);

    }


}
