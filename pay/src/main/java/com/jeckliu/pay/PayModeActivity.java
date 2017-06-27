package com.jeckliu.pay;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.alipay.sdk.app.EnvUtils;
import com.jeckliu.framwork.base.BaseActivity;
import com.jeckliu.framwork.view.CommonTitleBar;
import com.jeckliu.pay.alipay.AlipayManager;
import com.jeckliu.pay.wxpay.WxpayManager;

/***
 * Created by Jeck.Liu on 2017/6/20 0020.
 */

public class PayModeActivity extends BaseActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_mode);
        new CommonTitleBar(this,"支付方式");
    }

    public void onAlipay(View view){
        EnvUtils.setEnv(EnvUtils.EnvEnum.SANDBOX);
        AlipayManager.pay(this);
    }

    public void onWxpay(View view){
        WxpayManager.pay(this);
    }

    public void onCheckWxpay(View view){
        WxpayManager.checkWxpay(this);
    }

}
