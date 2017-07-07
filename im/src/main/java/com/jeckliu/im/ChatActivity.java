package com.jeckliu.im;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.Nullable;
import com.hyphenate.easeui.EaseConstant;
import com.jeckliu.framwork.base.BaseActivity;
import com.jeckliu.framwork.permission.IPermission;
import com.jeckliu.framwork.view.DenyPermissionDialog;

import java.util.List;

/***
 * Created by Jeck.Liu on 2017/7/5 0005.
 */

public class ChatActivity extends BaseActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ChatFragment chatFragment = new ChatFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(EaseConstant.EXTRA_CHAT_TYPE,EaseConstant.CHATTYPE_SINGLE);
        bundle.putString(EaseConstant.EXTRA_USER_ID,getIntent().getStringExtra(EaseConstant.EXTRA_USER_ID));
        chatFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.activity_chat_fragment,chatFragment)
                .commit();
        permissionAction.addPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                .requestPermission(new IPermission() {
                    @Override
                    public void done() {

                    }

                    @Override
                    public void unPermission(List<String> denyPermissions) {
                        DenyPermissionDialog.show(ChatActivity.this,denyPermissions);
                    }
                });

    }
}
