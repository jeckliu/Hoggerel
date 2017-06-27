package com.jeckliu.pay.alipay;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import com.alipay.sdk.app.PayTask;
import java.util.Map;

/***
 * Created by Jeck.Liu on 2017/6/20 0020.
 */

public class AlipayManager {
    private static Context mContext;
    /**
     * 支付宝支付业务：入参app_id
     */
    public static final String APPID = "2016080600176967";

    public static final String RSA2_PRIVATE = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCIx20mvlxmDjDjqyMcUcA9V2WRshoeUmHZqUCal8f3gxVUoppFz50eIw/l4lkhg88hQZJxVKa8x/8lwXK2SZlVGxJoJqWRST2qieiKPdaQQZVJGVH1JPNM4QGs/pM+iUdf1G/UJ1r3H/lWSD7wtU6fahyXDbbk+9aou9sISgkrrD1I4B5fZTZFSxqdKa06AZ2SrvEwTBQ/Uom/Hcnx0ZTaFtlHuXqgaViv7rcb0te9CEfyT9xSPVtSbnUJTmkKJu5XAKeZjpuL3cttoZGLrMSio3riYkAtXmRk4PT4GS91mynpH6ah0lcnDpDX2ySnZW8Z1CIfvU27xhpEcVvkkQT5AgMBAAECggEAMwYyMcHRtkCU/FlbP7U/gaKYwUEvfSA+YHRDJWh5sFu2GVyPMHvCoPtzcs59cYM6Qa20c5KgoMv64prHsDjEense4ruICCrKVlQb63YmFKpihmJrsIeYO5W0ilRtmWacuaMGFJ6Z0Jac7RMGZ8U7Dz0GbVMwZzuLWGa7ztvPj/qgMzP+FormOFJe4P8BkmEDj9UIhC5io9A2ub437a8gEMKHfp2FZX5uqQQig1/f0acok4Oax8axIUTR/F7pXWaf2EIzOya0t2WhkYkAV9SZuN0imoGfs+vehYfSf+tf/ZHVrXr0pX86pJOyrLN4UzlFuJn+h3V1D5VNYn2ts0I4AQKBgQD0z5wgnFgCqkYK9LT8rJ86lSR9Ha1FpePgkYrZDeT00hc0WK/VFJPdjwPx6ga9p3sxL6u6qnI0ZTE6f/zGNhrOJ+Z0TmWO0TAyJVXFM/X5vW22AgPxOuDW0/5KXDn6V3Y1wtGPzMMt6NyAVZR/pXnkXJzT3wxv7kXaxSmYbS8SJQKBgQCPB8wv6dOaGxjsRaE6bC85mW4NRlFVHEAkYfZGNfHXp83MdgWVbk6RgDe3tnafmNQvnKZTzE08n5svIZpJvGyoPNGz/dIZJaCatxit136mkN2w2/fVwh4yk9WujHsFG5y11Qq3VIQaH+NporbX3o11cSf8JKuKpoX54wSUCvxNRQKBgQCa4EEArIhNX1Wilr9tXvP5RuNnt/+noVRx/QGfYdfoPoVpm3XZ0wPc1h6DzC9pimw4aNU26aAIn8AuJ0xORWpd5AY6rGI5oQPhpZcGhGHoFjwzOkEuOraFkmY8uu3+/5gMWOzlEYClKb1d/0ZMHu7nVuPsch7XLSHKTpOLGAAAdQKBgG0xpcf6empPP6K5sdH5X8BYizUlNtiEPc/I3gxSCLT8TdhHThH76Y2ZVnAxo7RKJ8vFixG0ik67Bu1fePvMFyQFco84Otqp6EfVesjVGMKvHCB2fmm5zfYM+PhOFWkb4HLNF2ZI5qVPLP2rlG2PYW2EmuMbV779TJEuqIMhM+cFAoGAV3mt4KzwwVrwf+zcHL6g9qLO8BAzUVpVFVBICZyKrScbxNhm26wNqt0bgXbzu/R7t8ahpQjfruI14pUsp40Lc1aMJfAD8b9DTqL5OGRQj3ozl9Ler6NZOjF/JJ0xSMX0xLmV6KLAQd0bx8swhYmDAUd4SoxR2tdriSnO42yemzk=";
    public static final String RSA_PRIVATE = "";
    private static final int SDK_PAY_FLAG = 1;

    @SuppressLint("HandlerLeak")
    private static Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        Toast.makeText(mContext, "支付成功", Toast.LENGTH_SHORT).show();
                    } else if (TextUtils.equals(resultStatus, "4000")) {
                        Toast.makeText(mContext, "支付失败", Toast.LENGTH_SHORT).show();
                    } else if (TextUtils.equals(resultStatus, "6001")) {
                        Toast.makeText(mContext, "支付取消", Toast.LENGTH_SHORT).show();
                    } else if (TextUtils.equals(resultStatus, "6002")) {
                        Toast.makeText(mContext, "网络异常", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(mContext, "其他支付错误", Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
            }
        }

        ;
    };

    public static void pay(final Context context) {
        mContext = context;
        if (TextUtils.isEmpty(APPID) || (TextUtils.isEmpty(RSA2_PRIVATE) && TextUtils.isEmpty(RSA_PRIVATE))) {
            new AlertDialog.Builder(context).setTitle("警告").setMessage("需要配置APPID | RSA_PRIVATE")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialoginterface, int i) {
                        }
                    }).show();
            return;
        }

        /**
         * 这里只是为了方便直接向商户展示支付宝的整个支付流程；所以Demo中加签过程直接放在客户端完成；
         * 真实App里，privateKey等数据严禁放在客户端，加签过程务必要放在服务端完成；
         * 防止商户私密数据泄露，造成不必要的资金损失，及面临各种安全风险；
         *
         * orderInfo的获取必须来自服务端；
         */
        boolean rsa2 = (RSA2_PRIVATE.length() > 0);
        Map<String, String> params = OrderInfoUtil2_0.buildOrderParamMap(APPID, rsa2);
        String orderParam = OrderInfoUtil2_0.buildOrderParam(params);

        String privateKey = rsa2 ? RSA2_PRIVATE : RSA_PRIVATE;
        String sign = OrderInfoUtil2_0.getSign(params, privateKey, rsa2);
        final String orderInfo = orderParam + "&" + sign;

        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask((Activity) context);
                Map<String, String> result = alipay.payV2(orderInfo, true);
                Log.i("msp", result.toString());

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

}
