package cn.xietong.healthysportsexperts.utils;

import cn.bmob.im.bean.BmobMsg;

/**
 * 林思旭，2016.4.9
 */
//2016.4.2尝试补偿之前的错误，继承了BmobMsg
public class ChatData extends BmobMsg{

	private String ChatContent;//信息内容
	private String Chatname; //聊天对象的名字
	private Boolean flag ; //判断是否是当前聊天对象
	private String time;//2016.4.22.1.29增加时间
	
	
	public Boolean getFlag() {
		return flag;
	}

	public ChatData(String content,String Chatname , String time,Boolean flag){
		this.ChatContent = content;
		this.Chatname = Chatname;
		this.time = time;
		this.flag = flag;
	}

	public String getChatContent() {
		return ChatContent;
	}

	public String getChatname() {
		return Chatname;
	}

	public String getTime(){return time;}
	
}
