package com.eshore.socialsharedemo;

import java.io.ByteArrayOutputStream;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMessage;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.AsyncWeiboRunner;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.net.WeiboParameters;
import com.sina.weibo.sdk.utils.Utility;

/**
 * 新浪微博分享工具类，此类目前只能分享文字，要想分享其他的，要改写才行
 * 
 * @author fayuan
 */
public class WeiboShare {
	
	private AuthInfo mAuthInfo;  
	
	private SsoHandler mSsoHandler;
	
	private IWeiboShareAPI mWeiboShareAPI;
	
	private static final String API_BASE_URL = "https://api.weibo.com/2/users/show.json";
	
	private LoginCallback loginCallback;
	
	private Context context;
	
	/**用于区分登录操作和分享操作, true表示这是登录操作， false表示这是分享操作*/
	private boolean isLoginByWeibo;
	
	/**
	 * 注册到微博,并返回微博分享实例
	 * 
	 * @param context
	 */
	public WeiboShare(Context context) {
		this.context = context;
		mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(context, ShareKey.WEIBO_APP_KEY);
		mWeiboShareAPI.registerApp(); // 将应用注册到微博客户端	
	}
	
	/**获取微博SsoHandler*/
	public SsoHandler getWeiboSsoHandler() {
		if (mSsoHandler == null) {
			mAuthInfo = new AuthInfo(context, ShareKey.WEIBO_APP_KEY, ShareKey.WEIBO_REDIRECT_URL, ShareKey.WEIBO_SCOPE);			
			mSsoHandler = new SsoHandler((Activity) context, mAuthInfo);
		}
		return mSsoHandler;
	}
	
	public void handleWeiboResponse(Intent intent, IWeiboHandler.Response response) {
		mWeiboShareAPI.handleWeiboResponse(intent, response);
	}
	
	/**发起 SSO 登陆的 Activity 必须重写 onActivityResult*/
	public void authorizeCallBack(int requestCode, int resultCode, Intent data) {
		if (mSsoHandler != null) {
			mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
		}		
	}
		
	/**
	 * 使用新浪微博登录
	 */
	public void loginByWeibo() {			
		authorize();
	}	
	
	/**
	 * 分享文本到微博
	 * 
	 * @param context
	 * @param text
	 */
	public void shareText(String text) {
		//微博必须验证完后才能分享
		//authorize(text);
	}

	public void shareWebpage(String title, String desc, Bitmap bitmap, String url) {
		isLoginByWeibo = false; //分享操作
		authorize(title, desc, bitmap, url);
	}
	
	private void doShareWebpage(Context context, String title, String desc,
			Bitmap bitmap, String url) {
		WeiboMultiMessage weiboMessage = new WeiboMultiMessage();

		WebpageObject webpageObject = new WebpageObject();	
		webpageObject.identify = Utility.generateGUID();
        webpageObject.title = title;
        webpageObject.description = desc;
        webpageObject.actionUrl = url;
        webpageObject.defaultText = "消息盒子";       
        webpageObject.thumbData = bmpToByteArray(bitmap);
        
        ImageObject imageObject = new ImageObject();
        imageObject.setImageObject(bitmap);
        
        weiboMessage.imageObject = imageObject;
        weiboMessage.mediaObject = webpageObject;       
        
        SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
        // 用transaction唯一标识一个请求
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.multiMessage = weiboMessage;   
        
        mWeiboShareAPI.sendRequest((Activity)context, request);		
	}	
	
	/**
	 * 分享文本到微博
	 * 
	 * @param context
	 * @param text
	 */	
	private void doShareText(String text) {
		WeiboMessage weiboMessage = new WeiboMessage();// 初始化微博的分享消息
		
		//创建文本对象
		TextObject textObject = new TextObject();
		textObject.text = text;
		
		weiboMessage.mediaObject = textObject;
			
		SendMessageToWeiboRequest request = new SendMessageToWeiboRequest();
		request.transaction = String.valueOf(System.currentTimeMillis());
		request.message = weiboMessage;
		
		// 发送请求消息到微博，唤起微博分享界面
		mWeiboShareAPI.sendRequest((Activity)context, request);
	}
	
	/**使用新浪微博登录之前必须先验证*/
	private void authorize() {
		if (!isWeiboAppSupportAPI()) {
			return;
		}
		
		isLoginByWeibo = true;
		
		getWeiboSsoHandler().authorizeClientSso(new AuthListener());
	}	
		
	/**分享之前必须先验证*/
	private void authorize(String title, String desc, Bitmap bitmap, String url) {
		if (!isWeiboAppSupportAPI()) {
			return;
		}
		
		isLoginByWeibo = false;
		
		//SessionValid有效，就直接分享,不需要再次验证
		if (AccessTokenKeeper.readAccessToken(context).isSessionValid()) {
			doShareWebpage(context, title, desc, bitmap, url);
		} else { //SessionValid无效，就先验证，再分享
			getWeiboSsoHandler().authorizeClientSso(new AuthListener(title, desc, bitmap, url));
		}
	}

	/**检查微博客户端是否不支持 SDK 分享或微博客户端未安装或微博客户端是非官方版本。*/
	private boolean isWeiboAppSupportAPI() {
		if (!mWeiboShareAPI.isWeiboAppSupportAPI()) {
			Toast.makeText(context, "微博客户端不支持 SDK 分享或微博客户端未安装或微博客户端是非官方版本。", 
					Toast.LENGTH_SHORT).show();
			return false; 
		}
		return true;		
	}
	
