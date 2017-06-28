package com.jeckliu.hoggerel.mine.register;

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
import com.jeckliu.im.IMHelper;

/***
 * Created by Jeck.Liu on 2017/6/28 0028.
 */

public class RegisterActivity extends BaseActivity{
    private EditText etUserName;
    private EditText etPwd;
    private EditText etPwdAgain;
    private TextView tvRegister;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
    }

    private void initView() {
        new CommonTitleBar(this,"注册");
        etUserName = (EditText) findViewById(R.id.activity_register_user_name);
        etPwd = (EditText) findViewById(R.id.activity_register_pwd);
        etPwdAgain = (EditText) findViewById(R.id.activity_register_pwd_again);
        tvRegister = (TextView) findViewById(R.id.activity_register_register);

        tvRegister.setOnClickListener(new View.OnClickListener() {
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
                if(!etPwd.getText().toString().equals(etPwdAgain.getText().toString())){
                    ToastShow.showLongMessage("两次输入密码不一致");
                    return;
                }
                IMHelper.getInstance().register(RegisterActivity.this,etUserName.getText().toString(),etPwd.getText().toString());
            }
        });
    }
}
