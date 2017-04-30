package cn.xietong.healthysportsexperts.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.db.BmobDB;
import cn.xietong.healthysportsexperts.R;
import cn.xietong.healthysportsexperts.adapter.UserFriendAdapter;
import cn.xietong.healthysportsexperts.app.App;
import cn.xietong.healthysportsexperts.model.MyUser;
import cn.xietong.healthysportsexperts.ui.activity.Activity_Chatting;
import cn.xietong.healthysportsexperts.ui.activity.Activity_NewFriend;
import cn.xietong.healthysportsexperts.utils.CharacterParser;
import cn.xietong.healthysportsexperts.utils.CollectionUtils;


/**当前用户好友
 * Created by Administrator on 2015/10/18.
 */
public class FragmentPageMessage_son2 extends BaseFragment {

    private String TAG = "FragmentPageMessage_son2";
    private NewFriendMessageReceive receive_friend;
    private int new_message = 1;
    public static String New_Message = "MyFriend";//设置广播接收的标签
    private LinearLayout layout_newFriend;
    private ListView listview_user_friend;
    private static boolean exit_message_new_friend = false;
    private ImageView image_new_message;
    private Set<String> set_friend_name = new HashSet<String>();
    private UserFriendAdapter userAdapter;
    //2016.3.29(用于获取用户的好友列表)
    private List<MyUser> friends = new ArrayList<MyUser>();
    private CharacterParser characterParser = new CharacterParser();
    private Map<String,BmobChatUser> users;
    private int SHOW_LISTVIEW = 1;
    private MyHandler myhandler = new MyHandler();
    private Show_logo_Handler logo_handler = new Show_logo_Handler();
    private MyThread myThread = new MyThread();

    @Override
    public int getLayoutId() {
        return R.layout.fragment2_son2;
    }

    @Override
    protected void initViews(View mContentView) {
        initNewFriendBroadcast();
        layout_newFriend = (LinearLayout)findViewById(R.id.layout_new);
        image_new_message = (ImageView)findViewById(R.id.imageView_new_friend_message);
        listview_user_friend = (ListView)findViewById(R.id.lv_contact);
        users = App.getInstance().getContactList();
        if(users!=null){
            filledData(CollectionUtils.map2list(users));
            userAdapter = new UserFriendAdapter(getActivity(), friends);
            listview_user_friend.setAdapter(userAdapter);
            userAdapter.notifyDataSetChanged();
        }
        if(BmobDB.create(getActivity()).hasNewInvite()){
            image_new_message.setVisibility(View.VISIBLE);
            exit_message_new_friend = true;
        }else{
            image_new_message.setVisibility(View.GONE);
            exit_message_new_friend = false;
            //2016.4.6(用封装好的hasNewInvite()来检测出是否存在新消息)
        }
        if(exit_message_new_friend == true){
            image_new_message.setVisibility(View.VISIBLE);
        }else{
            image_new_message.setVisibility(View.GONE);
        }
        initOnclickListener();
        myThread.start();
    }

    @Override
    public void setupViews(Bundle savedInstanceState) {

    }

