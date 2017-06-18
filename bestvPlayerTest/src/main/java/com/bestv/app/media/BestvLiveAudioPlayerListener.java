package com.bestv.app.media;

public interface BestvLiveAudioPlayerListener {
	void onPrepared();
	void onError(int what, int extra);
	void onCompletion();
}
