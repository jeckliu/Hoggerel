/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jeckliu.codescanner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * This view is overlaid on top of the camera preview. It adds the viewfinder rectangle and partial
 * transparency outside it, as well as the laser scanner animation and result points.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class ViewfinderView extends View {

    private static final int[] SCANNER_ALPHA = {0, 64, 128, 192, 255, 192, 128, 64};
    private static final long ANIMATION_DELAY = 15L;
    private static final int CURRENT_POINT_OPACITY = 0xA0;
    private static final int MAX_RESULT_POINTS = 20;
    private static final int POINT_SIZE = 6;

    private final Paint paint;
    private Bitmap resultBitmap;
    private final int maskColor;
    private final int resultColor;
    private final int laserColor;
    private final int resultPointColor;
    private int scannerAlpha;
    private List<ResultPoint> possibleResultPoints;
    private List<ResultPoint> lastPossibleResultPoints;
    float strokeWidth;
    float halfStrokeWidth;
    private Bitmap bouderBitmap;
    private Bitmap lineBitmap;
    private int lineTop = 0;

    private String drawText = "将二维码放入框内，即可扫描播放";

    private boolean isScan = true;
    private Camera mCamera;
    private List<Camera.Size> mPreviewSizeList;
    private Camera.Size mPreviewSize;
    public int previewWidth;
    public int previewHeight;
    private int mWidth;
    private int mHeight;

    public int showScanArea; //扫码区域

    // This constructor is used when the class is built from an XML resource.
    public ViewfinderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        strokeWidth = 2 * getResources().getDisplayMetrics().density;
        halfStrokeWidth = strokeWidth / 2;
        loadBitmap();

        // Initialize these once for performance rather than calling them every time in onDraw().
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Resources resources = getResources();
        maskColor = resources.getColor(R.color.viewfinder_mask);
        resultColor = resources.getColor(R.color.result_view);
        laserColor = resources.getColor(R.color.viewfinder_laser);
        resultPointColor = resources.getColor(R.color.possible_result_points);
        scannerAlpha = 0;
        possibleResultPoints = new ArrayList<>(5);
        lastPossibleResultPoints = null;
    }

    private void loadBitmap(){
        try {
            bouderBitmap=BitmapFactory.decodeResource(getResources(),R.drawable.p_icon_family_code_border);
            lineBitmap =BitmapFactory.decodeResource(getResources(),R.drawable.p_icon_family_code_line);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @SuppressLint("DrawAllocation")
    @Override
    public void onDraw(Canvas canvas) {
        System.out.println("onDraw");

        Rect frame = new Rect(0, 0, canvas.getWidth(), canvas.getHeight());
        Rect previewFrame = new Rect(0, 0, canvas.getWidth(), canvas.getHeight());

        int width = canvas.getWidth();
        int height = canvas.getHeight();

        paint.setColor(resultBitmap != null ? resultColor : maskColor);
        int w = previewWidth * 5 / 8;
        int h = w;
        showScanArea = w;
        int left = (previewWidth - w) / 2 - (previewWidth - width) / 2;
        int right = left + w;
        int top = (previewHeight - h) * 4 / 13 - (previewHeight - height) / 2;
        int bottom = top + h;
        Rect dFrame = new Rect(left, top, right, bottom);
        lineTop += 9;
        if (lineTop > dFrame.height() - lineBitmap.getHeight()) {
            lineTop = 0;
        }
        canvas.drawRect(0, 0, width, dFrame.top, paint);
        canvas.drawRect(0, dFrame.top, dFrame.left, dFrame.bottom + 1, paint);
        canvas.drawRect(dFrame.right + 1, dFrame.top, width, dFrame.bottom + 1, paint);
        canvas.drawRect(0, dFrame.bottom + 1, width, height, paint);
        int color = paint.getColor();
        paint.setColor(Color.rgb(0xff, 0xff, 0xff));
        paint.setStrokeWidth(strokeWidth);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(new RectF(dFrame.left + halfStrokeWidth, dFrame.top + halfStrokeWidth, dFrame.right - halfStrokeWidth, dFrame.bottom - halfStrokeWidth), paint);
        Rect mSrcRect = new Rect(0, 0, bouderBitmap.getWidth(), bouderBitmap.getHeight());
        canvas.drawBitmap(bouderBitmap, mSrcRect, dFrame, paint);
        canvas.drawBitmap(lineBitmap, new Rect(0, 0, lineBitmap.getWidth(), lineBitmap.getHeight()),
                new Rect(dFrame.left
                        , lineTop + dFrame.top
                        , dFrame.right
                        , lineTop + dFrame.top + lineBitmap.getHeight() * dFrame.width() / lineBitmap.getWidth()), paint);
        paint.setTextSize(12 * getResources().getDisplayMetrics().density);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawText(drawText, (dFrame.left + dFrame.right) / 2, dFrame.bottom + 50, paint);
        paint.setColor(color);

//            postInvalidateDelayed(ANIMATION_DELAY,
//                    frame.left - POINT_SIZE,
//                    frame.top - POINT_SIZE,
//                    frame.right + POINT_SIZE,
//                    frame.bottom + POINT_SIZE);
//            postInvalidateDelayed(ANIMATION_DELAY);
        if (isScan) {
            startScan();
        }
    }

    public void startScan() {
        isScan = true;
        postInvalidateDelayed(ANIMATION_DELAY);
    }

    public void cancelScan() {
        this.isScan = false;
    }

    public void setDrawText(String drawText) {
        this.drawText = drawText;
    }

    public void setCamera(Camera camera) {
        mCamera = camera;
        if (mCamera != null) {
            mPreviewSizeList = mCamera.getParameters().getSupportedPreviewSizes();
        }
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        mHeight = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        setMeasuredDimension(mWidth, mHeight);

        if (mPreviewSizeList != null) {
            mPreviewSize = getOptimalPreviewSize(mPreviewSizeList, mWidth, mHeight);

        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (mPreviewSize != null) {
            previewWidth = mPreviewSize.height;
            previewHeight = mPreviewSize.width;
        }
        if (mWidth * previewHeight > mHeight * previewWidth) {
            float scale = mWidth * 1f / previewWidth;
            previewWidth = mWidth;
            previewHeight = (int) (previewHeight * scale);
        } else {
            float scale = mHeight * 1f / previewHeight;
            previewHeight = mHeight;
            previewWidth = (int) (previewWidth * scale);
        }
    }

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        if (sizes == null) return null;

        Camera.Size optimalSize = sizes.get(sizes.size() - 1);
        double minDiff = Double.MAX_VALUE;
        int targetHeight = h;
        for (Camera.Size size : sizes) {
            if (optimalSize.width < size.width) {
                optimalSize = size;
            }
        }
        w = optimalSize.width * 2 / 3;

        for (Camera.Size size : sizes) {
            if (Math.abs(size.width - w) < minDiff) {
                minDiff = Math.abs(size.width - w);
                optimalSize = size;
            }
        }
        return optimalSize;
    }
}
