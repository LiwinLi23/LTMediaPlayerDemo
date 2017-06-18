package com.bestv.priv.util;

import java.util.List;

import android.content.Context;
import android.util.Log;

import com.bestv.app.util.HttpRequestTool;
import com.bestv.app.util.NetWorkUtil;
import com.bestv.app.util.ResponseContent;
import com.bestv.app.util.StringTool;

public class AndroidToolPriv {
	/**
     * �ص��������ӿ�
     * @param _context
     * @param _replyAdNode
     */    
	public static void replyAd(Context _context, List<String> _list) {
		try {
			final Context mContext = _context;
			final List<String> list = _list;
			if (list != null && list.size() > 0) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						for (int i = 0; i < list.size(); i++) {
							String requestUrl = list.get(i);
							if (requestUrl != null && !StringTool.isEmpty(requestUrl)) {
								try {
									Log.e("Ad Callback", String.valueOf(requestUrl));
//									String ret = CustomerHttpClient.GetFromWebByHttpClient(mContext, list.get(i));
									ResponseContent content = HttpRequestTool.getResponseContent(requestUrl, null, null, null);
									Log.e("Ad Callback", String.valueOf(content.status));
								} catch (Exception e) {
//									Log.e(TAG, "�ص��쳣��" + e.getMessage());
								}
							}
						}
					}
				}).start();
			}
		} catch (Exception e) {
//			Log.e(TAG, "���ص��ӿ��쳣,�����лص�,�쳣��Ϣ:" + e.getMessage());
		}
	}
	
	/**
	 * ƴ�ӹ������ӿڲ���
	 * @param context
	 * @param adId
	 * @return
	 */
	public static String getAdUrlParams(Context context, String adId) {
		String width = "1280";
		String height = "720";
//		if (adId.equals(Properties.bannerAdId)) {
//			width = "750";
//			height = "375";
//		}
		
		String con = NetWorkUtil.getCurrentNetworkType(context);
		String carrier = NetWorkUtil.getProvider(context);
		
		String url = "adspaceid="+adId+
				"&adtype=2"+
				"&width="+ width +
				"&height="+ height +
				"&pid=2"+
				"&pcat=10"+
				"&media=1" +
				"&uid=" +
				"&idfa=" +
				"&oid=" +
				"&vid=" +
				"&aid=" +
				"&aaid=" +
				"&imei=" + NetWorkUtil.getImei(context) +
				"&wma=" +
				"&os=0" +
				"&osv=" + android.os.Build.VERSION.SDK_INT +
				// "&pkgname=" + AndroidTool.getPkgName(context) +
				"&appname=" + "SDK" +
				"&conn=" + con +
				"&carrier=" + carrier +
				//"&density=" + AndroidTool.getDensity(context) +
				"&cell=" +
				// "&device=" + AndroidTool.getDeviceName() +
				"&apitype=4"+
				"&ua="+
				"&ip=";
		
//		Log.e(TAG, url);
		
		return url;
	}
}
