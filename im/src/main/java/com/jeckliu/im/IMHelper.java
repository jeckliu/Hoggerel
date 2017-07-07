package com.jeckliu.im;

import android.app.ActivityManager;
import android.content.Context;
import android.support.v4.app.FragmentActivity;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.easeui.EaseUI;
import com.hyphenate.exceptions.HyphenateException;
import com.jeckliu.framwork.base.BaseApplication;
import com.jeckliu.framwork.base.Configure;
import com.jeckliu.framwork.event.EventLoginSuccess;
import com.jeckliu.framwork.util.SpUtil;
import com.jeckliu.framwork.view.LoadingDialog;
import com.jeckliu.framwork.view.ToastShow;

import org.greenrobot.eventbus.EventBus;

import java.util.Iterator;
import java.util.List;

import static android.content.Context.ACTIVITY_SERVICE;

/***
 * Created by Jeck.Liu on 2017/6/28 0028.
 */

public class IMHelper {

    private static IMHelper instance;

    public static IMHelper getInstance() {
        if (instance == null) {
            instance = new IMHelper();
        }
        return instance;
    }

    public void init() {
        Context context = BaseApplication.getContext();
        EaseUI.getInstance().init(context, null);
        EMClient.getInstance().setDebugMode(true);
    }

    public void register(final FragmentActivity activity, final String userName, final String pwd) {
        LoadingDialog.getInstance().show(activity.getSupportFragmentManager());
        new Thread(new Runnable() {
            @Override
            public void run() {
                String msg = "注册成功";
                try {
                    EMClient.getInstance().createAccount(userName, pwd);//同步方法
                } catch (HyphenateException e) {
                    final int errorCode = e.getErrorCode();
                    if (errorCode == EMError.NETWORK_ERROR) {
                        msg = "网络错误";
                    } else if (errorCode == EMError.USER_ALREADY_EXIST) {
                        msg = "用户已存在";
                    } else if (errorCode == EMError.USER_AUTHENTICATION_FAILED) {
                        msg = "注册失败，无权限";
                    } else if (errorCode == EMError.USER_ILLEGAL_ARGUMENT) {
                        msg = "用户名不合法";
                    } else {
                        msg = "注册失败";
                    }

                } finally {
                    final String finalMsg = msg;
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {ToastShow.showLongMessage(finalMsg);
                            LoadingDialog.getInstance().dismiss();
                            if(finalMsg.equals("注册成功")){
                                activity.finish();
                            }
                        }
                    });
                }
            }
        }).start();
    }

    public void login(Context context, final String userName, final String pwd) {
        final FragmentActivity activity = (FragmentActivity) context;
        LoadingDialog.getInstance().show(((FragmentActivity) context).getSupportFragmentManager());
        EMClient.getInstance().login(userName, pwd, new EMCallBack() {//回调
            @Override
            public void onSuccess() {
                SpUtil.getInstance().put(Configure.STATIC_USER_NAME,userName);
                SpUtil.getInstance().put(Configure.STATIC_USER_PWD,pwd);

                EMClient.getInstance().groupManager().loadAllGroups();
                EMClient.getInstance().chatManager().loadAllConversations();
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LoadingDialog.getInstance().dismiss();
                        ToastShow.showLongMessage("登录服务器成功！");
                    }
                });
                EventBus.getDefault().post(new EventLoginSuccess());
                activity.finish();
            }

            @Override
            public void onProgress(int progress, String status) {
            }

            @Override
            public void onError(int code, final String message) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LoadingDialog.getInstance().dismiss();
                        ToastShow.showLongMessage("登录服务器失败," + message);
                    }
                });
            }
        });
    }

    public void logout() {
        EMClient.getInstance().logout(true);
    }


    private String getAppName(int pID) {
        Context context = BaseApplication.getContext();
        String processName = null;
        ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                if (info.pid == pID) {
                    processName = info.processName;
                    return processName;
                }
            } catch (Exception e) {
                // Log.d("Process", "Error>> :"+ e.toString());
            }
        }
        return processName;
    }

}
