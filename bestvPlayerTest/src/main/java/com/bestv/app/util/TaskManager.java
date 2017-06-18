package com.bestv.app.util;

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

public class TaskManager {

	enum AsyncState {
		GET_CONTENT//, GET_CACHE, GET_CACHE_REFRESH, UPDATE_CACHE, UPDATE_CACHE_REFRESH
	}

	private TaskListener _taskListener;
	private Handler _handler;

	public TaskManager(TaskListener taskListener) {
		_taskListener = taskListener;
		_handler = new Handler();
	}

	public void release() {
		_taskListener = null;
		_handler = null;
	}

	public void getContent(String... params)// 缃戠粶浜や簰锛屽埛鏂扮晫闈�
	{
		BaseAsyncTask asyncTask = new BaseAsyncTask(AsyncState.GET_CONTENT,
				params);
		asyncTask.execute(params);
	}

	public class BaseAsyncTask extends AsyncTask<String, Integer, String> {

		private AsyncState _state;
		private String[] _params = null;
		private String _url = "";

		public BaseAsyncTask(AsyncState state, String... params) {
			_state = state;
			_params = params.clone();
			for (int i = 0; i < _params.length; i++) {
				_url += _params[i];
				if (i < _params.length - 1)
					_url += "/";
			}
		}

		@Override
		protected String doInBackground(String... params) {

			if (_state == AsyncState.GET_CONTENT) {
				return _taskListener.taskWorking(_params);
			}

			return null;
		}

		protected void onPostExecute(String data) {
			if (_state == AsyncState.GET_CONTENT) {
				if (data != null) {
					taskComplete(data);
				}
			}
		}

		private void taskComplete(String data) {
			try {
				if (data != null) {
					TaskRunnable taskRunnable = new TaskRunnable(data, _params);
					if (_handler != null)
						_handler.post(taskRunnable);
				}
			} catch (Exception e) {
			}
		}
	}

	public class TaskRunnable implements Runnable {

		private String[] _params = null;
		private String _data = null;

		public TaskRunnable(String data, String[] params) {
			if (data != null && params != null) {
				_data = data;
				_params = params;
			}
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			if (_taskListener != null)
				_taskListener.taskComplete(_data, _params);
		}

	}
}
