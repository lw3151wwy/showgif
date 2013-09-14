package com.showgif.gif.util;

import java.security.PublicKey;

import android.R.integer;

public interface AppConstantS {
	public static final int headOriHeightPx = 264;
	public static final int bodyOriHeightPx = 351;
	public static final int makePicOriHeightPx = 450;
	public static final int headAndBodyOverLap = 165;
	
	public static final String Asset_BG_FolderName = "background";
	public static final String Asset_320BG_FileName = "blue_bg_320.png";
	//新浪微博：权限参数APP KEY
	public static final String APP_KEY="3064320766";
	
	//新浪微博：REDIRECT_URL（分享下面的来自……的LINK地址）
	public static final String REDIRECT_URL = "http://www.grocamp.com/showgif.html";
	    
	//新浪微博：权限参数
	public static final String SCOPE = "email,direct_messages_read,direct_messages_write," +
			"friendships_groups_read,friendships_groups_write,statuses_to_me_read," +
				"follow_app_official_microblog";

	public static String APP_STORE_FOLDERNAME = "SHOWGIF";
	
	public static final String CLIENT_ID = "client_id";
	
	public static final String RESPONSE_TYPE = "response_type";
	
	public static final String USER_REDIRECT_URL = "redirect_uri";
	
	public static final String DISPLAY = "display";
	
	public static final String USER_SCOPE = "scope";
	
	public static final String PACKAGE_NAME = "packagename";
	
	public static final String KEY_HASH = "key_hash";


	
}
