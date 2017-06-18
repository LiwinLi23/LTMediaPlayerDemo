package com.bestv.app.liveaudio;

public interface LiveAudioPlayerListener {
	void onPrepared();
	void onError(int code);
	void onCompletion();
}
