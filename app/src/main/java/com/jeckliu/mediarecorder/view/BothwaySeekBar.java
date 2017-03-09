package com.jeckliu.mediarecorder.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import com.jeckliu.mediarecorder.R;

/***
 * 双向seekBar
 * Created by Jeck.Liu on 2017/3/7 0007.
 */
public class BothwaySeekBar extends View{

    private OnSeekBarChangeListener onSeekBarChangeListener;
    private int width;
    private int height;
    private int barBg;
    private int progressBg;
    private float barPaintWidth;
    private int thumbLeftColor;
    private int thumbRightColor;
    private float thumbRadius;
    private int seekLeftProgress;
    private int seekRightProgress;
    private int seekTotalProgress;
    private float thumbLeftLocation;
    private float thumbRightLocation;
    private float rateLocation;

    private boolean thumbLeftTouch;
    private boolean thumbRightTouch;

    private Paint thumbLeftPaint;
    private Paint thumbRightPaint;
    private Paint barPaint;
    private Paint progressPaint;

    public BothwaySeekBar(Context context) {
        this(context,null);
    }

    public BothwaySeekBar(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public BothwaySeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs,R.styleable.BothwaySeekBar);
        int count = a.getIndexCount();
        for(int i =  0; i < count; i++){
            int value = a.getIndex(i);
            switch (value){
                case R.styleable.BothwaySeekBar_barBg:
                    barBg = a.getColor(value, Color.GRAY);
                    break;
                case R.styleable.BothwaySeekBar_progressBg:
                    progressBg = a.getColor(value,Color.RED);
                    break;
                case R.styleable.BothwaySeekBar_thumbLeftColor:
                    thumbLeftColor = a.getColor(value,Color.BLUE);
                    break;
                case R.styleable.BothwaySeekBar_thumbRightColor:
                    thumbRightColor = a.getColor(value,Color.BLUE);
                    break;
                case R.styleable.BothwaySeekBar_barHeight:
                    barPaintWidth = a.getDimension(value,10);
                    break;
                case R.styleable.BothwaySeekBar_thumbRadius:
                    thumbRadius = a.getDimension(value,5);
                    break;
                case R.styleable.BothwaySeekBar_seekLeftProgress:
                    seekLeftProgress = a.getInt(value,0);
                    break;
                case R.styleable.BothwaySeekBar_seekRightProgress:
                    seekRightProgress = a.getInt(value,100);
                    break;
                case R.styleable.BothwaySeekBar_seekTotalProgress:
                    seekTotalProgress = a.getInt(value,100);
                    break;
            }
        }
        a.recycle();
        init();
    }

    private void init() {
        barPaint = createPaint(barBg, barPaintWidth);
        progressPaint = createPaint(progressBg,barPaintWidth);
        thumbLeftPaint = createPaint(thumbLeftColor,1);
        thumbRightPaint = createPaint(thumbRightColor,1);
    }

    private Paint createPaint(int color,float width){
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(color);
        paint.setStrokeWidth(width);
        return paint;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawLine(0,thumbRadius,width,thumbRadius,barPaint);
        canvas.drawLine(thumbLeftLocation,thumbRadius,thumbRightLocation,thumbRadius,progressPaint);
        canvas.drawCircle(thumbLeftLocation,thumbRadius,thumbRadius,thumbLeftPaint);
        canvas.drawCircle(thumbRightLocation,thumbRadius,thumbRadius,thumbRightPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        width = widthSize;

        if(heightMode == MeasureSpec.EXACTLY){
            height = heightSize;
        }else{
            height = (int) (2 * (thumbRadius + thumbLeftPaint.getStrokeWidth()));
       }
        progressToLocation();
        if(onSeekBarChangeListener != null){
            onSeekBarChangeListener.onProgressChanged(thumbLeftTouch,thumbRightTouch,seekLeftProgress,seekRightProgress,thumbLeftLocation,thumbRightLocation);
        }
        setMeasuredDimension(width,height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        progressToLocation();
        if(onSeekBarChangeListener != null){
            onSeekBarChangeListener.onProgressChanged(thumbLeftTouch,thumbRightTouch,seekLeftProgress,seekRightProgress,thumbLeftLocation,thumbRightLocation);
        }

    }

    private void progressToLocation() {
        rateLocation = (width ) * 1.0f / seekTotalProgress;
        thumbLeftLocation = seekLeftProgress * rateLocation;
        thumbRightLocation = seekRightProgress * rateLocation;
    }

    private void locationToProgress(){
        float rate = seekTotalProgress * 1.0f / (width );
        seekLeftProgress = (int) ((thumbLeftLocation ) * rate);
        seekRightProgress = (int) ((thumbRightLocation ) * rate + 0.5);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if(x <= (thumbLeftLocation + thumbRadius) && x >= (thumbLeftLocation -thumbRadius)){
                    thumbLeftTouch = true;
                }else{
                    thumbLeftTouch = false;
                }
                if(x <= (thumbRightLocation + thumbRadius) && x >= (thumbRightLocation -thumbRadius)){
                    thumbRightTouch = true;
                }else{
                    thumbRightTouch = false;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if(thumbLeftTouch && x > 0 && ((thumbRightLocation - x) > rateLocation)
                        && x < (thumbRightLocation - 2 * thumbRadius)){
                    thumbLeftLocation = x;
                }
                if(thumbRightTouch && x < width && ((x - thumbLeftLocation) > rateLocation) && x >(thumbLeftLocation + 2 * thumbRadius)){
                    thumbRightLocation = x;
                }
                locationToProgress();
                if(onSeekBarChangeListener != null){
                    onSeekBarChangeListener.onProgressChanged(thumbLeftTouch,thumbRightTouch,seekLeftProgress,seekRightProgress,thumbLeftLocation,thumbRightLocation);
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        invalidate();
        return true;
    }

    public void setSeekTotalProgress(int seekTotalProgress){
        this.seekTotalProgress = seekTotalProgress;
    }

    public void setSeekLeftProgress(int seekLeftProgress){
        this.seekLeftProgress = seekLeftProgress;
    }

    public void setSeekRightProgress(int seekRightProgress){
        this.seekRightProgress = seekRightProgress;
    }

    public void setOnSeekBarChangeListener(OnSeekBarChangeListener onSeekBarChangeListener){
        this.onSeekBarChangeListener = onSeekBarChangeListener;
    }

    public interface OnSeekBarChangeListener{
        void onProgressChanged(boolean leftTouch, boolean rightTouch, int leftProgress,int rightProgress,float leftLocation,float rightLocation);
    }
}
