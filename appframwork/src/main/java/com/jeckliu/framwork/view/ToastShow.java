package com.jeckliu.framwork.view;

import android.widget.Toast;

import com.jeckliu.framwork.base.BaseApplication;

public class ToastShow {

	public static void showLongMessage(String msg){
		Toast.makeText(BaseApplication.getContext(),msg,Toast.LENGTH_LONG).show();
	}

	public static void showShortMessage(String msg){
		Toast.makeText(BaseApplication.getContext(),msg,Toast.LENGTH_SHORT).show();
	}

	public static void showLongMessage(int resId){
		Toast.makeText(BaseApplication.getContext(),resId,Toast.LENGTH_LONG).show();
	}

	public static void showShortMessage(int resId){
		Toast.makeText(BaseApplication.getContext(),resId,Toast.LENGTH_LONG).show();
	}
}