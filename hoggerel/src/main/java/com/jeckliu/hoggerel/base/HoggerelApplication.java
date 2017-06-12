package com.jeckliu.hoggerel.base;

import com.jeckliu.framwork.base.BaseApplication;

import cn.jpush.android.api.JPushInterface;

/***
 * Created by Jeck.Liu on 2017/6/9 0009.
 */

public class HoggerelApplication extends BaseApplication{

    @Override
    public void onCreate() {
        super.onCreate();
        JPushInterface.setDebugMode(true); 	// 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);     		// 初始化 JPush
    }
}
