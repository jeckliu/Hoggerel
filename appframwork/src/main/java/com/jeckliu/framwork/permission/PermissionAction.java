package com.jeckliu.framwork.permission;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import java.util.ArrayList;
import java.util.List;

/***
 * Created by Jeck.Liu on 2017/1/22 0022.
 */
public class PermissionAction{
    private static Activity activity;
    private static Context context;
    private IPermission iPermission;
    private static PermissionAction instance;
    private List<String> permissions;

    public static PermissionAction getNewInstance(Context mContext){
        activity = (Activity) mContext;
        context = mContext;
        instance = new PermissionAction();
        return instance;
    }

    private PermissionAction() {
        permissions = new ArrayList<>();
    }

    public void permissionResult(int requestCode, @NonNull String[] grantPermissions, @NonNull int[] grantResults){
        for(int i = 0; i < grantResults.length; i++){
            if(grantResults[i] == PackageManager.PERMISSION_GRANTED){
                permissions.remove(grantPermissions[i]);
            }
        }
        if(permissions.size() == 0){
            iPermission.done();
        }else{
            iPermission.unPermission(permissions);
        }
    }

    public  PermissionAction addPermission(String permission){
        if(ActivityCompat.checkSelfPermission(context,permission) != PackageManager.PERMISSION_GRANTED){
            permissions.add(permission);
        }
        return instance;
    }

    private String[] getPermissions(){
        return permissions.toArray(new String[permissions.size()]);
    }

    public void requestPermission(IPermission iPermission){
        this.iPermission = iPermission;
        if(permissions.size() == 0){
            iPermission.done();
        }else{
            ActivityCompat.requestPermissions(activity,getPermissions(),(int)( Math.random()*10000));
        }
    }
}
