package com.bestv.app.view;

import java.io.IOException;
import java.util.Map;

import com.xbfxmedia.player.IMediaPlayer;
import com.xbfxmedia.player.XBFXAndroidMediaPlayer;
import com.xbfxmedia.player.XBFXAndroidMediaPlayer.*;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;

import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

public class XBFXVideoView extends SurfaceView {

    private Context mContext;
    private final String TAG = "XBFXVideoView";

    private int mXBFXPlayerType = XBFXAndroidMediaPlayer.PLAYER_TYPE_SW;

    //	private final int STATE_ERROR = -1;
    private final int STATE_IDLE = 0;
    private final int STATE_PREPARING = 1;
    private final int STATE_PREPARED = 2;

    private int mCurrentState = STATE_IDLE;

    // private boolean isPause = false;
    // private boolean isStop = true;

    private String mVideoFilePath;
    private Uri mUri;
    private Map<String, String> mHeaders;
    private long mDuration;
    private long mSeekWhenPrepared; // recording the seek position while preparing

    private XBFXAndroidMediaPlayer mMediaPlayer;
    private SurfaceHolder mSurfaceHolder;
    private int mVideoWidth;
    private int mVideoHeight;

    public int mContainerWidth;
    public int mContainerHeight;

    private OnPreparedListener mOnPreparedListener = null;
    private OnBufferingUpdateListener mOnBufferingUpdateListener = null;
    private OnSeekCompleteListener mOnSeekCompleteListener = null;
    private OnCompletionListener mOnCompletionListener = null;
    private OnErrorListener mOnErrorListener = null;
    private OnInfoListener mOnInfoListener = null;

    private long mBeginningPosition = 0;

    private long last_retry_tick = 0;

    private OnPreparedListener onPreparedListener = new OnPreparedListener(){

        @Override
        public void onPrepared(IMediaPlayer mp) {

            Log.e(TAG, "mMediaPlayer.onPrepared");

            if (mOnPreparedListener != null)
                mOnPreparedListener.onPrepared(mp);

            mCurrentState = STATE_PREPARED;

            //String ip = mMediaPlayer.getIP();
            //Log.i(TAG, "ip: " + ip);

            mVideoWidth = mp.getVideoWidth();
            mVideoHeight = mp.getVideoHeight();
            mDuration = mp.getDuration();

            if (mSeekWhenPrepared != 0)
            {
                seekTo(mSeekWhenPrepared);
                mSeekWhenPrepared = 0;
            }
            else if (mBeginningPosition > 0)
            {
                seekTo(mBeginningPosition);
                mBeginningPosition = 0;
            }

            start();

        }
    };

    private OnBufferingUpdateListener onBufferingUpdateListener = new OnBufferingUpdateListener(){
        @Override
        public void onBufferingUpdate(int arg0) {

        }
    };
    private OnSeekCompleteListener onSeekCompleteListener = new OnSeekCompleteListener(){

        @Override
        public void onSeekComplete() {

            Log.e(TAG, "mMediaPlayer.onSeekComplete");

            if (mOnSeekCompleteListener != null)
            {
                mOnSeekCompleteListener.onSeekComplete();
            }
        }

    };
    private OnCompletionListener onCompletionListener = new OnCompletionListener(){

        @Override
        public void onCompletion() {

            Log.e(TAG, "mMediaPlayer.onCompletion");

            long cur_pos = mMediaPlayer.getCurrentPosition();
            long dur = mMediaPlayer.getDuration();
            if (cur_pos >= dur || (dur-cur_pos<500))
            {
                if (mOnCompletionListener != null)
                {
                    mOnCompletionListener.onCompletion();
                }
            }
            else
            {
                if (mOnErrorListener != null)
                {
                    mOnErrorListener.onError(0, 0);
                }
            }
        }
    };
    private OnErrorListener onErrorListener = new OnErrorListener(){

        @Override
        public boolean onError(int arg0, int arg1) {

            if (System.currentTimeMillis() - last_retry_tick > 5000)
            {
                Log.e(TAG, "mMediaPlayer.onError" + arg0 + "," + arg1);

                if (mOnErrorListener != null)
                {
                    mOnErrorListener.onError(arg0, arg1);
                }

                last_retry_tick = System.currentTimeMillis();
            }
            return false;
        }
    };
    private OnInfoListener onInfoListener = new OnInfoListener(){

        @Override
        public boolean onInfo(int arg0, int arg1) {
            if (mOnInfoListener != null)
            {
                Log.e(TAG, "mMediaPlayer.onInfo" + arg0 + "," + arg1);

                mOnInfoListener.onInfo(arg0, arg1);
            }
            return false;
        }
    };

