package com.bestv.priv.util;

import android.content.Context;

public class BestvComponentsPreloader {

    private static Context appContext = null;
	
    public static Context GetApplicationContext()
    {
    	return appContext;
    }
    
    public static void SetApplicationContext(Context _appContext)
    {
    	appContext = _appContext;
    }
        
}
