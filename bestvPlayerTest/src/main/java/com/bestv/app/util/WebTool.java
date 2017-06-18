package com.bestv.app.util;

import java.io.IOException;
import java.net.URLEncoder;

import android.util.Log;

public class WebTool {
	private static final String TAG="WebTool";
	public static final String HTTP_ERROR="HTTP_ERROR";
	
	private static final int REQUEST_RETRY_TIMES = 3;  //设置http重连
	
	public static boolean IsDataOK(String data) {
		if (data == null || data.trim().equals(HTTP_ERROR)
				|| data.trim().equals(""))
			return false;

		return true;
	}
	
}
