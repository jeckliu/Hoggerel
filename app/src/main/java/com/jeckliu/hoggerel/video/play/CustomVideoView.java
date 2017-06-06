package com.jeckliu.hoggerel.video.play;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

/***
 * Created by Jeck.Liu on 2016/8/22 0022.
 */
public class CustomVideoView extends VideoView {
    private int mVideoWidth = 0;
    private int mVideoHeight = 0;

    public CustomVideoView(Context context) {
        super(context);
    }

    public CustomVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
//        int width = getDefaultSize(mVideoWidth, widthMeasureSpec);
//        int height = getDefaultSize(mVideoHeight, heightMeasureSpec);
//
//        setMeasuredDimension(width, height);
    }

}
