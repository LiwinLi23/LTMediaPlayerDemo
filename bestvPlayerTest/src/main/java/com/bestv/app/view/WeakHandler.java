package com.bestv.app.view;

import java.lang.ref.WeakReference;

import android.os.Handler;

public class WeakHandler<T> extends Handler {
	private WeakReference<T> mOwner;

	public WeakHandler(T owner) {
		mOwner = new WeakReference<T>(owner);
	}

	public T getOwner() {
		return mOwner.get();
	}
}
