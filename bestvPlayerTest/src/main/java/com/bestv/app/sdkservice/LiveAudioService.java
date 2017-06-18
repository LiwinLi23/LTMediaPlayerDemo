package com.bestv.app.sdkservice;

import java.io.IOException;

import com.bestv.app.sdkservice.ILiveAudioForClient;
import com.bestv.app.sdkservice.ILiveAudioForServer;
import com.bestv.app.util.HttpRequestTool;
import com.bestv.app.util.ResponseContent;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.RemoteException;

public class LiveAudioService extends Service {
	
	ILiveAudioForClient mCallback;
	LiveAudioBinder mBinder = new LiveAudioBinder();
	
	//members
	static MediaPlayer gMediaPlayer = null;
	static boolean gActivated = false;	
	static boolean gPrepared = false;
	static boolean gPaused = false;

	class LiveAudioBinder extends ILiveAudioForServer.Stub{

		@Override
		public void registerCallBack(ILiveAudioForClient cb)
				throws RemoteException {
			// TODO Auto-generated method stub
			mCallback = cb;
			
		}

		@Override
		public String hello() throws RemoteException {
			// TODO Auto-generated method stub
			mCallback.hello_reply();
			
			return "hello123";
		}

		@Override
		public void startPlay(String url) throws RemoteException {
			// TODO Auto-generated method stub
			if (gActivated)
			{
				stopPlay();
			}
			
			//get redirected play url
			final String _url = url;
			new AsyncTask<Object, Object, String>() {
				@Override
				protected void onPreExecute() {
					
				}

				@Override
				protected String doInBackground(Object... params) {

					String res = getRedirectedUrl(_url);
								
					return res;
				}

				@Override
				protected void onPostExecute(String redirectedUrl) {
					
					if (gMediaPlayer == null)
					{
						gMediaPlayer = new MediaPlayer();

						try {
							gMediaPlayer.setOnPreparedListener(new OnPreparedListener(){

								@Override
								public void onPrepared(MediaPlayer arg0) {
									arg0.start();
									
									gPrepared = true;

									try {
										mCallback.onPrepared();
									} catch (RemoteException e) {
										e.printStackTrace();
									}
								}
								
							});
							gMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
								
								@Override
								public void onCompletion(MediaPlayer arg0) {

									try {
										mCallback.onCompletion();
									} catch (RemoteException e) {
										e.printStackTrace();
									}
								}
							});
							gMediaPlayer.setOnErrorListener(new OnErrorListener() {
								
								@Override
								public boolean onError(MediaPlayer arg0, int arg1, int arg2) {

									try {
										mCallback.onError(arg1);
									} catch (RemoteException e) {
										e.printStackTrace();
									}
									return false;
								}
							});

							gMediaPlayer.setDataSource(LiveAudioService.this.getApplicationContext(), Uri.parse(redirectedUrl));					
							gMediaPlayer.prepareAsync();
							
							gActivated = true;
							
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}

			}.execute();
			
		}

		@Override
		public void stopPlay() throws RemoteException {
			// TODO Auto-generated method stub
			if (gActivated)
			{
				if (gMediaPlayer != null)
				{
					gMediaPlayer.stop();
					gMediaPlayer.release();
					gMediaPlayer = null;
				}
				
				gActivated = false;
				gPaused = false;
				gPrepared = false;
			}
		}

		@Override
		public void pausePlay() throws RemoteException {
			// TODO Auto-generated method stub
			if (gActivated)
			{
				if (!gPaused)
				{
					gMediaPlayer.pause();
					gPaused = true;
				}
			}
		}

		@Override
		public void resumePlay() throws RemoteException {
			// TODO Auto-generated method stub
			if (gActivated)
			{
				if (gPaused)
				{
					gMediaPlayer.start();
					gPaused = false;
				}
			}
		}

		@Override
		public boolean isActivated() throws RemoteException {
			// TODO Auto-generated method stub
			return gActivated;
		}

		@Override
		public boolean isPrepared() throws RemoteException {
			// TODO Auto-generated method stub
			return gPrepared;
		}

		@Override
		public boolean isPaused() throws RemoteException {
			// TODO Auto-generated method stub
			return gPaused;
		}

		@Override
		public long getCurrentPosition() throws RemoteException {
			// TODO Auto-generated method stub
			if (gMediaPlayer != null && gMediaPlayer.isPlaying())			
				return gMediaPlayer.getCurrentPosition();
			else
				return 0;
		}
		
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return mBinder;
	}

	private String getRedirectedUrl(final String uri)
	{
		String res = uri;
		
		boolean isRedirected = false;
		do
		{
			ResponseContent content = HttpRequestTool.getResponseContent(res, null, null, null, false);
			if (content.status == 200)
			{
				break;
			}
			else if (content.status == 302)
			{
				isRedirected = true;
				res = content.redirectLocation;
			}
			else
			{
				res = uri;
				break;
			}
			
		} while (isRedirected);
		
		return res;
	}
}
