package com.bangbangcoding.screenmirror.web.ui.player.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;

import com.bangbangcoding.screenmirror.R;


public class VolumeView extends View {
    private Paint mPaintRoot;
    private Paint mPaintProgress;
    private Paint mPaintIcon;
    int cornersRadiusBackground = 25;
    int cornersRadiusProgress = 12;
    int paddingBackground = 12;
    private int mHeight = 0;
    private int mWidth = 0;
    int padding = 8;
    RectF rectFRoot;
    RectF rectFProgress;
    float progress = 0;
    float max = 0;
    float heightProgress = 0;
    Bitmap bitmapIcon = null;
    private float mRotation = 0f;
    private boolean isRotate = true;

    public VolumeView(Context context) {
        super(context);
        init();
    }

    public VolumeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
        init();
    }

    public VolumeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
        init();
    }

    public Matrix mMatrix = new Matrix();

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRoundRect(
                rectFRoot,
                cornersRadiusBackground,
                cornersRadiusBackground,
                mPaintRoot
        );

        canvas.drawRoundRect(
                rectFProgress,
                cornersRadiusProgress,
                cornersRadiusProgress,
                mPaintProgress
        );

        if (isRotate) {
            float cy = mHeight - bitmapIcon.getHeight() / 2f - cornersRadiusBackground;
            float cx = mWidth / 2f;
            mMatrix.postRotate(mRotation, cx, cy);
        }
        canvas.drawBitmap(bitmapIcon, mMatrix, mPaintIcon);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        init();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mHeight = h;
        mWidth = w;
        heightProgress = mHeight - (cornersRadiusBackground + padding) * 2 - bitmapIcon.getHeight();
    }

    public void init(AttributeSet attr) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attr, R.styleable.VolumeView, 0, 0);
        int drawableResId = typedArray.getResourceId(R.styleable.VolumeView_resIcon, -1);
        if (drawableResId != -1) {
            bitmapIcon = BitmapFactory.decodeResource(getResources(), drawableResId);
        }
        isRotate = typedArray.getBoolean(R.styleable.VolumeView_isRotate, false);
    }

    private void init() {
        mPaintRoot = new Paint();
        mPaintRoot.setStyle(Paint.Style.FILL);
        mPaintRoot.setColor(Color.BLACK);
        mPaintRoot.setAntiAlias(true);

        mPaintProgress = new Paint();
        mPaintProgress.setStyle(Paint.Style.FILL);
        mPaintProgress.setColor(Color.parseColor("#1976D2"));
        mPaintProgress.setAntiAlias(true);

        mPaintIcon = new Paint();
        mPaintIcon.setStyle(Paint.Style.FILL);
        mPaintIcon.setAntiAlias(true);

        if (bitmapIcon == null)
            bitmapIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_brightness);

        float start = mWidth / 2f - bitmapIcon.getWidth() / 2f;
        float top = (int) (mHeight - bitmapIcon.getHeight() - cornersRadiusBackground);
        mMatrix.setTranslate(start, top);
        progress = cornersRadiusBackground + padding;

        rectFRoot = new RectF(paddingBackground,
                              paddingBackground,
                              mWidth - paddingBackground,
                              mHeight - paddingBackground
        );

        float topProgress = mHeight - (mHeight - (heightProgress + cornersRadiusBackground + padding));
        rectFProgress = new RectF(cornersRadiusBackground + padding,
                                  topProgress,
                                  mWidth - cornersRadiusBackground - padding,
                                  mHeight - cornersRadiusBackground - padding - bitmapIcon.getHeight()
        );
    }

    public void setProgress(float prog) {
        float topProgress = heightProgress - ((prog / max) * heightProgress) + cornersRadiusBackground + padding;
        if (topProgress > (cornersRadiusBackground + padding) && topProgress < (mHeight - cornersRadiusBackground - padding - bitmapIcon.getHeight())) {
            progress = prog;
            mRotation = (progress / max) * 5;
            rectFProgress = new RectF(cornersRadiusBackground + padding, // left
                                      topProgress, // top
                                      mWidth - cornersRadiusBackground - padding, // right
                                      mHeight - cornersRadiusBackground - padding - bitmapIcon.getHeight() // bottom
            );
            invalidate();
        }
    }

    public void incrementProgressBy(int diff) {
        setProgress(progress + diff);
    }

    public void setMax(float max) {
        this.max = max;
    }

    public float getMax() {
        return max;
    }

    public float getProgress() {
        return progress;
    }

    public void setBitmapIcon(int icon_resource) {
        bitmapIcon = BitmapFactory.decodeResource(getResources(), icon_resource);
        float start = mWidth / 2f - bitmapIcon.getWidth() / 2f;
        float top = (int) (mHeight - bitmapIcon.getHeight() - cornersRadiusBackground);
        mMatrix.setTranslate(start, top);
        invalidate();
    }

    public void setRotate(boolean bol) {
        isRotate = bol;
        invalidate();
    }

    public void showOrHide(final boolean isShow, final long duration) {
        if (isShow) {
            this.animate().setInterpolator(new FastOutSlowInInterpolator()).alpha(1f)
                    .setDuration(duration).setStartDelay(0)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(final Animator animation) {
                        }

                        @Override
                        public void onAnimationStart(Animator animation) {
                            setVisibility(View.VISIBLE);
                            super.onAnimationStart(animation);
                        }
                    }).start();
        } else {
            this.animate().setInterpolator(new FastOutSlowInInterpolator()).alpha(0f)
                    .setDuration(duration).setStartDelay(0)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(final Animator animation) {
                            setVisibility(View.INVISIBLE);
                        }
                    }).start();
        }
    }

    private int measureDimension(int desiredSize, int measureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = desiredSize;
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }

        if (result < desiredSize) {
            Log.e("ChartView", "The view is too small, the content might get cut");
        }
        return result;
    }
}
