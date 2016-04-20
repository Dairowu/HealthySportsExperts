package cn.xietong.healthysportsexperts.ui.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.im.BmobChatManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.db.BmobDB;
import cn.xietong.healthysportsexperts.R;
import cn.xietong.healthysportsexperts.adapter.ChatAdapter;
import cn.xietong.healthysportsexperts.adapter.GridViewAdapter;
import cn.xietong.healthysportsexperts.adapter.ViewPagerAdapter;
import cn.xietong.healthysportsexperts.utils.ChatData;
import cn.xietong.healthysportsexperts.utils.FaceText;
import cn.xietong.healthysportsexperts.utils.FaceTextUtils;
import cn.xietong.healthysportsexperts.utils.MessageJson;

/**
 * Created by 林思旭 on 2016/4/9.。。
 */
public class Activity_Chatting extends BaseActivity implements View.OnClickListener {
    private static String TAG = "Activity_Chatting";
    //
    private ImageView imageView_face;
    private RelativeLayout Layout_send_face;
    private boolean showLayout = false,KeyShow = false;
    private int lastX,lastY,offsetX,offsetY;//3.4
    private MyHandler myHandler = new MyHandler();
    private int SHOW = 1;
    //11.22
    private List<FaceText> faceList;
    private EditText et_msg;
    private Button btn_send;
    private ListView listView_msg;
    private List<ChatData> DataList ;
    private ViewPager viewpager;
    //11.23
    private ChatAdapter adapter;
    private NewMessageReceiver receiver;
    private View view;
    //4.17
    private SwipeRefreshLayout myRefreshLayout;
    String msg = "";
    String targetId = "";
    String targetName = "";
    String targetNick = "";
    String current_name;
    String current_targetId = "";
    String  current_Nick = "我";
    BmobChatUser targetUser;
    BmobChatManager manager;
    private MessageJson messageJson;
    private static int MsgPagerNum = 5;//2016.4.15
    private int refreshNumber = 0;
    private List<String> current_history_time,target_history_time;//储存时间
    private List<String> current_history_content,target_history_content;//储存内容
    public static String ACTION_INTENT_RECEIVER = "NewMessage";

    @Override
    public int getLayoutId() {
        return R.layout.activity_chatting;
    }

    @Override
    public void initViews() {
        manager = BmobChatManager.getInstance(this);
        //注册接受广播
        initNewMessageBroadCast();
        //加载历史消息的List数组
        current_history_content = new ArrayList<String>();
        target_history_content = new ArrayList<String>();
        current_history_time = new ArrayList<String>();
        target_history_time = new ArrayList<String>();

        DataList = new ArrayList<ChatData>();
        adapter = new ChatAdapter(Activity_Chatting.this, R.layout.activity_chatting_listview_item, DataList);

        myRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.refresh_history_content);
        myRefreshLayout.setColorSchemeResources(R.color.main_bg_color, R.color.ring_color,
                R.color.ring_text_color , R.color.sel_color);//2016.4.7(设置旋转刷新颜色)
        view = (View)findViewById(R.id.include_button);
        et_msg = (EditText)view.findViewById(R.id.et_input_context);
        btn_send = (Button)view.findViewById(R.id.btn_send);
        listView_msg = (ListView)findViewById(R.id.listView);

