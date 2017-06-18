package com.bestv.app.util;

import java.util.Observable;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class SharedData extends Observable {
	public static final String TAG = "SharedData";

	public static final String PREFS_NAME = "com.bestv.app";

	public final static String _isWIFI = "isWIFI";
	public final static String _szVersion = "szVersion";
	public final static String _szIMSI = "szIMSI";
	public final static String _szIMEI = "szIMEI";
	public final static String _szPhoneType = "szPhoneType";
	public final static String _szSDKVersion = "szSDKVersion";

	public final static String _sessionId = "session_id";
	public final static String _wifiWatch = "wifi_watch";
	public final static String _3g4gNotify = "3g4g_notify";
	public final static String _pushFlag = "push_flag";
	public final static String _cacheCleard = "cache_cleard";
	
	//第一次安装的提示信息
	//统一存版本号
	public final static String FIRST_SEARCH_HINT="FIRST_SEARCH_HINT"; 
	public final static String FIRST_MOVIE_HINT="FIRST_MOVIE_HINT";
	public final static String FIRST_INSTALL_HINT="FIRST_INSTALL_HINT";
	
	public static enum SavedValues {
		ONE_DATA_ADDED, ONE_DATA_REMOVED, ALL_DATA_REMOVED
	}

	private static SharedData _instance;

	SharedPreferences _settings;

	private SharedData(Context appContext) {
		// Restore preferences
		Log.e(TAG, "init");
		_settings = appContext.getSharedPreferences(
				PREFS_NAME, 0);
	}

	public static SharedData getInstance(Context appContext) {
		if (_instance == null) {
			_instance = new SharedData(appContext);
		}
		return _instance;
	}

	public void cancel() {
		Log.e(TAG, "cancel");
		_instance = null;
	}

	public boolean set(String key, Object value) {
		SharedPreferences.Editor editor = _settings.edit();
		set(key, value, editor);
		boolean ret = editor.commit();
		if (ret == true) {
			setChanged();
			notifyObservers(SavedValues.ONE_DATA_ADDED);
		}
		return ret;
	}

	private void set(String key, Object value, SharedPreferences.Editor editor) {
		if (value instanceof String) {
			editor.putString(key, (String) value);
		} else if (value instanceof Boolean) {
			boolean b = (Boolean) value;
			editor.putBoolean(key, b);
		} else if (value instanceof Float) {
			float f = (Float) value;
			editor.putFloat(key, f);
		} else if (value instanceof Integer) {
			int i = (Integer) value;
			editor.putInt(key, i);
			Log.i(TAG, "Databasekey " + key);
			Log.i(TAG, "value " + value);
		} else if (value instanceof Long) {
			long l = (Long) value;
			editor.putLong(key, l);
		}
	}

	public String getString(String key) {
		return _settings.getString(key, null);
	}

	public boolean getBooleanDefValueFalse(String key) {
		return _settings.getBoolean(key, false);
	}

	public boolean getBooleanDefValueTrue(String key) // php
	{
		return _settings.getBoolean(key, true);
	}

	public float getFloat(String key) {
		return _settings.getFloat(key, -1);
	}

	public int getInt(String key) {
		return _settings.getInt(key, -1);
	}

	public long getLong(String key) {
		return _settings.getLong(key, -1);
	}

	public void setSessionId(String sessionId) {
		set(_sessionId, sessionId);
	}

	public String getSessionId() {
		return _settings.getString(_sessionId, "");
	}

	public void setWifiWatch(boolean flag) {
		set(_wifiWatch, flag);
	}

	public boolean getWifiWatch() {
		return _settings.getBoolean(_wifiWatch, false);
	}

	public void set3g4gNotify(boolean flag) {
		set(_3g4gNotify, flag);
	}

	public boolean get3g4gNotify() {
		return _settings.getBoolean(_3g4gNotify, true);
	}

	public void setPushFlag(boolean flag) {
		set(_pushFlag, flag);
	}

	public boolean getPushFlag() {
		return _settings.getBoolean(_pushFlag, true);
	}

	public boolean removeAll() {
		SharedPreferences.Editor editor = _settings.edit();
		editor.clear();
		boolean ret = editor.commit();
		if (ret == true) {
			setChanged();
			notifyObservers(SavedValues.ALL_DATA_REMOVED);
		}
		return ret;
	}

	public boolean removeDB(String key) {
		SharedPreferences.Editor editor = _settings.edit();
		editor.remove(key);
		boolean ret = editor.commit();
		if (ret == true) {
			setChanged();
			notifyObservers(SavedValues.ONE_DATA_REMOVED);
		}
		return ret;
	}
}