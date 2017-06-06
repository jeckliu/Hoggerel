package com.jeckliu.hoggerel.video.shoot2;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.List;

/***
 * Created by Jeck.Liu on 2017/1/19 0019.
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private Camera.Size previewSize;
    private Camera.Size videoSize;
    private Camera.Size pictureSize;
    private int width;
    private int height;
    private int cameraFacing; //摄像头方向

    public CameraPreview(Context context, Camera camera, int cameraFacing) {
        super(context);
        mCamera = camera;
        this. cameraFacing = cameraFacing;
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void setCamera(Camera camera){
        this.mCamera = camera;
    }

    public void switchCameraFacing(int cameraFacing){
        this.cameraFacing = cameraFacing;
        if(mCamera != null){
            try {
                mCamera.setPreviewDisplay(mHolder);
                mCamera.setDisplayOrientation(90);
            } catch (IOException e) {
                e.printStackTrace();
            }
            setParameters(width,height);
            mCamera.startPreview();
            mCamera.autoFocus(autoFocusCallback);
        }
    }

    public void surfaceCreated(SurfaceHolder holder) {
        try {
            if (mCamera != null) {
                mCamera.setPreviewDisplay(holder);
                mCamera.setDisplayOrientation(90);
            }
        } catch (IOException e) {
            Log.d("CameraPreview", "Error setting camera preview: " + e.getMessage());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mCamera != null) {
            mCamera.stopPreview();
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        width = w;
        height = h;
        if (mHolder.getSurface() == null) {
            return;
        }

        try {
            if (mCamera != null) {
                setParameters(w, h);
                mCamera.startPreview();
                mCamera.autoFocus(autoFocusCallback);
            }
        } catch (Exception e) {
            Log.d("CameraPreview", "Error starting camera preview: " + e.getMessage());
        }
    }

    private void setParameters(int w, int h) {
        Camera.Parameters parameters = mCamera.getParameters();
        //
        List<String> supportedFocusModes = parameters.getSupportedFocusModes();
        for (String mode : supportedFocusModes) {
            if (mode.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            }
            if (mode.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            }
        }

        List<Camera.Size> supportPreviewSizes =  parameters.getSupportedPreviewSizes();
        previewSize = getOptimalSize(supportPreviewSizes,w,h);
        parameters.setPreviewSize(previewSize.width,previewSize.height);

        List<Camera.Size> supportedVideoSizes = parameters.getSupportedVideoSizes();
        videoSize = getOptimalSize(supportedVideoSizes,w,h);
//        parameters.setPreviewSize(videoSize.width, videoSize.height);

        List<Camera.Size> supportedPictureSize = parameters.getSupportedPictureSizes();
        pictureSize = getOptimalSize(supportedPictureSize,w,h);
        parameters.setPictureSize(pictureSize.width,pictureSize.height);

        if (cameraFacing == Camera.CameraInfo.CAMERA_FACING_BACK) {
            parameters.setRotation(90);
        } else {
            parameters.setRotation(270);
        }

        mCamera.setParameters(parameters);
    }

    private Camera.AutoFocusCallback autoFocusCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            if (success) {
                camera.cancelAutoFocus();
            }
        }
    };

    public Camera.Size getVideoSize() {
        return videoSize;
    }

    private Camera.Size getOptimalSize(List<Camera.Size> sizes, int w, int h) {
        if (sizes == null) return null;
        Camera.Size optimalSize = null;
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) w / h;

        double minDiff = Double.MAX_VALUE;

        for (Camera.Size size : sizes) {
            double ratio = (double) size.height / size.width;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - w) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - w);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - w) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - w);
                }
            }
        }
        return optimalSize;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                break;
        }
        return super.onTouchEvent(event);
    }
}