        imageView_face = (ImageView)view.findViewById(R.id.imageView_sendImageView);//11.22
        Layout_send_face = (RelativeLayout)findViewById(R.id.RelativeLayout_bmob_speak_face);//11.22
        viewpager = (ViewPager) findViewById(R.id.viewPager_speak_face);
        //组装聊天对象
        targetUser = (BmobChatUser) getIntent().getSerializableExtra("user");
        Log.i(TAG, "targetUser="+targetUser);
        Log.i(TAG, targetUser.getUsername()+"");
        targetId = targetUser.getObjectId();
        targetName = targetUser.getUsername();
        targetNick = targetUser.getNick();
        //获取登录用户的信息
//        current_Nick = BmobChatUser.getCurrentUser(this).get;
        current_name = BmobChatUser.getCurrentUser(this).getUsername();
        Log.i(TAG, BmobChatUser.getCurrentUser(this).getUsername());
        current_targetId = BmobChatUser.getCurrentUser(this).getObjectId();
        Log.i(TAG, targetId+"");
        initFace();//11.23+
        initOnClickListener();//监听普通的按钮点击
//        loadHistoryContent();//加载历史消息(这里还是出现长度问题)
    }
    //按钮的监听
    private void initOnClickListener(){
        //2016.4.17,下拉刷新
        myRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                clearList();//清空数组
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshNumber += 5;
                        loadHistoryContent();
                        myRefreshLayout.setRefreshing(false);
                    }
                },4000);//刷新2秒
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
                    Log.i(TAG, "收起键盘");
                }else if(event.getAction() == MotionEvent.ACTION_DOWN){
                    KeyShow = true;
                }else{
                    if(showLayout == true){
                        Log.i(TAG,"收起来");
                        Layout_send_face.setVisibility(View.GONE);
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
                    Log.i(TAG, ">0" + " " + offsetY + " " + rawY + " " + lastY);
                }
                if(offsetY == 0){
                    Layout_send_face.setVisibility(View.GONE);
                    hideSoftInputView();
                }
                if(offsetY < 0){
                    showSoftInputView();
                    Layout_send_face.setVisibility(View.GONE);
                    showLayout = false;
                    Log.i(TAG, "<0"+offsetY+" "+rawY+" "+lastY);
                }
                return onTouchEvent(event);
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
                msg = et_msg.getText().toString();
                BmobMsg message = BmobMsg.createTextSendMsg(this, targetId, msg);
                initChaList(msg, current_Nick,true); //true为自己发送消息的文本识别
                et_msg.setText("");
                manager.sendTextMessage(targetUser, message);
                Log.i(TAG,"成功发送消息给对方");
                break;
            }
            //11.23(点击表情事件)
            case R.id.imageView_sendImageView :{
                Log.i(TAG, "点击表情");
//			Toast.makeText(this, "imageView", Toast.LENGTH_LONG).show();
                if (showLayout == false) {
                    //2016.3.19(成功)
                    if(KeyShow == true){
                        hideSoftInputView();
                        new MyThread().start();
                        Log.i(TAG, "KeyShow=="+KeyShow+"");
                        KeyShow = false;
                    }else{
                        Layout_send_face.setVisibility(View.VISIBLE);
                    }
//
                    showLayout = true;
                    Log.i(TAG, "Visible");
                } else {
                    Layout_send_face.setVisibility(View.GONE);
                    showLayout = false;
                    KeyShow = true;
                    showSoftInputView();
                    Log.i(TAG, "Gone");
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
        Log.i(TAG, "initFace");
        // 添加每个View的内容
        for (int i = 0; i < 2; i++) {
            views.add(getGridView(i));
        }
        ViewPagerAdapter Viewadapter = new ViewPagerAdapter(
                Activity_Chatting.this, views);
        Log.i(TAG, Viewadapter+"");
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
        Log.i(TAG, "getGridView");
        List<FaceText> list = new ArrayList<FaceText>();
        //区分两个版
        if(index == 0){
            list.addAll(faceList.subList(0, 21));
        }else{
            list.addAll(faceList.subList(21 , faceList.size()));
        }
        Log.i(TAG, "list:" + list.size());
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
    //11.23
    /**
     *
     * @param content 聊天的内容
     * @param chatname 聊天对象名字
     * @param flag  判断是否是当前聊天对象的信息,false为接受到对方信息，true为自己发信息
     */

    private void initChaList(String content , String chatname , Boolean flag){
        ChatData myChat = new ChatData(content,chatname,flag);
        DataList.add(myChat);
        adapter.notifyDataSetChanged();
        listView_msg.setAdapter(adapter);
        listView_msg.setSelection(listView_msg.getCount()-1);
        Log.i(TAG, "initChatList");
    }

    /**
     *
     * @param Json //解析接受到的数据
     * @return  //返回接收到并且解析好的数据
     */
    private  MessageJson parseMessage(String Json){
        MessageJson messageJson = new MessageJson();
        try {
            JSONObject myJson = new JSONObject(Json);
            messageJson.setFt(myJson.getString("ft"));
            messageJson.setFid(myJson.getString("fId"));
            messageJson.setMc(myJson.getString("mc"));
//			messageJson.setMc(replace(myJson.getString("mc")));
            messageJson.settId(myJson.getString("tId"));
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return messageJson;
    }

    class NewMessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            if(intent.getAction().equals(ACTION_INTENT_RECEIVER)){
                messageJson = parseMessage(intent.getStringExtra("Json"));
                if( current_targetId.equals(messageJson.gettId())){
                    Log.i(TAG, messageJson.gettId());
                    initChaList(messageJson.getMc(), targetNick ,false);
                    Log.i(TAG, "Broadcast");
                }
            }
        }

    }

    private void initNewMessageBroadCast(){
        // 注册接收消息广播
        receiver = new NewMessageReceiver();
        IntentFilter intentFilter = new IntentFilter(BmobConfig.BROADCAST_NEW_MESSAGE);
        //设置广播的优先级别大于Mainacitivity,这样如果消息来的时候正好在chat页面，直接显示消息，而不是提示消息未读
        intentFilter.setPriority(3);
        intentFilter.addAction(ACTION_INTENT_RECEIVER);
        registerReceiver(receiver, intentFilter);
    }
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        unregisterReceiver(receiver);
        finish();
    }
    /**
     * 隐藏软键盘
     */
    public void hideSoftInputView() {
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
        if (Activity_Chatting.this.getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE) {
            if (Activity_Chatting.this.getCurrentFocus() != null)
                ((InputMethodManager) (Activity_Chatting.this)
                        .getSystemService(Activity.INPUT_METHOD_SERVICE))
                        .showSoftInput(et_msg, 0);
        }
    }
    @Override
    public void onBackPressed() {
        if (Layout_send_face.getVisibility() == View.VISIBLE) {
            Layout_send_face.setVisibility(View.GONE);
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
                Layout_send_face.setVisibility(View.VISIBLE);
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
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if(KeyShow == true){
            hideSoftInputView();
            KeyShow = false;
        }else{
            finish();
        }
        return true;
    }
    /**
     * 加载消息历史，从数据库中读出,加载自己发出去的话(应该通过时间的比对来加载历史消息)
     */
    private void initCurrentMsg() {
        List<BmobMsg> list = BmobDB.create(this).queryMessages(targetId,
                MsgPagerNum);
        Log.i(TAG, "size=" + list.size() + "list=" + list);
        if(list.size() > 0){
            for(int i = 0; i < list.size() && i < refreshNumber; i++){
                list.get(i).getMsgTime();
                String current_content = list.get(list.size()-1 - i).getContent();
                String current_time = list.get(list.size()-1 - i).getMsgTime();
                current_history_time.add(current_time);
                current_history_content.add(current_content);
                Log.i(TAG, "content=" + current_content);
            }
        }
    }//查询对方发过来的话
    private void initTargetMsg(){
        List<BmobMsg> list_target = BmobDB.create(this,targetId).queryMessages(targetId, MsgPagerNum);
        Log.i(TAG, "sizeTarget=" + list_target.size() + " " + "list_target=" + list_target);
        if(list_target.size() > 0){
            for(int i = 0; i < list_target.size() && i < refreshNumber; i++){
                String target_content = list_target.get(list_target.size()-1 - i ).getContent();
                String target_time = list_target.get(list_target.size()-1 -i).getMsgTime();
                target_history_time.add(target_time);
                target_history_content.add(target_content);
            }
        }

    }
    /**
     * 加载历史消息并且显示在UI上面(2016.4.17)
     */
    private void loadHistoryContent(){
        initTargetMsg();
        initCurrentMsg();
        if(current_history_content.size() > 0 && target_history_content.size() == 0){//只有自己发送消息时候
            for(int k = current_history_content.size() - 1; k >= 0; k--){
                initChaList(current_history_content.get(k), current_Nick, true);
            }
        }
        if(current_history_content.size() == 0 && target_history_content.size() > 0){//只有别人发送消息给自己，自己没发信息给别人
            for (int k = target_history_content.size() - 1; k >= 0; k--){
                initChaList(target_history_content.get(k) , targetNick , false);
            }
        }
        if(current_history_content.size() > 0 && target_history_content.size() > 0){//双方都有历史消息
            Log.i(TAG,"load");
            for(int i = current_history_content.size() - 1 ,j = target_history_content.size() - 1; i >= 0 || j >= 0;) {
                Log.i(TAG,"for");
                if(j == 0){
                    initChaList(current_history_content.get(i), current_Nick, true);
                    if(i > 0){
                        i--;
                        continue;
                    }
                    Log.i(TAG,"j==0");
                }
                if (i == 0){
                    initChaList(target_history_content.get(j) , targetNick , false);
                    if(j > 0){
                        j--;
                        continue;
                    }
                    if(j==0)break;
                    Log.i(TAG, "i==0");

                }
                if( j > 0 && i > 0){
                    if(current_history_time.get(i).compareTo(target_history_time.get(j)) < 0){ //历史时间小于它返回小于0
                        initChaList(current_history_content.get(i) , current_Nick , true);
                        i--;
                        Log.i(TAG,"<0");
                    }else if(current_history_time.get(i).compareTo(target_history_time.get(j)) == 0){ //历史时间相同返回0
                        initChaList(current_history_content.get(i) , current_Nick , true);
                        initChaList(target_history_content.get(j) , targetNick ,false);
                        i--;
                        j--;
                        Log.i(TAG,"==0"+"i="+i+"j="+j);
                    }else if(current_history_time.get(i).compareTo(target_history_time.get(j)) > 0){ //历史时间大于他返回大于0
                        initChaList(target_history_content.get(j) , targetNick , false);
                        j--;
                        Log.i(TAG,">0");
                    }
                }
            }
        }
    }
    /**
     * 清空List数组里面的数据
     */
    private void clearList(){
        Log.i(TAG , "清楚数组");
        adapter.clear();
        current_history_content.clear();
        current_history_time.clear();
        target_history_content.clear();
        target_history_time.clear();
    }
}
