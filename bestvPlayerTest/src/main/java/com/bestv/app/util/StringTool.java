package com.bestv.app.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.text.format.Time;
import android.util.Log;

public class StringTool {
	private static final String TAG = "StringTool";
	
	/**
	 * 视频播放时长 格式化
	 * 
	 * @param timeMs
	 * @return
	 */
	public static String stringForTime(int timeMs) {
		String ret = null;
		StringBuilder formatBuilder = new StringBuilder();
		Formatter formatter = new Formatter(formatBuilder, Locale.getDefault());

		try {
			int totalSeconds = timeMs / 1000;

			int seconds = totalSeconds % 60;
			int minutes = (totalSeconds / 60) % 60;
			int hours = totalSeconds / 3600;

			formatBuilder.setLength(0);

			if (hours > 0 && hours < 10) {
				ret = formatter.format("%d:%02d:%02d", hours, minutes, seconds)
						.toString();
			} else if (hours > 9 && hours < 49) { // 直播情况下，有可能timeMs非常大
				ret = formatter.format("%02d:%02d:%02d", hours, minutes,
						seconds).toString();
			} else if (hours > 48) {
				ret = "--:--";
			} else {
				ret = formatter.format("%02d:%02d", minutes, seconds)
						.toString();
			}

			return ret;
		} catch (Exception e) {
			Log.e(TAG, "stringForTime catch exception:" + e.getMessage());

			return "--:--";
		} finally {
			formatter.close();
		}
	}
	
	/**
	 * 判断字符串是否为空
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str) {
		if (str == null || str.trim().length() == 0 || str.trim().equals(""))
			return true;

		return false;
	}

	/**
	 * 判断是否是Email
	 * 
	 * @param strEmail
	 * @return
	 */
	public static boolean isEmail(String strEmail) {
		if (isEmpty(strEmail))
			return false;

		try {
			String strPattern = "^[0-9a-zA-Z]([-_.~]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-.]?[0-9a-zA-Z])*\\.[a-zA-Z]{2,4}$";
			Pattern p = Pattern.compile(strPattern);
			Matcher m = p.matcher(strEmail);

			return m.matches();
		} catch (Exception e) {
			Log.e(TAG, "isEmail catch exception:" + e.getMessage());

			return false;
		}
	}

	/**
	 * 判断是否为手机号码
	 * 
	 * @param strMobileNO
	 * @return
	 */
	public static boolean isMobileNO(String strMobileNO) {
		if (isEmpty(strMobileNO))
			return false;

		try {
			String strPattern = "^[1](3|5|8)[0-9]{9}$";
			Pattern p = Pattern.compile(strPattern);
			Matcher m = p.matcher(strMobileNO);
			return m.matches();
		} catch (Exception e) {
			Log.e(TAG, "isMobileNO catch exception:" + e.getMessage());

			return false;
		}
	}

	/**
	 * 判断是否为合法密码
	 * 
	 * @param pwd
	 * @return
	 */
	public static boolean isPwd(String pwd) {
		if (isEmpty(pwd))
			return false;

		try {
			String strPattern = "^[0-9a-zA-Z]{6,12}$";
			Pattern p = Pattern.compile(strPattern);
			Matcher m = p.matcher(pwd);
			return m.matches();
		} catch (Exception e) {
			Log.e(TAG, "isPwd catch exception:" + e.getMessage());

			return false;
		}
	}

	/**
	 * 根据生日获得年龄
	 * 
	 * @param birthday
	 * @return
	 */
	public static int getAge(String birthday) {
		if (isEmpty(birthday))
			return 0;

		try {
			int age = Integer.valueOf(birthday);
			Time t = new Time();
			t.setToNow();
			return t.year - age;
		} catch (Exception e) {
			Log.e(TAG, "getAge catch exception:" + e.getMessage());

			return 0;
		}
	}

	/**
	 * 转换成 yyyy-MM-dd HH:mm:ss 格式
	 * 
	 * @param time
	 * @return
	 */
	public static String parseDate(long time) {
		try {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.getDefault());
			Date date = new Date(time);

			return df.format(date);
		} catch (Exception e) {
			Log.e(TAG, "parseDate catch exception:" + e.getMessage());

			return null;
		}
	}

	/**
	 * 转换成 yyyy-MM-dd 格式
	 * 
	 * @param time
	 * @return
	 */
	public static String parseDate2(long time) {
		try {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd",Locale.getDefault());
			Date date = new Date(time);

			return df.format(date);
		} catch (Exception e) {
			Log.e(TAG, "parseDate2 catch exception:" + e.getMessage());

			return null;
		}
	}
}
