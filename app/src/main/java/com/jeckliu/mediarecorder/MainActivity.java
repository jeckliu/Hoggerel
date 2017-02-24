package com.jeckliu.mediarecorder;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.jeckliu.mediarecorder.video.clip.VideoClipActivity;
import com.jeckliu.mediarecorder.video.record.VideoRecorderActivity;

/***
 * Created by Jeck.Liu on 2017/2/14 0014.
 */
public class MainActivity extends FragmentActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onVideoRecord(View view){
        startActivity(new Intent(this,VideoRecorderActivity.class));
    }

    public void onVideoClip(View view){
        startActivity(new Intent(this, VideoClipActivity.class));
    }
}
