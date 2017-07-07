package com.jeckliu.im;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.widget.EaseContactList;
import com.hyphenate.exceptions.HyphenateException;
import com.jeckliu.framwork.base.BaseActivity;
import com.jeckliu.framwork.view.CommonTitleBar;
import com.jeckliu.framwork.view.ToastShow;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/***
 * Created by Jeck.Liu on 2017/7/4 0004.
 */

public class ContactListActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);
        new CommonTitleBar(this,"联系人");
        getSupportFragmentManager().beginTransaction()
                .add(R.id.activity_contact_list_fragment, new ContactListFragment())
                .commit();
    }
}
