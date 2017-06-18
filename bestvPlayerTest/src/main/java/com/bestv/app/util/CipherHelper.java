package com.bestv.app.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import android.util.Log;

public class CipherHelper {
	
	private static final String TAG = "CipherHelper";

	public static String encryptMD5(String key) {
		String cacheKey;
		try {
			final MessageDigest mDigest = MessageDigest.getInstance("MD5");
			mDigest.update(key.getBytes());
			cacheKey = bytesToHexString(mDigest.digest());
		} catch (NoSuchAlgorithmException e) {
			cacheKey = String.valueOf(key.hashCode());
		}
		return cacheKey;
	}

	public static String bytesToHexString(byte[] bytes) {
		// http://stackoverflow.com/questions/332079
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			String hex = Integer.toHexString(0xFF & bytes[i]);
			if (hex.length() == 1) {
				sb.append('0');
			}
			sb.append(hex.toUpperCase());
		}
		return sb.toString();
	}
	
	private static String hexString = "0123456789ABCDEF";

	public static byte[] hexString2Bytes(String s) {
		s = s.toUpperCase();
		byte[] bytes = new byte[s.length() / 2];
		for (int i = 0; i < bytes.length; i++)
			bytes[i] = (byte) (hexString.indexOf(s.charAt(i * 2)) << 4 | hexString
					.indexOf(s.charAt(i * 2 + 1)));
		return bytes;
	}

	public static byte[] decryptAES128(byte[] data, byte[] key, byte[] iv){
		try {
			//byte[] _data = Base64.decode(data, Base64.DEFAULT);
			IvParameterSpec _iv = new IvParameterSpec(iv);
			SecretKeySpec _key = new SecretKeySpec(key, "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
			cipher.init(Cipher.DECRYPT_MODE, _key, _iv);
			byte[] decryptedData = cipher.doFinal(data);
			return decryptedData;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static byte[] decryptDES(String data, String key) {
		return decryptDES(hexString2Bytes(data), key);		
	}
	
	public static byte[] decryptDES(byte[] data, String key) {
		try {
			SecureRandom random = new SecureRandom();
			DESKeySpec desKey = new DESKeySpec(key.getBytes());
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			SecretKey securekey = keyFactory.generateSecret(desKey);
			Cipher cipher = Cipher.getInstance("DES");
			cipher.init(Cipher.DECRYPT_MODE, securekey, random);
			byte[] result = cipher.doFinal(data);
			Log.i(TAG, "from [" + bytesToHexString(data) + "] to ["
					+ new String(result) + "]");
			return hexString2Bytes(new String(result));
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
		
	}
	
	public static String encryptDES(String data, String key) {
		try {
			SecureRandom random = new SecureRandom();
			DESKeySpec desKey = new DESKeySpec(key.getBytes());
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			SecretKey securekey = keyFactory.generateSecret(desKey);			
			
			
			Cipher cipher = Cipher.getInstance("DES");
			cipher.init(Cipher.ENCRYPT_MODE, securekey, random);
			byte[] result = cipher.doFinal(data.getBytes());
			Log.i(TAG, "from [" + data + "] to ["
					+ bytesToHexString(result).toLowerCase() + "]");
			return bytesToHexString(result);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
		
	}
	
	public static String generateCDNToken() {
		String uid = "android_test";
		String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
		
		Log.i(TAG, timestamp);
		
		return encryptDES(uid + "_" + timestamp, "BestV_+8");
	}


	public static byte[] hmacsha256(byte[] content, byte[] key) throws Exception {
        SecretKey secretKey = new SecretKeySpec(key, "HmacSHA256");
        Mac mac = Mac.getInstance(secretKey.getAlgorithm());
        mac.init(secretKey);
        return mac.doFinal(content);
    }
	
	public static String UrlEnc(String s)
	{
		String res = "";
		try {
			res = URLEncoder.encode(s, "utf-8");
		} catch (UnsupportedEncodingException e) {
			res = "";
		}
		
		return res;
	}
}
