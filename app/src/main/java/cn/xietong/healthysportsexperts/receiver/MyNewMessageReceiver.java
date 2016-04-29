package cn.xietong.healthysportsexperts.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobNotifyManager;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.config.BmobConstant;
import cn.bmob.im.db.BmobDB;
import cn.bmob.im.inteface.EventListener;
import cn.bmob.im.inteface.OnReceiveListener;
import cn.bmob.im.util.BmobJsonUtil;
import cn.bmob.v3.listener.FindListener;
import cn.xietong.healthysportsexperts.R;
import cn.xietong.healthysportsexperts.app.App;
import cn.xietong.healthysportsexperts.ui.activity.Activity_Chatting;
import cn.xietong.healthysportsexperts.ui.activity.Activity_NewFriend;
import cn.xietong.healthysportsexperts.ui.activity.MainActivity;
import cn.xietong.healthysportsexperts.ui.fragment.FragmentPageMessage_son2;
import cn.xietong.healthysportsexperts.utils.CollectionUtils;
import cn.xietong.healthysportsexperts.utils.CommonUtils;

/**
 * Created by 林思旭 on 2016/4/9.
 */
public class MyNewMessageReceiver extends BroadcastReceiver {

    private static String TAG = "MyNewMessageReceiver";
    public static int mNewNum = 0;
    private JSONObject jo;
    BmobUserManager userManager;
    BmobChatUser currentUser;
    // 事件监听
    public ArrayList<EventListener> ehList = new ArrayList<EventListener>();
    private String Msgjson;
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        Msgjson = intent.getStringExtra("msg");
        userManager = BmobUserManager.getInstance(context);
        currentUser = userManager.getCurrentUser();
        boolean isNetConnected = CommonUtils.isNetworkAvailable(context);
        Log.i(TAG,"123");
        if(isNetConnected){
            parseMessage(context, Msgjson);
            Log.i(TAG, "isNetConnected");
        }else{
            for (int i = 0; i < ehList.size(); i++)
                ((EventListener) ehList.get(i)).onNetChange(isNetConnected);
        }
//        Intent intentJson = new Intent(MainActivity.ACTION_INTENT_RECEIVER);
//        intentJson.putExtra("Json", Msgjson);
//        context.sendBroadcast(intentJson);
        Log.i(TAG, Msgjson);

