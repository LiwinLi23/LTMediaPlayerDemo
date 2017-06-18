package com.bestv.app.view;

import java.io.IOException;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnSeekCompleteListener;
//import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
//import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class LocalVideoView extends SurfaceView {

	private Context mContext;
	
//	private final int STATE_ERROR = -1;
	private final int STATE_IDLE = 0;
	private final int STATE_PREPARING = 1;
	private final int STATE_PREPARED = 2;
	
	private int mCurrentState = STATE_IDLE;
	
	private boolean isPause = false;
	private boolean isStop = true;
	
	private String mVideoFilePath;	
	private Uri mUri;
	private Map<String, String> mHeaders;
	private long mDuration;
	private long mSeekWhenPrepared; // recording the seek position while preparing
	  
	private MediaPlayer mMediaPlayer;
	private SurfaceHolder mSurfaceHolder;
	private int mVideoWidth;
	private int mVideoHeight;

	private OnPreparedListener mOnPreparedListener = null;
//	private OnBufferingUpdateListener mOnBufferingUpdateListener = null;
	private OnSeekCompleteListener mOnSeekCompleteListener = null;
	private OnCompletionListener mOnCompletionListener = null;
	private OnErrorListener mOnErrorListener = null;
//	private OnInfoListener mOnInfoListener = null;
	
	private int inner_error_retry = 0;
	private long last_retry_tick = 0;
	
	private OnPreparedListener onPreparedListener = new OnPreparedListener(){

		@Override
		public void onPrepared(MediaPlayer mp) {			
			
			if (mOnPreparedListener != null)
				mOnPreparedListener.onPrepared(mp);

			mCurrentState = STATE_PREPARED;
					
		      mVideoWidth = mp.getVideoWidth();
		      mVideoHeight = mp.getVideoHeight();
		      mDuration = mp.getDuration();

		      long seekToPosition = mSeekWhenPrepared;
		      if (seekToPosition != 0)
		        seekTo(seekToPosition);
		      
		      mSeekWhenPrepared = 0;
		      
		      start();
		      
		      inner_error_retry = 0;
		}
	};
	
//	private OnBufferingUpdateListener onBufferingUpdateListener = new OnBufferingUpdateListener(){
//
//		@Override
//		public void onBufferingUpdate(MediaPlayer arg0, int arg1) {
//			//do nothing			
//		}
//	};
	private OnSeekCompleteListener onSeekCompleteListener = new OnSeekCompleteListener(){

		@Override
		public void onSeekComplete(MediaPlayer arg0) {
			if (mOnSeekCompleteListener != null)
			{
				mOnSeekCompleteListener.onSeekComplete(arg0);
			}
		}

	};
	private OnCompletionListener onCompletionListener = new OnCompletionListener(){

		@Override
		public void onCompletion(MediaPlayer arg0) {
			if (mOnCompletionListener != null)
			{
				mOnCompletionListener.onCompletion(arg0);
			}
		}
	};
	private OnErrorListener onErrorListener = new OnErrorListener(){

		@Override
		public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
			
			if (System.currentTimeMillis() - last_retry_tick > 5000)
			{
				if (mOnErrorListener != null)
				{
					mOnErrorListener.onError(arg0, arg1, arg2);
				}
				
				last_retry_tick = System.currentTimeMillis();
				inner_error_retry++;
			}
			return false;
		}
	};
//	private OnInfoListener onInfoListener = new OnInfoListener(){
//
//		@Override
//		public boolean onInfo(MediaPlayer arg0, int arg1, int arg2) {
//			//do nothing
//			
//			return false;
//		}
//	};
	
	public LocalVideoView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		initVideoView(context);
	}
	
	public LocalVideoView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		initVideoView(context);
	}
	
	public LocalVideoView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		initVideoView(context);
	}
	
	SurfaceHolder.Callback mSHCallback = new SurfaceHolder.Callback() {
		@Override
		public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2,
				int arg3) {
			// TODO Auto-generated method stub
			if (mMediaPlayer != null)
			{
				mMediaPlayer.setDisplay(arg0);//这里必须在surface初始化成功后执行
			}
			
			mSurfaceHolder = arg0;
			
		}

		@Override
		public void surfaceCreated(SurfaceHolder arg0) {
			// TODO Auto-generated method stub

			if (mMediaPlayer != null)
			{
				mMediaPlayer.setDisplay(arg0);//这里必须在surface初始化成功后执行
			}
			
			mSurfaceHolder = arg0;
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder arg0) {
			// TODO Auto-generated method stub

			if (mMediaPlayer != null)
			{
				mMediaPlayer.setDisplay(null);
			}
		}

	};

	private void initVideoView(Context ctx) {
		mContext = ctx;
		mSurfaceHolder = this.getHolder();
		mSurfaceHolder.addCallback(mSHCallback);

	}

	public void setOnPreparedListener(OnPreparedListener l)
	{
		this.mOnPreparedListener = l;
	}
