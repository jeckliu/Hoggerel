package com.jeckliu.hoggerel.video.shoot;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.WindowManager;

import com.jeckliu.hoggerel.R;

/***
 * Created by Jeck.Liu on 2017/5/12 0012.
 */
public class ShootActivity extends FragmentActivity {
    public static final String TAG_CAMERA_FACING_STATE = "cameraFacingState";
    public static final String TAG_FILE_PATH = "filePath";
    public static final String TAG_SHOOT_TIME ="shootTime";
    public static final String TAG_PHOTO_VIDEO = "photoVideo";
    public static final int FLAG_PHOTO = 1;
    public static final int FLAG_VIDEO = 2;

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        setContentView(R.layout.activity_shoot2);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment, new ShootingFragment()).commit();

    }

}
