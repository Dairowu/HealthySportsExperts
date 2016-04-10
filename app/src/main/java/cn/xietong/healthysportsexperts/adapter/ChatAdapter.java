package cn.xietong.healthysportsexperts.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.xietong.healthysportsexperts.R;
import cn.xietong.healthysportsexperts.ui.view.BmobTextView;
import cn.xietong.healthysportsexperts.utils.ChatData;

/**
 * 林思旭，2016.4.9
 */
public class ChatAdapter extends ArrayAdapter<ChatData> {

	private Boolean flag;
	private int resourceId;
	public ChatAdapter(Context context, int viewresource,List<ChatData> objects) {
		super(context, viewresource,objects);
		resourceId = viewresource;
		// TODO Auto-generated constructor stub
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ChatData chatData = getItem(position);
		View view = LayoutInflater.from(getContext()).inflate(resourceId, null);
		//接受方
		BmobTextView txt_Content_receive = (BmobTextView)view.findViewById(R.id.txt_textcontent_receive);//接受到信息背景是白色的
		TextView txt_receive_Name = (TextView)view.findViewById(R.id.txt_target_receive_Name);
		ImageView ig_ChatView_receive = (ImageView)view.findViewById(R.id.imageView_target_receive);
		//发送方
		BmobTextView txt_Content_send = (BmobTextView)view.findViewById(R.id.txt_textcontent_sending);
		ImageView ig_ChatView_send = (ImageView)view.findViewById(R.id.imageView_current_sending);
	    TextView txt_ChatName_send = (TextView)view.findViewById(R.id.txt_current_sending_Name);


	    
		if(chatData.getFlag() == true){

			txt_Content_send.setVisibility(View.VISIBLE);
			ig_ChatView_send.setVisibility(View.VISIBLE);
			txt_ChatName_send.setVisibility(View.VISIBLE);

			txt_Content_receive.setVisibility(View.INVISIBLE);
			txt_receive_Name.setVisibility(View.INVISIBLE);
			ig_ChatView_receive.setVisibility(View.INVISIBLE);


			txt_Content_send.setText(chatData.getChatContent());
			txt_ChatName_send.setText(chatData.getChatname());
			
		}else{
			txt_Content_receive.setVisibility(View.VISIBLE);
			txt_receive_Name.setVisibility(View.VISIBLE);
			ig_ChatView_receive.setVisibility(View.VISIBLE);

			txt_Content_send.setVisibility(View.INVISIBLE);
			ig_ChatView_send.setVisibility(View.INVISIBLE);
			txt_ChatName_send.setVisibility(View.INVISIBLE);

			txt_Content_receive.setText(chatData.getChatContent());
			txt_receive_Name.setText(chatData.getChatname());
		    
			
		}
	    
		
		return view;
	}
	

}
