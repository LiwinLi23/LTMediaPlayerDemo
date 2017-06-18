package com.bestv.app.sdkservice;

interface ILiveAudioForClient{
	void hello_reply();
	
	void onPrepared();
	void onCompletion();
	void onError(int code);
	
}