    /**
     * Magic.2016.4.8
     */
    private void initOnclickListener(){
        layout_newFriend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if(exit_message_new_friend == true){
                    image_new_message.setVisibility(View.GONE);
                    exit_message_new_friend = false;
                }
                Intent intent_add_friend = new Intent(getActivity(),Activity_NewFriend.class);
                intent_add_friend.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent_add_friend);
            }
        });
        listview_user_friend.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                MyUser user_select = (MyUser) userAdapter.getItem(position);
                Intent intent_select = new Intent(getActivity(), Activity_Chatting.class);
                intent_select.putExtra("user", user_select);
                Log.i(TAG, "user_select=" + user_select);
                startActivity(intent_select);
            }
        });
    }
    //2016.3.29(为当前用户填充好友信息)
    private void filledData(List<BmobChatUser> datas) {
        friends.clear();
        int total = datas.size();
//		Log.i(TAG, "filledData - 1");
        for (int i = 0; i < total; i++) {
            BmobChatUser user = datas.get(i);
            MyUser sortModel = new MyUser();
            sortModel.setAvatar(user.getAvatar());
            sortModel.setNick(user.getNick());
            sortModel.setUsername(user.getUsername());
            sortModel.setObjectId(user.getObjectId());
            sortModel.setContacts(user.getContacts());
            // 汉字转换成拼音
            String username = sortModel.getNick();//改变了(获取Nick来排序)
            // 若没有username
//			Log.i(TAG, "filledData - 2");
            if (username != null) {
//				Log.i(TAG, "filledData - 3");
//                Log.i(TAG, "Username="+sortModel.getUsername() + "");
                String pinyin = characterParser.getSelling(sortModel.getNick());//出现空指针(忘记New了)改变了(获取Nick来排序)
                String sortString = pinyin.substring(0, 1).toUpperCase();
                // 正则表达式，判断首字母是否是英文字母
                if (sortString.matches("[A-Z]")) {
                    Log.i(TAG, "UpperCase="+sortString.toUpperCase()+" ");
                    sortModel.setSortLetters(sortString.toUpperCase());
                } else {
                    sortModel.setSortLetters("#");
                }
            } else {
                sortModel.setSortLetters("#");
            }
            friends.add(sortModel);
        }
        // 根据a-z进行排序(排序成功)
        Collections.sort(friends, new Comparator<MyUser>() {
            @Override
            public int compare(MyUser lhs, MyUser rhs) {
                // TODO Auto-generated method stub
                return lhs.getSortLetters().compareToIgnoreCase(rhs.getSortLetters());
            }
        });
    }
    //线程问题，不是每一次都启动
    class MyThread extends Thread{
        @Override
        public void run() {
            // TODO Auto-generated method stub
            super.run();
            try {
                sleep(15);//因为每次启动都是不一样的Application，所以需要时间储存数据重新获取好友列表
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            Message msg_delay = Message.obtain();
            msg_delay.what = SHOW_LISTVIEW;
            myhandler.sendMessage(msg_delay);
            Log.i(TAG, "MyThread");
        }

    }
    //Magic，2016.4.8
    class MyHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            if(msg.what == SHOW_LISTVIEW){
                users = App.getInstance().getContactList();
                Log.i(TAG,"users="+users);
                Log.i(TAG,"App.getInstance().getContactList()"+ App.getInstance().getContactList());
                if(users!=null){
                    filledData(CollectionUtils.map2list(users));//(强转换问题出现在这里)
                    Log.i(TAG, "friends="+friends);
                    listview_user_friend.setAdapter(userAdapter);
                    listview_user_friend.setSelection(listview_user_friend.getCount() + 1);
                    userAdapter.notifyDataSetChanged();
                    Log.i(TAG, "Myhandler");
                }
            }
        }
    }
    //2016.3.25
    private void initNewFriendBroadcast(){
        // 注册接收消息广播
        receive_friend = new NewFriendMessageReceive();
        IntentFilter intentFilter = new IntentFilter(BmobConfig.TAG_ADD_CONTACT);//设置是监听添加好友的信息
        //设置广播的优先级别大于Mainacitivity,这样如果消息来的时候正好在chat页面，直接显示消息，而不是提示消息未读
        intentFilter.setPriority(2);
        intentFilter.addAction(New_Message);
        getActivity().registerReceiver(receive_friend,intentFilter);
   }
    class NewFriendMessageReceive extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            if(intent.getAction().equals(New_Message)){
                String message = intent.getStringExtra("new_add_friend");
                exit_message_new_friend = true;
                new NewMessageThread().start();
            }
        }
    }
    /**
     * 当有消息来的时候就显示有个红点在朋友图标旁边
     * @author 林思旭
     *
     */
    class NewMessageThread extends Thread{
        @Override
        public void run() {
            // TODO Auto-generated method stub
            Message msg = Message.obtain();
            msg.what = new_message;
            logo_handler.sendMessage(msg);
            Log.i(TAG, "NewThread");
            super.run();
        }
    }
    class Show_logo_Handler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            if(msg.what == new_message){
                image_new_message.setVisibility(View.VISIBLE);
                exit_message_new_friend = true;
            }
            Log.i(TAG, "出现吧，哥玛兽");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(receive_friend);
    }
}
