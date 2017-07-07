package com.jeckliu.im;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.ui.EaseConversationListFragment;
import com.jeckliu.framwork.view.ToastShow;

/***
 * Created by Jeck.Liu on 2017/7/3 0003.
 */

public class ConversationListFragment extends EaseConversationListFragment {

    @Override
    protected void initView() {
        super.initView();
        titleBar.setRightImageResource(R.drawable.icon_address_book);
        titleBar.setBackgroundColor(getResources().getColor(R.color.common_theme));
        titleBar.setTitle("");
        titleBar.setRightLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(EMClient.getInstance().isLoggedInBefore()){
                    startActivity(new Intent(getContext(),ContactListActivity.class));
                }else {
                    ToastShow.showLongMessage("赶紧去登录吧，就可以获取联系人聊天啦");
                }
            }
        });
        conversationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EMConversation conversation = conversationListView.getItem(position);
                Intent intent = new Intent(getContext(),ChatActivity.class);
                intent.putExtra(EaseConstant.EXTRA_CHAT_TYPE,EaseConstant.CHATTYPE_SINGLE);
                intent.putExtra(EaseConstant.EXTRA_USER_ID,conversation.conversationId());
                startActivity(intent);
            }
        });
    }

}