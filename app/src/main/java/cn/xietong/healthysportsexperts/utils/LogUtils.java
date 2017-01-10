package cn.xietong.healthysportsexperts.utils;

/**
 * @TODO Log工具类，设置开关，防止发布版本时log信息泄露
 * @author Bright Van
 * @date 2015-7-28
 * 
 */

import android.util.Log;

import cn.xietong.healthysportsexperts.BuildConfig;


public class LogUtils {

	public static void v(String tag, String msg) {
		if (BuildConfig.DEBUG) {
			Log.v(tag, msg);
		}

	}

	public static void d(String tag, String msg) {
		if (BuildConfig.DEBUG) {
			Log.d(tag, msg);
		}

	}

	public static void i(String tag, Object obj) {
		if (BuildConfig.DEBUG) {
			Log.i(tag, obj + "");
		}

	}

	public static void w(String tag, String msg) {
		if (BuildConfig.DEBUG) {
			Log.w(tag, msg);
		}

	}

	public static void e(String tag, String msg) {
		if (BuildConfig.DEBUG) {
			Log.e(tag, msg);
		}
	}

}
