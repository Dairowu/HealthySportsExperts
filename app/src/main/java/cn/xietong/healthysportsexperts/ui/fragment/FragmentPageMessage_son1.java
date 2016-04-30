package cn.xietong.healthysportsexperts.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.bean.BmobRecent;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.db.BmobDB;
import cn.xietong.healthysportsexperts.R;
import cn.xietong.healthysportsexperts.adapter.MessageRecentAdapter;
import cn.xietong.healthysportsexperts.ui.activity.Activity_Chatting;
import cn.xietong.healthysportsexperts.ui.view.dialog.DeleteDialog;

/**发帖界面
 * Created by 林思旭 on 2015/10/18.。。
 */
public class FragmentPageMessage_son1 extends BaseFragment implements AdapterView.OnItemClickListener,AdapterView.OnItemLongClickListener {
    private String TAG = "FragmentPageMessage_son1";
    private DeleteDialog dialog;
    private boolean hidden;
    private ListView listview;
    private MessageRecentAdapter adapter;
    public static String ACTION_INTENT_RECEIVER = "NewMessage";//2016.4.30
    private NewMessageReceiver receiver;//2016.4.30
    @Override
    public int getLayoutId() {
        return R.layout.fragment2_son1;
    }

    @Override
    protected void initViews(View mContentView) {
        init();
    }

    @Override
    public void setupViews(Bundle savedInstanceState) {

    }
    private void init(){
        listview = (ListView) findViewById(R.id.lv_message);
        listview.setOnItemClickListener(this);
        listview.setOnItemLongClickListener(this);
        adapter = new MessageRecentAdapter(getActivity(), R.layout.item_conversation, BmobDB.create(getActivity()).queryRecents());
        Log.i(TAG,"fragment复活");
        listview.setAdapter(adapter);
        initNewMessageBroadCast();//注册广播
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        BmobRecent recent = adapter.getItem(position);
        //重置未读消息
        BmobDB.create(getActivity()).resetUnread(recent.getTargetid());
        //组装聊天对象
        BmobChatUser user = new BmobChatUser();
        user.setAvatar(recent.getAvatar());
        user.setNick(recent.getNick());
        user.setUsername(recent.getUserName());
        user.setObjectId(recent.getTargetid());
        Intent intent = new Intent(getActivity(), Activity_Chatting.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        BmobRecent recent = adapter.getItem(position);
        showDialog(recent,view);
        return true;
    }

    /**
     *
     * @param recent 需要删除的最近聊天对话框
     * @param itemview ListView的Item
     */
    private void showDialog(final BmobRecent recent,final View itemview){
        dialog = new DeleteDialog(getActivity(), new DeleteDialog.DeleteListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case R.id.txt_delete_recent_message:
                        deleteRecent(recent);
                        Toast.makeText(getActivity(),"点击删除",Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        break;
                    case R.id.txt_set_unread_message:
                        TextView txt_unread = (TextView)itemview.findViewById(R.id.tv_recent_unread);
                        Log.i(TAG,"txt_unread="+txt_unread);
                        txt_unread.setVisibility(View.VISIBLE);
                        dialog.dismiss();
                        Toast.makeText(getActivity(),"设置未读",Toast.LENGTH_SHORT).show();
                        break;
                    default:break;
                }
            }
        });
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = 250;
        params.height = 150;
        params.gravity = Gravity.CENTER;
        dialog.getWindow().setAttributes(params);
        dialog.show();
    }
    /** 删除会话
     * deleteRecent
     * @param @param recent
     * @return void
     * @throws
     */
    private void deleteRecent(BmobRecent recent){
        adapter.remove(recent);
        BmobDB.create(getActivity()).deleteRecent(recent.getTargetid());
        BmobDB.create(getActivity()).deleteMessages(recent.getTargetid());
    }
    public void refresh(){
        Log.i(TAG,"refresh");
        try {
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    adapter = new MessageRecentAdapter(getActivity(), R.layout.item_conversation, BmobDB.create(getActivity()).queryRecents());
                    listview.setAdapter(adapter);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        this.hidden = hidden;
        if(!hidden){
            refresh();
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        if(!hidden){
            refresh();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(receiver);
    }

    //2016.4.30林思旭增加刷新功能
    private void initNewMessageBroadCast(){

        // 注册接收消息广播
        receiver = new NewMessageReceiver();
        IntentFilter intentFilter = new IntentFilter(BmobConfig.BROADCAST_NEW_MESSAGE);
        //设置广播的优先级别大于Mainacitivity,这样如果消息来的时候正好在chat页面，直接显示消息，而不是提示消息未读
        intentFilter.setPriority(3);
        intentFilter.addAction(ACTION_INTENT_RECEIVER);
        getActivity().registerReceiver(receiver, intentFilter);
    }
    class NewMessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            if(intent.getAction().equals(ACTION_INTENT_RECEIVER)){
                adapter.clear();
                adapter = new MessageRecentAdapter(getActivity(), R.layout.item_conversation, BmobDB.create(getActivity()).queryRecents());
                listview.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        }
    }
}
