package com.bestv.app.view;

public interface VideoViewListener {
		
	void onAdBegin();
	
	void onAdEnd();
	
	void onPlayerClick();

	void onPrepared(long videoDuration, int videoWidth, int videoHeight);

	void onBufferingUpdate(int percent);
	
	void onBufferStart();

	void onBufferEnd();

	void onCompletion();
	
	boolean onError(int what, int extra);
	
	//what:
	//2001 = VideoViewShell.ERROR_SDK_CALL_FAILED;
	//2002 = VideoViewShell.ERROR_VIDEO_PLAY_FAILED;
	
	boolean onNetStreamingReport(int download_rate_kBps);
}