    public XBFXVideoView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        initVideoView(context);
    }

    public XBFXVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        initVideoView(context);
    }

    public XBFXVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
        initVideoView(context);
    }

    SurfaceHolder.Callback mSHCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2,
                                   int arg3) {

        }

        private boolean bShouldRestore = false;
        @Override
        public void surfaceCreated(SurfaceHolder arg0) {
            // TODO Auto-generated method stub

            Log.e(TAG, "SurfaceHolder.surfaceCreated");

            if (mMediaPlayer != null)
            {
                mSurfaceHolder = arg0;
                Log.e(TAG, "mMediaPlayer.setDisplay");
                mMediaPlayer.setDisplay(mSurfaceHolder);
                if (bShouldRestore)
                {
                    bShouldRestore = false;

                    if (mUri != null)
                    {
                        playPath(mUri.toString());
                    }
                    else if (mVideoFilePath != null && mVideoFilePath.length()>0)
                    {
                        playPath(mVideoFilePath);
                    }
                }
            }
            else
            {
                Log.e(TAG, "mMediaPlayer is null");
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder arg0) {
            // TODO Auto-generated method stub

            Log.e(TAG, "SurfaceHolder.surfaceDestroyed");

            if (isInPlaybackState())
            {
                if (mMediaPlayer != null)
                {
                    Log.e(TAG, "mMediaPlayer.setDisplay");
                    mMediaPlayer.setDisplay(null);

                    bShouldRestore = true;

                    if (mMediaPlayer.getDuration() > 0) {
                        mBeginningPosition = mMediaPlayer.getCurrentPosition();
                        Log.e(TAG, "mMediaPlayer.getCurrentPosition");
                    } else {
                        mBeginningPosition = 0;
                    }
                    mMediaPlayer.stop();
                    Log.e(TAG, "mMediaPlayer.stop");
                    mMediaPlayer.reset();
                    Log.e(TAG, "mMediaPlayer.reset");
                    mCurrentState = STATE_IDLE;
                }
            }
        }

    };

    private void initVideoView(Context ctx) {

        Log.e(TAG, "initVideoView");

        mContext = ctx;
        mSurfaceHolder = this.getHolder();
        mSurfaceHolder.addCallback(mSHCallback);

    }

    public void setOnPreparedListener(OnPreparedListener l)
    {
        this.mOnPreparedListener = l;
    }
    public void setOnBufferingUpdateListener(OnBufferingUpdateListener l)
    {
        this.mOnBufferingUpdateListener = l;
    }
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
    public void setOnInfoListener(OnInfoListener l)
    {
        this.mOnInfoListener = l;
    }

    void release() {
        Log.e(TAG, "release");
        if (mMediaPlayer != null)
        {
            mMediaPlayer.reset();
            Log.e(TAG, "after reset");
            mMediaPlayer.release();
            Log.e(TAG, "after release");
            mMediaPlayer = null;
        }
    }

    public void setVideoLayout(int layout, float aspectRatio) {

        mSurfaceHolder.setFixedSize(mVideoWidth, mVideoHeight);
    }

    public void setVideoURI(Uri uri, Map<String, String> headers, long startTime) {
        mUri = uri;
        mHeaders = headers;
        openVideo();
    }

    public void setVideoURI(Uri uri, Map<String, String> headers) {
        setVideoURI(uri, headers, 0L);
    }

    public void setVideoFilePath(String path, long startTime) {
        mVideoFilePath = path;
        openVideo();
    }

    public void setVideoFilePath(String path) {
        setVideoFilePath(path, 0L);
    }

    public void stopPlayback()
    {
        Log.e(TAG, "stopPlayback");
        if (mMediaPlayer != null)
        {
            mMediaPlayer.stop();
            Log.e(TAG, "after stop");
            mMediaPlayer.release();
            Log.e(TAG, "after release");
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

        Log.e(TAG, "openVideo");

        if ((mVideoFilePath == null && mUri == null) || mSurfaceHolder == null)
            return;

        Intent i = new Intent("com.android.music.musicservicecommand");
        i.putExtra("command", "pause");
        mContext.sendBroadcast(i);

        release();

        mMediaPlayer = new XBFXAndroidMediaPlayer(mXBFXPlayerType);
        Log.e(TAG, "new XBFXAndroidMediaPlayer");
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setDisplay(mSurfaceHolder);

        mMediaPlayer.setOnPreparedListener(onPreparedListener);
        mMediaPlayer.setOnBufferingUpdateListener(onBufferingUpdateListener);
        mMediaPlayer.setOnSeekCompleteListener(onSeekCompleteListener);
        mMediaPlayer.setOnCompletionListener(onCompletionListener);
        mMediaPlayer.setOnErrorListener(onErrorListener);
        mMediaPlayer.setOnInfoListener(onInfoListener);

        if (mUri != null)
        {
            playPath(mUri.toString());
        }
        else if (mVideoFilePath != null && mVideoFilePath.length()>0)
        {
            playPath(mVideoFilePath);
        }

    }

    public void playPath(String path)
    {
        try
        {
            Log.e(TAG, "playPath:"+path);
            mMediaPlayer.setDataSource(path, mHeaders);

            mMediaPlayer.setScreenOnWhilePlaying(true);
            mMediaPlayer.prepareAsync();
            mCurrentState = STATE_PREPARING;

            Log.e(TAG, "mMediaPlayer.prepareAsync");

        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            onErrorListener.onError(0, 0);
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            onErrorListener.onError(0, 0);
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            onErrorListener.onError(0, 0);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            onErrorListener.onError(0, 0);
        }
    }

    public void pause() {
        if (isInPlaybackState()) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
                Log.e(TAG, "mMediaPlayer.pause");
            }
        }
    }

    public void start() {
        if (isInPlaybackState()) {
            mMediaPlayer.start();
            Log.e(TAG, "mMediaPlayer.start");
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
            Log.e(TAG, "mMediaPlayer.seekTo"+msec);
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
