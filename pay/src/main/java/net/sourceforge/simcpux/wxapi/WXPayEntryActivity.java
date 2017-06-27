package net.sourceforge.simcpux.wxapi;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onReq(BaseReq req) {
        int type = req.getType();
        String id = req.openId;
    }

    @Override
    public void onResp(BaseResp resp) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("支付完成");
        builder.setMessage(String.valueOf(resp.errCode));
        builder.show();
    }
}