package com.jeckliu.im;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.ui.EaseContactListFragment;
import com.hyphenate.exceptions.HyphenateException;
import com.jeckliu.framwork.view.ToastShow;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/***
 * Created by Jeck.Liu on 2017/7/5 0005.
 */

public class ContactListFragment extends EaseContactListFragment {

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 0){
                List<String> contactList = (List<String>) msg.getData().getSerializable("contactList");
                HashMap<String,EaseUser> map = new HashMap<>();
                if (contactList != null) {
                    for(String name : contactList){
                        map.put(name,new EaseUser(name));
                    }
                }
                setContactsMap(map);
                refresh();
            }
        }
    };


    @Override
    protected void setUpView() {
        setContactListItemClickListener(new EaseContactListItemClickListener() {
            @Override
            public void onListItemClicked(EaseUser user) {
                Intent intent = new Intent(getContext(),ChatActivity.class);
                intent.putExtra(EaseConstant.EXTRA_USER_ID,user.getUsername());
                startActivity(intent);
            }
        });
        super.setUpView();
        hideTitleBar();
        initContactList();
    }

    private void initContactList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    List<String> results = EMClient.getInstance().contactManager().getAllContactsFromServer();
                    Message msg = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("contactList", (Serializable) results);
                    msg.setData(bundle);
                    msg.what = 0;
                    handler.sendMessage(msg);
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
