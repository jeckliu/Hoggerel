package com.jeckliu.framwork.view;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;

import com.jeckliu.framwork.R;

import java.util.List;

/***
 * Created by Jeck.Liu on 2017/6/8 0008.
 */

public class DenyPermissionDialog {

    public static void show(final FragmentActivity activity, List<String> denyPermissions) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(activity.getString(R.string.app_name));
        builder.setMessage(getMessage(activity, denyPermissions));
        builder.setPositiveButton("进入设置", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                intent.setData(Uri.fromParts("package", activity.getPackageName(), null));
                activity.startActivity(intent);
                activity.finish();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                activity.finish();
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                activity.finish();
            }
        });
        builder.show();
    }

    private static String getMessage(FragmentActivity activity, List<String> denyPermissions) {
        StringBuilder builder = new StringBuilder();
        for (String permission : denyPermissions) {
            if (permission.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                builder.append("文件读取、");
            }
            if (permission.equals(Manifest.permission.CAMERA)) {
                builder.append("相机、");
            }
            if (permission.equals(Manifest.permission.RECORD_AUDIO)) {
                builder.append("麦克风、");
            }
            if(permission.equals(Manifest.permission.ACCESS_FINE_LOCATION)){
                builder.append("GPS定位");
            }
            if(permission.equals(Manifest.permission.ACCESS_COARSE_LOCATION)){
                builder.append("网络定位");
            }
        }
        String result = builder.toString().substring(0, builder.lastIndexOf("、"));
        return "未授予" + result + "权限，请在系统设置页授予" + activity.getResources().getString(R.string.app_name) + result + "权限";
    }
}
