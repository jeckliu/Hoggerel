package com.jeckliu.codescanner;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;
import com.jeckliu.framwork.base.BaseActivity;
import com.jeckliu.framwork.base.BaseWebActivity;
import com.jeckliu.framwork.view.CommonTitleBar;
import com.jeckliu.framwork.view.ToastShow;

/***
 * Created by Jeck.Liu on 2017/6/9 0009.
 */

public class ScanResultActivity extends BaseActivity implements View.OnClickListener{
    public static final String PARAM_CONTEXT = "paramContent";
    private TextView tvContent;
    private TextView tvGo;

    private String content;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_result);
        new CommonTitleBar(this,"扫码结果");
        tvContent = (TextView) findViewById(R.id.activity_scan_result_content);
        tvGo = (TextView) findViewById(R.id.activity_scan_result_go);
        tvGo.setOnClickListener(this);
        content = getIntent().getStringExtra(PARAM_CONTEXT);
        tvContent.setText(content);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.activity_scan_result_go) {
            if(canGo(content)){
                Intent intent = new Intent(this, BaseWebActivity.class);
                intent.putExtra(BaseWebActivity.URL,content);
                startActivity(intent);
            }else{
                ToastShow.showLongMessage("该地址不能跳转");
            }
        }
    }

    private boolean canGo(String content){
        return content.startsWith("http://") || content.startsWith("https://")
                || content.startsWith("HTTP://") || content.startsWith("HTTPS://");
    }
}
