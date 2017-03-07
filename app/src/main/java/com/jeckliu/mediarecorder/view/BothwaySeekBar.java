package com.jeckliu.mediarecorder.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import com.jeckliu.mediarecorder.R;

/***
 * 双向seekBar
 * Created by Jeck.Liu on 2017/3/7 0007.
 */
public class BothwaySeekBar extends View{

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
    private int thumbLeftLocation;
    private int thumbRightLoaction;

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
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);
        paint.setStrokeWidth(width);
        return paint;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawLine(0,0,width,0,barPaint);
        canvas.drawLine(thumbLeftLocation,0,thumbRightLoaction,0,progressPaint);
        canvas.drawCircle(thumbLeftLocation,0,thumbRadius,thumbLeftPaint);
        canvas.drawCircle(thumbRightLoaction,0,thumbRadius,thumbRightPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if(widthMode == MeasureSpec.EXACTLY){
            width = widthSize;
        }else{
            width = widthSize;
        }

        if(heightMode == MeasureSpec.EXACTLY){
            height = heightSize;
        }else{
            height = heightSize;
        }
        calculateLocation();
        setMeasuredDimension(width,height);
    }

    private void calculateLocation() {
        double rate = width * 1.0 / seekTotalProgress;
        thumbLeftLocation = (int) (seekLeftProgress * rate );
        thumbRightLoaction = (int) (seekRightProgress * rate );
    }

}
