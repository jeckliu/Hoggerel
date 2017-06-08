package com.jeckliu.framwork.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import com.jeckliu.framwork.permission.PermissionAction;

/***
 * Created by Jeck.Liu on 2017/6/6 0006.
 */

public class BaseFragment extends Fragment{
    protected PermissionAction permissionAction;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        permissionAction = PermissionAction.getNewInstance(getContext());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionAction.permissionResult(requestCode,permissions,grantResults);
    }
}
