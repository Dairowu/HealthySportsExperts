package cn.xietong.healthysportsexperts.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bmob.utils.BmobLog;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;
import java.util.Map;

import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.db.BmobDB;
import cn.bmob.v3.listener.UpdateListener;
import cn.xietong.healthysportsexperts.R;
import cn.xietong.healthysportsexperts.app.App;
import cn.xietong.healthysportsexperts.utils.CollectionUtils;
import cn.xietong.healthysportsexperts.utils.FriendViewHolder;
import cn.xietong.healthysportsexperts.utils.ImageLoaderOptions;

/**
 * Created by 林思旭 on 2016/4/8.
 */
public class NewFriendAdapter extends BaseListAdapter<BmobInvitation> {

    private String TAG = "NewFriendAdapter";
    public NewFriendAdapter(Context context, List<BmobInvitation> list) {
        super(context, list);
        Log.i(TAG, "进入NewFriendAdapter");
        // TODO Auto-generated constructor stub
    }

    @Override
    public View bindView(int arg0, View convertView, ViewGroup arg2) {
        // TODO Auto-generated method stub
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_add_friend, null);
        }
        final BmobInvitation msg = getList().get(arg0);
        Log.i(TAG, msg + "1");
        TextView name = FriendViewHolder.get(convertView, R.id.name);
        ImageView iv_avatar = FriendViewHolder.get(convertView, R.id.avatar);

        final Button btn_add = FriendViewHolder.get(convertView, R.id.btn_add);
        Map<String,BmobChatUser> users = App.getInstance().getContactList();
        if(users!=null){
            if(users.containsKey(msg.getFromname())){
                btn_add.setText("已同意");
                btn_add.setBackgroundDrawable(null);
                btn_add.setTextColor(mContext.getResources().getColor(R.color.base_color_text_black));
            }else{
                btn_add.setText("同意");
                Log.i(TAG, "不存在好友"+msg.getFromname());
            }
        }

        String avatar = msg.getAvatar();

        if (avatar != null && !avatar.equals("")) {
			ImageLoader.getInstance().displayImage(avatar, iv_avatar, ImageLoaderOptions.getOptions());
            BmobLog.i("缺失一个");
        } else {
            iv_avatar.setImageResource(R.drawable.activity_add_user_head);
        }
        Log.i(TAG, "2");
        int status = msg.getStatus();
        Log.i(TAG, "status="+status+"BmobConfig.INVITE_ADD_NO_VALIDATION="+ BmobConfig.INVITE_ADD_NO_VALIDATION);
        if(btn_add.getText().equals("同意")){
            btn_add.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    BmobLog.i("点击同意按钮:"+msg.getFromid());
                    agressAdd(btn_add, msg);
                }
            });
        }else{
            btn_add.setText("已同意");
            btn_add.setBackgroundDrawable(null);
            btn_add.setTextColor(mContext.getResources().getColor(R.color.base_color_text_black));
            btn_add.setEnabled(false);
        }
        name.setText(msg.getNick());
        return convertView;
    }


    /**添加好友
     * agressAdd
     * @Title: agressAdd
     * @Description: TODO
     * @param @param btn_add
     * @param @param msg
     * @return void
     * @throws
     */
    private void agressAdd(final Button btn_add,final BmobInvitation msg){
        final ProgressDialog progress = new ProgressDialog(mContext);
        progress.setMessage("正在添加...");
        progress.setCanceledOnTouchOutside(false);
        progress.show();
        Log.i(TAG, "4");
        try {
            //同意添加好友
            BmobUserManager.getInstance(mContext).agreeAddContact(msg, new UpdateListener() {

                @Override
                public void onSuccess() {
                    // TODO Auto-generated method stub
                    progress.dismiss();
                    btn_add.setText("已同意");
                    btn_add.setBackgroundDrawable(null);
                    btn_add.setTextColor(mContext.getResources().getColor(R.color.base_color_text_black));
                    btn_add.setEnabled(false);
                    Log.i(TAG, "5");
                    Log.i(TAG, App.getInstance()+"");//��
                    App.getInstance().setContactList(CollectionUtils.list2map(BmobDB.create(mContext).getContactList()));
                    Log.i(TAG, "已经同意");
                }

                @Override
                public void onFailure(int arg0, final String arg1) {
                    // TODO Auto-generated method stub
                    progress.dismiss();
                    ShowToast("添加失败: " +arg1);
                }
            });
        } catch (final Exception e) {
            progress.dismiss();
            ShowToast("添加失败: " +e.getMessage());
        }
    }
}
