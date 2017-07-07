package com.jeckliu.hoggerel;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.TextView;

import com.jeckliu.framwork.base.BaseActivity;
import com.jeckliu.framwork.permission.IPermission;
import com.jeckliu.framwork.view.DenyPermissionDialog;
import com.jeckliu.hoggerel.home.HomeFragment;
import com.jeckliu.hoggerel.mine.MineFragment;
import com.jeckliu.im.ConversationListFragment;

import java.util.List;

/***
 * Created by Jeck.Liu on 2017/2/14 0014.
 */
public class MainActivity extends BaseActivity implements View.OnClickListener{
    public static final String TAG_HOME_FRAGMENT = "tagHomeFragment";
    public static final String TAG_CHAT_FRAGMENT = "tagChatFragment";
    public static final String TAG_MINE_FRAGMENT = "tagMineFragment";
    private HomeFragment homeFragment;
    private ConversationListFragment chatFragment;
    private MineFragment mineFragment;
    private FragmentManager fragmentManager;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        initFragment();

    }

    private void initView() {
        permissionAction.addPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .requestPermission(new IPermission() {
                    @Override
                    public void done() {

                    }

                    @Override
                    public void unPermission(List<String> denyPermissions) {
                        DenyPermissionDialog.show(MainActivity.this,denyPermissions);
                    }
                });

        TextView tvTabHome = (TextView) findViewById(R.id.activity_main_tab_home);
        TextView tvChat = (TextView) findViewById(R.id.activity_main_tab_chat);
        TextView tvTabMine = (TextView) findViewById(R.id.activity_main_tab_mine);

        tvTabHome.setOnClickListener(this);
        tvChat.setOnClickListener(this);
        tvTabMine.setOnClickListener(this);
    }

    private void initFragment() {
        homeFragment = new HomeFragment();
        chatFragment = new ConversationListFragment();
        mineFragment = new MineFragment();
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.activity_main_fragment_container,homeFragment,TAG_HOME_FRAGMENT)
                .add(R.id.activity_main_fragment_container,chatFragment,TAG_CHAT_FRAGMENT)
                .add(R.id.activity_main_fragment_container,mineFragment,TAG_MINE_FRAGMENT)
                .hide(chatFragment)
                .hide(mineFragment)
                .show(homeFragment)
                .commit();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.activity_main_tab_home:
                fragmentManager.beginTransaction()
                        .show(homeFragment)
                        .hide(chatFragment)
                        .hide(mineFragment)
                        .commit();
                break;
            case R.id.activity_main_tab_chat:
                fragmentManager.beginTransaction()
                        .show(chatFragment)
                        .hide(homeFragment)
                        .hide(mineFragment)
                        .commit();
                break;
            case R.id.activity_main_tab_mine:
                fragmentManager.beginTransaction()
                        .show(mineFragment)
                        .hide(homeFragment)
                        .hide(chatFragment)
                        .commit();
                break;
        }
    }

}
