package com.jeckliu.codescanner;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

class CameraPreview extends ViewGroup implements SurfaceHolder.Callback {
    private final String TAG = "CameraPreview";
    private Context context;
    private Activity activity;
    SurfaceView mSurfaceView;
    SurfaceHolder mHolder;
    Size mPreviewSize;
    List<Size> mSupportedPreviewSizes;
    Camera mCamera;
    PreviewCallback mPreviewCallback;
    private Handler autoFocusHandler;

    private int mZoom=-1;
    private int optimalSizeType=0;

    CameraPreview(Context context, PreviewCallback previewCallback, int zoom, int optimalSizeType) {
        super(context);
        this.context = context;
        this.activity = (Activity) context;
        this.mZoom=zoom;
        this.optimalSizeType=optimalSizeType;

        if(mCamera == null){
            try {
                mCamera = Camera.open();
                mCamera.setDisplayOrientation(90);
            }catch (Exception e){
                unPermissionsShow();
            }

        }
        if (mCamera != null) {
            mSupportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();
            requestLayout();
        }
        autoFocusHandler = new Handler();
        setBackgroundColor(Color.BLACK);
        mPreviewCallback = previewCallback;
        mSurfaceView = new SurfaceView(context);
        addView(mSurfaceView);

        mHolder = mSurfaceView.getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public Camera getCamera(){
        if(mCamera == null){
            return  null;
        }
        return mCamera;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        setMeasuredDimension(width, height);

        if (mSupportedPreviewSizes != null) {
            mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, width, height);

        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed && getChildCount() > 0) {
            final View child = getChildAt(0);

            final int width = r - l;
            final int height = b - t;

            int previewWidth = width;
            int previewHeight = height;
            if (mPreviewSize != null) {
                previewWidth = mPreviewSize.height;
                previewHeight = mPreviewSize.width;
            }

            if (width * previewHeight > height * previewWidth) {
                int w = previewWidth;
                int h = previewHeight;
                float scale = width * 1f / w;
                h = (int) (h * scale);
                child.layout(0, (height - h) / 2,
                        width, (height + h) / 2);
            } else {
                int w = previewWidth;
                int h = previewHeight;
                float scale = height * 1f / h;
                w = (int) (w * scale);
                child.layout((width - w) / 2, 0,
                        (width + w) / 2, height);
            }
        }
    }

    private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
        if (sizes == null) return null;
        Size optimalSize=null;
//        if (optimalSizeType==0){
//            optimalSize = sizes.get(sizes.size() - 1);
//            double minDiff = Double.MAX_VALUE;
//            int targetHeight = h;
//            for (Size size : sizes) {
//                if (optimalSize.width < size.width) {
//                    optimalSize = size;
//                }
//            }
//            w = optimalSize.width * 2 / 3;
//
//            for (Size size : sizes) {
//                if (Math.abs(size.width - w) < minDiff) {
//                    minDiff = Math.abs(size.width - w);
//                    optimalSize = size;
//                }
//            }
//        }else{
            final double ASPECT_TOLERANCE = 0.1;
            double targetRatio = (double) w / h;
            if (sizes == null) return null;

            double minDiff = Double.MAX_VALUE;

            int targetHeight = w;

            // Try to find an size match aspect ratio and size
            for (Size size : sizes) {
                Log.i(TAG, "getOptimalPreviewSize:"+size.width+","+size.height);
                double ratio = (double) size.height / size.width;
                if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }

            // Cannot find the one match the aspect ratio, ignore the requirement
            if (optimalSize == null) {
                minDiff = Double.MAX_VALUE;
                for (Size size : sizes) {
                    if (Math.abs(size.height - targetHeight) < minDiff) {
                        optimalSize = size;
                        minDiff = Math.abs(size.height - targetHeight);
                    }
                }
            }
//        }
        //ToastUtils.DebugToast(optimalSize.height+":"+optimalSize.width);
        return optimalSize;
    }

    public void surfaceCreated(SurfaceHolder holder) {
        try {
            if (mCamera == null) {
                mCamera = Camera.open();
                mCamera.setDisplayOrientation(90);
            }
            if (mCamera != null) {
                mCamera.setPreviewDisplay(holder);
            }
        }catch (Exception e){
            unPermissionsShow();
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mCamera != null) {
            try {
                mCamera.setPreviewCallback(null);
                mCamera.release();
                mCamera = null;
            } catch (Exception e) {
            }
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        if (holder.getSurface() == null) {
            return;
        }

        if (mCamera != null) {
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
            if (mZoom==-1){
                parameters.setZoom(parameters.getMaxZoom() / 5);
            }else if (mZoom>0&&mZoom<=parameters.getMaxZoom()){
                parameters.setZoom(mZoom);
            }
            requestLayout();

            mCamera.setParameters(parameters);
            mCamera.setPreviewCallback(mPreviewCallback);
            mCamera.startPreview();
            mCamera.autoFocus(autoFocusCB);
        }
    }

    AutoFocusCallback autoFocusCB = new AutoFocusCallback() {
        public void onAutoFocus(boolean success, Camera camera) {
            autoFocusHandler.postDelayed(doAutoFocus, 1000);
        }
    };

    private Runnable doAutoFocus = new Runnable() {
        public void run() {
            if( mCamera != null && autoFocusHandler != null)
                mCamera.autoFocus(autoFocusCB);
        }
    };

    private void unPermissionsShow(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(context.getString(R.string.app_name));
        builder.setMessage("未授予拍照权限，请在系统设置页授予" + getResources().getString(R.string.app_name) + "相机使用权限后重新使用扫码功能。");
        builder.setPositiveButton("进入设置", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                intent.setData(Uri.fromParts("package", context.getPackageName(), null));
                activity.startActivity(intent);
                activity.finish();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                activity.finish();
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                activity.finish();
            }
        });
        builder.show();
    }

}