//	public void setOnBufferingUpdateListener(OnBufferingUpdateListener l)
//	{
//		this.mOnBufferingUpdateListener = l;
//	}
	public void setOnSeekCompleteListener(OnSeekCompleteListener l)
	{
		this.mOnSeekCompleteListener = l;
	}
	public void setOnCompletionListener(OnCompletionListener l)
	{
		this.mOnCompletionListener = l;
	}
	public void setOnErrorListener(OnErrorListener l)
	{
		this.mOnErrorListener = l;
	}
//	public void setOnInfoListener(OnInfoListener l)
//	{
//		this.mOnInfoListener = l;
//	}
	
	void release() {
		if (mMediaPlayer != null)
		{
			mMediaPlayer.reset();
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
	}
	
	public void setVideoLayout(int layout, float aspectRatio) {
	}
	
	 public void setVideoURI(Uri uri, Map<String, String> headers) {
	    mUri = uri;
	    mHeaders = headers;
	    openVideo();
	 }
	 
	 public void setVideoFilePath(String path) {
		 mVideoFilePath = path;
		 openVideo();
	 }
	 
	 public void stopPlayback()
	 {
		 if (mMediaPlayer != null)
		 {
			 mMediaPlayer.stop();
			 mMediaPlayer.release();
			 mMediaPlayer = null;
		 }
		 
		 mDuration = 0;
		 mVideoWidth = 0;
		 mVideoHeight = 0;
		 mUri = null;
		 mHeaders = null;
		 
		 mCurrentState = STATE_IDLE;
	 }
	 
	 private void openVideo() {
	    if ((mVideoFilePath == null && mUri == null) || mSurfaceHolder == null)
	      return;

	    Intent i = new Intent("com.android.music.musicservicecommand");
	    i.putExtra("command", "pause");
	    mContext.sendBroadcast(i);

	    release();
		try {
			
			mMediaPlayer = new MediaPlayer();
			mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//			mMediaPlayer.setDisplay(LocalVideoView.this.getHolder());//这里必须在surface初始化成功后执行
			mMediaPlayer.setDisplay(mSurfaceHolder);//这里必须在surface初始化成功后执行

			mMediaPlayer.setOnPreparedListener(onPreparedListener);
	//		mMediaPlayer.setOnBufferingUpdateListener(onBufferingUpdateListener);
			mMediaPlayer.setOnSeekCompleteListener(onSeekCompleteListener);
			mMediaPlayer.setOnCompletionListener(onCompletionListener);
			mMediaPlayer.setOnErrorListener(onErrorListener);
	//		mMediaPlayer.setOnInfoListener(onInfoListener);
			if (mUri != null)
			{
				mMediaPlayer.setDataSource(mContext, mUri, mHeaders);
			}
			else if (mVideoFilePath != null && mVideoFilePath.length()>0)
			{
				mMediaPlayer.setDataSource(mVideoFilePath);
			}

		    mCurrentState = STATE_PREPARING;
		    mMediaPlayer.setScreenOnWhilePlaying(true);
			mMediaPlayer.prepareAsync();
			
			
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			onErrorListener.onError(mMediaPlayer, 0, 0);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			onErrorListener.onError(mMediaPlayer, 0, 0);
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			onErrorListener.onError(mMediaPlayer, 0, 0);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			onErrorListener.onError(mMediaPlayer, 0, 0);
		}
		
	}
	 
	 public void pause() {
	    if (isInPlaybackState()) {
	      if (mMediaPlayer.isPlaying()) {
	        mMediaPlayer.pause();
	      }
	    }
	  }
	 
	  public void start() {
	    if (isInPlaybackState()) {
	      mMediaPlayer.start();
	    }
	  }
	  
	  public long getDuration() {
	    if (isInPlaybackState()) {
	      if (mDuration > 0)
	        return mDuration;
	      mDuration = mMediaPlayer.getDuration();
	      return mDuration;
	    }
//	    mDuration = -1;
	    return mDuration;
	  }

	  public long getCurrentPosition() {
	    if (isInPlaybackState())
	      return mMediaPlayer.getCurrentPosition();
	    return 0;
	  }

	  public void seekTo(long msec) {
	    if (isInPlaybackState()) {
	      mMediaPlayer.seekTo((int)msec);
	      mSeekWhenPrepared = 0;
	    } else {
	      mSeekWhenPrepared = msec;
	    }
	  }

	  public boolean isPlaying() {
	    return isInPlaybackState() && mMediaPlayer.isPlaying();
	  }
		  
	  protected boolean isInPlaybackState() {
	    return (mMediaPlayer != null && this.mCurrentState != STATE_IDLE && this.mCurrentState != STATE_PREPARING);
	  }
	  
	  public int getVideoWidth()
	  {
		  return mVideoWidth;
	  }
	  
	  public int getVideoHeight()
	  {
		  return mVideoHeight;
	  }
}
