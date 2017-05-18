package com.jeckliu.mediarecorder.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.jeckliu.mediarecorder.R;

/***
 * Created by Jeck.Liu on 2017/5/12 0012.
 */
public class ShootIconView extends View {
    private final long longPressFlagTime = 500;  //超过500毫秒按照长按事件处理

    private Paint innerPaint;
    private Paint outerPaint;
    private Paint outerProgressPaint;
    private RectF arc;
    private int centerX;
    private int centerY;
    private int innerPaintColor;
    private int outerPaintColor;
    private int outerProgressPaintColor;
    private float innerOriginalRadius;
    private float outerOriginalRadius;
    private float innerScaleRadius;
    private float outerScaleRadius;
    private float innerDrawRadius;
    private float outerDrawRadius;
    private float progressWidth;
    private long shootMaxTime;
    private long pressDownTime;
    private OnCallbackListener onCallbackListener;
    private boolean isStopDraw;
    private boolean isExpanded;
    private long shootTime;

    public ShootIconView(Context context) {
        this(context, null);
    }

    public ShootIconView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShootIconView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ShootIconView);
        int count = a.getIndexCount();
        for (int i = 0; i < count; i++) {
            int value = a.getIndex(i);
            switch (value) {
                case R.styleable.ShootIconView_outerPaintColor:
                    outerPaintColor = a.getColor(value, Color.GRAY);
                    break;
                case R.styleable.ShootIconView_outerProgressPaintColor:
                    outerProgressPaintColor = a.getColor(value, Color.BLUE);
                    break;
                case R.styleable.ShootIconView_innerPaintColor:
                    innerPaintColor = a.getColor(value, Color.WHITE);
                    break;
                case R.styleable.ShootIconView_outerOriginalRadius:
                    outerOriginalRadius = a.getDimension(value, 100);
                    break;
                case R.styleable.ShootIconView_innerOriginalRadius:
                    innerOriginalRadius = a.getDimension(value, 70);
                    break;
                case R.styleable.ShootIconView_innerScaleRadius:
                    innerScaleRadius = a.getDimension(value, 70);
                    break;
                case R.styleable.ShootIconView_outerScaleRadius:
                    outerScaleRadius = a.getDimension(value, 100);
                    break;
                case R.styleable.ShootIconView_progressWidth:
                    progressWidth = a.getDimension(value,20);
                    break;
                case R.styleable.ShootIconView_shootMaxTime:
                    shootMaxTime = a.getInt(value, 10000);
                    break;
            }
        }
        a.recycle();
        outerDrawRadius = outerOriginalRadius;
        innerDrawRadius = innerOriginalRadius;
        init();
    }

    private void init() {
        innerPaint = createPaint(innerPaintColor);

        outerPaint = createPaint(outerPaintColor);

        outerProgressPaint = createPaint(outerProgressPaintColor);

    }

    private Paint createPaint(int color) {
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        return paint;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(centerX, centerY, outerDrawRadius, outerPaint);
        if (isExpanded) {
            canvas.drawArc(arc, -90, 360 * shootTime / shootMaxTime, true, outerProgressPaint);
            canvas.drawCircle(centerX, centerY, outerDrawRadius - progressWidth, outerPaint);
        }
        canvas.drawCircle(centerX, centerY, innerDrawRadius, innerPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                pressDownTime = System.currentTimeMillis();
                isStopDraw = false;
                new DrawThread().start();
                break;
            case MotionEvent.ACTION_UP:
                if (!isStopDraw) {
                    isStopDraw = true;
                    if (shootTime <= 0) {
                        if (onCallbackListener != null) {
                            onCallbackListener.onTakePhoto();
                        }
                    } else {
                        if (onCallbackListener != null) {
                            onCallbackListener.onStopRecordVideo(shootTime);
                        }
                    }
                }
                break;
        }
        return true;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width;
        int height;
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            width = (int) (2 * outerScaleRadius);
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            height = (int) (2 * outerScaleRadius);
        }
        centerX = width / 2;
        centerY = height / 2;
        setMeasuredDimension(width, height);
    }

    public void reset() {
        outerDrawRadius = outerOriginalRadius;
        innerDrawRadius = innerOriginalRadius;
        isExpanded = false;
        postInvalidate();
    }

    public void setOnCallbackListener(OnCallbackListener onCallbackListener) {
        this.onCallbackListener = onCallbackListener;
    }

    public interface OnCallbackListener {
        void onTakePhoto();

        void onStartRecordVideo();

        void onStopRecordVideo(long duration);
    }

    private class DrawThread extends Thread {

        @Override
        public void run() {
            while (!isStopDraw) {
                long pressUpTime = System.currentTimeMillis();
                shootTime = pressUpTime - pressDownTime - longPressFlagTime;
                if (shootTime >= 0) {
                    if (!isExpanded) {
                        outerDrawRadius = outerScaleRadius;
                        innerDrawRadius = innerScaleRadius;
                        arc = new RectF(centerX - outerDrawRadius, centerY - outerDrawRadius,
                                centerX + outerDrawRadius, centerY + outerDrawRadius);
                        isExpanded = true;
                        if (onCallbackListener != null) {
                            onCallbackListener.onStartRecordVideo();
                        }
                    }
                    postInvalidate();
                }
                if (shootTime >= shootMaxTime) {
                    isStopDraw = true;
                    if (onCallbackListener != null) {
                        onCallbackListener.onStopRecordVideo(shootTime);
                    }
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}