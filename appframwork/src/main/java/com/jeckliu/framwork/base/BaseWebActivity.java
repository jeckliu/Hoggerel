package com.jeckliu.framwork.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.webkit.WebView;

/***
 * Created by Jeck.Liu on 2017/6/8 0008.
 */

public class BaseWebActivity extends FragmentActivity{
    public static String URL = "url";

    private WebView webView;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        webView = new WebView(this);
        setContentView(webView);

        initData();
    }

    private void initData() {
        webView.loadUrl(getIntent().getStringExtra(URL));
    }
}
