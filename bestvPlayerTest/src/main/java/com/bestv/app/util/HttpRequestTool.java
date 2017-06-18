package com.bestv.app.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import android.util.Log;

public class HttpRequestTool {

	private static final String TAG = "HttpRequestTool";

	private static TrustManager myX509TrustManager = new X509TrustManager() { 

	    @Override 
	    public X509Certificate[] getAcceptedIssuers() { 
	        return null; 
	    }

		@Override
		public void checkClientTrusted(X509Certificate[] arg0, String arg1)
				throws CertificateException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void checkServerTrusted(X509Certificate[] arg0, String arg1)
				throws CertificateException {
			// TODO Auto-generated method stub
			
		} 

	};
	
	public static ResponseContent getResponseContent(String baseUrl, String params, String post_body, Map<String,String>headers, boolean isAllowRedirect) {
		
		ResponseContent ret = new ResponseContent();
		ret.status = 0;
		ret.content = "";
		ret.redirectLocation = "";

		URLConnection httpCon = null;
		InputStream inStream = null;
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();

		long cur_tick = System.currentTimeMillis();
		long start_tick = cur_tick;
		
		int iQuery = baseUrl.indexOf("?");
		String brief_debug_url = iQuery>0?baseUrl.substring(0, iQuery):baseUrl;
		Log.e(TAG, String.format("enter getResponseString(%s?..)", brief_debug_url));
		
		try
		{
			String httpGetUrl = null;
			if (params == null) {
				httpGetUrl = baseUrl;
			} else {
				URLEncoder.encode(params, "utf-8");
				httpGetUrl = String.format("%s?%s", baseUrl, params);
			}
			
			URL httpUrl = new URL(httpGetUrl);
			httpCon = httpUrl.openConnection();
			httpCon.setUseCaches(false);
			httpCon.setConnectTimeout(5000);
			httpCon.setReadTimeout(5000);
						
			if (headers!=null)
			{
				Iterator it = headers.keySet().iterator();			
				while (it.hasNext())
				{
					String key = it.next().toString();
					String val = headers.get(key);
					httpCon.addRequestProperty(key, val);				
				}
			}

			if (!isAllowRedirect)
			{
				((HttpURLConnection)httpCon).setInstanceFollowRedirects(false);
//				((HttpURLConnection)httpCon).setFollowRedirects(false);
			}
			
			if (post_body != null)
			{
				if (baseUrl.indexOf("https")==0)
				{
		            SSLContext sslcontext = SSLContext.getInstance("TLS"); 
		            sslcontext.init(null, new TrustManager[]{myX509TrustManager}, null);
		            
					((HttpsURLConnection)httpCon).setSSLSocketFactory(sslcontext.getSocketFactory());
				}
				
				((HttpURLConnection)httpCon).setRequestMethod("POST");
				
				httpCon.setUseCaches(false);
				httpCon.setDoOutput(true);
				
				OutputStream postOutStream = httpCon.getOutputStream();//getOutputStream involve httpCon.connect()
				if (null != postOutStream)
				{
					byte[] buff = post_body.getBytes("utf-8");
					postOutStream.write(buff);
				}
			}
			else
			{
				//open input http response stream
				httpCon.connect();
			}
			
			Log.e(TAG, String.format("http connected, connect time=%dms", System.currentTimeMillis()-cur_tick));
			cur_tick = System.currentTimeMillis();
			
			int httpResponseCode = ((HttpURLConnection) httpCon).getResponseCode();
			ret.status = httpResponseCode;
			
			if (httpResponseCode == 200)
			{
				inStream = httpCon.getInputStream();
				
				if (null != inStream) {
					byte[] buf = new byte[1024];
					int nRead = 0;

					while ((nRead = inStream.read(buf)) != -1) {
						outStream.write(buf, 0, nRead);
					}
					
					Log.e(TAG, String.format("receive time=%dms", System.currentTimeMillis()-cur_tick));
					cur_tick = System.currentTimeMillis();
				}
			}
			else if (httpResponseCode == 302)
			{
				ret.redirectLocation = ((HttpURLConnection) httpCon).getHeaderField("Location");
			}
		}
		catch (Exception ex)
		{
			Log.e(TAG, String.format("Exception caught: name=%s, message=%s", ex.toString(), ex.getMessage()));
		}
		finally 
		{
			try {

				if (inStream != null)
					inStream.close();

				if (httpCon != null)
					((HttpURLConnection) httpCon).disconnect();

				if (outStream != null)
				{
					outStream.flush();
					ret.content = outStream.toString("utf-8");
					
					outStream.close();
				}
				
			} catch (Exception e) {
			}
		}
		Log.e(TAG, String.format("leave getResponseString, t=%dms, ret_length=%d", System.currentTimeMillis()-start_tick, String.valueOf(ret).length()));
		return ret;
	}
	
	public static ResponseContent getResponseContent(String baseUrl, String params, String post_body, Map<String,String>headers) {
		return getResponseContent(baseUrl, params, post_body, headers, true);
	}
	
	public static String getResponseString(String baseUrl, String params, String post_body, Map<String,String>headers)
	{
		return getResponseContent(baseUrl, params, post_body, headers).content;
	}
}
