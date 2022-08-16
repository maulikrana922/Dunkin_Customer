package com.dunkin.customer.customSeekBar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.dunkin.customer.R;

/**
 * Created by owais.ali on 6/20/2016.
 */
public class CustomSeekBar extends View {

    private static final int INVALID_POINTER_ID = 255;
    //private static int DEFAULT_THUMB_WIDTH ;
    //private static int DEFAULT_THUMB_HEIGHT;

    private final float NO_STEP = -1f;
    private OnSeekBarChangeListener onSeekBarChangeListener;
    private OnSeekBarFinalValueListener onSeekBarFinalValueListener;
    private float absoluteMinValue;
    private float absoluteMaxValue;
    private float minValue;
    private float maxValue;
    private float minStartValue;
    private float steps;
    private int mActivePointerId = INVALID_POINTER_ID;
    private int position;
    private int nextPosition;
    private int dataType;
    private float cornerRadius;
    private int barColorMode;
    private int barColor;
    private int barGradientStart;
    private int barGradientEnd;
    private int barHighlightColorMode;
    private int barHighlightColor;
    private int barHighlightGradientStart;
    private int barHighlightGradientEnd;
    private int thumbColor;
    private int thumbColorNormal;
    private int thumbColorPressed;
    private boolean seekBarTouchEnabled;
    private float barPadding;
    private float barHeight;
    private float thumbWidth;
    private float thumbHeight;
    private float thumbDiameter;
    private Drawable thumbDrawable;
    private Drawable thumbDrawablePressed;
    private Bitmap thumb;
    private Bitmap thumbPressed;
    private Thumb pressedThumb;
    private double normalizedMinValue = 0d;
    private double normalizedMaxValue = 100d;
    private int pointerIndex;
    private RectF _rect;
    private Paint _paint;
    private RectF rectThumb;
    private boolean mIsDragging;

    public CustomSeekBar(Context context) {
        this(context, null);
    }

