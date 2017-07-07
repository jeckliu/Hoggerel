package com.jeckliu.multimedia.clip;

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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.jeckliu.framwork.view.LoadingDialog;
import com.jeckliu.framwork.view.ToastShow;
import com.jeckliu.multimedia.R;
import com.jeckliu.multimedia.mp4parser.MediaController;
import com.jeckliu.multimedia.util.FileUtils;
import com.jeckliu.multimedia.util.VideoUtil;
import com.jeckliu.multimedia.view.PictureSeekPickerHelper;

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

    private ImageView ivBack;
    private TextView tvDone;
    private TextView tvName;
    private TextView tvShowTotalTime;
    private VideoView videoView;
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
        ivBack = (ImageView) findViewById(R.id.back);
        ivBack.setOnClickListener(this);
        tvDone = (TextView) findViewById(R.id.done);
        tvDone.setOnClickListener(this);
        tvName = (TextView) findViewById(R.id.name);
        tvShowTotalTime = (TextView) findViewById(R.id.total_time);
        videoView = (VideoView) findViewById(R.id.video_view);
        tvShowProgress = (TextView) findViewById(R.id.show_progress);
        pictureSeekPickerHelper = PictureSeekPickerHelper.getNewInstance(this);
        checkPermission();
    }

    private void initData() {
        path = getIntent().getStringExtra(SRC_PATH);
        tvName.setText("视频源路径："+path);

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
                tvShowProgress.setText("剪辑开始：" + leftProgress + ",结束：" + rightProgress);
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
        int i = v.getId();
        if (i == R.id.done) {
            new VideoCompressorTask().execute();
        }else if(i == R.id.back){
            finish();
        }
    }

    private class VideoCompressorTask extends AsyncTask<Void, Void, Boolean> {
        private String desPath = FileUtils.getOutputFile();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            LoadingDialog.getInstance().show(getSupportFragmentManager());
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            return MediaController.getInstance().compressVideo(path, desPath, startPosition - 1, endPosition);
        }

        @Override
        protected void onPostExecute(Boolean compressed) {
            super.onPostExecute(compressed);
            LoadingDialog.getInstance().dismiss();
            if (compressed) {
                ToastShow.showLongMessage("查看路径："+desPath);
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