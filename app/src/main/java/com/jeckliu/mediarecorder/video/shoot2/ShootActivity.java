package com.jeckliu.mediarecorder.video.shoot2;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;
import com.jeckliu.mediarecorder.R;
import com.jeckliu.mediarecorder.util.FileUtils;
import com.jeckliu.mediarecorder.view.ShootIconView;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/***
 * Created by Jeck.Liu on 2017/5/12 0012.
 */
public class ShootActivity extends FragmentActivity implements ShootIconView.OnCallbackListener, View.OnClickListener{
    public static final int MEDIA_TYPE_PHOTO = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    private FragmentManager manager;
    private Camera camera;
    private MediaRecorder mMediaRecorder;

    private RelativeLayout rlShootingControl;
    private FrameLayout preview;
    private CameraPreview cameraPreview;
    private ImageView ivSwitchCameraFacing;
    private ShootIconView shootIconView;
    private ImageView ivBack;
    private RelativeLayout rlCompletedControl;
    private ImageView ivResume;
    private ImageView ivConfirm;
    private VideoView videoView;
    private ImageView ivShowImage;

    private int cameraFacing;
    private String filePath;
    private int mediaType;

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        setContentView(R.layout.activity_shoot);

        initView();

        initCameraPreview();
    }

    private void initView() {
        rlShootingControl = (RelativeLayout) findViewById(R.id.activity_shoot_shooting_control);
        rlCompletedControl = (RelativeLayout) findViewById(R.id.activity_shoot_completed_control);
        rlShootingControl.setVisibility(View.VISIBLE);
        ivSwitchCameraFacing = (ImageView) findViewById(R.id.activity_shoot_shooting_switch_camera);
        ivBack = (ImageView) findViewById(R.id.activity_shoot_shooting_back);
        shootIconView = (ShootIconView) findViewById(R.id.activity_shoot_shooting_start);
        preview = (FrameLayout) findViewById(R.id.activity_shoot_shooting_preview);
        videoView = (VideoView) findViewById(R.id.activity_shoot_completed_video);
        ivResume = (ImageView) findViewById(R.id.activity_shoot_completed_resume);
        ivConfirm = (ImageView) findViewById(R.id.activity_shoot_completed_confirm);
        ivShowImage = (ImageView) findViewById(R.id.activity_shoot_completed_image);

        shootIconView.setOnCallbackListener(this);
        ivSwitchCameraFacing.setOnClickListener(this);
        ivBack.setOnClickListener(this);
        ivResume.setOnClickListener(this);
        ivConfirm.setOnClickListener(this);
    }

    private void initCameraPreview() {
        initCamera();
        cameraPreview = new CameraPreview(this, camera,cameraFacing);
        preview.addView(cameraPreview);
    }

    private void initCamera() {
        try {
            camera = Camera.open(cameraFacing);
        } catch (Exception e) {
            //
        }
        int cameraNumbers = Camera.getNumberOfCameras();
        if (cameraNumbers >= 2) {
            ivSwitchCameraFacing.setVisibility(View.VISIBLE);
        } else {
            ivSwitchCameraFacing.setVisibility(View.GONE);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.activity_shoot_shooting_switch_camera:
                if (cameraFacing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    cameraFacing = Camera.CameraInfo.CAMERA_FACING_FRONT;
                } else {
                    cameraFacing = Camera.CameraInfo.CAMERA_FACING_BACK;
                }
                releaseCamera();
                camera = Camera.open(cameraFacing);
                cameraPreview.setCamera(camera);
                cameraPreview.switchCameraFacing(cameraFacing);
                break;
            case R.id.activity_shoot_completed_resume:
                showShootingControl();
                break;
            case R.id.activity_shoot_completed_confirm:
                finish();
                break;
            case R.id.activity_shoot_shooting_back:
                finish();
                break;
        }
    }

    private void showShootingControl(){
        rlShootingControl.setVisibility(View.VISIBLE);

        rlCompletedControl.setVisibility(View.GONE);
    }

    private void showCompletedControl(){
        rlCompletedControl.setVisibility(View.VISIBLE);
        if(mediaType == MEDIA_TYPE_PHOTO){
            ivShowImage.setVisibility(View.VISIBLE);
            videoView.setVisibility(View.GONE);

            ivShowImage.setImageURI(Uri.parse(filePath));
        }else if(mediaType == MEDIA_TYPE_VIDEO){
            videoView.setVisibility(View.VISIBLE);
            ivShowImage.setVisibility(View.GONE);

            videoView.setVideoPath(filePath);
            videoView.start();
        }

        rlShootingControl.setVisibility(View.GONE);

        shootIconView.reset();
    }

    @Override
    public void onTakePhoto() {
        camera.takePicture(null,null,new MyPictureCallback());
    }

    @Override
    public void onStartRecordVideo() {
        startRecorder();
    }

    @Override
    public void onStopRecordVideo(long duration) {
        stopRecorder();

        mediaType = 2;
        showCompletedControl();
    }

    private void startRecorder(){
        if(prepareVideoRecorder()){
            try {
                mMediaRecorder.start();
            }catch (Exception e){
                releaseMediaRecorder();
            }
        }else{
            releaseMediaRecorder();
        }
    }

    private void stopRecorder(){
        try {
            mMediaRecorder.stop();
        }catch (Exception e){
            releaseMediaRecorder();
        }
        releaseMediaRecorder();
    }

    private boolean prepareVideoRecorder(){
        mMediaRecorder = new MediaRecorder();

        camera.unlock();
        mMediaRecorder.setCamera(camera);

        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        Camera.Size videoSize = cameraPreview.getVideoSize();

        CamcorderProfile camcorderProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
        camcorderProfile.videoFrameWidth = videoSize.width;
        camcorderProfile.videoFrameHeight = videoSize.height;
        camcorderProfile.videoBitRate = 2000000;
        mMediaRecorder.setProfile(camcorderProfile);

        filePath = FileUtils.getOutputMediaFile(FileUtils.MEDIA_TYPE_VIDEO);
        mMediaRecorder.setOutputFile(filePath);
        if(cameraFacing == Camera.CameraInfo.CAMERA_FACING_BACK){
            mMediaRecorder.setOrientationHint(90);
        }else{
            mMediaRecorder.setOrientationHint(270);
        }

        mMediaRecorder.setPreviewDisplay(cameraPreview.getHolder().getSurface());

        try {
            mMediaRecorder.prepare();
        } catch (IllegalStateException e) {
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            releaseMediaRecorder();
            return false;
        }
        return true;
    }

    private class MyPictureCallback implements Camera.PictureCallback {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            try {
                filePath = FileUtils.getOutputMediaFile(1);
                FileOutputStream fos = new FileOutputStream(filePath);

                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,fos);

                mediaType = 1;
                showCompletedControl();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void releaseMediaRecorder() {
        if (mMediaRecorder != null) {
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;
            if(camera != null){
                camera.lock();
            }
        }
    }

    private void releaseCamera() {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
            cameraPreview.setCamera(null);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseCamera();
    }
}