    public CustomSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // prevent render is in edit mode
        if (isInEditMode()) return;

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CustomSeekBar);
        try {
            cornerRadius = getCornerRadius(array);
            minValue = getMinValue(array);
            maxValue = getMaxValue(array);
            minStartValue = getMinStartValue(array);
            steps = getSteps(array);
            barHeight = getBarHeight(array);
            barColorMode = getBarColorMode(array);
            barColor = getBarColor(array);
            barGradientStart = getBarGradientStart(array);
            barGradientEnd = getBarGradientEnd(array);
            barHighlightColorMode = getBarHighlightColorMode(array);
            barHighlightColor = getBarHighlightColor(array);
            barHighlightGradientStart = getBarHighlightGradientStart(array);
            barHighlightGradientEnd = getBarHighlightGradientEnd(array);
            thumbColorNormal = getThumbColor(array);
            thumbColorPressed = getThumbColorPressed(array);
            thumbDrawable = getThumbDrawable(array);
            thumbDrawablePressed = getThumbDrawablePressed(array);
            dataType = getDataType(array);
            position = getPosition(array);
            nextPosition = position;
            thumbDiameter = getDiameter(array);
            seekBarTouchEnabled = isSeekBarTouchEnabled(array);
        } finally {
            array.recycle();
        }

        init();
    }

    protected void init() {
        absoluteMinValue = minValue;
        absoluteMaxValue = maxValue;
        thumbColor = thumbColorNormal;
        thumb = getBitmap(thumbDrawable);
        thumbPressed = getBitmap(thumbDrawablePressed);
        thumbPressed = (thumbPressed == null) ? thumb : thumbPressed;

        thumbWidth = getThumbWidth();
        thumbHeight = getThumbHeight();

        barHeight = getBarHeight();
        barPadding = getBarPadding();

        _paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        _rect = new RectF();
        rectThumb = new RectF();

        pressedThumb = null;

        setMinStartValue();

        setWillNotDraw(false);
    }

    public CustomSeekBar setBarColorMode(int barColorMode) {
        this.barColorMode = barColorMode;
        return this;
    }

    public CustomSeekBar setBarGradientStart(int barGradientStart) {
        this.barGradientStart = barGradientStart;
        return this;
    }

    public CustomSeekBar setBarGradientEnd(int barGradientEnd) {
        this.barGradientEnd = barGradientEnd;
        return this;
    }

    public CustomSeekBar setBarHighlightColorMode(int barHighlightColorMode) {
        this.barHighlightColorMode = barHighlightColorMode;
        return this;
    }

    public CustomSeekBar setBarHighlightGradientStart(int barHighlightGradientStart) {
        this.barHighlightGradientStart = barHighlightGradientStart;
        return this;
    }

    public CustomSeekBar setBarHighlightGradientEnd(int barHighlightGradientEnd) {
        this.barHighlightGradientEnd = barHighlightGradientEnd;
        return this;
    }

    public CustomSeekBar setThumbColor(int leftThumbColorNormal) {
        this.thumbColorNormal = leftThumbColorNormal;
        return this;
    }

    public CustomSeekBar setThumbHighlightColor(int leftThumbColorPressed) {
        this.thumbColorPressed = leftThumbColorPressed;
        return this;
    }

    public CustomSeekBar setThumbDrawable(int resId) {
        setThumbDrawable(ContextCompat.getDrawable(getContext(), resId));
        return this;
    }

    public CustomSeekBar setThumbDrawable(Drawable drawable) {
        setThumbBitmap(getBitmap(drawable));
        return this;
    }

    public CustomSeekBar setThumbBitmap(Bitmap bitmap) {
        thumb = bitmap;
        return this;
    }

    public CustomSeekBar setThumbHighlightDrawable(int resId) {
        setThumbHighlightDrawable(ContextCompat.getDrawable(getContext(), resId));
        return this;
    }

    public CustomSeekBar setThumbHighlightDrawable(Drawable drawable) {
        setThumbHighlightBitmap(getBitmap(drawable));
        return this;
    }

    public CustomSeekBar setThumbHighlightBitmap(Bitmap bitmap) {
        thumbPressed = bitmap;
        return this;
    }

    public void setOnSeekBarChangeListener(OnSeekBarChangeListener onSeekBarChangeListener) {
        this.onSeekBarChangeListener = onSeekBarChangeListener;
        if (this.onSeekBarChangeListener != null) {
            this.onSeekBarChangeListener.valueChanged(getSelectedMinValue());
        }
    }

    public void setOnSeekBarFinalValueListener(OnSeekBarFinalValueListener onSeekBarFinalValueListener) {
        this.onSeekBarFinalValueListener = onSeekBarFinalValueListener;
    }

    public Thumb getPressedThumb() {
        return pressedThumb;
    }

    public RectF getThumbRect() {
        return rectThumb;
    }

    public float getCornerRadius() {
        return cornerRadius;
    }

    public CustomSeekBar setCornerRadius(float cornerRadius) {
        this.cornerRadius = cornerRadius;
        return this;
    }

    public float getMinValue() {
        return minValue;
    }

    public CustomSeekBar setMinValue(float minValue) {
        this.minValue = minValue;
        this.absoluteMinValue = minValue;
        return this;
    }

    public float getMaxValue() {
        return maxValue;
    }

    public CustomSeekBar setMaxValue(float maxValue) {
        this.maxValue = maxValue;
        this.absoluteMaxValue = maxValue;
        return this;
    }

    public float getMinStartValue() {
        return minStartValue;
    }

    public CustomSeekBar setMinStartValue(float minStartValue) {
        this.minStartValue = minStartValue;
        return this;
    }

    public float getSteps() {
        return steps;
    }

    public CustomSeekBar setSteps(float steps) {
        this.steps = steps;
        return this;
    }

    public int getBarColor() {
        return barColor;
    }

    public CustomSeekBar setBarColor(int barColor) {
        this.barColor = barColor;
        return this;
    }

    public int getBarHighlightColor() {
        return barHighlightColor;
    }

    public CustomSeekBar setBarHighlightColor(int barHighlightColor) {
        this.barHighlightColor = barHighlightColor;
        return this;
    }

    public int getLeftThumbColor() {
        return thumbColor;
    }

    public int getLeftThumbColorPressed() {
        return thumbColorPressed;
    }

    public Drawable getLeftDrawable() {
        return thumbDrawable;
    }

    public Drawable getLeftDrawablePressed() {
        return thumbDrawablePressed;
    }

    public int getDataType() {
        return dataType;
    }

    public CustomSeekBar setDataType(int dataType) {
        this.dataType = dataType;
        return this;
    }

    public int getPosition() {
        return this.position;
    }

    public CustomSeekBar setPosition(int pos) {
        this.nextPosition = pos;
        return this;
    }

    public float getThumbWidth() {
        return (thumb != null) ? thumb.getWidth() : getThumbDiameter();
    }

    public float getThumbHeight() {
        return (thumb != null) ? thumb.getHeight() : getThumbDiameter();
    }

    protected float getThumbDiameter() {
        return (thumbDiameter > 0) ? thumbDiameter : getResources().getDimension(R.dimen.thumb_size);
    }

    public float getBarHeight() {
        return barHeight > 0 ? barHeight : (thumbHeight * 0.5f) * 0.3f;
    }

    public CustomSeekBar setBarHeight(float barHeight) {
        this.barHeight = barHeight;
        return this;
    }

    public float getDiameter(final TypedArray typedArray) {
        return typedArray.getDimensionPixelSize(R.styleable.CustomSeekBar_thumb_diameter, getResources().getDimensionPixelSize(R.dimen.thumb_size));
    }

    protected boolean isSeekBarTouchEnabled(final TypedArray typedArray) {
        return typedArray.getBoolean(R.styleable.CustomSeekBar_seek_bar_touch_enabled, false);
    }

    public float getBarPadding() {
        return thumbWidth * 0.5f;
    }

    public Number getSelectedMinValue() {
        double nv = normalizedMinValue;
        if (steps > 0 && steps <= ((absoluteMaxValue) / 2)) {
            float stp = steps / (absoluteMaxValue - absoluteMinValue) * 100;
            double half_step = stp / 2;
            double mod = nv % stp;
            if (mod > half_step) {
                nv = nv - mod;
                nv = nv + stp;
            } else {
                nv = nv - mod;
            }
        } else {
            if (steps != NO_STEP)
                throw new IllegalStateException("steps out of range " + steps);
        }

        nv = (position == Position.LEFT) ? nv : Math.abs(nv - maxValue);
        return formatValue(normalizedToValue(nv));
    }

    public Number getSelectedMaxValue() {

        double nv = normalizedMaxValue;
        if (steps > 0 && steps <= (absoluteMaxValue / 2)) {
            float stp = steps / (absoluteMaxValue - absoluteMinValue) * 100;
            double half_step = stp / 2;
            double mod = nv % stp;
            if (mod > half_step) {
                nv = nv - mod;
                nv = nv + stp;
            } else {
                nv = nv - mod;
            }
        } else {
            if (steps != NO_STEP)
                throw new IllegalStateException("steps out of range " + steps);
        }

        return formatValue(normalizedToValue(nv));
    }

    public void apply() {

        // reset normalize min and max value
        //normalizedMinValue = 0d;
        //normalizedMaxValue = 100d;

        thumbWidth = (thumb != null) ? thumb.getWidth() : getResources().getDimension(R.dimen.thumb_size);
        thumbHeight = (thumb != null) ? thumb.getHeight() : getResources().getDimension(R.dimen.thumb_size);

//        barHeight = (thumbHeight * 0.5f) * 0.3f;
        barPadding = thumbWidth * 0.5f;

        // set min start value
        if (minStartValue <= minValue) {
            minStartValue = 0;
            setNormalizedMinValue(minStartValue);
        } else if (minStartValue > maxValue) {
            minStartValue = maxValue;
            setNormalizedMinValue(minStartValue);
        } else {
            //minStartValue = (long)getSelectedMinValue();
            if (nextPosition != position) {
                minStartValue = (float) Math.abs(normalizedMaxValue - normalizedMinValue);
            }
            if (minStartValue > minValue) {
                minStartValue = Math.min(minStartValue, absoluteMaxValue);
                minStartValue -= absoluteMinValue;
                minStartValue = minStartValue / (absoluteMaxValue - absoluteMinValue) * 100;
            }

            setNormalizedMinValue(minStartValue);
            position = nextPosition;

        }

        invalidate();
        if (onSeekBarChangeListener != null) {
            onSeekBarChangeListener.valueChanged(getSelectedMinValue());
        }
    }

    protected Bitmap getBitmap(Drawable drawable) {
        return (drawable != null) ? ((BitmapDrawable) drawable).getBitmap() : null;
    }

    protected float getCornerRadius(final TypedArray typedArray) {
        return typedArray.getFloat(R.styleable.CustomSeekBar_corner_radius, 0f);
    }

    protected float getMinValue(final TypedArray typedArray) {
        return typedArray.getFloat(R.styleable.CustomSeekBar_min_value, 0f);
    }

    protected float getMaxValue(final TypedArray typedArray) {
        return typedArray.getFloat(R.styleable.CustomSeekBar_max_value, 100f);
    }

    protected float getMinStartValue(final TypedArray typedArray) {
        return typedArray.getFloat(R.styleable.CustomSeekBar_min_start_value, minValue);
    }

    protected float getSteps(final TypedArray typedArray) {
        return typedArray.getFloat(R.styleable.CustomSeekBar_steps, NO_STEP);
    }

    protected float getBarHeight(final TypedArray typedArray) {
        return typedArray.getDimensionPixelSize(R.styleable.CustomSeekBar_bar_height, 0);
    }

    protected int getBarColorMode(final TypedArray typedArray) {
        return typedArray.getInt(R.styleable.CustomSeekBar_bar_color_mode, ColorMode.SOLID);
    }

    protected int getBarColor(final TypedArray typedArray) {
        return typedArray.getColor(R.styleable.CustomSeekBar_bar_color, Color.GRAY);
    }

    protected int getBarGradientStart(final TypedArray typedArray) {
        return typedArray.getColor(R.styleable.CustomSeekBar_bar_gradient_start, Color.GRAY);
    }

    protected int getBarGradientEnd(final TypedArray typedArray) {
        return typedArray.getColor(R.styleable.CustomSeekBar_bar_gradient_end, Color.DKGRAY);
    }

    protected int getBarHighlightColorMode(final TypedArray typedArray) {
        return typedArray.getInt(R.styleable.CustomSeekBar_bar_highlight_color_mode, ColorMode.SOLID);
    }

    protected int getBarHighlightColor(final TypedArray typedArray) {
        return typedArray.getColor(R.styleable.CustomSeekBar_bar_highlight_color, Color.BLACK);
    }

    protected int getBarHighlightGradientStart(final TypedArray typedArray) {
        return typedArray.getColor(R.styleable.CustomSeekBar_bar_highlight_gradient_start, Color.DKGRAY);
    }

    protected int getBarHighlightGradientEnd(final TypedArray typedArray) {
        return typedArray.getColor(R.styleable.CustomSeekBar_bar_highlight_gradient_end, Color.BLACK);
    }

    protected int getThumbColor(final TypedArray typedArray) {
        return typedArray.getColor(R.styleable.CustomSeekBar_thumb_color, Color.BLACK);
    }

    protected int getThumbColorPressed(final TypedArray typedArray) {
        return typedArray.getColor(R.styleable.CustomSeekBar_thumb_color_pressed, Color.DKGRAY);
    }

    protected Drawable getThumbDrawable(final TypedArray typedArray) {
        return typedArray.getDrawable(R.styleable.CustomSeekBar_thumb_image);
    }

    protected Drawable getThumbDrawablePressed(final TypedArray typedArray) {
        return typedArray.getDrawable(R.styleable.CustomSeekBar_thumb_image_pressed);
    }

    protected int getDataType(final TypedArray typedArray) {
        return typedArray.getInt(R.styleable.CustomSeekBar_data_type, DataType.INTEGER);
    }

    protected final int getPosition(final TypedArray typedArray) {
        final int pos = typedArray.getInt(R.styleable.CustomSeekBar_position, Position.LEFT);

        normalizedMinValue = (pos == Position.LEFT) ? normalizedMinValue : normalizedMaxValue;
        return pos;
    }

    protected void setupBar(final Canvas canvas, final Paint paint, final RectF rect) {
        rect.left = barPadding;
        rect.top = 0.5f * (getHeight() - barHeight);
        rect.right = getWidth() - barPadding;
        rect.bottom = 0.5f * (getHeight() + barHeight);

        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);

        if (barColorMode == ColorMode.SOLID) {
            paint.setColor(barColor);
            drawBar(canvas, paint, rect);

        } else {
            paint.setShader(
                    new LinearGradient(rect.left, rect.bottom, rect.right, rect.top,
                            barGradientStart,
                            barGradientEnd,
                            Shader.TileMode.MIRROR)
            );

            drawBar(canvas, paint, rect);

            paint.setShader(null);
        }
    }

    protected void drawBar(final Canvas canvas, final Paint paint, final RectF rect) {
        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, paint);
    }

    protected void setupHighlightBar(final Canvas canvas, final Paint paint, final RectF rect) {
        if (position == Position.RIGHT) {
            rect.left = normalizedToScreen(normalizedMinValue) + (getThumbWidth() / 2);
            rect.right = getWidth() - (getThumbWidth() / 2);
        } else {
            rect.left = getThumbWidth() / 2;
            rect.right = normalizedToScreen(normalizedMinValue) + (getThumbWidth() / 2);
        }

        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);

        if (barHighlightColorMode == ColorMode.SOLID) {
            paint.setColor(barHighlightColor);
            drawHighlightBar(canvas, paint, rect);

        } else {
            paint.setShader(
                    new LinearGradient(rect.left, rect.bottom, rect.right, rect.top,
                            barHighlightGradientStart,
                            barHighlightGradientEnd,
                            Shader.TileMode.MIRROR)
            );

            drawHighlightBar(canvas, paint, rect);

            paint.setShader(null);
        }
    }

    protected void drawHighlightBar(final Canvas canvas, final Paint paint, final RectF rect) {
        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, paint);
    }

    protected void setupLeftThumb(final Canvas canvas, final Paint paint, final RectF rect) {

        thumbColor = (Thumb.MIN.equals(pressedThumb)) ? thumbColorPressed : thumbColorNormal;
        paint.setColor(thumbColor);

        rectThumb.left = normalizedToScreen(normalizedMinValue);
        rectThumb.right = Math.min(rectThumb.left + (getThumbWidth() / 2) + barPadding, getWidth());

        rectThumb.top = 0f;
        rectThumb.bottom = thumbHeight;

        if (thumb != null) {
            Bitmap lThumb = (Thumb.MIN.equals(pressedThumb)) ? thumbPressed : thumb;
            drawLeftThumbWithImage(canvas, paint, rectThumb, lThumb);
        } else {
            drawLeftThumbWithColor(canvas, paint, rectThumb);
        }
    }

    protected void drawLeftThumbWithColor(final Canvas canvas, final Paint paint, final RectF rect) {
        canvas.drawOval(rect, paint);
    }

    protected void drawLeftThumbWithImage(final Canvas canvas, final Paint paint, final RectF rect, final Bitmap image) {
        canvas.drawBitmap(image, rect.left, rect.top, paint);
    }

    protected void trackTouchEvent(MotionEvent event) {
        final int pointerIndex = event.findPointerIndex(mActivePointerId);
        try {
            final float x = event.getX(pointerIndex);

            if (Thumb.MIN.equals(pressedThumb)) {
                setNormalizedMinValue(screenToNormalized(x));
            }
        } catch (Exception ignored) {
        }
    }

    protected void touchDown(final float x, final float y) {

    }

    protected void touchMove(final float x, final float y) {

    }

    protected void touchUp(final float x, final float y) {

    }

    protected int getMeasureSpecWith(int widthMeasureSpec) {
        int width = 200;
        if (MeasureSpec.UNSPECIFIED != MeasureSpec.getMode(widthMeasureSpec)) {
            width = MeasureSpec.getSize(widthMeasureSpec);
        }
        return width;
    }

    protected int getMeasureSpecHeight(int heightMeasureSpec) {
        int height = Math.round(thumbHeight);
        if (MeasureSpec.UNSPECIFIED != MeasureSpec.getMode(heightMeasureSpec)) {
            height = Math.min(height, MeasureSpec.getSize(heightMeasureSpec));
        }
        return height;
    }

    protected final void log(Object object) {
        Log.d("CRS=>", String.valueOf(object));
    }

    private void setMinStartValue() {
        if (minStartValue > minValue && minStartValue < maxValue) {
            minStartValue = Math.min(minStartValue, absoluteMaxValue);
            minStartValue -= absoluteMinValue;
            minStartValue = minStartValue / (absoluteMaxValue - absoluteMinValue) * 100;
            setNormalizedMinValue(minStartValue);
        }
    }

    private Thumb evalPressedThumb(float touchX) {
        Thumb result = null;

        boolean minThumbPressed = isInThumbRange(touchX, normalizedMinValue);
        if (seekBarTouchEnabled || minThumbPressed) {
            // if both thumbs are pressed (they lie on top of each other), choose the one with more room to drag. this avoids "stalling" the thumbs in a corner, not being able to drag them apart anymore.
            result = Thumb.MIN;
        }
        return result;
    }

    private boolean isInThumbRange(float touchX, double normalizedThumbValue) {
        float thumbPos = normalizedToScreen(normalizedThumbValue);
        float left = thumbPos - (getThumbWidth() / 2);
        float right = thumbPos + (getThumbWidth() / 2);
        float x = touchX - (getThumbWidth() / 2);
        if (thumbPos > (getWidth() - thumbWidth)) x = touchX;
        return (x >= left && x <= right);
    }

    private void onStartTrackingTouch() {
        mIsDragging = true;
    }

    private void onStopTrackingTouch() {
        mIsDragging = false;
    }

    private float normalizedToScreen(double normalizedCoord) {

        float width = getWidth() - (barPadding * 2);
        return (float) normalizedCoord / 100f * width;
    }

    private double screenToNormalized(float screenCoord) {
        double width = getWidth();

        if (width <= 2 * barPadding) {
            // prevent division by zero, simply return 0.
            return 0d;
        } else {
            width = width - (barPadding * 2);
            double result = screenCoord / width * 100d;
            result = result - (barPadding / width * 100d);
            result = Math.min(100d, Math.max(0d, result));
            return result;

        }
    }

    private void setNormalizedMinValue(double value) {
        normalizedMinValue = Math.max(0d, Math.min(100d, Math.min(value, normalizedMaxValue)));
        invalidate();
    }

    private void setNormalizedMaxValue(double value) {
        normalizedMaxValue = Math.max(0d, Math.min(100d, Math.max(value, normalizedMinValue)));
        invalidate();
    }

    private double normalizedToValue(double normalized) {
        double val = normalized / 100 * (maxValue - minValue);
        val = (position == Position.LEFT) ? val + minValue : val;
        return val;
    }

    private void attemptClaimDrag() {
        if (getParent() != null) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }
    }

    private <T extends Number> Number formatValue(T value) throws IllegalArgumentException {
        final Double v = (Double) value;
        if (dataType == DataType.LONG) {
            return v.longValue();
        }
        if (dataType == DataType.DOUBLE) {
            return v;
        }
        if (dataType == DataType.INTEGER) {
            return Math.round(v);
        }
        if (dataType == DataType.FLOAT) {
            return v.floatValue();
        }
        if (dataType == DataType.SHORT) {
            return v.shortValue();
        }
        if (dataType == DataType.BYTE) {
            return v.byteValue();
        }
        throw new IllegalArgumentException("Number class '" + value.getClass().getName() + "' is not supported");
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // prevent render is in edit mode
        if (isInEditMode()) return;

        // setup bar
        setupBar(canvas, _paint, _rect);

        // setup seek bar active range line
        setupHighlightBar(canvas, _paint, _rect);

        // draw left thumb
        setupLeftThumb(canvas, _paint, _rect);

    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getMeasureSpecWith(widthMeasureSpec), getMeasureSpecHeight(heightMeasureSpec));
    }

    /**
     * Handles thumb selection and movement. Notifies listener callback on certain events.
     */
    @Override
    public synchronized boolean onTouchEvent(MotionEvent event) {

        if (!isEnabled())
            return false;


        final int action = event.getAction();

        switch (action & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_DOWN:
                mActivePointerId = event.getPointerId(event.getPointerCount() - 1);
                pointerIndex = event.findPointerIndex(mActivePointerId);
                float mDownMotionX = event.getX(pointerIndex);

                pressedThumb = evalPressedThumb(mDownMotionX);

                if (pressedThumb == null) return super.onTouchEvent(event);

                touchDown(event.getX(pointerIndex), event.getY(pointerIndex));
                setPressed(true);
                invalidate();
                onStartTrackingTouch();
                trackTouchEvent(event);
                attemptClaimDrag();

                break;

            case MotionEvent.ACTION_MOVE:
                if (pressedThumb != null) {

                    if (mIsDragging) {
                        touchMove(event.getX(pointerIndex), event.getY(pointerIndex));
                        trackTouchEvent(event);
                    }

                    if (onSeekBarChangeListener != null) {
                        onSeekBarChangeListener.valueChanged(getSelectedMinValue());
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mIsDragging) {
                    trackTouchEvent(event);
                    onStopTrackingTouch();
                    setPressed(false);
                    touchUp(event.getX(pointerIndex), event.getY(pointerIndex));
                    if (onSeekBarFinalValueListener != null) {
                        onSeekBarFinalValueListener.finalValue(getSelectedMinValue());
                    }
                } else {
                    // Touch up when we never crossed the touch slop threshold
                    // should be interpreted as a tap-seek to that location.
                    onStartTrackingTouch();
                    trackTouchEvent(event);
                    onStopTrackingTouch();
                }

                pressedThumb = null;
                invalidate();
                if (onSeekBarChangeListener != null) {
                    onSeekBarChangeListener.valueChanged(getSelectedMinValue());
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN: {
                //final int index = event.getPointerCount() - 1;
                // final int index = ev.getActionIndex();
                /*mDownMotionX = event.getX(index);
                mActivePointerId = event.getPointerId(index);
                invalidate();*/
                break;
            }
            case MotionEvent.ACTION_POINTER_UP:
                /*onSecondaryPointerUp(event);*/
                invalidate();
                break;
            case MotionEvent.ACTION_CANCEL:
                if (mIsDragging) {
                    onStopTrackingTouch();
                    setPressed(false);
                    touchUp(event.getX(pointerIndex), event.getY(pointerIndex));
                }
                invalidate(); // see above explanation
                break;
        }

        return true;

    }

    protected enum Thumb {MIN}

    public static final class DataType {
        public static final int LONG = 0;
        public static final int DOUBLE = 1;
        public static final int INTEGER = 2;
        public static final int FLOAT = 3;
        public static final int SHORT = 4;
        public static final int BYTE = 5;
    }

    public static final class Position {
        public static final int LEFT = 0;
        public static final int RIGHT = 1;
    }

    public static final class ColorMode {
        public static final int SOLID = 0;
        public static final int GRADIENT = 1;
    }
}
