package com.jeckliu.hoggerel;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.jeckliu.framwork.base.BaseActivity;
import com.jeckliu.framwork.base.Configure;
import com.jeckliu.framwork.event.EventLoginSuccess;
import com.jeckliu.framwork.permission.IPermission;
import com.jeckliu.framwork.util.SpUtil;
import com.jeckliu.framwork.view.DenyPermissionDialog;
import com.jeckliu.hoggerel.home.HomeFragment;
import com.jeckliu.hoggerel.mine.MineFragment;
import com.jeckliu.im.IMHelper;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/***
 * Created by Jeck.Liu on 2017/2/14 0014.
 */
public class MainActivity extends BaseActivity implements View.OnClickListener{
    public static final String TAG_HOME_FRAGMENT = "tagHomeFragment";
    public static final String TAG_MINE_FRAGMENT = "tagMineFragment";
    private HomeFragment homeFragment;
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
        TextView tvTabMine = (TextView) findViewById(R.id.activity_main_tab_mine);

        tvTabHome.setOnClickListener(this);
        tvTabMine.setOnClickListener(this);
    }

    private void initFragment() {
        homeFragment = new HomeFragment();
        mineFragment = new MineFragment();
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.activity_main_fragment_container,homeFragment,TAG_HOME_FRAGMENT)
                .add(R.id.activity_main_fragment_container,mineFragment,TAG_MINE_FRAGMENT)
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
                        .hide(mineFragment)
                        .commit();
                break;
            case R.id.activity_main_tab_mine:
                fragmentManager.beginTransaction()
                        .show(mineFragment)
                        .hide(homeFragment)
                        .commit();
                break;
        }
    }

}
