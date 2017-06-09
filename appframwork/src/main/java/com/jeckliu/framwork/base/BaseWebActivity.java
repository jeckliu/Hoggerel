package com.jeckliu.framwork.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.widget.ProgressBar;
import com.jeckliu.framwork.R;
import com.jeckliu.framwork.view.CommonTitleBar;
import com.jeckliu.framwork.web.CustomWebChromeClient;
import com.jeckliu.framwork.web.CustomWebViewClient;

/***
 * Created by Jeck.Liu on 2017/6/8 0008.
 */

public class BaseWebActivity extends FragmentActivity{
    public static String URL = "url";

    private WebView webView;
    private ProgressBar progressBar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_web);

        initWebView();
        initData();
    }

    private void initWebView() {
        CommonTitleBar titleBar = new CommonTitleBar(this);
        webView = (WebView) findViewById(R.id.web_view);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        webView.getSettings().setJavaScriptEnabled(true);

        webView.setWebChromeClient(new CustomWebChromeClient(titleBar,progressBar));
        webView.setWebViewClient(new CustomWebViewClient(progressBar));
    }

    private void initData() {
        webView.loadUrl(getIntent().getStringExtra(URL));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
