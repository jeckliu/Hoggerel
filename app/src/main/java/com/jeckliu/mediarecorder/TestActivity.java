package com.jeckliu.mediarecorder;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jeckliu.mediarecorder.view.BothwaySeekBar;

/***
 * Created by Jeck.Liu on 2017/3/7 0007.
 */
public class TestActivity extends FragmentActivity{
    private BothwaySeekBar bothwaySeekBar;
    private TextView textView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        bothwaySeekBar = (BothwaySeekBar) findViewById(R.id.both_way_seek_bar);
        textView = (TextView) findViewById(R.id.activity_test_show);

        bothwaySeekBar.setOnSeekBarChangeListener(new BothwaySeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(boolean leftTouch, boolean rightTouch, int leftProgress, int rightProgress,float leftLocation,float rightLocation) {
                textView.setText(" 左边="+leftProgress+",位置"+leftLocation +"，右边= "+rightProgress+"，位置"+rightLocation);
            }
        });
    }
}
