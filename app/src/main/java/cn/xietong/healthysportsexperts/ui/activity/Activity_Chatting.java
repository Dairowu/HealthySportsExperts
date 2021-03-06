package cn.xietong.healthysportsexperts.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobNotifyManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.db.BmobDB;
import cn.bmob.im.inteface.EventListener;
import cn.bmob.v3.listener.PushListener;
import cn.xietong.healthysportsexperts.R;
import cn.xietong.healthysportsexperts.adapter.GridViewAdapter;
import cn.xietong.healthysportsexperts.adapter.MessageChatAdapter;
import cn.xietong.healthysportsexperts.adapter.ViewPagerAdapter;
import cn.xietong.healthysportsexperts.receiver.MyNewMessageReceiver;
import cn.xietong.healthysportsexperts.ui.view.EmoticonsEditText;
import cn.xietong.healthysportsexperts.ui.view.dialog.DialogTips;
import cn.xietong.healthysportsexperts.utils.CommonUtils;
import cn.xietong.healthysportsexperts.utils.FaceText;
import cn.xietong.healthysportsexperts.utils.FaceTextUtils;

/**
 * Created by 林思旭 on 2016/4/9.。。
 */
public class Activity_Chatting extends BaseActivity implements View.OnClickListener,EventListener {
    private static String TAG = "Activity_Chatting";
    //
    private ImageView imageView_face;
    private boolean showLayout = false,KeyShow = false;
    private int lastX,lastY,offsetX,offsetY;//3.4
    private MyHandler myHandler = new MyHandler();
    private int SHOW = 1;
    //11.22
    private List<FaceText> faceList;
    private EmoticonsEditText et_msg;
    private Button btn_send;
    private ListView listView_msg;
    private ViewPager viewpager;
    private View view_button;
    //4.17
    private SwipeRefreshLayout myRefreshLayout;
    String targetId = "";
    String targetName = "";
    String targetNick = "";
    String current_name;
    String current_targetId = "";
    BmobChatUser targetUser;
    BmobChatManager manager;
    private static int MsgPagerNum  = 0;//2016.4.15

    //2016.4.29
    private MessageChatAdapter mAdapter;
    public static final int NEW_MESSAGE = 0x001;// 收到消息
    //2016.5.13
    private String avatar = null;
//    private FaceReplace myReplace = new FaceReplace();
    @Override
    public int getLayoutId() {
        return R.layout.activity_chatting;
    }

    @Override
    public void initViews() {
        manager = BmobChatManager.getInstance(this);
        //注册广播接收器
//        initMyNewMessageBroadCast();
        myRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.refresh_history_content);
        myRefreshLayout.setColorSchemeResources(R.color.main_bg_color, R.color.ring_color,
                R.color.ring_text_color , R.color.sel_color);//2016.4.7(设置旋转刷新颜色)
        view_button = (LinearLayout) findViewById(R.id.include_button);
        et_msg = (EmoticonsEditText)view_button.findViewById(R.id.et_input_context);
        btn_send = (Button)view_button.findViewById(R.id.btn_send);
        listView_msg = (ListView)findViewById(R.id.listView);

