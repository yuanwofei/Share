package com.eshore.socialsharedemo;

/***
 * 第三方平台分享需要申请的key常数，这些key常数要替换为本项目到第三方分享平台申请的key
 * <p>
 * 下面的key在本项目中是无效的
 * 
 * @author fayuan
 */
public class ShareKey {
	
	/**用于微信好友和微信朋友圈的分享的app id*/
	public final static String WX_APP_ID = "wxe3e4a2ad7273108f";
	
	//×××××××××新浪微博 beigin××××××××××//
	public static final String WEIBO_APP_KEY = "1942548199";
	
	/**这个要看你在微博官网上是如何设置的，官网上设置成哪个，这里就填哪个*/
	public static final String WEIBO_REDIRECT_URL =
			"https://api.weibo.com/oauth2/default.html";
	
	/**这个不需要填写，默认这样就可以了*/
	public static final String WEIBO_SCOPE = "show.json";
	//×××××××××新浪微博 end××××××××××//
	
	/**用于qq好友和qq空间的分享的app id*/
	public final static String QQ_APP_ID = "1104177374";
}
