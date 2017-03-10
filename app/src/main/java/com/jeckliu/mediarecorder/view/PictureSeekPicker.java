package com.jeckliu.mediarecorder.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.jeckliu.mediarecorder.R;

import java.util.ArrayList;
import java.util.List;

/***
 * 双向滑动的图片选择器
 * Created by Jeck.Liu on 2017/3/7 0007.
 */
public class PictureSeekPicker extends View{

    private OnSeekBarChangeListener onSeekBarChangeListener;
    private int width;
    private int height;
    private int lineColor;
    private float thumbWidth;
    private float thumbHeight;
    private int thumbColor;
    private int thumbLeftProgress;
    private int thumbRightProgress;
    private int thumbTotalProgress;
    private float thumbLeftLocation;
    private float thumbRightLocation;
    private float rateLocation;

    private List<Bitmap> bitmaps;
    private int pictureMaxSize;
    private float pictureFirstLocation;

    List<PictureBean> pictureBeans = new ArrayList<>();
    PictureBean bean;
    private boolean thumbLeftTouch;
    private boolean thumbRightTouch;

    private Paint thumbPaint;
    private Paint linePaint;
    private RectF rectF;

    public PictureSeekPicker(Context context) {
        this(context,null);
    }

    public PictureSeekPicker(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public PictureSeekPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs,R.styleable.PictureSeekPicker);
        int count = a.getIndexCount();
        for(int i =  0; i < count; i++){
            int value = a.getIndex(i);
            switch (value){
                case R.styleable.PictureSeekPicker_lineColor:
                    lineColor = a.getColor(value,Color.WHITE);
                    break;
                case R.styleable.PictureSeekPicker_thumbColor:
                    thumbColor = a.getColor(value, Color.WHITE);
                    break;
                case R.styleable.PictureSeekPicker_thumbWidth:
                    thumbWidth = a.getDimension(value,10);
                    break;
                case R.styleable.PictureSeekPicker_thumbHeight:
                    thumbHeight = a.getDimension(value,50);
                    break;
                case R.styleable.PictureSeekPicker_thumbTotalProgress:
                    thumbTotalProgress = a.getInt(value,10);
                    break;
                case R.styleable.PictureSeekPicker_thumbLeftProgress:
                    thumbLeftProgress = a.getInt(value,0);
                    break;
                case R.styleable.PictureSeekPicker_thumbRightProgress:
                    thumbRightProgress = a.getInt(value,10);
                    break;
            }
        }
        a.recycle();
        init();
    }

    private void init() {
        rectF = new RectF();
        thumbPaint = createPaint(thumbColor,thumbWidth);
        linePaint = createPaint(lineColor,1);
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

        drawPictures(canvas);
        canvas.drawLine(thumbLeftLocation,0,thumbLeftLocation,thumbHeight,thumbPaint);
        canvas.drawLine(thumbRightLocation,0,thumbRightLocation,thumbHeight,thumbPaint);
        canvas.drawLine(thumbLeftLocation,0,thumbRightLocation,0,linePaint);
        canvas.drawLine(thumbLeftLocation,thumbHeight,thumbRightLocation,thumbHeight,linePaint);

        for(int i = 0; i < pictureBeans.size(); i++){
            PictureBean bean = pictureBeans.get(i);
            if(bean.left <= thumbLeftLocation && bean.right >= thumbLeftLocation){
                thumbLeftProgress = i + 1;
            }
            if(bean.left <= thumbRightLocation && bean.right >= thumbRightLocation) {
                thumbRightProgress = i + 1;
            }
        }
        if(onSeekBarChangeListener != null){
            onSeekBarChangeListener.onProgressChanged(thumbLeftProgress, thumbRightProgress);
        }
    }

    private void drawPictures(Canvas canvas){
        if(pictureMaxSize <= 0){
            return;
        }
        int rectW = width / 10;
        float pictureDrawLocation = pictureFirstLocation;
        pictureBeans.clear();
        for(int i = 0; i < pictureMaxSize; i++){
            bean = new PictureBean();
            rectF.set(pictureDrawLocation,0, pictureDrawLocation + rectW,thumbHeight);
            if((pictureDrawLocation ) <= (width - rectW) && pictureDrawLocation >= 0){
                bean.isVisible = true;
            }else{
                bean.isVisible = false;
            }
            bean.left = pictureDrawLocation;
            bean.right = pictureDrawLocation + rectW;
            pictureBeans.add(i,bean);
            canvas.drawBitmap(bitmaps.get(i),null,rectF,linePaint);
            pictureDrawLocation += rectW;
        }
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
            height = (int) (thumbHeight + 2 * linePaint.getStrokeWidth());
       }
        progressToLocation();
        setMeasuredDimension(width,height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        progressToLocation();
    }

    private void progressToLocation() {
        rateLocation = (width ) * 1.0f / thumbTotalProgress;
        thumbLeftLocation = thumbLeftProgress * rateLocation + thumbWidth / 2;
        thumbRightLocation = thumbRightProgress * rateLocation - thumbWidth / 2;
    }

    private float dx;
    private float mx;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                dx = event.getX();
                if(x <= (thumbLeftLocation + thumbWidth / 2) && x >= (thumbLeftLocation - thumbWidth / 2)){
                    thumbLeftTouch = true;
                }else{
                    thumbLeftTouch = false;
                }
                if(x <= (thumbRightLocation + thumbWidth / 2) && x >= (thumbRightLocation - thumbWidth / 2)){
                    thumbRightTouch = true;
                }else{
                    thumbRightTouch = false;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                mx = event.getX();
                if(y <= thumbHeight && y >= 0) {
                    if (thumbLeftTouch || thumbRightTouch) {
                        calculateDistance(0);
                    } else {
                        calculateDistance(mx - dx);
                    }

                    if (thumbLeftTouch && x > (thumbWidth / 2) && ((thumbRightLocation - x) > rateLocation)) {
                        thumbLeftLocation = x;
                    }
                    if (thumbRightTouch && x < (width - thumbWidth / 2) && ((x - thumbLeftLocation) > rateLocation)) {
                        thumbRightLocation = x;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        invalidate();
        return true;
    }

    private void calculateDistance(float distance){
        if(distance > 0){
            if(pictureBeans.get(0).isVisible){
                distance = 0;
            }else{
                if((pictureBeans.get(0).left + distance) > 0){
                    distance = 0 - pictureBeans.get(0).left;
                }
            }
        }else if(distance < 0){
            if(pictureBeans.get(pictureMaxSize - 1).isVisible){
                distance = 0;
            }else{
                if((pictureBeans.get(pictureMaxSize - 1).right + distance) < width){
                    distance = width  - pictureBeans.get(pictureMaxSize - 1).right;
                }
            }
        }
        pictureFirstLocation += distance;
    }

    public void setBitmaps(List<Bitmap> bitmaps){
        this.bitmaps = bitmaps;
        if(this.bitmaps != null && this.bitmaps.size() > 0){
            pictureMaxSize = bitmaps.size();
            invalidate();
        }
    }

    public void setThumbTotalProgress(int thumbTotalProgress){
        this.thumbTotalProgress = thumbTotalProgress;
    }

    public void setThumbLeftProgress(int thumbLeftProgress){
        this.thumbLeftProgress = thumbLeftProgress;
    }

    public void setThumbRightProgress(int thumbRightProgress){
        this.thumbRightProgress = thumbRightProgress;
    }

    public void setOnSeekBarChangeListener(OnSeekBarChangeListener onSeekBarChangeListener){
        this.onSeekBarChangeListener = onSeekBarChangeListener;
    }

    public interface OnSeekBarChangeListener{
        void onProgressChanged(int leftProgress, int rightProgress);
    }

    private class PictureBean{
        public boolean isVisible; //true完全可见
        public float left;
        public float right;
    }
}
