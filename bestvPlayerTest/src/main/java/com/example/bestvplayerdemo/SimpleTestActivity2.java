package com.example.bestvplayerdemo;

import java.lang.ref.WeakReference;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import android.net.Uri;

import com.bestv.app.util.SystemStatusManager;
import com.bestv.app.view.VideoViewListener;
import com.bestv.app.view.VideoViewShell;

public class SimpleTestActivity2 extends Activity {

	private View mRootLayout;
	private FrameLayout mVideoFrame;
	private LinearLayout mBottomFrame;
	private VideoViewShell mPlayer;
	private ImageButton mImgBtnResize;
	private VideoViewListener mListener;
	
	private SeekBar mSeek;
	private TextView mTextTime;
	private TextView mTextTotalTime;
	private Button mBtnPlayPause;
	private Button mBtnStop;
	private Button mBtnLayoutScale;
	private Button mBtnLayoutStretch;
	private TextView mTextDownloadRate;
	private TextView mTextStatus;

	private static final int SHOW_PROGRESS = 1;
	private static final int SHOW_FULLSCREEN = 2;
	private static final int SHOW_HALFSCREEN = 3;
	
	public static Boolean isLive = false;
	private final Handler SimpleTestHandler = new SimpleTestHandler(this);
	
	private Boolean isFullScreen = false;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_simpletest);

		Uri uri = getIntent().getData();
		if (null != uri)
			mUrlStr = getIntent().getData().toString();

		Log.i(TAG, "mUrlStr: " + mUrlStr);
		
		mRootLayout = findViewById(R.id.simpletest_root_layout);
		
		mVideoFrame = (FrameLayout)mRootLayout.findViewById(R.id.frame_v);
		mPlayer = (VideoViewShell)mVideoFrame.findViewById(R.id.videoView1);
		mImgBtnResize = (ImageButton)mVideoFrame.findViewById(R.id.imgBtnResize);

		mBottomFrame = (LinearLayout)mRootLayout.findViewById(R.id.frame_control);
		mSeek = (SeekBar)mRootLayout.findViewById(R.id.seek);
		mTextTime = (TextView)mRootLayout.findViewById(R.id.txt_time);
		mTextTotalTime = (TextView)mRootLayout.findViewById(R.id.txt_total_time);
		mTextDownloadRate = (TextView)mBottomFrame.findViewById(R.id.txt_download_rate);
		mTextStatus = (TextView)mBottomFrame.findViewById(R.id.txt_status);		
		mBtnPlayPause = (Button)mBottomFrame.findViewById(R.id.btnPlayPause);
		mBtnStop = (Button)mBottomFrame.findViewById(R.id.btnStop);
		mBtnStop.setEnabled(false);
		mBtnLayoutScale = (Button)mBottomFrame.findViewById(R.id.btnLayoutScale);
		mBtnLayoutStretch = (Button)mBottomFrame.findViewById(R.id.btnLayoutStretch);
		mListener = new VideoViewListener(){
			
			@Override
			public void onPrepared(long arg0, int arg1, int arg2) {
				
				long duration = arg0;
				mTextTotalTime.setText("/"+sec_to_timeFormat((int)duration));
//				mTextStatus.setText("Prepared");
				mBtnPlayPause.setEnabled(true);
				
//				for test
//				mPlayer.seekTo(30000);
			}

			@Override
			public void onBufferStart() {
				mTextStatus.setText("Buffer starting");
				
			}

			@Override
			public void onBufferEnd() {
				mTextStatus.setText("Buffer end");
				
			}

			@Override
			public void onCompletion() {
				mTextStatus.setText("Completion");
				
			}

			@Override
			public boolean onError(int arg0, int arg1) {
				mTextStatus.setText("Error"+arg0);
				SimpleTestActivity2.this.finish();
				return false;
			}

			@Override
			public boolean onNetStreamingReport(int arg0) {
				int rate = arg0;
				//Log.d("", "download rate is " + rate + "kBps");
				if (!mPlayer.IsPlayerBuffering())
				{
					mTextDownloadRate.setText(rate + "kB/s");
				}
				return false;
			}

			@Override
			public void onPlayerClick() {				
			}

			@Override
			public void onAdBegin() {				
			}

			@Override
			public void onAdEnd() {
			}

			@Override
			public void onBufferingUpdate(int arg0) {
				mTextStatus.setText("Buffering update.."+arg0+"%");
				
			}};

		mPlayer.setPlayerEventListner(mListener);
			
		mBtnPlayPause.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View arg0) {

					if (mPlayer.IsPlayerStop())
					{
						if (!isLive)
						{
							// HashMap<String, String> headers = new HashMap<String, String>();
							// headers.put("QQ", "12345678");
							if (DBG)
								mPlayer.StartPlay(LOCAL_FILE, null);
							else
								mPlayer.StartPlay((null != mUrlStr) ? mUrlStr : DEFAULT_URL, null);

						}
						
						mHandler.sendEmptyMessageDelayed(SHOW_PROGRESS, 1000);
						
						mBtnPlayPause.setText("pause");
						mTextStatus.setText("Pause..");
						mBtnPlayPause.setEnabled(false);
						mBtnStop.setEnabled(true);
					}
					else
					{
						if (!mPlayer.IsPlayerPaused())
						{
							mPlayer.pause();
							mTextStatus.setText("��ͣ");
							mBtnPlayPause.setText("play");
						}
						else
						{
							mPlayer.play();
							mTextStatus.setText("������");
							mBtnPlayPause.setText("pause");
						}
					}
					
				}});

			mBtnStop.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					if (!mPlayer.IsPlayerStop())
					{
						mPlayer.stop();
						
						mPlayer.invalidate();
						mTextStatus.setText("����");
						mBtnPlayPause.setText("play");
						mBtnStop.setEnabled(false);
						mBtnPlayPause.setEnabled(true);
						
						//test
						//mPlayer.StartPlay(SimpleTestActivity.this, "2185508", "1", "2345");
					}

					mHandler.removeMessages(SHOW_PROGRESS);
				}});
						
			mSeek.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

				@Override
				public void onProgressChanged(SeekBar arg0, int arg1,
						boolean arg2) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void onStartTrackingTouch(SeekBar arg0) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onStopTrackingTouch(SeekBar arg0) {
					if (!mPlayer.IsLiveStreamMode() && mPlayer.IsPlayerPrepared())
					{
						long progress = mSeek.getProgress();
						long seek = mPlayer.getDuration() * progress / mSeek.getMax();
						mPlayer.seekTo((int)seek);
					}							
				}
			});

			mBtnPlayPause.performClick();

			mImgBtnResize.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Log.e("", "resize");
					if (isFullScreen) {
						mHandler.sendEmptyMessage(SHOW_HALFSCREEN);
					} else {
						mHandler.sendEmptyMessage(SHOW_FULLSCREEN);
					}

				}
			});
			
			mBtnLayoutScale.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					mPlayer.seekTo(1000*(0*3600+26*60+5));