        //2016.3.23
    }

    /**
     * 2016.4.9,林思旭
     * @param context
     * @param json
     */

    private void parseMessage(final Context context, String json){
        try {
            jo = new JSONObject(json);
            String fromId = BmobJsonUtil.getString(jo, BmobConstant.PUSH_KEY_TARGETID);
            //增加消息接收方的ObjectId--目的是解决多账户登陆同一设备时，无法接收到非当前登陆用户的消息。
            final String toId = BmobJsonUtil.getString(jo, BmobConstant.PUSH_KEY_TOID);
            //4.6
            String tag = BmobJsonUtil.getString(jo, BmobConstant.PUSH_KEY_TAG);
            String targetId = BmobJsonUtil.getString(jo, BmobConstant.PUSH_KEY_TARGETID);
            String username = BmobJsonUtil.getString(jo, BmobConstant.PUSH_KEY_TARGETUSERNAME);
            String avatar = BmobJsonUtil.getString(jo,	BmobConstant.PUSH_KEY_TARGETAVATAR);
            String nick = BmobJsonUtil.getString(jo,	BmobConstant.PUSH_KEY_TARGETNICK);

            Log.i(TAG,"targetId = " + targetId);
            Log.i(TAG,"username = "+username);
            Log.i(TAG,"nick = "+nick);
            Log.i(TAG,"tag = "+tag);
            Log.i(TAG,"avatar = "+avatar);
            //聊天用户
            BmobChatUser targetUser = new BmobChatUser();
            targetUser.setObjectId(targetId==null?"":targetId);
            targetUser.setAvatar(avatar==null?"":avatar);
            targetUser.setNick(nick==null?"":nick);
            targetUser.setUsername(username==null?"":username);
            if(TextUtils.isEmpty(tag)){//不携带tag标签(聊天内容)
                //16:09
                BmobChatManager.getInstance(context).createReceiveMsg(json, new OnReceiveListener() {
                    @Override
                    public void onSuccess(BmobMsg bmobMsg) {
                        if (ehList.size() > 0) {// 有监听的时候，传递下去
                            for (int i = 0; i < ehList.size(); i++) {
                                ((EventListener) ehList.get(i)).onMessage(bmobMsg);
                            }
                        } else {
                            boolean isAllow = App.getInstance().getSharedPreferencesUtil().isAllowPushNotify();
                            if(isAllow && currentUser!=null && currentUser.getObjectId().equals(toId)){//当前登陆用户存在并且也等于接收方id
                                mNewNum++;
                                showMsgNotify(context , bmobMsg);
                            }
                        }
                    }
                    @Override
                    public void onFailure(int i, String s) {
                        Log.i(TAG,"获取接收的消息失败："+s);
                    }
                });
//                String message = BmobJsonUtil.getString(jo,	BmobConstant.PUSH_KEY_CONTENT);
//                int msgtype = BmobJsonUtil.getInt(jo, BmobConstant.PUSH_KEY_MSGTYPE);
                BmobMsg msg =BmobMsg.createTextSendMsg(context, targetId, json);
                Log.i(TAG, "msg="+msg);
                Intent intentJson = new Intent(Activity_Chatting.ACTION_INTENT_RECEIVER);
                intentJson.putExtra("Json", Msgjson);
                context.sendBroadcast(intentJson);
//                // 普通消息，（修改）
//                if (ehList.size() > 0) {// 有监听的时候，传递下去
//                    for (int i = 0; i < ehList.size(); i++) {
//                        ((EventListener) ehList.get(i)).onMessage(msg);
//                    }
//                } else {
//                    // 存储接收到的消息
////					BmobChatManager.getInstance(context).saveReceiveMessage(true, msg);
//					  BmobDB.create(context, targetId).saveMessage(msg);//这样封装可以将信息发送到targetId用户中去
////                  BmobDB.create(context,targetId).resetUnread(msg.getContent());
//                    boolean isAllow = App.getInstance().getSharedPreferencesUtil().isAllowPushNotify();
//                    if(isAllow){
//                        mNewNum++;
////						showNotification(context, targetUser, msg.getContent());
//                    }
//                }
//                //修改
            }else {
                if(tag.equals(BmobConfig.TAG_ADD_CONTACT)){
                    BmobInvitation message_invitate =BmobInvitation.createReceiverInvitation(json);
                    //保存好友请求消息
                    BmobDB.create(context, toId).saveInviteMessage(message_invitate);//可以用的
//					if (targetId.equals(userManager.getCurrentUserObjectId()))//不能添加自己为好友
//						return;//2016.4.6    23:22
                    Log.i(TAG, "保存邀请信息");
                    if (ehList.size() > 0) {// 有监听的时候，传递下去
                        for (EventListener handler : ehList)
                            handler.onAddUser(message_invitate);
                        Log.i(TAG, "3-2");
                    }else{
                        boolean isAllow = App.getInstance().getSharedPreferencesUtil().isAllowPushNotify();
                        if(currentUser!=null && currentUser.getObjectId().equals(toId)){
                            String tickerText = message_invitate.getFromname()+"请求添加好友";
                            Intent intent_new_friend = new Intent(Activity_NewFriend.NEW_FRIEND_RECEIVE);
                            intent_new_friend.putExtra("new_friend", json);
                            context.sendBroadcast(intent_new_friend);
                            Intent intent_friend = new Intent(FragmentPageMessage_son2.New_Message);
                            intent_friend.putExtra("new_add_friend", json);
                            context.sendBroadcast(intent_friend);
                        }
                    }
                }else if(tag.equals(BmobConfig.TAG_ADD_AGREE)){
                    //收到对方的同意请求之后，就得添加对方为好友--已默认添加同意方为好友，并保存到本地好友数据库
                    BmobUserManager.getInstance(context).addContactAfterAgree(username, new FindListener<BmobChatUser>() {

                        @Override
                        public void onError(int arg0, final String arg1) {
                            // TODO Auto-generated method stub
//					       ((Activity)context).runOnUiThread(new Runnable() {
//
//								@Override
//								public void run() {
//									Toast.makeText(context, "��Ӻ���ʧ��: " +arg1, 1).show();
//								}
//							});
                        }
                        @Override
                        public void onSuccess(List<BmobChatUser> arg0) {
                            // TODO Auto-generated method stub
                            //保存到内存中
                            App.getInstance().setContactList(CollectionUtils.list2map(BmobDB.create(context).getContactList()));
                        }
                    });
                    boolean isAllow = App.getInstance().getSharedPreferencesUtil().isAllowPushNotify();
                    boolean isAllowVoice = App.getInstance().getSharedPreferencesUtil().isAllowVoice();
                    boolean isAllowVibrate = App.getInstance().getSharedPreferencesUtil().isAllowVibrate();
                    if(isAllow && currentUser!=null && currentUser.getObjectId().equals(toId)){
                        String tickerText = username+"同意添加您为好友";
                        BmobNotifyManager.getInstance(context).showNotify(isAllowVoice,isAllowVibrate,R.drawable.ic_launcher, tickerText, username, tickerText.toString(),MainActivity.class);
                    }
                    //创建一个临时验证会话--用于在会话界面形成初始会话
                    BmobMsg.createAndSaveRecentAfterAgree(context, json);
                }else if(tag.equals(BmobConfig.TAG_OFFLINE)){//����֪ͨ
                    if (ehList.size() > 0) {// �м�����ʱ�򣬴�����ȥ
                        for (EventListener handler : ehList)
                            handler.onOffline();
                    }else{
                        //�������
                        App.getInstance().logout();
                    }
                }else if(tag.equals(BmobConfig.TAG_READED)){//已读回执
                    String conversionId = BmobJsonUtil.getString(jo,BmobConstant.PUSH_READED_CONVERSIONID);
                    String msgTime = BmobJsonUtil.getString(jo,BmobConstant.PUSH_READED_MSGTIME);
                    if(currentUser!=null){
                        //更改某条消息的状态
                        BmobChatManager.getInstance(context).updateMsgStatus(conversionId, msgTime);
                        if(toId.equals(currentUser.getObjectId())){
                            if (ehList.size() > 0) {// 有监听的时候，传递下去--便于修改界面
                                for (EventListener handler : ehList)
                                    handler.onReaded(conversionId, msgTime);
                            }
                        }
                    }
                }
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    /**
     *  显示与聊天消息的通知
     * @Title: showNotify
     * @return void
     * @throws
     */
    public void showMsgNotify(Context context,BmobMsg msg) {
        // 更新通知栏
        int icon = R.drawable.ic_launcher;
        String trueMsg = "";
        if(msg.getMsgType()==BmobConfig.TYPE_TEXT && msg.getContent().contains("\\ue")){
            trueMsg = "[表情]";
        }else if(msg.getMsgType()==BmobConfig.TYPE_IMAGE){
            trueMsg = "[图片]";
        }else if(msg.getMsgType()==BmobConfig.TYPE_VOICE){
            trueMsg = "[语音]";
        }else if(msg.getMsgType()==BmobConfig.TYPE_LOCATION){
            trueMsg = "[位置]";
        }else{
            trueMsg = msg.getContent();
        }
        CharSequence tickerText = msg.getBelongUsername() + ":" + trueMsg;
        String contentTitle = msg.getBelongUsername()+ " (" + mNewNum + "条新消息)";

        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        boolean isAllowVoice = App.getInstance().getSharedPreferencesUtil().isAllowVoice();
        boolean isAllowVibrate = App.getInstance().getSharedPreferencesUtil().isAllowVibrate();

        BmobNotifyManager.getInstance(context).showNotifyWithExtras(isAllowVoice,isAllowVibrate,icon, tickerText.toString(), contentTitle, tickerText.toString(),intent);
    }
}
