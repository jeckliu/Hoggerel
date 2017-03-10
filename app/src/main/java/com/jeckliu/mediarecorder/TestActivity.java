package com.jeckliu.mediarecorder;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jeckliu.mediarecorder.util.FileUtils;
import com.jeckliu.mediarecorder.util.VideoUtil;
import com.jeckliu.mediarecorder.view.BothwaySeekBar;
import com.jeckliu.mediarecorder.view.PictureSeekPicker;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/***
 * Created by Jeck.Liu on 2017/3/7 0007.
 */
public class TestActivity extends FragmentActivity{
    private PictureSeekPicker pictureSeekPicker;
    private TextView textView;
    private String path;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    Bundle bundle = msg.getData();
                    ArrayList<Bitmap> bitmaps = (ArrayList<Bitmap>) bundle.getSerializable("bitmaps");
                    pictureSeekPicker.setBitmaps(bitmaps);
                    break;
            }
        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        if(FileUtils.getInputVideoFiles() != null && FileUtils.getInputVideoFiles().size() > 0){
            path = FileUtils.getInputVideoFiles().get(0);
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                VideoUtil.getInstance().init(path);
                List<Bitmap> bitmaps = VideoUtil.getInstance().getBitmapsForVideo();
                Message msg = new Message();
                Bundle bundle = new Bundle();
                bundle.putSerializable("bitmaps", (Serializable) bitmaps);
                msg.setData(bundle);
                msg.what = 0;
                handler.sendMessage(msg);
            }
        }).start();

        pictureSeekPicker = (PictureSeekPicker) findViewById(R.id.both_way_seek_bar);
        textView = (TextView) findViewById(R.id.activity_test_show);
        pictureSeekPicker.setOnSeekBarChangeListener(new PictureSeekPicker.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(int leftProgress, int rightProgress) {
                textView.setText(" 左边="+leftProgress +"，右边= "+rightProgress);
            }
        });

    }
}
