package com.jeckliu.hoggerel.video.play;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;
import android.view.WindowManager;

import com.jeckliu.hoggerel.R;

/***
 * Created by Jeck.Liu on 2016/8/10 0010.
 */
public class VideoViewActivity extends FragmentActivity {
    public static final String VIDEO_URL = "videoUrl";
    private CustomVideoView videoView;

    private String videoUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_video_play);

        videoUrl = getIntent().getStringExtra(VIDEO_URL);

        videoView = (CustomVideoView) findViewById(R.id.videoview);

        videoView.setVideoPath(videoUrl);
        videoView.requestFocus();
        videoView.setOnPreparedListener(onPreparedListener);
        videoView.start();
    }

    MediaPlayer.OnPreparedListener onPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
        }
    };


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                finish();
                return true;
        }
        return false;
    }
}