	/**验证监听器*/
	class AuthListener implements WeiboAuthListener {
		
		String title;
		String desc;
		Bitmap bitmap;
		String url;
				
		public AuthListener() {			
		}

		public AuthListener(String title, String desc, Bitmap bitmap, String url) {
			this.title = title;
			this.desc = desc;
			this.bitmap = bitmap;
			this.url = url;
		}
		
		@Override
		public void onComplete(Bundle values) {
			// 从Bundle// 中解析Token
			Oauth2AccessToken mAccessToken = Oauth2AccessToken.parseAccessToken(values); 
			Log.d("TAG", "values = " + values.toString());

			if (mAccessToken.isSessionValid()) {
				// 保存 Token 到 SharedPreferences
                AccessTokenKeeper.writeAccessToken(context, mAccessToken);
				
                if (isLoginByWeibo) { //登录操作                
	                WeiboParameters params = new WeiboParameters(ShareKey.WEIBO_APP_KEY);
	                params.put("uid", Long.parseLong(mAccessToken.getUid()));
	                params.put("access_token", mAccessToken.getToken());
	                AsyncWeiboRunner asyncWeiboRunner = new AsyncWeiboRunner(context);
	                asyncWeiboRunner.requestAsync(API_BASE_URL, params, "GET", requestListener);
				} else { //分享操作
					doShareWebpage(context, title, desc, bitmap, url);
				}
			} else {
				// 以下几种情况，您会收到 Code：
				// 1. 当您未在平台上注册的应用程序的包名与签名时；
				// 2. 当您注册的应用程序包名与签名不正确时；
				// 3. 当您在平台上注册的包名和签名与您当前测试的应用的包名和签名不匹配时。
				String code = values.getString("code");
				String message = "授权失败";
				if (!TextUtils.isEmpty(code)) {
					message = message + "\nObtained the code: " + code;
				}
				Toast.makeText(context, message, Toast.LENGTH_LONG).show();
			}
		}

		@Override
		public void onCancel() {
			Toast.makeText(context, "取消授权", Toast.LENGTH_LONG).show();
		}

		@Override
		public void onWeiboException(WeiboException e) {
			Toast.makeText(context, "Auth exception : " + e.getMessage(),
					Toast.LENGTH_LONG).show();
		}
	}
	
    /**
     * 微博 OpenAPI 回调接口。 获取用户信息
     */
    private RequestListener requestListener = new RequestListener() {
        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response) && loginCallback != null) {
            	Log.d("TAG", "response = " + response);
            	
            	loginCallback.onResponse(response);    	
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            
        }
    };	

    interface LoginCallback {
    	void onResponse(String response);
    }
    
    public void setLoginCallback(LoginCallback loginCallback) {
    	this.loginCallback = loginCallback;
    }
    
	/**
	 * 保存微博验证信息
	 */
	static class AccessTokenKeeper {
	    private static final String PREFERENCES_NAME = "weibo_access_token";

	    private static final String KEY_UID           = "uid";
	    private static final String KEY_ACCESS_TOKEN  = "access_token";
	    private static final String KEY_EXPIRES_IN    = "expires_in";
	    
	    /**
	     * 保存 Token 对象到 SharedPreferences。
	     * 
	     * @param context 应用程序上下文环境
	     * @param token   Token 对象
	     */
	    public static void writeAccessToken(Context context, 
	    		Oauth2AccessToken token) {
	        if (null == context || null == token) {
	            return;
	        }
	        
	        SharedPreferences pref = context.getSharedPreferences(
	        		PREFERENCES_NAME, Context.MODE_APPEND);
	        Editor editor = pref.edit();
	        editor.putString(KEY_UID, token.getUid());
	        editor.putString(KEY_ACCESS_TOKEN, token.getToken());
	        editor.putLong(KEY_EXPIRES_IN, token.getExpiresTime());
	        editor.commit();
	    }

	    /**
	     * 从 SharedPreferences 读取 Token 信息。
	     * 
	     * @param context 应用程序上下文环境
	     * 
	     * @return 返回 Token 对象
	     */
	    public static Oauth2AccessToken readAccessToken(Context context) {
	        if (null == context) {
	            return null;
	        }
	        
	        Oauth2AccessToken token = new Oauth2AccessToken();
	        SharedPreferences pref = context.getSharedPreferences(
	        		PREFERENCES_NAME, Context.MODE_APPEND);
	        token.setUid(pref.getString(KEY_UID, ""));
	        token.setToken(pref.getString(KEY_ACCESS_TOKEN, ""));
	        token.setExpiresTime(pref.getLong(KEY_EXPIRES_IN, 0));
	        return token;
	    }

	    /**
	     * 清空 SharedPreferences 中 Token信息。
	     * 
	     * @param context 应用程序上下文环境
	     */
	    public static void clear(Context context) {
	        if (null == context) {
	            return;
	        }
	        
	        SharedPreferences pref = context.getSharedPreferences(
	        		PREFERENCES_NAME, Context.MODE_APPEND);
	        Editor editor = pref.edit();
	        editor.clear();
	        editor.commit();
	    }
	}	
	   
    private static byte[] bmpToByteArray(Bitmap bmp) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(CompressFormat.PNG, 100, output);
       
        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }	
}