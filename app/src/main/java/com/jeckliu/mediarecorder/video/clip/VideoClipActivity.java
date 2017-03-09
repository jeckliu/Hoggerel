package com.jeckliu.mediarecorder.video.clip;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;
import com.jeckliu.mediarecorder.R;
import com.jeckliu.mediarecorder.mp4parser.MediaController;
import com.jeckliu.mediarecorder.util.FileUtils;
import com.jeckliu.mediarecorder.util.VideoUtil;
import com.jeckliu.mediarecorder.view.BothwaySeekBar;

import java.util.ArrayList;
import java.util.List;

/***
 * Created by Jeck.Liu on 2017/2/13 0013.
 */
public class VideoClipActivity extends FragmentActivity implements View.OnClickListener{
    private String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private List<String> grantedPermission = new ArrayList<>();

    private TextView tvDone;
    private TextView tvName;
    private TextView tvShowTotalTime;
    private VideoView videoView;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private BothwaySeekBar bothwaySeekBar;
    private TextView tvShowProgress;

    private String path;

    private int startPosition;
    private int endPosition;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    if(videoView.getCurrentPosition() >= endPosition){
                        videoView.pause();
                    }
                    if(!videoView.isPlaying()){
                        videoView.seekTo(startPosition);
                        videoView.start();
                    }
                    handler.sendEmptyMessageDelayed(0,1000);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_clip);
        tvDone = (TextView) findViewById(R.id.done);
        tvDone.setOnClickListener(this);
        tvName = (TextView) findViewById(R.id.name);
        tvShowTotalTime = (TextView) findViewById(R.id.total_time);
        videoView = (VideoView) findViewById(R.id.video_view);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        bothwaySeekBar = (BothwaySeekBar) findViewById(R.id.both_way_seek_bar);
        tvShowProgress = (TextView) findViewById(R.id.show_progress);
        bothwaySeekBar.setOnSeekBarChangeListener(new BothwaySeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(boolean leftTouch, boolean rightTouch, int leftProgress, int rightProgress, float leftLocation, float rightLocation) {
                tvShowProgress.setText("进度"+leftProgress+"，位置"+leftLocation+"，进度"+rightProgress+"，位置"+rightLocation);
                if(leftTouch){
                    startPosition = leftProgress;
                    videoView.seekTo(leftProgress);
                    videoView.start();
                    handler.sendEmptyMessage(0);
                }
                if(rightTouch){
                    endPosition = rightProgress;
                }
            }
        });

        checkPermission();
    }

    private void initData() {
        if(FileUtils.getInputVideoFiles() != null && FileUtils.getInputVideoFiles().size() > 0){
            path = FileUtils.getInputVideoFiles().get(0);
            tvName.setText(path);
        }
        if(path == null){
            return;
        }

        videoPlay(path);

        bitmapFromClipVideo(path);
    }

    private void bitmapFromClipVideo(String path) {
        VideoUtil.getInstance().init(path);
        List<Bitmap> bitmaps = VideoUtil.getInstance().getBitmapsForVideo();
        ImageAdapter adapter = new ImageAdapter(this,bitmaps);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        recyclerView.setAdapter(adapter);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void videoPlay(String path) {
        videoView.setVideoPath(path);
        videoView.requestFocus();
        videoView.start();

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                int duration = videoView.getDuration();
                bothwaySeekBar.setSeekTotalProgress(duration);
                bothwaySeekBar.setSeekLeftProgress(0);
                bothwaySeekBar.setSeekRightProgress(duration);
                startPosition = 0;
                endPosition = duration;
                tvShowTotalTime.setText("视频总时长:"+ duration + "毫秒");
                handler.sendEmptyMessage(0);
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.done:
                new VideoCompressorTask().execute();
                break;
        }
    }

    class VideoCompressorTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            return MediaController.getInstance().compressVideo(path,FileUtils.getOutputFile(),2,10);
        }

        @Override
        protected void onPostExecute(Boolean compressed) {
            super.onPostExecute(compressed);
            progressBar.setVisibility(View.GONE);
            if(compressed){
            }
        }
    }


    private void checkPermission() {

            for(String permission : permissions){
                if(ActivityCompat.checkSelfPermission(this,permission) != PackageManager.PERMISSION_GRANTED){
                    grantedPermission.add(permission);
                }
            }
            if(grantedPermission.size() == 0){
                initData();
            }else{
                List<String> tempList = grantedPermission;
                int i = grantedPermission.size();
                while (i != 0){
                    ActivityCompat.requestPermissions(this,new String[]{tempList.get(i-1)},(int)Math.random());
                    i--;
                    grantedPermission.remove(i);
                }
            }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        initData();
    }

}
