package com.bestv.app.view;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.PixelFormat;
//import android.media.MediaPlayer;
//import android.media.MediaPlayer.OnCompletionListener;
//import android.media.MediaPlayer.OnErrorListener;
//import android.media.MediaPlayer.OnPreparedListener;
//import android.media.MediaPlayer.OnSeekCompleteListener;
import com.xbfxmedia.player.IMediaPlayer;
import com.xbfxmedia.player.XBFXAndroidMediaPlayer;
import com.xbfxmedia.player.XBFXAndroidMediaPlayer.*;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.bestv.app.util.CipherHelper;
import com.bestv.app.util.Properties;
import com.bestv.app.util.StringTool;

public class VideoViewShell extends RelativeLayout {

	private static final String TAG = "VideoViewShell";

	private static final int CHECK_STATUS = 10010;
	private static final int START_PLAY = 10030;

	private Context mContext;

	private VideoViewContainer mVideoView;
	private XBFXVideoView mPlayer;
	private CenterLayout mask;

	private VideoViewListener mPlayerEventListner;

	private int playedTime = 0;

	// 接口参数
	String mCurrentSessionId;
	
	//直播
	String mTid = "";
	String mTidDay = "";
	String mTidTime = "";
	boolean livePlayback = false;
	boolean livePlay = false;
	
	//点播
	String mVid = "";
	String mFdn = "";
	String channel = Properties.Eguan_channel;

	// 播放本地文件
	private boolean localFileMode = false;

	// 播放器参数
	private String mUrl;
	private Integer mErrorCode = 0;
	private Map<String, String> mExtraHeaders;// 2015.12.2
		
	//GlideUtil glideInstance = null;

	private boolean isPrepared = false;

	// sdr 2015.10.27 预缓冲机制,网络状况统计相关成员
	private boolean isStop = true;
	private boolean isCompletion = false;
	private boolean isPaused = false;

	private boolean isSuspend = false;
	private long suspendPosition = 0;

	private boolean isStopSuspend = false;
	private boolean isPreBuffering = false;// 视频是否正在缓冲

	private boolean isLiveStreamMode = false;// 当前是直播
	private long video_duration_msec = 0;// 视频时长 毫秒

	// sdr 2015.11.14下行速率刷新
	long lastDownloadRateUpdateTick = 0;
	int lastDownloadRateUpdateValue = 0;

	// //播放器状态检查定时器
	// //功能:1刷新当前下载速率 2异常状态处理(如果seek超时则重试，如果缓冲停滞则重放)
	// private Timer timerCheckPlayerStatus = null;

	private final int MAX_ERROR_RECOVER_ATTEMPTS = 3;
	private static int error_retry_times = 0;

	private boolean isReplayAndSeeking = false;
	private boolean isReplayAndSeekingComplete = true;
	private long replayResetTick = 0;

	final private long PLAYING_TIME_OUT = 10000L;

	private static final int AD_REQUEST_TIMEOUT = 10000;
	private boolean bPlayUrlLoadingStarted = false;// 避免广告请求卡死

	private long debug_ad_request_start = 0;
	private long debug_ad_download_start = 0;
	private long debug_playurl_request_start = 0;

	private boolean isEnableVideoReconneting = true;

	/************** handler处理，解决handler可能的内存泄露 *************************************************/
	private final Handler mHandler = new VideoViewShellHandler(this);

	private  class VideoViewShellHandler extends
			WeakHandler<VideoViewShell> {

		public VideoViewShellHandler(VideoViewShell owner) {
			super(owner);
		}

		@Override
		public void handleMessage(Message msg) {
			VideoViewShell view = getOwner();
//			Log.e("info", "view...."+view+"....."+msg.what);
			if (view == null)
				return;
			switch (msg.what) {

			case CHECK_STATUS:
				handleCheckStatus();
				mHandler.sendEmptyMessageDelayed(CHECK_STATUS, 3000L);
				break;

			case START_PLAY:

				view.player_control_setMediaStartTime(view.playedTime);
				view.player_control_init();
				view.start_player();

				break;
			default:
				break;
			}
		}
	}
	
	private void handleCheckStatus() {
		if (mPlayer.isPlaying()) {
		}

	}

	public VideoViewShell(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init(context);
	}

	public VideoViewShell(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		// TODO Auto-generated constructor stub
		init(context);
	}

	public VideoViewShell(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		init(context);				
	}
	

	private void init(Context context) {
		mContext = context;

		RelativeLayout.LayoutParams relLayoutParams = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		CenterLayout cl = new CenterLayout(context);
		mask = new CenterLayout(context);
		addView(cl, relLayoutParams);
		addView(mask, relLayoutParams);

		mPlayer = new XBFXVideoView(context);
		RelativeLayout.LayoutParams layoutParamsP = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		mVideoView = new VideoViewContainer(context);
		mVideoView.addView(mPlayer, layoutParamsP);

		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		cl.addView(mVideoView, layoutParams);

	}
	
