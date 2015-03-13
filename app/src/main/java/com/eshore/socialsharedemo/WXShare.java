package com.eshore.socialsharedemo;

import java.io.ByteArrayOutputStream;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.widget.Toast;

import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXImageObject;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXTextObject;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * 微信分享工具类，此类目前只能分享文字、图片和网页，要想分享其他的，要改写才行
 * 
 * @author fayuan
 */
public class WXShare {

	private IWXAPI api;
	
	private Context context;
	
	/**
	 * 注册到微信,并返回微信分享实例
	 * 
	 * @param context
	 */
	public WXShare(Context context) {
		this.context = context;
		api = WXAPIFactory.createWXAPI(context, ShareKey.WX_APP_ID, false);
		api.registerApp(ShareKey.WX_APP_ID);
	}
	
	public IWXAPI getApi() {
		return api;
	}	
	
    /**
     * 分享文本到微信好友或朋友圈
     * 
     * @param text 分享的文本
     * @param isTimeline true表示发送到朋友圈，false表示发送好友
     */
    public void shareText(String text, boolean isTimeline) {
        if (!isWxInstalled()) {			
        	return;
		}
    	
    	// 初始化WXTextObject对象
        WXTextObject textObject = new WXTextObject();
        textObject.text = text;

        // 用WXTextObject对象初始化一个WXMediaMessage对象
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = textObject;
        msg.description = textObject.text;

        // 构造一个Req
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.message = msg;
        req.transaction = String.valueOf(System.currentTimeMillis());// 唯一字段，标识一个请求
        req.scene = isTimeline ? SendMessageToWX.Req.WXSceneTimeline : 
        		SendMessageToWX.Req.WXSceneSession;
        
        //执行分享
        api.sendReq(req);
    }
 
    /**
     * 分享图片到微信好友或朋友圈
     * 
     * @param bitmap 大小不超过10MB
     * @param description 文字说明，可空
     * @param isTimeline true表示发送到朋友圈，false表示发送好友
     */
    public void shareImage(Bitmap bitmap, String description, boolean isTimeline) {
        if (!isWxInstalled()) {			
        	return;
		}
    	
    	// 初始化WXTextObject对象
        WXImageObject imageObject = new WXImageObject();
        imageObject.imageData = bmpToByteArray(bitmap);

        // 用WXTextObject对象初始化一个WXMediaMessage对象
        WXMediaMessage msg = new WXMediaMessage();
        msg.description = description + "";
        msg.mediaObject = imageObject;

        // 构造一个Req
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.message = msg;
        req.transaction = String.valueOf(System.currentTimeMillis());// 唯一字段，标识一个请求
        req.scene = isTimeline ? SendMessageToWX.Req.WXSceneTimeline : 
        		SendMessageToWX.Req.WXSceneSession;
        
        //执行分享
        api.sendReq(req);
    }
    
    /**
     * 分享网页到微信
     * 
     * @param context
     * @param title
     * @param description
     * @param url 点击缩略图的跳转地址
     * @param bitmap 缩略图
     * @param isTimeline true表示发送到朋友圈，false表示发送好友
     */
    public void shareWebPage(String title, String description,
    		String url, Bitmap bitmap, boolean isTimeline) {
        if (!isWxInstalled()) {			
        	return;
		}
           
        WXMediaMessage msg = new WXMediaMessage();       
        msg.title = title;
        msg.description = description;   
        msg.thumbData = bmpToByteArray(bitmap);
        
    	// 初始化WXWebpageObject对象
        WXWebpageObject webpageObject = new WXWebpageObject();
        webpageObject.webpageUrl = url;        
        msg.mediaObject = webpageObject;

        // 构造一个Req
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.message = msg;
        req.transaction = String.valueOf(System.currentTimeMillis());// 唯一字段，标识一个请求
        req.scene = isTimeline ? SendMessageToWX.Req.WXSceneTimeline : 
        		SendMessageToWX.Req.WXSceneSession;
        
        //执行分享
        api.sendReq(req);
    }
    
    /**检查微信客户端是否安装*/
    private boolean isWxInstalled() {
    	if (!isAppInstalled("com.tencent.mm")) {
			Toast.makeText(context, "微信客户端尚未安装，请先安装微信客户端!", 
					Toast.LENGTH_SHORT).show();
        	return false;
		}
    	return true;
    }
    
    /**
     * 检查是否安装了指定的软件
     * 
     * @param context
     * @param packageName 应用包名
     * @return true表示已安装了，false没有安装
     */
    private boolean isAppInstalled(String packageName) {
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo pInfo = packageManager.getPackageInfo(packageName,
                    PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);
            // 判断是否获取到了对应的包名信息
            if (pInfo != null) {
                return true;
            }
        } catch (NameNotFoundException e) {
        }
        return false;
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
