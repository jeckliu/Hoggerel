package com.jeckliu.mediarecorder;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.jeckliu.mediarecorder.base.BaseActivity;
import com.jeckliu.mediarecorder.permission.IPermission;
import com.jeckliu.mediarecorder.video.clip.VideoClipActivity;
import com.jeckliu.mediarecorder.video.clip.VideoSelectActivity;
import com.jeckliu.mediarecorder.video.record.VideoRecorderActivity;

/***
 * Created by Jeck.Liu on 2017/2/14 0014.
 */
public class MainActivity extends BaseActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        permissionAction.addPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .addPermission(Manifest.permission.CAMERA)
                .addPermission(Manifest.permission.RECORD_AUDIO)
                .requestPermission(new IPermission() {
            @Override
            public void done() {

            }

            @Override
            public void unPermission() {

            }
        });
    }

    public void onVideoRecord(View view){
        startActivity(new Intent(this,VideoRecorderActivity.class));
    }

    public void onVideoClip(View view){
        startActivity(new Intent(this, VideoSelectActivity.class));
    }
}
