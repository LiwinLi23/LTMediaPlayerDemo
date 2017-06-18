package com.bestv.app.liveaudio;

import java.io.IOException;

import com.bestv.app.sdkservice.ILiveAudioForClient;
import com.bestv.app.sdkservice.ILiveAudioForServer;
import com.bestv.app.sdkservice.LiveAudioService;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

public class LiveAudioPlayer implements ILiveAudioForClient {

	private Context mContext = null;
	private Handler mHandler = null;

	private final int MSG_ONPREPARED = 1;
	private final int MSG_ONERROR = 2;
	private final int MSG_ONCOMPLETION = 3;

	private boolean bConnected = false;
	private String mPlayUrl = "";

	private LiveAudioPlayerListener mListener = null;

	private ILiveAudioForServer mLiveAudioService = null;
	private ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName arg0, IBinder arg1) {

			mLiveAudioService = ILiveAudioForServer.Stub.asInterface(arg1);

			try {
				mLiveAudioService.registerCallBack(LiveAudioPlayer.this);

				if (mPlayUrl.length()>0)
				{
					mLiveAudioService.startPlay(mPlayUrl);
					mPlayUrl = "";
				}
			} catch (RemoteException e) {
				e.printStackTrace();
			}

			bConnected = true;
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {

			bConnected = false;
		}
	};

	public LiveAudioPlayer(Context context, LiveAudioPlayerListener listener)
	{
		mContext = context;
		mListener = listener;

		mHandler = new Handler(Looper.getMainLooper())
		{
			public void handleMessage(Message msg)
			{
				switch (msg.what)
				{
				case MSG_ONPREPARED:
				{
					mListener.onPrepared();
				} break;
				case MSG_ONERROR:
				{
					int code = msg.arg1;
					mListener.onError(code);
				} break;
				case MSG_ONCOMPLETION:
				{
					mListener.onCompletion();
				} break;
				default:
					break;
				}
			}
		};

		if (!bind())
		{
			Log.e("LiveAudioPlayer", "bind bestv audio play servive failed!");
		}
	}

	private boolean bind()
	{
		Intent intent = new Intent("com.bestv.app.sdkservice.LiveAudioService");
		boolean res = mContext.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

		return res;
	}

	private void unbind()
	{
		mContext.unbindService(mConnection);
	}

	public void setStreamUrlAndStart(String url)//
	{
		if (!bConnected)
		{
			mPlayUrl = url;
		}
		else
		{
			try {
				mLiveAudioService.startPlay(url);
			} catch (RemoteException e) {
				e.printStackTrace();
			}	
		}
	}

	public void stop()
	{
		if (bConnected)
		{
			try {
				mLiveAudioService.stopPlay();
			} catch (RemoteException e) {
				e.printStackTrace();
			}			
		}
		mPlayUrl = "";
	}

	public void pause()
	{
		if (bConnected)
		{
			try {
				mLiveAudioService.pausePlay();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	public void play()
	{
		if (bConnected)
		{
			try {
				mLiveAudioService.resumePlay();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	public boolean isActivated()
	{
		boolean res = false;
		if (bConnected)
		{
			try {
				res = mLiveAudioService.isActivated();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}

		return res;
	}

	public boolean isPrepared()
	{
		boolean res = false;
		if (bConnected)
		{
			try {
				res = mLiveAudioService.isPrepared();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}

		return res;
	}

	public boolean isPaused()
	{
		boolean res = false;
		if (bConnected)
		{
			try {
				res = mLiveAudioService.isPaused();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}

		return res;
	}

	public long getCurrentPosition()
	{
		long res = 0;
		if (bConnected)
		{
			try {
				res = mLiveAudioService.getCurrentPosition();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}

		return res;
	}

	@Override
	public IBinder asBinder() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void hello_reply() throws RemoteException {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPrepared() throws RemoteException {
		Message msg = new Message();
		msg.what = MSG_ONPREPARED;
		mHandler.sendMessage(msg);
	}

	@Override
	public void onCompletion() throws RemoteException {
		Message msg = new Message();
		msg.what = MSG_ONCOMPLETION;
		mHandler.sendMessage(msg);		
	}

	@Override
	public void onError(int code) throws RemoteException {
		Message msg = new Message();
		msg.what = MSG_ONERROR;
		msg.arg1 = code;
		mHandler.sendMessage(msg);
	}
}
