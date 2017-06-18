package com.bestv.app.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

public class VideoViewContainer extends ViewGroup {
	
    public static final int ASPECT_HOLD_RATIO 		= 0;
	public static final int ASPECT_FOLLOW_WIDTH 	= 1;
	public static final int ASPECT_FOLLOW_HEIGHT 	= 2;
	public static final int ASPECT_MATCH_PARENT 	= 3;
    
	public void changeMode(final int newMode, final int videoWidth, final int videoHeight) {
    	Log.i(TAG, "change mode");
    	mAspectMode 	= newMode;
    	mVideoWidth 	= videoWidth;
    	mVideoHeight 	= videoHeight;
    	this.forceLayout();
    }
	
	public VideoViewContainer(Context context) {
        super(context);
    }

    public VideoViewContainer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoViewContainer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean shouldDelayChildPressedState() {
        return false;
    }
	
    /**
     * Ask all children to measure themselves and compute the measurement of this
     * layout based on the children.
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) { 
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);  
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        measureChildren(widthMeasureSpec, heightMeasureSpec);
    	setMeasuredDimension(sizeWidth, sizeHeight);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        final int count = getChildCount();
        Log.i(TAG, "Child count: " + count);
        final View child = getChildAt(0);
        switch (mAspectMode) {
        case ASPECT_HOLD_RATIO:
        	layoutVideoByRatio(child, left, top, right, bottom);
        	break;
        
        case ASPECT_FOLLOW_WIDTH:
        	layoutVideoFollowWidth(child, left, top, right, bottom);
        	break;
        
        case ASPECT_FOLLOW_HEIGHT:
        	layoutVideoFollowHeight(child, left, top, right, bottom);
        	break;
        	
        default:
        	child.layout(left, top, right, bottom);
        	break;
        }
    }
    
    private void layoutVideoByRatio(View vv, int left, int top, int right, int bottom) {
    	int containerWidth = right - left;
        int containerHeight = bottom - top;
        int rw, rh, rleft, rtop, rright, rbottom;
        if (containerWidth * mVideoHeight / mVideoWidth >= containerHeight) {
        	rw = containerHeight * mVideoWidth / mVideoHeight;
        	rleft = (containerWidth - rw) / 2;
        	rtop = 0;
        	rright = rleft + rw;
        	rbottom = containerHeight;
        } else {
        	rh = containerWidth * mVideoHeight / mVideoWidth;
        	rleft = 0;
        	rtop = (containerHeight - rh) / 2;
        	rright = containerWidth;
        	rbottom = rtop + rh;
        }
        
        vv.layout(rleft, rtop, rright, rbottom);
    }
    
    private void layoutVideoFollowWidth(View vv, int left, int top, int right, int bottom) {
    	int containerWidth = right - left;
        int containerHeight = bottom - top;
        int rh, rtop, rbottom;
        
        rh = containerWidth * mVideoHeight / mVideoWidth;
        rtop = (containerHeight - rh) / 2;
        rbottom = rtop + rh;
        vv.layout(left, rtop, right, rbottom);
    }
    
    private void layoutVideoFollowHeight(View vv, int left, int top, int right, int bottom) {
    	int containerWidth = right - left;
        int containerHeight = bottom - top;
        int rw, rl, rr;
        
        rw = containerHeight * mVideoWidth / mVideoHeight;
        rl = (containerWidth - rw) / 2;
        rr = rl + rw;

        vv.layout(rl, top, rr, bottom);
    }
    
    private int mAspectMode = ASPECT_MATCH_PARENT;
    private int mVideoWidth;
    private int mVideoHeight;
	private static final String TAG = "VideoViewContainer";
}
