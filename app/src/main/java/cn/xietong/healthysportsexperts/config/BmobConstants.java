package cn.xietong.healthysportsexperts.config;

import android.annotation.SuppressLint;
import android.os.Environment;


/**
 * @ClassName: BmobConstants
 * @date 2015-11-17 下午2:48:33
 */
@SuppressLint("SdCardPath")
public class BmobConstants {

	/**
	 * 存放发送图片的目录
	 */
	public static String BMOB_PICTURE_PATH = Environment.getExternalStorageDirectory()	+ "/bmobimdemo/image/";

	/**
	 * 我的头像保存目录
	 */
	public static String MyAvatarDir = "/sdcard/bmobimdemo/avatar/";
	/**
	 * 拍照回调
	 */
	public static final int REQUESTCODE_UPLOADAVATAR_CAMERA = 1;//拍照修改头像
	public static final int REQUESTCODE_UPLOADAVATAR_LOCATION = 2;//本地相册修改头像
	public static final int REQUESTCODE_UPLOADAVATAR_CROP = 3;//系统裁剪头像

	public static final int REQUESTCODE_TAKE_CAMERA = 0x000001;//拍照
	public static final int REQUESTCODE_TAKE_LOCAL = 0x000002;//本地图片
	public static final int REQUESTCODE_TAKE_LOCATION = 0x000003;//位置

	public static final int REQUESTCODE_CHOOSE_PHOTO = 0x000004;
	public static final int REQUESTCODE_CHANGE_INFO = 0x000005;
	public static final int REQUESTCODE_CHANGE_INFO_NICK = 0x000006;
	public static final int REQUESTCODE_CHANGE_INFO_SIGNATURE = 0x000007;
	public static final String EXTRA_STRING = "extra_string";

	/**
	 * 一些布局类型标识符
	 */
	public static final int LAYOUT_ONE_TV  = 0;
	public static final int LAYOUT_TV_SWITCH = 1;
	public static final int LAYOUT_TWO_TV  = 1;
	public static final int LAYOUT_IV_TV  = 1;
	public static final int LAYOUT_TV_IV  = 2;
	public static final int LAYOUT_TWOTV_IV  = 2;
	public static final int LAYOUT_ONE_BTN = 3;

	public static final String ACTION_REGISTER_SUCCESS_FINISH ="register.success.finish";//注册成功之后登陆页面退出
}
