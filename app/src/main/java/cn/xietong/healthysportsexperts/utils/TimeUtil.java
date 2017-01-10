package cn.xietong.healthysportsexperts.utils;

import android.annotation.SuppressLint;
import android.text.format.DateUtils;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 林思旭，2016.4.13
 */
@SuppressLint("SimpleDateFormat")
public class TimeUtil {
	
	public final static String FORMAT_YEAR = "yyyy";
	public final static String FORMAT_MONTH_DAY = "MM月dd日";
	
	public final static String FORMAT_DATE = "yyyy-MM-dd";
	public final static String FORMAT_TIME = "HH:mm";
	public final static String FORMAT_MONTH_DAY_TIME = "MM月dd日  hh:mm";
	
	public final static String FORMAT_DATE_TIME = "yyyy-MM-dd HH:mm";
	public final static String FORMAT_DATE1_TIME = "yyyy/MM/dd HH:mm";
	public final static String FORMAT_DATE_TIME_SECOND = "yyyy/MM/dd HH:mm:ss";
	
	private static SimpleDateFormat sdf = new SimpleDateFormat();
	private static final int YEAR = 365 * 24 * 60 * 60;// 年
	private static final int MONTH = 30 * 24 * 60 * 60;// 月
	private static final int DAY = 24 * 60 * 60;// 天
	private static final int HOUR = 60 * 60;// 小时
	private static final int MINUTE = 60;// 分钟

	private final int reduceTime = 8*60*60*1000;//这个是需要减去的时间差 2017.1.3
	public static TimeUtil myTimeUtil = null;//2017.1.3
	private final double EARTH_RADIUS = 6378137.0;//2017.1.3


	public static TimeUtil getMyTimeUtils(){
		if(myTimeUtil == null){
			synchronized (DateUtils.class){
				if (myTimeUtil == null){
					myTimeUtil = new TimeUtil();
				}
			}
		}
		return myTimeUtil;
	}
	/**
	 * 根据时间戳获取描述性时间，如3分钟前，1天前
	 * 
	 * @param timestamp
	 *            时间戳 单位为毫秒
	 * @return 时间字符串
	 */
	public static String getDescriptionTimeFromTimestamp(long timestamp) {
		long currentTime = System.currentTimeMillis();
		long timeGap = (currentTime - timestamp) / 1000;// 与现在时间相差秒数
		System.out.println("timeGap: " + timeGap);
		String timeStr = null;
		if (timeGap > YEAR) {
			timeStr = timeGap / YEAR + "年前";
		} else if (timeGap > MONTH) {
			timeStr = timeGap / MONTH + "个月前";
		} else if (timeGap > DAY) {// 1天以上
			timeStr = timeGap / DAY + "天前";
		} else if (timeGap > HOUR) {// 1小时-24小时
			timeStr = timeGap / HOUR + "小时前";
		} else if (timeGap > MINUTE) {// 1分钟-59分钟
			timeStr = timeGap / MINUTE + "分钟前";
		} else {// 1秒钟-59秒钟
			timeStr = "刚刚";
		}
		return timeStr;
	}

	/**
	 * 获取当前日期的指定格式的字符串
	 * 
	 * @param format
	 *            指定的日期时间格式，若为null或""则使用指定的格式"yyyy-MM-dd HH:MM"
	 * @return
	 */
	public static String getCurrentTime(String format) {
		if (format == null || format.trim().equals("")) {
			sdf.applyPattern(FORMAT_DATE_TIME);
		} else {
			sdf.applyPattern(format);
		}
		return sdf.format(new Date());
	}

	// date类型转换为String类型
 	// formatType格式为yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日 HH时mm分ss秒
 	// data Date类型的时间
 	public static String dateToString(Date data, String formatType) {
 		return new SimpleDateFormat(formatType).format(data);
 	}
 
 	// long类型转换为String类型
 	// currentTime要转换的long类型的时间
 	// formatType要转换的string类型的时间格式
 	public static String longToString(long currentTime, String formatType){
 		String strTime="";
		Date date = longToDate(currentTime, formatType);// long类型转成Date类型
		strTime = dateToString(date, formatType); // date类型转成String 
 		return strTime;
 	}
 