	public void StartPlay(String playUrl,
			Map<String, String> headers) {
		this.mExtraHeaders = headers;

		startPlayVideo(playUrl, 0);

	}

	private void startPlayVideo(String url, int playedTime) {
		mUrl = url;
		mHandler.sendEmptyMessage(START_PLAY);
	}

	public void setPlayerEventListner(VideoViewListener listner) {
		// TODO Auto-generated method stub
		mPlayerEventListner = listner;
	}

	public void play() {
		// TODO Auto-generated method stub
		player_control_play();
	}

	public void pause() {		
		player_control_pause();
	}

	public void start() {
		player_control_play();
	}

	public void seekTo(int mesc) {
		// player_control_seekTo(mesc);
		if (mPlayer != null)
			mPlayer.seekTo(mesc);
	}

	public void stop() {
		// TODO Auto-generated method stub
		player_control_stop();
	}

	public void release() {
		// TODO Auto-generated method stub
		isStop = false;
		this.player_control_stop();

		if (mPlayerEventListner != null)
			mPlayerEventListner = null;

		if (mContext != null)
			mContext = null;
	}

	public void reset() {
		// TODO Auto-generated method stub
		isStop = false;
		this.player_control_stop();

	}

	public long getDuration() {
		if (!isPrepared) {
			return 1;
		} else {
			return player_control_getPlayer().getDuration();
		}
	}

	public int getVideoWidth() {
		if (!isPrepared) {
			return 0;
		} else {
			return mPlayer.getVideoWidth();
		}
	}

	public int getVideoHeight() {
		if (!isPrepared) {
			return 0;
		} else {
			return mPlayer.getVideoHeight();
		}
	}

	public long getCurrentPosition() {
		// TODO Auto-generated method stub
		if (!isPrepared) {
			return 0;
		} else {
			return player_control_getPlayer().getCurrentPosition();
		}
	}

	public boolean IsPlayerPlaying() {
		// 表示画面正在动
		if (isStop)
			return false;

		return player_control_getPlayer().isPlaying();
	}

	public boolean IsPlayerPaused() {
		return isPaused;
	}

	public boolean IsPlayerComplete() {
		return isCompletion;
	}

	public boolean IsPlayerBuffering() {
		return isPreBuffering;
	}

	public boolean IsPlayerPrepared() {
		// 表示播放器准备就绪
		return isPrepared;
	}

	public boolean IsPlayerSuspend() {
		return isSuspend;
	}

	private void start_player() {

		player_control_start_new_stream(mUrl);

	}

	private void player_control_setMediaStartTime(int _playedTime) {
		this.playedTime = _playedTime;
	}

