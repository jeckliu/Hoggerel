package com.jeckliu.hoggerel;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import com.jeckliu.hoggerel.base.BaseActivity;
import com.jeckliu.hoggerel.permission.IPermission;
import com.jeckliu.hoggerel.video.clip.VideoSelectActivity;
import com.jeckliu.hoggerel.video.record.VideoRecorderActivity;

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

    public void onShoot(View view){
        startActivity(new Intent(this,com.jeckliu.hoggerel.video.shoot.ShootActivity.class));
    }

    public void onShoot2(View view){
        startActivity(new Intent(this, com.jeckliu.hoggerel.video.shoot2.ShootActivity.class));
    }


    public void onVideoClip(View view){
        startActivity(new Intent(this, VideoSelectActivity.class));
    }
}