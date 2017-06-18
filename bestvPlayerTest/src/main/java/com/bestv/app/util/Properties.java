package com.bestv.app.util;

public class Properties {
	
	//SDK 接口基地址
	private final static String BesTVBaseUrl = "http://bestvapp-test-147008356.cn-north-1.elb.amazonaws.com.cn";

	//SDK 初始化接口
	public static final String initUrl =BesTVBaseUrl + "/api/client_init/?app=android";

    //广告的基地址从初始化接口中获得
//	public static String adBaseUrl = "http://elb-test-ssp-cps-859807094.cn-north-1.elb.amazonaws.com.cn:8088/ssp/api/adrequest";
//	public static String adBaseUrl = "http://elb-ssp-cps-1372355746.cn-north-1.elb.amazonaws.com.cn:8088/ssp/api/adrequest";
//	public static final String videoAdId = "QP000VODQT01";
	
	public static final String load_ad = "load_ad";
	public static final String load_url = "load_url";
	public static final String load_detail = "load_detail";
	public static final String post_ir = "post_ir";
	public static final String load_live_url = "live_url";

	public static final String playUrl = "http://playersdk.bestv.cn/parasite.portal/VideoPlay/PlayURL?";
	public static final String collectUrl = "http://playersdk.bestv.cn/parasite.bi/Behavior/Collect?view=json";
	public static final String irUrl = "http://irs01.com/hvt?";
//	public static final String liveUrl = "http://ec2-54-223-153-135.cn-north-1.compute.amazonaws.com.cn/video/tv_detail?";

	public static final String sdkBaseUrl = "https://bestvapi.bestv.cn";
//	public static final String sdkBaseUrl = "https://bestvapp-test.bestv.cn";
//	public static final String sdkBaseUrl = "http://ec2-54-223-153-135.cn-north-1.compute.amazonaws.com.cn";
	
	public static final String sdkLiveUrl = sdkBaseUrl + "/video/tv_detail";
	public static final String sdkInit = sdkBaseUrl + "/app/init";
	public static final String sdkUserOrderList = sdkBaseUrl + "/user/orderList";
	public static final String sdkSendVerifCode = sdkBaseUrl + "/user/sendcaptcha";
	public static final String sdkBestvUserRegister = sdkBaseUrl + "/user/sdkLogin";
	public static final String sdkBestvProductList = sdkBaseUrl + "/user/productList";
	public static final String sdkBestvCreateOrder = sdkBaseUrl + "/user/createOrder";
	public static final String sdkBestvLogger = sdkBaseUrl + "/track/logger";
	public static final String sdkBestvTvDetail = sdkBaseUrl + "/video/tv_detail";
	public static final String sdkBestvProgramDetail = sdkBaseUrl + "/video/program_detail";
	
	//test
	public static final String sdkBestvCategoryList = sdkBaseUrl + "/video/category_list";
	public static final String sdkBestvProgramList = sdkBaseUrl + "/video/program_list";
	public static final String sdkBestvVideorate = sdkBaseUrl + "/video/video_rate";
	

	public static final String clickAd = "clickAd";
	public static final String startPlay = "startPlay";
	public static final String endPlay = "endPlay";
	public static final String playError = "playError";
	
	public static boolean g_bScaleImage = true;
    public static int g_nScaleImage = 2;
    
	public static final String load_history = "/history.php";
	public static final String del_history = "/delhistory.php";
	public static final String delall_history = "/delallhistory.php";
	public static final String add_fav = "/addfav.php";
	public static final String del_fav = "/delfav.php";
	public static final String delall_fav = "/delallfav.php";
	public static final String load_fav = "/loadfav.php";
	public static final String feedback = "/feedback.php";
	
	public static final int adTime = 5;
	public static String UA = "";

//	public static String BESTV_APP_CHANNEL = "524a3856-6196-4ee2-bb35-891514036522";
//	public static String BESTV_APP_KEY = "24f348deae3dd9498f2f2410f6e42eb3";
//	public static String BESTV_USER_DATA_CHANNEL = "UV_BESTV_APP";
	//艾瑞
//	public static String IR_UAID = "UA-DFMZ-140001";
//	public static String IR_USERAgent = "BestvPlayer2345";
//	public static String IR_MEDIA_TYPE = "BestvPlayerAndroid";
//	//易观方舟
//	public static String Eguan_appKey = "4qqr7oeatx7ckj6sb";
//	public static String Eguan_channel = "2345";

	
	public static String BESTV_APP_CHANNEL = "aa2ddfdb-4387-49c0-9651-92cc83b8e905";
	public static String BESTV_APP_KEY = "3a10f1283920d1c86960e22f307968d8";
	public static String BESTV_USER_DATA_CHANNEL = "UV_BESTV_2345";
	//艾瑞
	public static String IR_UAID = "UA-DFMZ-140001";
	public static String IR_USERAgent = "BestvPlayer2345";
	public static String IR_MEDIA_TYPE = "BestvPlayerAndroid";
	//易观方舟
	public static String Eguan_appKey = "4qqr7oeatx7ckj6sb";
	public static String Eguan_channel = "2345";
	public static boolean flag2345 = false;
	
//	public static String BESTV_APP_CHANNEL = "fdd5b523-0f53-4588-919d-36d9a25ab49b";
//	public static String BESTV_APP_KEY = "0df63721cda583306c0a332cc5f529e2";
//	public static String BESTV_USER_DATA_CHANNEL = "UV_BESTV_meizu";
//	//艾瑞
//	public static String IR_UAID = "UA-DFMZ-140001";
//	public static String IR_USERAgent = "BestvPlayerMeizu";
//	public static String IR_MEDIA_TYPE = "BestvPlayerAndroid";
//	//易观方舟
//	public static String Eguan_appKey = "4qqr7oeatx7ckj6sb";
//	public static String Eguan_channel = "meizu";
//	public static boolean flag2345 = false;

//	public static String BESTV_APP_CHANNEL = "c704b5e8-838d-4082-aef5-10d83d4a094a";
//	public static String BESTV_APP_KEY = "fd5c6e0295aa6113f657053c214803df";
//	public static String BESTV_USER_DATA_CHANNEL = "UV_BESTV_360";
//	//艾瑞
//	public static String IR_UAID = "UA-DFMZ-140001";
//	public static String IR_USERAgent = "BestvPlayer360";
//	public static String IR_MEDIA_TYPE = "BestvPlayerAndroid";
//	//易观方舟
//	public static String Eguan_appKey = "4qqr7oeatx7ckj6sb";
//	public static String Eguan_channel = "360";
//	public static boolean flag2345 = false;
		
}
