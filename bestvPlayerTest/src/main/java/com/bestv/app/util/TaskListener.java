package com.bestv.app.util;

public interface TaskListener {

	public String taskWorking(String[] params);

	public void taskComplete(String data, String[] params);
}
