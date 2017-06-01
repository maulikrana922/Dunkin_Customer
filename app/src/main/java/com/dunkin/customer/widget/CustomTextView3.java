package com.dunkin.customer.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.dunkin.customer.R;

/**
 * Created by qtm-c-android on 1/6/17.
 */

public class CustomTextView3 extends TextView {


    public CustomTextView3(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);

    }

    public CustomTextView3(Context context) {
        super(context);
    }

    public CustomTextView3(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(attrs);

    }

    private void init(AttributeSet attrs) {


        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CustomTextView);
            String fontName = a.getString(R.styleable.CustomTextView_fontName);
            if (fontName != null) {
                Typeface myTypeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/" + fontName);
                setTypeface(myTypeface);

            }
            setTextSize(22);
            a.recycle();
        }


    }
}

