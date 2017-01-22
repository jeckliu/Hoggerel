package com.jeckliu.mediarecorder;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaCodec;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.jeckliu.mediarecorder.videoplay.VideoViewActivity;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
/***
 * Created by Jeck.Liu on 2017/1/19 0019.
 */
public class VideoRecorderActivity extends FragmentActivity implements View.OnClickListener{
    private final String TAG = getClass().getSimpleName();
    private final int FLAG_TIME = 100;
    private final int TOTAL_TIME = 10;
    private String[] permissions = new String[]{Manifest.permission.CAMERA,Manifest.permission.RECORD_AUDIO,Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private List<String> grantedPermission = new ArrayList<>();
    private CameraPreview cameraPreview;
    private FrameLayout preview;
    private TextView tvRecorder;
    private TextView tvPlay;
    private TextView tvTime;
    private TextView tvChange;

    private Camera mCamera;
    private MediaRecorder mMediaRecorder;

    private boolean isRecording = false;
    private String outFile = null;
    private int countTime;
    private int cameraFacing = 0;
    private int cameraNumbers;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case FLAG_TIME:
                    tvTime.setText(""+countTime);
                    if(countTime == TOTAL_TIME){
                        stopRecorder();
                    }else{
                        handler.sendEmptyMessageDelayed(FLAG_TIME,1000);
                    }
                    countTime++;
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_video_recorder);

        initView();

        checkPermission();
    }

    private void initView() {
        preview = (FrameLayout) findViewById(R.id.preview);
        tvRecorder = (TextView) findViewById(R.id.recorder);
        tvPlay = (TextView) findViewById(R.id.play);
        tvTime = (TextView) findViewById(R.id.tvTime);
        tvChange = (TextView) findViewById(R.id.tv_change);

        tvRecorder.setOnClickListener(this);
        tvPlay.setOnClickListener(this);
        tvChange.setOnClickListener(this);

    }

    private void initCamera() {
        try {
            mCamera = Camera.open(cameraFacing);
        }catch (Exception e){
            Log.d(TAG, "IllegalStateExc：" + e.getMessage());
        }
        cameraPreview = new CameraPreview(this, mCamera);
        preview.addView(cameraPreview);

        initCameraFacing();
    }

    private void initCameraFacing(){
        cameraNumbers = Camera.getNumberOfCameras();
        if(cameraNumbers >= 2){
            tvChange.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.recorder:
                if(isRecording){
                   stopRecorder();
                }else{
                   startRecorder();
                }
                break;
            case R.id.play:
                if(isRecording){
                    Toast.makeText(this,"请先停止视频录制，再进行视频播放",Toast.LENGTH_LONG).show();
                    return;
                }
                if(outFile != null){
                    Intent intent = new Intent(this, VideoViewActivity.class);
                    intent.putExtra(VideoViewActivity.VIDEO_URL,outFile);
                    startActivity(intent);
                }else{
                    Toast.makeText(this,"视频文件为空",Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.tv_change:
                if(isRecording){
                    Toast.makeText(this,"请先停止视频录制，再切换视角",Toast.LENGTH_LONG).show();
                    return;
                }
                if(cameraFacing == Camera.CameraInfo.CAMERA_FACING_BACK){
                    cameraFacing = Camera.CameraInfo.CAMERA_FACING_FRONT;
                }else{
                    cameraFacing = Camera.CameraInfo.CAMERA_FACING_BACK;
                }
                releaseCamera();
                mCamera = Camera.open(cameraFacing);
                cameraPreview.setCamera(mCamera);
                cameraPreview.switchCameraFacing();
                break;
        }
    }

    private void startRecorder(){
        if(prepareVideoRecorder()){
            try {
                mMediaRecorder.start();
                isRecording = true;
                tvRecorder.setText("停止");
                handler.sendEmptyMessage(FLAG_TIME);
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
            tvRecorder.setText("录制");
            countTime = 0;
            handler.removeMessages(FLAG_TIME);
        }catch (Exception e){
            Log.d(TAG, "IllegalStateExc：" + e.getMessage());
        }
        releaseMediaRecorder();
        isRecording = false;
    }

    private boolean prepareVideoRecorder(){
        mMediaRecorder = new MediaRecorder();

        mCamera.unlock();
        mMediaRecorder.setCamera(mCamera);

        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        Camera.Size videoSize = cameraPreview.getVideoSize();

        CamcorderProfile camcorderProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
        camcorderProfile.videoFrameWidth = videoSize.width;
        camcorderProfile.videoFrameHeight = videoSize.height;
        camcorderProfile.videoBitRate = 2000000;
        mMediaRecorder.setProfile(camcorderProfile);

        outFile = FileUtils.getOutputMediaFile(FileUtils.MEDIA_TYPE_VIDEO);
        mMediaRecorder.setOutputFile(outFile);
        if(cameraFacing == Camera.CameraInfo.CAMERA_FACING_BACK){
            mMediaRecorder.setOrientationHint(90);
        }else{
            mMediaRecorder.setOrientationHint(270);
        }

        mMediaRecorder.setMaxDuration(TOTAL_TIME * 1000);
        mMediaRecorder.setPreviewDisplay(cameraPreview.getHolder().getSurface());

        try {
            mMediaRecorder.prepare();
        } catch (IllegalStateException e) {
            Log.d(TAG, "IllegalStateException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            Log.d(TAG, "IOException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        }
        return true;
    }

    private void releaseMediaRecorder() {
        if (mMediaRecorder != null) {
            mMediaRecorder.reset();   // clear recorder configuration
            mMediaRecorder.release(); // release the recorder object
            mMediaRecorder = null;
            if(mCamera != null){
                mCamera.lock();
            }
        }
    }

    private void releaseCamera(){
        if(mCamera != null){
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
            cameraPreview.setCamera(null);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseMediaRecorder();
        isRecording = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseCamera();
    }

    private void checkPermission(){
        for(String permission : permissions){
            if(ActivityCompat.checkSelfPermission(this,permission) != PackageManager.PERMISSION_GRANTED){
                grantedPermission.add(permission);
            }
        }
        if(grantedPermission.size() == 0){
            initCamera();
        }else{
            int i = grantedPermission.size();
            while (i != 0){
                ActivityCompat.requestPermissions(this,new String[]{grantedPermission.get(i-1)},(int)Math.random());
                i--;
                grantedPermission.remove(i);
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        initCamera();
    }
}
