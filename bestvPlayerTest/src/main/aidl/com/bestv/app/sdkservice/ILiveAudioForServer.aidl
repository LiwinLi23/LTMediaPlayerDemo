package com.bestv.app.sdkservice;

import com.bestv.app.sdkservice.ILiveAudioForClient;

interface ILiveAudioForServer{
	void registerCallBack(ILiveAudioForClient cb); 
	String hello();
	
	void startPlay(String url);
	void stopPlay();
	void pausePlay();
	void resumePlay();
	long getCurrentPosition();
	boolean isActivated();
	boolean isPrepared();
	boolean isPaused();
	
}