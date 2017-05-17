package com.jeckliu.mediarecorder.video.shoot;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import com.jeckliu.mediarecorder.R;
import com.jeckliu.mediarecorder.util.FileUtils;

/***
 * Created by Jeck.Liu on 2017/5/15 0015.
 */
public class ShootCompletedFragment extends Fragment implements View.OnClickListener{
    private ImageView ivPhoto;
    private VideoView videoView;
    private ImageView ivResume;
    private ImageView ivConfirm;
    private FragmentManager manager;

    private int cameraFacing;
    private long videoDuration;
    private String filePath;
    private int flag_photo_video;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if(bundle != null){
            cameraFacing = bundle.getInt(ShootActivity.TAG_CAMERA_FACING_STATE);
            videoDuration = bundle.getLong(ShootActivity.TAG_SHOOT_TIME);
            filePath = bundle.getString(ShootActivity.TAG_FILE_PATH);
            flag_photo_video = bundle.getInt(ShootActivity.TAG_PHOTO_VIDEO);
        }
        Toast.makeText(getContext(),cameraFacing+"-" + videoDuration,Toast.LENGTH_SHORT).show();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_shoot_completed,null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ivPhoto = (ImageView) view.findViewById(R.id.fragment_shoot_completed_photo);
        videoView = (VideoView) view.findViewById(R.id.fragment_shoot_completed_video);
        ivResume = (ImageView) view.findViewById(R.id.fragment_shoot_completed_resume);
        ivConfirm = (ImageView) view.findViewById(R.id.fragment_shoot_completed_confirm);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        manager = getActivity().getSupportFragmentManager();
        ivResume.setOnClickListener(this);
        ivConfirm.setOnClickListener(this);

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                videoView.start();
            }
        });

        if(flag_photo_video == ShootActivity.FLAG_PHOTO){
            ivPhoto.setVisibility(View.VISIBLE);
            videoView.setVisibility(View.GONE);
            ivPhoto.setImageURI(Uri.parse(filePath));
        }else if(flag_photo_video == ShootActivity.FLAG_VIDEO){
            videoView.setVisibility(View.VISIBLE);
            ivPhoto.setVisibility(View.GONE);
            videoView.setVideoPath(filePath);
            videoView.start();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fragment_shoot_completed_resume:
                FileUtils.deleteFile(filePath);
                Fragment showFra = new ShootingFragment();
                Bundle bundle = new Bundle();
                bundle.putInt(ShootActivity.TAG_CAMERA_FACING_STATE,cameraFacing);
                showFra.setArguments(bundle);
                manager.beginTransaction().replace(R.id.fragment,showFra).commit();
                break;
            case R.id.fragment_shoot_completed_confirm:
                getActivity().finish();
                break;
        }
    }

}
