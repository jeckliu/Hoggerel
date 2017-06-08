package com.jeckliu.framwork.base;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

/***
 * Created by Jeck.Liu on 2017/6/8 0008.
 */

public class BaseApplication extends Application {
    @SuppressLint("StaticFieldLeak")
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }
}
