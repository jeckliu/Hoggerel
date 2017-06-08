package com.jeckliu.multimedia;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import com.jeckliu.framwork.base.BaseActivity;
import com.jeckliu.framwork.permission.IPermission;
import com.jeckliu.framwork.view.CommonTitleBar;
import com.jeckliu.framwork.view.DenyPermissionDialog;
import com.jeckliu.multimedia.clip.VideoSelectActivity;
import com.jeckliu.multimedia.shoot.ShootActivity;

import java.util.List;

/***
 * Created by Jeck.Liu on 2017/6/6 0006.
 */

public class MultimediaActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multimedia);
        new CommonTitleBar(this,"多媒体");
        permissionAction.addPermission(Manifest.permission.CAMERA)
                .addPermission(Manifest.permission.RECORD_AUDIO)
                .requestPermission(new IPermission() {
                    @Override
                    public void done() {

                    }

                    @Override
                    public void unPermission(List<String> denyPermissions) {
                        DenyPermissionDialog.show(MultimediaActivity.this,denyPermissions);
                    }
                });

    }

    public void onShoot(View view) {
        startActivity(new Intent(this, ShootActivity.class));
    }

    public void onVideoClip(View view) {
        startActivity(new Intent(this, VideoSelectActivity.class));
    }
}
