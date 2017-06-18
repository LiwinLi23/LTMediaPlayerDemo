package com.bestv.app.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Locale;

public class M3U8Parser {
	
	static public final int M3U8_TYPE_PRIME = 0;
	static public final int M3U8_TYPE_STREAM = 1;
		
	static public class M3U8Info {
		public int type;
		public int mediaSequence;
		public double targetDuration;
		List<Variant> variants;
		List<Segment> segments;
		public M3U8Info(){}
	}
	
	static public class Variant
	{
		int bandwidth;
		String url;
	}
	
	static public class Segment
	{
		double duration;
		String name;
		String url;
	}
		
	static public M3U8Info parse(InputStream in, String url)
	{
		M3U8Info info = null;
		String baseUrl = "";
		
		if (url != null)
		{
			baseUrl = url;
			if (baseUrl.indexOf("/")>0 && baseUrl.indexOf(".m3u8")>0)
			{
				int i = baseUrl.lastIndexOf("/");
				baseUrl = baseUrl.substring(0, i);
			}
		}
		
		BufferedReader buffReader = new BufferedReader(new InputStreamReader(in));
		try {
			
			String lineText = "";
			do {
				lineText = buffReader.readLine();
				if (lineText == null || lineText.length()==0)
				{
					break;
				}
				
				if (lineText.contains("#EXTM3U"))
				{
					info = new M3U8Info();
				}
				else if (lineText.contains("#EXT-X-STREAM-INF:"))
				{
					info.type = M3U8_TYPE_PRIME;
					
					Variant v = new Variant();
					int tmp = lineText.toLowerCase(Locale.ENGLISH).lastIndexOf("bandwidth=")+10;
					String str_bandwidth = lineText.substring(tmp);

					v.bandwidth = Integer.parseInt(str_bandwidth);
					v.url = String.format("%s%s", baseUrl, buffReader.readLine());
					
					info.variants.add(v);
				}
				else if (lineText.contains("#EXT-X-TARGETDURATION:"))
				{
					info.type = M3U8_TYPE_STREAM;
					
					int tmp = "#EXT-X-TARGETDURATION:".length();
					String str_targetduration = lineText.substring(tmp);
					
					info.targetDuration = Double.parseDouble(str_targetduration);
				}
				else if (lineText.contains("#EXT-X-MEDIA-SEQUENCE:"))
				{
					info.type = M3U8_TYPE_STREAM;
					
					int tmp = "#EXT-X-MEDIA-SEQUENCE:".length();
					String str_mediasequence = lineText.substring(tmp);
					
					info.mediaSequence = Integer.parseInt(str_mediasequence);
				}
				else if (lineText.contains("#EXTINF:"))
				{
					int tmp = "#EXTINF:".length();
					int tmp2 = lineText.lastIndexOf(",");
					String str_dur = lineText.substring(tmp, tmp2-1);
					
					Segment s = new Segment();
					s.duration = Double.parseDouble(str_dur);
					s.url = String.format("%d%d", baseUrl, buffReader.readLine());
				}
			}
			while (true);
			
		} catch (IOException e) {
			info = null;
		}
		
		return info;
	}
	
}
