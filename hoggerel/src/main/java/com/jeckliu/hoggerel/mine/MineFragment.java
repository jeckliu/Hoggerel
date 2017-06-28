package com.jeckliu.hoggerel.mine;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.jeckliu.framwork.base.BaseFragment;
import com.jeckliu.framwork.base.Configure;
import com.jeckliu.framwork.event.EventLoginSuccess;
import com.jeckliu.framwork.util.SpUtil;
import com.jeckliu.hoggerel.R;
import com.jeckliu.hoggerel.mine.login.LoginActivity;
import com.jeckliu.im.IMHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/***
 * Created by Jeck.Liu on 2017/6/6 0006.
 */

public class MineFragment extends BaseFragment {
    private TextView tvLogin;
    private RelativeLayout rlUser;
    private ImageView ivUserHead;
    private TextView tvUserName;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mine,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvLogin = (TextView) view.findViewById(R.id.fragment_mine_login);
        rlUser = (RelativeLayout) view.findViewById(R.id.fragment_mine_user);
        ivUserHead = (ImageView) view.findViewById(R.id.fragment_mine_user_head);
        tvUserName = (TextView) view.findViewById(R.id.fragment_mine_user_name);
        initLogin();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), LoginActivity.class));
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventLoginSuccess(EventLoginSuccess event){
        tvLogin.setVisibility(View.GONE);
        rlUser.setVisibility(View.VISIBLE);
        tvUserName.setText((String) SpUtil.getInstance().get(Configure.STATIC_USER_NAME,""));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void initLogin() {
        String userName = (String) SpUtil.getInstance().get(Configure.STATIC_USER_NAME,"");
        String pwd = (String) SpUtil.getInstance().get(Configure.STATIC_USER_PWD,"");
        if(EMClient.getInstance().isLoggedInBefore()){
            EMClient.getInstance().chatManager().loadAllConversations();
            EMClient.getInstance().groupManager().loadAllGroups();
            EventBus.getDefault().post(new EventLoginSuccess());
        }else if(!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(pwd)){
            IMHelper.getInstance().login(getContext(),userName,pwd);
        }
    }

}
