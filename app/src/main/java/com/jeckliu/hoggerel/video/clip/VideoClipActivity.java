package com.jeckliu.hoggerel.video.clip;

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
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.jeckliu.hoggerel.R;
import com.jeckliu.hoggerel.mp4parser.MediaController;
import com.jeckliu.hoggerel.util.FileUtils;
import com.jeckliu.hoggerel.util.VideoUtil;
import com.jeckliu.hoggerel.view.PictureSeekPickerHelper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/***
 * Created by Jeck.Liu on 2017/2/13 0013.
 */
public class VideoClipActivity extends FragmentActivity implements View.OnClickListener {
    public static  final String SRC_PATH = "srcPath";
    private String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private List<String> grantedPermission = new ArrayList<>();

    private TextView tvDone;
    private TextView tvName;
    private TextView tvShowTotalTime;
    private VideoView videoView;
    private ProgressBar progressBar;
    private TextView tvShowProgress;
    private PictureSeekPickerHelper pictureSeekPickerHelper;

    private String path;
    private int startPosition = 1;
    private int endPosition = 10;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    if (videoView.getCurrentPosition() >= endPosition * 1000) {
                        videoView.pause();
                    }
                    if (!videoView.isPlaying()) {
                        videoView.seekTo(startPosition * 1000);
                        videoView.start();
                    }
                    handler.sendEmptyMessageDelayed(0, 100);
                    break;
                case 1:
                    Bundle bundle = msg.getData();
                    ArrayList<Bitmap> bitmaps = (ArrayList<Bitmap>) bundle.getSerializable("bitmaps");
                    pictureSeekPickerHelper.setBitmaps(bitmaps);
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
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        tvShowProgress = (TextView) findViewById(R.id.show_progress);
        pictureSeekPickerHelper = PictureSeekPickerHelper.getInstance(this);
        checkPermission();
    }

    private void initData() {
        path = getIntent().getStringExtra(SRC_PATH);
        tvName.setText(path);

        videoPlay(path);

        bitmapFromClipVideo(path);
    }

    private void bitmapFromClipVideo(final String path) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                VideoUtil.getInstance().init(path);
                List<Bitmap> bitmaps = VideoUtil.getInstance().getBitmapsForVideo();
                Message msg = new Message();
                Bundle bundle = new Bundle();
                bundle.putSerializable("bitmaps", (Serializable) bitmaps);
                msg.setData(bundle);
                msg.what = 1;
                handler.sendMessage(msg);
            }
        }).start();

        pictureSeekPickerHelper.setOnSeekProgress(new PictureSeekPickerHelper.OnSeekProgress() {
            @Override
            public void setOnSeekProgress(int leftProgress, int rightProgress) {
                videoView.seekTo(leftProgress * 1000);
                videoView.start();
                startPosition = leftProgress;
                endPosition = rightProgress;
                tvShowProgress.setText("左边===" + leftProgress + ",右边===" + rightProgress);
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void videoPlay(String path) {
        videoView.setVideoPath(path);
        videoView.requestFocus();
        videoView.start();

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                int duration = videoView.getDuration() / 1000;
                tvShowTotalTime.setText("视频总时长:" + duration + "秒");
                handler.sendEmptyMessage(0);
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
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
            return MediaController.getInstance().compressVideo(path, FileUtils.getOutputFile(), startPosition - 1, endPosition);
        }

        @Override
        protected void onPostExecute(Boolean compressed) {
            super.onPostExecute(compressed);
            progressBar.setVisibility(View.GONE);
            if (compressed) {
            }
        }
    }


    private void checkPermission() {

        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                grantedPermission.add(permission);
            }
        }
        if (grantedPermission.size() == 0) {
            initData();
        } else {
            List<String> tempList = grantedPermission;
            int i = grantedPermission.size();
            while (i != 0) {
                ActivityCompat.requestPermissions(this, new String[]{tempList.get(i - 1)}, (int) Math.random());
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