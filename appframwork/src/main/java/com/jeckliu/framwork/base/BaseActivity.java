package com.jeckliu.framwork.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import com.jeckliu.framwork.permission.PermissionAction;

/***
 * Created by Jeck.Liu on 2017/1/22 0022.
 */
public abstract class BaseActivity extends FragmentActivity{
    protected PermissionAction permissionAction;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        permissionAction = PermissionAction.getNewInstance(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionAction.permissionResult(requestCode,permissions,grantResults);
    }

    public void onBack(View view){
        finish();
    }
}
