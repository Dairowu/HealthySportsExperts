package cn.xietong.healthysportsexperts.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import java.util.HashMap;
import java.util.Map;

import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.db.BmobDB;
import cn.xietong.healthysportsexperts.R;
import cn.xietong.healthysportsexperts.adapter.NewFriendAdapter;
import cn.xietong.healthysportsexperts.model.MyUser;
import cn.xietong.healthysportsexperts.ui.view.dialog.DialogTips;

/**
 * Created by Magic on 2016/4/8.
 */
public class Activity_NewFriend extends BaseActivity implements AdapterView.OnItemLongClickListener {
    private String TAG = "AddFriendActivity";
    private ListView listView_friend;
    private NewFriendAdapter adapter;
    private EditText edit_searchView_friend;
    private MyUser User_name;
    private String userName;
    private String userId;
    private View include_view;
    private NewFriendReceive receive_friend;
    public static String NEW_FRIEND_RECEIVE = "Friend_Message";//设置广播接收的标签+
    private Map<String,String> Map_request = new HashMap<String, String>();
    @Override
    public int getLayoutId() {
        return R.layout.activity_friend;
    }

    @Override
    public void initViews() {
        listView_friend = (ListView)findViewById(R.id.listView_receive_friend_request);
        include_view = (View)findViewById(R.id.include_newFriendActivity_head);
        edit_searchView_friend = (EditText)include_view.findViewById(R.id.edit_addFriendActivity_friends);
        adapter = new NewFriendAdapter(this, BmobDB.create(this).queryBmobInviteList());
        adapter.notifyDataSetChanged();
        listView_friend.setAdapter(adapter);
        for(int i = 0;i < listView_friend.getCount();i++){
            BmobInvitation all_invite = (BmobInvitation) adapter.getItem(i);
            Map_request.put(all_invite.getFromname(), all_invite.getFromname());
        }
        initAddFriendReceive();
        initOnclickListener();

    }

    @Override
    public void setupViews() {

    }

    /**
     * 监听点击事件
     */
    private void initOnclickListener(){
//        edit_searchView_friend.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                // TODO Auto-generated method stub
//                Intent intent_searchNewFriend_activity = new Intent(AddFriendActivity.this,SeachNewFriendActivity.class);
//                startActivity(intent_searchNewFriend_activity);
//            }
//        });
        include_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        listView_friend.setOnItemLongClickListener(this);
        listView_friend.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub

            }
        });
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        BmobInvitation invite = (BmobInvitation) adapter.getItem(position);
        showDeleteDialog(position, invite);
        return true;
    }
    public void showDeleteDialog(final int position,final BmobInvitation invite) {
        DialogTips dialog = new DialogTips(this,invite.getFromname(),"删除好友请求", "确定",true,true);
        // 设置成功事件
        dialog.SetOnSuccessListener(new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int userId) {
                deleteInvite(position,invite);
            }
        });
        // 显示确认对话框
        dialog.show();
        dialog = null;
    }
    /**
     * 删除请求
     * deleteRecent
     * @param @param recent
     * @return void
     * @throws
     */
    private void deleteInvite(int position, BmobInvitation invite){
        adapter.remove(position);
        Map_request.remove(invite.getFromname());
        BmobDB.create(this).deleteInviteMsg(invite.getFromid(), Long.toString(invite.getTime()));
    }
    //2016.3.25
    private void initAddFriendReceive(){
        // 注册接收消息广播
        receive_friend = new NewFriendReceive();
        IntentFilter intentFilter = new IntentFilter(BmobConfig.TAG_ADD_CONTACT);//设置是监听添加好友的信息
        //设置广播的优先级别大于Mainacitivity,这样如果消息来的时候正好在chat页面，直接显示消息，而不是提示消息未读
        intentFilter.setPriority(2);
        intentFilter.addAction(NEW_FRIEND_RECEIVE);
        registerReceiver(receive_friend, intentFilter);
    }
    class NewFriendReceive extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            if(intent.getAction().equals(NEW_FRIEND_RECEIVE)){
                String message = intent.getStringExtra("new_friend");
                BmobInvitation New_message =BmobInvitation.createReceiverInvitation(message);
                Log.i(TAG, "New_message=" + "New_message");
                if(! (Map_request.containsKey(New_message.getFromname())) ){//如果存在相同的返回true,不相同则添加
                    Map_request.put(New_message.getFromname(),New_message.getFromname());
                    adapter.add(New_message);
                    adapter.notifyDataSetChanged();
                    listView_friend.setSelection(listView_friend.getCount() - 1);
                    Log.i(TAG, New_message.getFromname()+"我是添加好友");
                }
            }
        }
    }
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        unregisterReceiver(receive_friend);
    }
}
