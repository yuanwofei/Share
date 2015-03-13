package com.eshore.socialsharedemo;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;


/**
 * QQ\QQ空间分享工具类，此类目前只能分享文字，要想分享其他的，要改写才行
 * 
 * @author fayuan
 */
public class TencentQQShare {

	private Tencent tencent;
	
	private QQUIListener listener;
	
	private Context context;
	
	/**
	 * 注册到QQ,并返回QQ分享实例
	 * 
	 * @param context
	 */
	public TencentQQShare(Context context) {
		this.context = context;
		tencent = Tencent.createInstance(ShareKey.QQ_APP_ID, context.getApplicationContext());
		listener = new QQUIListener();
	}
	
	/**
	 * qq分享回调接口
	 */
    class QQUIListener implements IUiListener {
    	
		@Override
		public void onCancel() {
			Toast.makeText(context, "用户取消", Toast.LENGTH_LONG).show();
		}

		@Override
		public void onComplete(Object object) {
			Toast.makeText(context, "分享成功", Toast.LENGTH_LONG).show();
		}

		@Override
		public void onError(UiError e) {
			Toast.makeText(context, "分享失败" + e.errorMessage, Toast.LENGTH_LONG).show();
		}		
	}
	
	/**如果要成功接收到回调，需要在调用接口的Activity的onActivityResult方法中调用该方法：*/
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (tencent != null) {
			tencent.onActivityResult(requestCode, resultCode, data);
		}
	}
	

    /**
     * 分享文本到qq好友或qq空间
     * 
     * @param context
     * @param text 分享的文本
     * @param isQQZone true表示发送到qq 空间，false表示发送qq好友
     */
    public void shareText(String text, boolean isQQZone) {    	
    	if (!isQQZone) {
			doShareToQQ(null, text, null, null);
		} else {
			doShareToQQZone(null, text, null, null);
		}
    }	
	
    /**
     * 分享网页到qq好友或qq空间
     * 
     * @param context
     * @param text 分享的文本
     * @param isQQZone true表示发送到qq 空间，false表示发送qq好友
     */
    public void shareWebpage(String title, String text, String imageUrl, String targeUrl, boolean isQQZone) {   	
    	if (!isQQZone) {
			doShareToQQ(title, text, imageUrl, targeUrl);
		} else {
			doShareToQQZone(title, text, imageUrl, targeUrl);
		}
    }
    
    /**分享到qq好友*/
    private void doShareToQQ(String title, String text, String imageUrl, String targeUrl) {
    	Bundle bundle = new Bundle();

    	bundle.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);

    	//分享的标题。注：PARAM_TITLE、PARAM_IMAGE_URL、PARAM_SUMMARY不能全为空，最少必须有一个是有值的。
    	if (!TextUtils.isEmpty(title)) {
			bundle.putString(QQShare.SHARE_TO_QQ_TITLE, title);
		}  	
    	
    	//分享的消息摘要，最长50个字
    	bundle.putString(QQShare.SHARE_TO_QQ_SUMMARY, text);
    	
    	//分享的图片URL
    	if (!TextUtils.isEmpty(imageUrl)) {
			bundle.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, imageUrl);
		}   	
    	
    	//这条分享消息被好友点击后的跳转URL。
    	if (!TextUtils.isEmpty(targeUrl)) {
    		bundle.putString(QQShare.SHARE_TO_QQ_TARGET_URL, targeUrl);
		}
    	bundle.putString(QQShare.SHARE_TO_QQ_TARGET_URL, "http://www.baidu.com");
		tencent.shareToQQ((Activity)context, bundle , listener);
    }
    
    /**分享到qq空间*/
    private void doShareToQQZone(String title, String text, String imageUrl, String targeUrl) {    	
    	Bundle bundle = new Bundle();
    	
    	bundle.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_NO_TYPE);
  	
    	//分享的标题。注：PARAM_TITLE、PARAM_IMAGE_URL、PARAM_SUMMARY不能全为空，最少必须有一个是有值的。
    	if (!TextUtils.isEmpty(title)) {
			bundle.putString(QQShare.SHARE_TO_QQ_TITLE, title);
		}  	
    	
    	//分享的消息摘要，最长50个字
    	bundle.putString(QQShare.SHARE_TO_QQ_SUMMARY, text);
    	
    	//分享的图片URL
    	if (!TextUtils.isEmpty(imageUrl)) {
			bundle.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, imageUrl);
		}   	
    	
    	//这条分享消息被好友点击后的跳转URL。
    	if (!TextUtils.isEmpty(targeUrl)) {
    		bundle.putString(QQShare.SHARE_TO_QQ_TARGET_URL, targeUrl);
		}
    	
    	if (!TextUtils.isEmpty(imageUrl)) {
    		ArrayList<String> urls = new ArrayList<String>();
        	urls.add(imageUrl);
        	bundle.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, urls);
		}   	
		    	
    	//手Q客户端顶部，替换“返回”按钮文字，如果为空，用返回代替
    	bundle.putString(QQShare.SHARE_TO_QQ_APP_NAME, "消息盒子");
    	
    	tencent.shareToQzone((Activity)context, bundle , listener);  	
    }
}