        imageView_face = (ImageView)view_button.findViewById(R.id.imageView_sendImageView);//11.22
        viewpager = (ViewPager) findViewById(R.id.viewPager_speak_face);
        //组装聊天对象viewpager
        targetUser = (BmobChatUser) getIntent().getSerializableExtra("user");
        Log.i(TAG, "targetUser_avart="+targetUser.getAvatar());
        targetId = targetUser.getObjectId();
        targetName = targetUser.getUsername();
        targetNick = targetUser.getNick();
        initTopbarForLeft(targetNick,new OnTopbarButtonClickListener());
        //获取登录用户的信息
//        current_Nick = BmobChatUser.getCurrentUser(this).get;
        current_name = BmobChatUser.getCurrentUser(this).getUsername();
        current_targetId = BmobChatUser.getCurrentUser(this).getObjectId();
        if(mApplication.getContactList()!=null){
            avatar = mApplication.getContactList().get(targetUser.getUsername()).getAvatar();//2016.5.13
        }
        initFace();//11.23+
        //2016.4.29
        initOrRefresh();
        listView_msg.setSelection(mAdapter.getCount() - 1);
        initOnClickListener();//监听普通的按钮点击
    }
    //按钮的监听
    private void initOnClickListener(){
        //2016.4.17,下拉刷新
        myRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        MsgPagerNum ++;
                        int total = BmobDB.create(Activity_Chatting.this).queryChatTotalCount(targetId);
                        int currents = mAdapter.getCount();
                        if (total <= currents) {
                            Toast.makeText(Activity_Chatting.this,"消息已全部加载完成!",Toast.LENGTH_SHORT).show();
                        } else {
                            List<BmobMsg> msgList = initMsgData();
                            mAdapter.setList(msgList);
                            mAdapter.notifyDataSetChanged();
                            listView_msg.setSelection(mAdapter.getCount() - currents - 1);
                        }
                        myRefreshLayout.setRefreshing(false);
                    }
                },4000);//刷新4秒
            }
        });

        imageView_face.setOnClickListener(this);
        et_msg.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                if(event.getAction() == MotionEvent.ACTION_OUTSIDE){
                    //其他要实现的功能。
                    et_msg.setFocusable(false);
                }else if(event.getAction() == MotionEvent.ACTION_DOWN){
                    KeyShow = true;
                }else{
                    if(showLayout == true){
                        imageView_face.setSelected(false);
                        viewpager.setVisibility(View.GONE);
                        showLayout = false;
                        KeyShow = true;
                    }
                }
                return false;
            }
        });

        btn_send.setOnClickListener(this);

        listView_msg.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                //点击ListView键盘和表情都消失
                int rawX = (int)event.getRawX();
                int rawY = (int)event.getRawY();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN :
                        lastX = rawX;
                        lastY = rawY;
                        break;

                    case MotionEvent.ACTION_UP:
                        offsetX = rawX - lastX;
                        offsetY = rawY - lastY;
                        break;
                }
                if(offsetY > 0){
                    hideSoftInputView();
                }
                if(offsetY == 0){
                    hideSoftInputView();
                }
                if(offsetY < 0 && listView_msg.getLastVisiblePosition() == mAdapter.getCount() - 1){
                    showSoftInputView();
                    viewpager.setVisibility(View.GONE);
                    viewpager.setVisibility(View.GONE);
                    showLayout = false;
                }
                return onTouchEvent(event);
            }
    });
        //2016.4.29
        // 重发按钮的点击事件
        mAdapter.setOnInViewClickListener(R.id.iv_fail_resend,
                new MessageChatAdapter.onInternalClickListener() {
                    @Override
                    public void OnClickListener(View parentV, View v,
                                                Integer position, Object values) {
                        // 重发消息
                        showResendDialog(parentV, v, values);
                    }
                });
    }
    @Override
    public void setupViews() {

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send:{
                final String msg = et_msg.getText().toString();
                boolean isNetConnected = CommonUtils.isNetworkAvailable(this);
                if (msg.equals("")) {
                    Toast.makeText(this,"请输入发送消息!",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!isNetConnected) {
                    Toast.makeText(this,"请检查你的网络",Toast.LENGTH_SHORT).show();
                    // return;
                }
                // 组装BmobMessage对象
                BmobMsg message = BmobMsg.createTextSendMsg(this, targetId, msg);
                message.setBelongId(current_targetId);
//                message.setExtra("Bmob");
                // 默认发送完成，将数据保存到本地消息表和最近会话表中
                manager.sendTextMessage(targetUser, message);
                // 刷新界面
                refreshMessage(message);
                break;
            }
            //11.23(点击表情事件)
            case R.id.imageView_sendImageView :{
                if (showLayout == false) {
                    //2016.3.19(成功)
                    if(KeyShow == true){
                        hideSoftInputView();
                        new MyThread().start();
                        KeyShow = false;
                    }else{
                        imageView_face.setSelected(true);//2016.4.22
                        viewpager.setVisibility(View.VISIBLE);
                    }
                    showLayout = true;
                } else {
                    imageView_face.setSelected(false);//2016.4.22
                    viewpager.setVisibility(View.GONE);
                    showLayout = false;
                    KeyShow = true;
                    showSoftInputView();
                }
                break;
            }
            default:
                break;
        }
    }

    //11.23
    /**
     * 初始化表情界面
     */
    private void initFace() {
        faceList = FaceTextUtils.faceTexts;
        List<View> views = new ArrayList<View>();
        // 添加每个View的内容
        for (int i = 0; i < 2; i++) {
            views.add(getGridView(i));
        }
        ViewPagerAdapter Viewadapter = new ViewPagerAdapter(
                Activity_Chatting.this, views);
        viewpager.setAdapter(Viewadapter);
    }
    /**
     *
     * @param index：表情页面的页数
     * @return
     */
    private View getGridView(int index){
        View faceView = View.inflate(this, R.layout.include_emo_gridview, null);
        GridView gridview = (GridView)faceView.findViewById(R.id.gridview);
        List<FaceText> list = new ArrayList<FaceText>();
        //区分两个版
        if(index == 0){
            list.addAll(faceList.subList(0, 21));
        }else{
            list.addAll(faceList.subList(21 , faceList.size()));
        }
        final GridViewAdapter gridAdapter = new GridViewAdapter(this, list);
        gridview.setAdapter(gridAdapter);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                FaceText faceText = (FaceText) gridAdapter.getItem(position);
                String key = faceText.text.toString();
                try {
                    if (et_msg != null && !TextUtils.isEmpty(key)) {
                        //插入特殊的字符串
                        int start = et_msg.getSelectionStart();//可以试试End结尾的
                        CharSequence content = et_msg.getText().insert(start, key);
                        et_msg.setText(content);
                        //定位光标的位置
                        CharSequence info = et_msg.getText();
                        if (info instanceof Spannable) {
                            Spannable spanText = (Spannable) info;
                            Selection.setSelection(spanText, start + key.length());
                        }
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                }
            }
        });
        return faceView;

    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        finish();
    }
    /**
     * 隐藏软键盘
     */
    public void hideSoftInputView() {
//        imageView_face.setSelected(false);
        InputMethodManager manager = ((InputMethodManager) this
                .getSystemService(Activity.INPUT_METHOD_SERVICE));
        if (Activity_Chatting.this.getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (Activity_Chatting.this.getCurrentFocus() != null)
                manager.hideSoftInputFromWindow(Activity_Chatting.this
                                .getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 显示软键盘
     */
    public void showSoftInputView() {
        imageView_face.setSelected(false);
        if (Activity_Chatting.this.getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE) {
            if (Activity_Chatting.this.getCurrentFocus() != null)
                ((InputMethodManager) (Activity_Chatting.this)
                        .getSystemService(Activity.INPUT_METHOD_SERVICE))
                        .showSoftInput(et_msg, 0);
        }
    }
    @Override
    public void onBackPressed() {
        if (viewpager.getVisibility() == View.VISIBLE) {
            viewpager.setVisibility(View.GONE);
            return;
        }else{
            finish();
        }
        super.onBackPressed();
    }
    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            if(msg.what == SHOW){
                imageView_face.setSelected(true);
                viewpager.setVisibility(View.VISIBLE);
            }
        }
    }
    class MyThread extends Thread{
        @Override
        public void run() {
            // TODO Auto-generated method stub
            super.run();
            try {
                sleep(60);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            Message message = Message.obtain();
            message.what = SHOW;
            myHandler.sendMessage(message);
        }
    }
    //2016.4.29
    /**
     * 刷新界面
     * @Title: refreshMessage
     * @Description: TODO
     * @param @param message
     * @return void
     * @throws
     */
    private void refreshMessage(BmobMsg msg) {
        // 更新界面
        mAdapter.add(msg);
        listView_msg.setSelection(mAdapter.getCount() - 1);
        et_msg.setText("");
    }

    /**
     * 界面刷新
     * @Title: initOrRefresh
     * @Description: TODO
     * @param
     * @return void
     * @throws
     */
    private void initOrRefresh() {
        if (mAdapter != null) {
            if (MyNewMessageReceiver.mNewNum != 0) {// 用于更新当在聊天界面锁屏期间来了消息，这时再回到聊天页面的时候需要显示新来的消息
                int news=  MyNewMessageReceiver.mNewNum;//有可能锁屏期间，来了N条消息,因此需要倒叙显示在界面上
                int size = initMsgData().size();
                for(int i=(news-1);i >= 0;i--){ //改成i>0而不是i>=0，如果是i>=0就会导致接受时候会多显示一次接受的内容
                    mAdapter.add(initMsgData().get(size-(i + 1)));// 添加最后一条消息到界面显示
                }
                listView_msg.setSelection(mAdapter.getCount() - 1);
            } else {
                mAdapter.notifyDataSetChanged();
            }
        } else {
            mAdapter = new MessageChatAdapter(this, initMsgData());
            listView_msg.setAdapter(mAdapter);
        }
    }
    /**
     * 加载消息历史，从数据库中读出
     */
    private List<BmobMsg> initMsgData() {
//        String avatar = targetUser.getAvatar();
        List<BmobMsg> list = BmobDB.create(this).queryMessages(targetId,MsgPagerNum);
            for(int i = 0 ; i < list.size(); i++) {
                if (list.get(i).getBelongId().equals(targetId)) {
                    if (list.get(i).getBelongAvatar().equals(avatar)) {
                        //如果头像相同就不需要重新设置过
                    } else {
                        list.get(i).setBelongAvatar(avatar);
                    }
                }else if(list.get(i).getBelongId().equals(current_targetId)){
                    list.get(i).setBelongAvatar(mApplication.getSharedPreferencesUtil().getAvatarUrl());
                }
            }
        return list;
    }
    /**
     * 显示重发按钮 showResendDialog
     * @Title: showResendDialog
     * @Description: TODO
     * @param @param recent
     * @return void
     * @throws
     */
    public void showResendDialog(final View parentV, View v, final Object values) {
        DialogTips dialog = new DialogTips(this, "确定重发该消息", "确定", "取消", "提示",
                true);
        // 设置成功事件
        dialog.SetOnSuccessListener(new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int userId) {
                if (((BmobMsg) values).getMsgType() == BmobConfig.TYPE_IMAGE
                        || ((BmobMsg) values).getMsgType() == BmobConfig.TYPE_VOICE) {// 图片和语音类型的采用
//                    resendFileMsg(parentV, values);
                } else {
                    resendTextMsg(parentV, values);
                }
                dialogInterface.dismiss();
            }
        });
        // 显示确认对话框
        dialog.show();
        dialog = null;
    }
    /**
     * 重发文本消息
     */
    private void resendTextMsg(final View parentV, final Object values) {
        BmobChatManager.getInstance(Activity_Chatting.this).resendTextMessage(
                targetUser, (BmobMsg) values, new PushListener() {

                    @Override
                    public void onSuccess() {
                        // TODO Auto-generated method stub
                        ((BmobMsg) values)
                                .setStatus(BmobConfig.STATUS_SEND_SUCCESS);
                        parentV.findViewById(R.id.progress_load).setVisibility(
                                View.INVISIBLE);
                        parentV.findViewById(R.id.iv_fail_resend)
                                .setVisibility(View.INVISIBLE);
                        parentV.findViewById(R.id.tv_send_status)
                                .setVisibility(View.VISIBLE);
                        ((TextView) parentV.findViewById(R.id.tv_send_status))
                                .setText("已发送");
                    }

                    @Override
                    public void onFailure(int arg0, String arg1) {
                        // TODO Auto-generated method stub
                        ((BmobMsg) values)
                                .setStatus(BmobConfig.STATUS_SEND_FAIL);
                        parentV.findViewById(R.id.progress_load).setVisibility(
                                View.INVISIBLE);
                        parentV.findViewById(R.id.iv_fail_resend)
                                .setVisibility(View.VISIBLE);
                        parentV.findViewById(R.id.tv_send_status)
                                .setVisibility(View.INVISIBLE);
                    }
                });
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 当有新消息来的时候，都会自动接受到message,与上面广播的作用是一样的，我注释掉
     * @param message
     */
    @Override
    public void onMessage(BmobMsg message) {
        // TODO Auto-generated method stub
        message.update(this);
        Message handlerMsg = handler.obtainMessage(NEW_MESSAGE);
        handlerMsg.obj = message;
        handler.sendMessage(handlerMsg);
    }
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == NEW_MESSAGE) {
                BmobMsg message = (BmobMsg) msg.obj;
                String uid = message.getBelongId();
                BmobMsg m = BmobChatManager.getInstance(Activity_Chatting.this).getMessage(message.getConversationId(), message.getMsgTime());
                if (!uid.equals(targetId))// 如果不是当前正在聊天对象的消息，不处理
                    return;
                if( m.getBelongId().equals(m.getToId()) ){
                    m.setBelongId(m.getBelongId()+"test");
                }
                m.setBelongAvatar(avatar);
                mAdapter.add(m);
                // 定位
                listView_msg.setSelection(mAdapter.getCount() - 1);
                //取消当前聊天对象的未读标示
                BmobDB.create(Activity_Chatting.this).resetUnread(targetId);
            }
        }
    };

    @Override
    public void onReaded(String s, String s1) {

    }

    @Override
    public void onNetChange(boolean isNetConnected) {
        if (!isNetConnected) {
            Toast.makeText(this, "当前网络不可用,请检查您的网络!",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onAddUser(BmobInvitation bmobInvitation) {

    }

    @Override
    public void onOffline() {
        showOfflineDialog(this);
    }
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        // 新消息到达，重新刷新界面
//        initOrRefresh();
        MyNewMessageReceiver.ehList.add(this);// 监听推送的消息
        // 有可能锁屏期间，在聊天界面出现通知栏，这时候需要清除通知和清空未读消息数
        BmobNotifyManager.getInstance(this).cancelNotify();
        BmobDB.create(this).resetUnread(targetId);
        //清空消息未读数-这个要在刷新之后
        MyNewMessageReceiver.mNewNum=0;
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyNewMessageReceiver.ehList.remove(this);// 监听推送的消息
    }
}
