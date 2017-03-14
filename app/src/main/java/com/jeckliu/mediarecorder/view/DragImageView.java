package com.jeckliu.mediarecorder.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

/***
 * Created by Jeck.Liu on 2017/3/13 0013.
 */
public class DragImageView extends ImageView{
    private Context context;

    public DragImageView(Context context) {
        super(context);
    }

    public DragImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DragImageView(final Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    float lastX;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                lastX = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                float endX = event.getX();
                    int dx = (int) (endX - lastX);
                    int left = getLeft();
                    int right = getRight();
                    int top = getTop();
                    int bottom = getBottom();
                    layout(left + dx,top,right + dx,bottom);
                break;
        }
        return true;
    }
}