	private void player_control_init() {
		Log.i("playerControl", "previous stream not closed, closing it..");
		player_control_stop();

		Log.i("playerControl", "init");

		mPlayer.requestFocus();

		mPlayer.setOnSeekCompleteListener(new OnSeekCompleteListener() {

			@Override
			public void onSeekComplete() {
				// TODO Auto-generated method stub
				Log.i("playerControl", "onSeekComplete");

				if (!isReplayAndSeekingComplete) {
					isReplayAndSeekingComplete = true;
				}

				if (!isPaused) {
					mPlayer.start();
				}
			}

		});

		mPlayer.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion() {

				isCompletion = true;

				if (!Properties.flag2345)
				{
					player_control_stop();
				}

				Log.i("playerControl", "onCompletion, streaming End\n");

				// TODO Auto-generated method stub
				if (mPlayerEventListner != null && !localFileMode)
					mPlayerEventListner.onCompletion();
			}

		});

		mPlayer.setOnErrorListener(new OnErrorListener() {

			@Override
			public boolean onError(int arg1, int arg2) {

				playing_error_processing();
				
				return false;
			}
		});

		mPlayer.setOnInfoListener(new OnInfoListener() {

			@Override
			public boolean onInfo(int arg1, int arg2) {
				// TODO Auto-generated method stub
				Log.e("bestv", "onInfo....what.." + arg1);
				switch (arg1) {
				case XBFXAndroidMediaPlayer.MEDIA_INFO_BUFFERING_START:

//					mPlayer.pause();

					isPreBuffering = true;
					if (mPlayerEventListner != null) {
						mPlayerEventListner.onBufferStart();
					}
					
					break;
				case XBFXAndroidMediaPlayer.MEDIA_INFO_BUFFERING_END:
					isPreBuffering = false;

					if (mPlayerEventListner != null) {
						mPlayerEventListner.onBufferEnd();
					}
//					mPlayer.start();
					
					break;
				}
				return true;
			}
		});

		mPlayer.setOnPreparedListener(new OnPreparedListener() {

			@Override
			public void onPrepared(IMediaPlayer arg0) {
				// TODO Auto-generated method stub
				isPrepared = true;

				if (isSuspend) {
					isSuspend = false;
					suspendPosition = 0;
				}

				video_duration_msec = arg0.getDuration();

				arg0.setLooping(false);

				int video_width = arg0.getVideoWidth();
				int video_height = arg0.getVideoHeight();

				if (mPlayerEventListner != null)
					mPlayerEventListner.onPrepared(video_duration_msec,
							video_width, video_height);

			}

		});
		mPlayer.setKeepScreenOn(true);

		/**
		 * 处理部分手机花屏的问题
		 */
		if (mPlayer.getHolder() != null) {
			try {
				mPlayer.getHolder().setFormat(PixelFormat.RGBX_8888);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

	private void playing_error_processing() {
		if (mPlayerEventListner != null)
			mPlayerEventListner.onError(0, 0);

		player_control_stop();
	}

	private XBFXVideoView player_control_getPlayer() {
		return mPlayer;
	}

	private void player_control_start_new_stream(String _url) {
		Log.i("playerControl", "start new stream");

		player_control_stop();

		isStop = false;
		isCompletion = false;
		mUrl = _url;
		
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("CDNtoken", CipherHelper.generateCDNToken());
		if (mExtraHeaders != null && mExtraHeaders.size() > 0) {
			headers.putAll(mExtraHeaders);
		}
		Log.i("playerControl", "net Stream play...." + mUrl);
		mPlayer.setVisibility(View.VISIBLE);
		mPlayer.setVideoURI(Uri.parse(mUrl), headers);

		if (mPlayerEventListner != null) {
//			isPreBuffering = true;
			mPlayerEventListner.onBufferStart();
		}
		
		if (isSuspend) {
			Log.i("playerControl", "suspend position is" + suspendPosition);
			playedTime = (int) suspendPosition;
		}

		if (playedTime > 0) {
			Log.i("playerControl", "player start! seekTo:" + playedTime);
			mPlayer.seekTo(playedTime);
		} else {
			mPlayer.seekTo(0);
		}

		mPlayer.start();

		mHandler.sendEmptyMessageDelayed(CHECK_STATUS, 1000L);
	}

	private void player_control_player_start() {
		mPlayer.start();
	}

	private void player_control_pause() {
		Log.i("playerControl", "pause");
		if (!isStop) {
			isPaused = true;
			if (!isPreBuffering) {
				mPlayer.pause();
			}
		}
	}

	private void player_control_play() {
		Log.i("playerControl", "play");
		if (!isStop) {
			isPaused = false;
			if (!isPreBuffering) {
				player_control_player_start();
			}
		} else {
			if (!StringTool.isEmpty(mUrl)) {
				player_control_start_new_stream(mUrl);
			}
		}
	}

	private long tickLastSeekOperation = 0;

	private void player_control_seekTo(int msec) {
		// if (!isStop) {
		long curTick = System.currentTimeMillis();
		if (curTick - tickLastSeekOperation > 100) {
			Log.i("playerControl", "seekTo" + msec);
			if (mPlayer.isPlaying()) {
				if (this.localFileMode) {
					// ReplayAndSeekTo
				} else {
					mPlayer.pause();
				}
			}
			mPlayer.seekTo(msec);
		} else {
			// 上个SEEK操作时间太近 取消
		}
		tickLastSeekOperation = System.currentTimeMillis();
		// }
	}

	private void player_control_resume() {
		if (isSuspend) {
			if (isSuspend) {
				isSuspend = false;
			}

			if (isStopSuspend) {
				isStopSuspend = false;
				// this.start_player();
			} else {
				// mPlayer.start();
				// player_control_seekTo((int) suspendPosition);
				// mPlayer.seekTo(suspendPosition);
			}
			// start_player();
			player_control_seekTo((int) suspendPosition);
			mPlayer.start();
		}
	}

	private void player_control_stop() {
		// if (!isStop) {
		mHandler.removeCallbacksAndMessages(null);// 2.29

		if (isPreBuffering && !isReplayAndSeeking)// 预缓冲状态下,并且非自动重连时,停止缓冲提示
		{
			if (mPlayerEventListner != null) {
				mPlayerEventListner.onBufferEnd();
			}
		}

		mPlayer.stopPlayback();

		isStop = true;
		isPaused = false;

		// sdr 2015.10.27
		isPreBuffering = false;
		isLiveStreamMode = false;
		video_duration_msec = 0;
		playedTime = 0;

		if (!isReplayAndSeeking) {
			isReplayAndSeekingComplete = true;
		}

		bPlayUrlLoadingStarted = false;

		Log.i("playerControl", "current stream closed");
	}

	public boolean IsLiveStreamMode() {
		return isLiveStreamMode;
	}

	public boolean IsPlayerStop() {
		return isStop;
	}

}