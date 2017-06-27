package com.jeckliu.pay.wxpay;

import android.content.Context;
import android.widget.Toast;
import com.tencent.mm.opensdk.constants.Build;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

/***
 * Created by Jeck.Liu on 2017/6/21 0021.
 */

public class WxpayManager {
    private static final String APP_ID = "wxd930ea5d5a258f4f";
    private static IWXAPI api;

    public static void pay(Context context) {
        api = WXAPIFactory.createWXAPI(context,APP_ID,true);
        api.registerApp(APP_ID);

        PayReq request = new PayReq();

        request.appId = "wxd930ea5d5a258f4f";

        request.partnerId = "1900000109";

        request.prepayId= "1101000000140415649af9fc314aa427";

        request.packageValue = "Sign=WXPay";

        request.nonceStr= "1101000000140429eb40476f8896f4c9";

        request.timeStamp= "1398746574";

        request.sign= "7FFECB600D7157C5AA49810D2D8F28BC2811827B";

        api.sendReq(request);

    }

    public static void checkWxpay(Context context){
        boolean isPaySupported = api.getWXAppSupportAPI() >= Build.PAY_SUPPORTED_SDK_INT;
        Toast.makeText(context, String.valueOf(isPaySupported), Toast.LENGTH_SHORT).show();
    }
}