//					mPlayer.SetVideoLayout(VideoViewShell.VIDEO_LAYOUT_SCALE, 0);
				}
			});
			
			mBtnLayoutStretch.setOnClickListener(new OnClickListener() {
				
				
				@Override
				public void onClick(View v) {
//					mPlayer.SetVideoLayout(VideoViewShell.VIDEO_LAYOUT_STRETCH, 0);
				}
			});
			
		mHandler.sendEmptyMessage(SHOW_HALFSCREEN);
	}

	private boolean bStartWhenResume  = false; 
	protected void onPause()
	{
		super.onPause();
		
//		if (!mPlayer.IsPlayerStop() && !mPlayer.IsPlayerPaused())
//		{
//			bStartWhenResume = true;
//			mPlayer.pause();
//		}

		mHandler.removeMessages(SHOW_PROGRESS);
	}
	
	protected void onResume()
	{
		super.onResume();

		mHandler.sendEmptyMessageDelayed(SHOW_PROGRESS, 1000);
		
//		if (bStartWhenResume)
//		{
//			bStartWhenResume = false;
//			mPlayer.start();
//		}
	}
	
	protected void onDestroy()
	{
		super.onDestroy();
		
		mPlayer.release();
	}
	
	private final SimpleTestHandler mHandler = new SimpleTestHandler(this);
	
	private static class SimpleTestHandler extends Handler{
		private final WeakReference<SimpleTestActivity2> activity;
		public SimpleTestHandler(SimpleTestActivity2 _activity)
		{
			activity = new WeakReference<SimpleTestActivity2>(_activity);
		}

		@Override
		public void handleMessage(Message msg){
			if (activity.get() == null)
			{
				return;
			}
			switch (msg.what) {
			case SHOW_FULLSCREEN:
				activity.get().zoomFull();
				break;
			case SHOW_HALFSCREEN:
				activity.get().zoomHalf();
				break;
			case SHOW_PROGRESS:
				activity.get().handleShowProgress();
				activity.get().mHandler.sendEmptyMessageDelayed(SHOW_PROGRESS, 800);
				break;
			}
		}
	}
	
	private void handleShowProgress(){
		if (!mPlayer.IsPlayerStop() && mPlayer.IsPlayerPrepared())
		{
			long current_msec = mPlayer.getCurrentPosition();
			mTextTime.setText(sec_to_timeFormat((int)current_msec));
			
			if (!mPlayer.IsLiveStreamMode())
			{
				long total_msec = mPlayer.getDuration();
				if (total_msec>0)
				{
					long progress = current_msec*mSeek.getMax()/total_msec;
					mSeek.setProgress((int)progress); 
				}
			}
		}
	}
	
	static private String sec_to_timeFormat(int msec)
	{
		int sec = msec/1000;
		int hh = sec/3600;
		int mm = (sec%3600)/60;
		int ss = sec%60;
		
		return String.format("%d:%02d:%02d", hh, mm, ss);
	}
	
	private void zoomFull(){
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		landscapeVideoView();
	}
	
	private void zoomHalf(){
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		portraitVideoView();
	}
	
	@SuppressWarnings("deprecation")
	private void landscapeVideoView() {
		WindowManager windowManager = (WindowManager) getSystemService("window");
		Display display = windowManager.getDefaultDisplay();
		int nScreenW = display.getWidth();
		SystemStatusManager sysManager = new SystemStatusManager(SimpleTestActivity2.this);
		int h = sysManager.getConfig().getStatusBarHeight();
		int nScreenH = display.getHeight() -h; 

		ViewGroup.LayoutParams layoutParams = this.mVideoFrame.getLayoutParams();
		layoutParams.width = nScreenW;
		layoutParams.height = nScreenH;

		Log.e("", "land   " + nScreenW + ", " + nScreenH);
		this.mVideoFrame.setLayoutParams(layoutParams);

		mBottomFrame.setVisibility(View.GONE);
		isFullScreen = true;
	}

	@SuppressWarnings("deprecation")
	private void portraitVideoView() {
		WindowManager windowManager = (WindowManager) getSystemService("window");
		Display display = windowManager.getDefaultDisplay();
		int nScreenW = display.getWidth();
		int nScreenH = display.getHeight();

		ViewGroup.LayoutParams layoutParams = this.mVideoFrame.getLayoutParams();
		layoutParams.width = nScreenW;
		int hvideo = nScreenW * 3 / 4; // nScreenW * nScreenW / nScreenH
		layoutParams.height = hvideo;

		Log.e("", "port   " + nScreenW + ", " + nScreenW * nScreenW / nScreenH);
		this.mVideoFrame.setLayoutParams(layoutParams);

		mBottomFrame.setVisibility(View.VISIBLE);

		layoutParams = this.mBottomFrame.getLayoutParams();
		layoutParams.width = nScreenW;
		layoutParams.height = nScreenH - hvideo;
		this.mBottomFrame.setLayoutParams(layoutParams);

		displayControlbar(false);

		isFullScreen = false;
	}
	
	private synchronized void displayControlbar(boolean bVisible) {
		
	}

	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {

//			mPlayer.SetVideoLayout(VideoViewShell.VIDEO_LAYOUT_SCALE, 0);
//			mPlayer.SetVideoLayout(VideoViewShell.VIDEO_LAYOUT_STRETCH, 0);
			
			// AndroidTool.fullScreen(SimpleTestActivity2.this, true);
			
			LayoutParams params = (LayoutParams) mVideoFrame.getLayoutParams(); 
			params.height =LayoutParams.MATCH_PARENT;
			params.width = LayoutParams.MATCH_PARENT;
			mVideoFrame.setLayoutParams(params);
						
//			mImgBtnResize.setImageResource(R.drawable.button_half);
	    } else {
	    	//Log.e("On Config Change", "PORTRAIT");

//			mPlayer.SetVideoLayout(VideoViewShell.VIDEO_LAYOUT_SCALE, 0);
//			mPlayer.SetVideoLayout(VideoViewShell.VIDEO_LAYOUT_STRETCH, 0);
	        
	    	// AndroidTool.fullScreen(SimpleTestActivity2.this, false);
	    	
//	    	mImgBtnResize.setImageResource(R.drawable.button_full);
	    }

		super.onConfigurationChanged(newConfig);
	}

	private static final String TAG = SimpleTestActivity2.class.getName();
	private static final boolean DBG = false;
	private static final String DEFAULT_URL =
			"http://devimages.apple.com/iphone/samples/bipbop/bipbopall.m3u8";
	private static final String LOCAL_FILE = "/sdcard/test.ts";
	private String mUrlStr;
}
