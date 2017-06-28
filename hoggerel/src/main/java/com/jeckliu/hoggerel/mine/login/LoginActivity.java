package com.jeckliu.hoggerel.mine.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.jeckliu.framwork.base.BaseActivity;
import com.jeckliu.framwork.view.CommonTitleBar;
import com.jeckliu.framwork.view.ToastShow;
import com.jeckliu.hoggerel.R;
import com.jeckliu.hoggerel.mine.register.RegisterActivity;
import com.jeckliu.im.IMHelper;

/***
 * Created by Jeck.Liu on 2017/6/28 0028.
 */

public class LoginActivity extends BaseActivity{
    private EditText etUserName;
    private EditText etPwd;
    private TextView tvLogin;
    private TextView tvRegister;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
    }

    private void initView() {
        new CommonTitleBar(this,"登录");
        etUserName = (EditText) findViewById(R.id.activity_login_user_name);
        etPwd = (EditText) findViewById(R.id.activity_login_pwd);
        tvLogin = (TextView) findViewById(R.id.activity_login_login);
        tvRegister = (TextView) findViewById(R.id.activity_login_register);

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(etUserName.getText().toString())){
                    ToastShow.showLongMessage("用户名不能为空");
                    return;
                }
                if(TextUtils.isEmpty(etPwd.getText().toString())){
                    ToastShow.showLongMessage("密码不能为空");
                    return;
                }
                IMHelper.getInstance().login(LoginActivity.this,etUserName.getText().toString(),etPwd.getText().toString());
            }
        });

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }
}
