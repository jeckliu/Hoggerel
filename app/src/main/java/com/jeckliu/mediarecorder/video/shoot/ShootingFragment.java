package com.jeckliu.mediarecorder.video.shoot;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.jeckliu.mediarecorder.R;
import com.jeckliu.mediarecorder.util.FileUtils;
import com.jeckliu.mediarecorder.view.ShootIconView;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/***
 * Created by Jeck.Liu on 2017/5/15 0015.
 */
public class ShootingFragment extends Fragment implements ShootIconView.OnCallbackListener {
    private FragmentManager manager;
    private FrameLayout preview;
    private CameraPreview cameraPreview;
    private Camera camera;
    private MediaRecorder mMediaRecorder;
    private ImageView ivSwitchCamera;
    private ShootIconView shootIconView;
    private ImageView ivBack;

    private int cameraFacing;
    private String filePath;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            cameraFacing = bundle.getInt(ShootActivity.TAG_CAMERA_FACING_STATE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_shooting, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        preview = (FrameLayout) view.findViewById(R.id.fragment_shooting_preview);
        ivSwitchCamera = (ImageView) view.findViewById(R.id.fragment_shooting_switch_camera);
        shootIconView = (ShootIconView) view.findViewById(R.id.shoot_icon_view);
        ivBack = (ImageView) view.findViewById(R.id.fragment_shooting_back);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initCamera();
        cameraPreview = new CameraPreview(getContext(), camera,cameraFacing);
        preview.addView(cameraPreview);
        shootIconView.setOnCallbackListener(this);
        manager = getActivity().getSupportFragmentManager();

        ivSwitchCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cameraFacing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    cameraFacing = Camera.CameraInfo.CAMERA_FACING_FRONT;
                } else {
                    cameraFacing = Camera.CameraInfo.CAMERA_FACING_BACK;
                }
                releaseCamera();
                camera = Camera.open(cameraFacing);
                cameraPreview.setCamera(camera);
                cameraPreview.switchCameraFacing(cameraFacing);
            }
        });

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
    }

    private void initCamera() {
        try {
            camera = Camera.open(cameraFacing);
        } catch (Exception e) {
            //
        }
        int cameraNumbers = Camera.getNumberOfCameras();
        if (cameraNumbers >= 2) {
            ivSwitchCamera.setVisibility(View.VISIBLE);
        } else {
            ivSwitchCamera.setVisibility(View.GONE);
        }
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
        Fragment showFra = new ShootCompletedFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(ShootActivity.TAG_SHOOT_TIME, duration);
        bundle.putInt(ShootActivity.TAG_CAMERA_FACING_STATE, cameraFacing);
        bundle.putString(ShootActivity.TAG_FILE_PATH,filePath);
        bundle.putInt(ShootActivity.TAG_PHOTO_VIDEO,ShootActivity.FLAG_VIDEO);
        showFra.setArguments(bundle);
        manager.beginTransaction().replace(R.id.fragment, showFra).commit();
        shootIconView.reset();
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
    public void onDestroyView() {
        super.onDestroyView();
        releaseCamera();
    }

    private class MyPictureCallback implements Camera.PictureCallback {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            try {
                filePath = FileUtils.getOutputMediaFile(1);
                FileOutputStream fos = new FileOutputStream(filePath);

                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,fos);

                Fragment showFra = new ShootCompletedFragment();
                Bundle bundle = new Bundle();
                bundle.putInt(ShootActivity.TAG_CAMERA_FACING_STATE, cameraFacing);
                bundle.putString(ShootActivity.TAG_FILE_PATH, filePath);
                bundle.putInt(ShootActivity.TAG_PHOTO_VIDEO, ShootActivity.FLAG_PHOTO);
                showFra.setArguments(bundle);
                manager.beginTransaction().replace(R.id.fragment, showFra).commit();
                shootIconView.reset();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

}
