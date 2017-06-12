package com.jeckliu.hoggerel.push;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.jeckliu.hoggerel.MainActivity;
import com.jeckliu.push.Logger;
import cn.jpush.android.api.JPushInterface;

/**
 * 自定义接收器
 * 
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class PushReceiver extends BroadcastReceiver {
	private static final String TAG = "JPush";

	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			Bundle bundle = intent.getExtras();

			if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
				String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
				Logger.d(TAG, "[PushReceiver] 接收Registration Id : " + regId);
				//send the Registration Id to your server...

			} else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
				Logger.d(TAG, "[PushReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));

			} else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
				Logger.d(TAG, "[PushReceiver] 接收到推送下来的通知");
				int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
				Logger.d(TAG, "[PushReceiver] 接收到推送下来的通知的ID: " + notifactionId);

			} else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
				Logger.d(TAG, "[PushReceiver] 用户点击打开了通知");
				Intent intent1 = new Intent(context, MainActivity.class);
				intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
				context.startActivity(intent1);
			} else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
				Logger.d(TAG, "[PushReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
				//在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..

			} else if(JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
				boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
				Logger.w(TAG, "[PushReceiver]" + intent.getAction() +" connected state change to "+connected);
			} else {
				Logger.d(TAG, "[PushReceiver] Unhandled intent - " + intent.getAction());
			}
		} catch (Exception e){

		}

	}

}