 	// string类型转换为date类型
 	// strTime要转换的string类型的时间，formatType要转换的格式yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日
 	// HH时mm分ss秒，
 	// strTime的时间格式必须要与formatType的时间格式相同
 	public static Date stringToDate(String strTime, String formatType){
 		SimpleDateFormat formatter = new SimpleDateFormat(formatType);
 		Date date = null;
 		try {
			date = formatter.parse(strTime);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 		return date;
 	}
 
 	// long转换为Date类型
 	// currentTime要转换的long类型的时间
 	// formatType要转换的时间格式yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日 HH时mm分ss秒
 	public static Date longToDate(long currentTime, String formatType){
 		Date dateOld = new Date(currentTime); // 根据long类型的毫秒数生命一个date类型的时间
 		String sDateTime = dateToString(dateOld, formatType); // 把date类型的时间转换为string
 		Date date = stringToDate(sDateTime, formatType); // 把String类型转换为Date类型
 		return date;
 	}
 
 	// string类型转换为long类型
 	// strTime要转换的String类型的时间
 	// formatType时间格式
 	// strTime的时间格式和formatType的时间格式必须相同
 	public static long stringToLong(String strTime, String formatType){
 		Date date = stringToDate(strTime, formatType); // String类型转成date类型
 		if (date == null) {
 			return 0;
 		} else {
 			long currentTime = dateToLong(date); // date类型转成long类型
 			return currentTime;
 		}
 	}
 
 	// date类型转换为long类型
 	// date要转换的date类型的时间
 	public static long dateToLong(Date date) {
 		return date.getTime();
 	}
	 	
	public static String getTime(long time) {
		SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd HH:mm");
		return format.format(new Date(time));
	}

	public static String getHourAndMin(long time) {
		SimpleDateFormat format = new SimpleDateFormat("HH:mm");
		return format.format(new Date(time));
	}

	/** 获取聊天时间：因为sdk的时间默认到秒故应该乘1000
	  * @Title: getChatTime
	  * @Description: TODO
	  * @param @param timesamp
	  * @param @return 
	  * @return String
	  * @throws
	  */
	public static String getChatTime(long timesamp) {
		long clearTime = timesamp*1000;
		String result = "";
		SimpleDateFormat sdf = new SimpleDateFormat("dd");
		Date today = new Date(System.currentTimeMillis());
		Log.i("info","today="+today);
		Date otherDay = new Date(clearTime);
		Log.i("info","otherDay="+otherDay);
		int temp = Integer.parseInt(sdf.format(today))
				- Integer.parseInt(sdf.format(otherDay));
		switch (temp) {
		case 0:
			result = "今天 " + getHourAndMin(clearTime);
			break;
		case 1:
			result = "昨天 " + getHourAndMin(clearTime);
			break;
		case 2:
			result = "前天 " + getHourAndMin(clearTime);
			break;

		default:
			result = getTime(clearTime);
			break;
		}

		return result;
	}
	//2017.1.3
	public  String getHMS(long timestamp) {
		timestamp = timestamp - reduceTime;
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		String time = null;
		try {
			return sdf.format(new Date(timestamp));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return time;
	}
	/**
	 * 2017.1.3
	 * @param longitude1 第一个点的经度
	 * @param latitude1  第一个点的纬度
	 * @param longitude2 第二个点的经度
	 * @param latitude2  第二个点的纬度
	 * @param sum  所有点距离的总合
	 * @return 返回千米
	 */
	public double getDistance(double longitude1, double latitude1,
							  double longitude2, double latitude2,double sum) {
		double Lat1 = rad(latitude1);
		double Lat2 = rad(latitude2);
		double a = Lat1 - Lat2;
		double b = rad(longitude1) - rad(longitude2);
		double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
				+ Math.cos(Lat1) * Math.cos(Lat2)
				* Math.pow(Math.sin(b / 2), 2)));
		s = s * EARTH_RADIUS;
		s = Math.round(s * 10000) / 10000;
		sum += s;
		return sum;
	}
	private double rad(double d) {
		return d * Math.PI / 180.0;
	}
	/**
	 * 2017.1.3
	 * @param sum 跑步的总路程
	 * @return 返回一个km为单位的字符串，小数点保留后两位
	 */
	public String dateToString(double sum){
		String result = null;
		String sum_test = String.valueOf(sum);
		int ind = sum_test.indexOf('.');
		if(ind > 3){
//            LogUtils.i(TAG,">3");
			String start = sum_test.substring(0,ind - 3);//1000距离所以减3
			String end = sum_test.substring(ind-3,ind -1);//只保留两位小数
			result = start+"."+end;
		}else{
			if(ind == 1){
//                LogUtils.i(TAG,"1");
				return "0:00";
			}
			if(ind == 2){
//                LogUtils.i(TAG,"2");
				String start = "0";
				String end = "0"+sum_test.substring(0,1);
				result = start+"."+end;
			}
			if(ind == 3){
//                LogUtils.i(TAG,"3");
				String start = "0";
				String end = sum_test.substring(0,2);
				result = start+"."+end;
			}
		}
		return result;
	}
}