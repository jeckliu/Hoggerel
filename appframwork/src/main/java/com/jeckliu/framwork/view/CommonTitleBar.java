package com.jeckliu.framwork.view;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.jeckliu.framwork.R;

/**
 * 公共title处理类
 * Created by Jeck.Liu on 2016/7/12 0012.
 */
public class CommonTitleBar implements View.OnClickListener{
    private Activity activity;
    private TextView tvTitle;
    private ImageView ivBack;
    private String strTitle;

    public CommonTitleBar(Activity activity){
        this.activity = activity;
        init();
    }

    public CommonTitleBar(Activity activity, String title){
        this.activity = activity;
        this.strTitle = title;
        init();
    }

    private void init() {
        ivBack = (ImageView) activity.findViewById(R.id.common_title_bar_back);
        tvTitle = (TextView) activity.findViewById(R.id.common_title_bar_title);
        tvTitle.setText(strTitle);
        ivBack.setOnClickListener(this);
    }

    public void setTiile(String strTitle){
        tvTitle.setText(strTitle);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.common_title_bar_back) {
            activity.finish();
        }
    }

}
