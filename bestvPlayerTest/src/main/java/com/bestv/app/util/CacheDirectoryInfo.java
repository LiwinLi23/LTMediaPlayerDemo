package com.bestv.app.util;

import java.io.File;

public class CacheDirectoryInfo{
	public File dir;
	public long bytes_available;
	public long bytes_total;
	public boolean isSdcard;
	public CacheDirectoryInfo(File _dir, long _bytes_available, long _bytes_total, boolean _isSdcard)
	{
		dir = _dir;
		bytes_available = _bytes_available;
		bytes_total = _bytes_total;
		isSdcard = _isSdcard;
	}
}