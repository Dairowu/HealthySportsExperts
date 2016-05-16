package cn.xietong.healthysportsexperts.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;
import java.util.Map;

import cn.bmob.im.BmobChatManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.inteface.MsgTag;
import cn.bmob.v3.listener.PushListener;
import cn.xietong.healthysportsexperts.R;
import cn.xietong.healthysportsexperts.app.App;
import cn.xietong.healthysportsexperts.utils.FriendViewHolder;
import cn.xietong.healthysportsexperts.utils.ImageLoaderOptions;

/**查找好友
  * @ClassName: AddFriendAdapter
  * @Description: TODO
  * @author 林思旭
  * @date 2016.4.9
  */
public class AddFriendAdapter extends BaseListAdapter<BmobChatUser> {

	private String TAG = "AddFriendAdapter";
	public AddFriendAdapter(Context context, List<BmobChatUser> list) {
		super(context, list);
		// TODO Auto-generated constructor stub
	}

	@Override
	public View bindView(int arg0, View convertView, ViewGroup arg2) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_add_friend, null);
		}
		final BmobChatUser contract = getList().get(arg0);
		TextView name = FriendViewHolder.get(convertView, R.id.name);
		ImageView iv_avatar = FriendViewHolder.get(convertView, R.id.avatar);
		
		Button btn_add = FriendViewHolder.get(convertView, R.id.btn_add);

		String avatar = contract.getAvatar();

		if (avatar != null && !avatar.equals("")) {
			ImageLoader.getInstance().displayImage(avatar, iv_avatar, ImageLoaderOptions.getOptions());
		} else {
			iv_avatar.setImageResource(R.drawable.activity_add_user_head);
		}

		name.setText(contract.getUsername());
		Map<String,BmobChatUser> users = App.getInstance().getContactList();//2016.5.12
		if(users != null){
			if( users.containsKey(contract.getUsername()) ){
				btn_add.setText("已添加");
				btn_add.setTextColor(mContext.getResources().getColor(R.color.base_color_text_black));
				btn_add.setBackgroundDrawable(null);
				btn_add.setEnabled(false);
			}else {
				btn_add.setText("添加");
			}
		}
		btn_add.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				final ProgressDialog progress = new ProgressDialog(mContext);
				progress.setMessage("正在添加...");
				progress.setCanceledOnTouchOutside(false);
				progress.show();
				//发送tag请求
				BmobChatManager.getInstance(mContext).sendTagMessage(MsgTag.ADD_CONTACT, contract.getObjectId(),new PushListener() {
					
					@Override
					public void onSuccess() {
						// TODO Auto-generated method stub
						progress.dismiss();
						ShowToast("发送请求成功，等待对方验证!");
					}
					
					@Override
					public void onFailure(int arg0, final String arg1) {
						// TODO Auto-generated method stub
						progress.dismiss();
						ShowToast("发送请求失败，请重新添加!");
						ShowLog("发送请求失败:"+arg1);
					}
				});
			}
		});
		return convertView;
	}

}
