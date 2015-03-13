package com.eshore.socialsharedemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.constant.WBConstants;

/**
 * 分享基类，其他用到分享的Activity都要继承该类才行</br>
 * 
 * @author fayuan
 *
 */
public class ShareActivity extends Activity implements IWeiboHandler.Response,
		SocialShare.ShareCallback{
	
	protected SocialShare socialShare;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		socialShare = new SocialShare(this, this);
		
		//新浪微博分享返回分享Activity被销毁后，会调用这个显示分享结果信息
		if (savedInstanceState != null) {
			socialShare.getWeiboShare().handleWeiboResponse(getIntent(), this);
		}
	}
	
	/**
	 * 需要实现分享的Activity重写该类时，必须调用父类的super.onNewIntent(intent);
	 * 否则新浪微博分享后无法显示提示信息
	 */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        
        // 从当前应用唤起微博并进行分享后，返回到当前应用时，需要在此处调用该函数
        // 来接收微博客户端返回的数据；执行成功，返回 true，并调用
        // {@link IWeiboHandler.Response#onResponse}；失败返回 false，不调用上述回调
        socialShare.getWeiboShare().handleWeiboResponse(intent, this);
    }
	
	/**
	 * 需要实现分享的Activity重写该类时，必须调用父类的super.onActivityResult(requestCode, resultCode, data);
	 * <p>
	 * 新浪微博验证返回后调用该方法和qq分享后调用回调接口显示分享结果信息
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		socialShare.getWeiboShare().authorizeCallBack(requestCode, resultCode, data);
		
		socialShare.getTencentQQShare().onActivityResult(requestCode, resultCode, data);
	}
	
	/**新浪微博分享回调*/
	@Override
	public void onResponse(BaseResponse resp) {
		switch (resp.errCode) {
        case WBConstants.ErrorCode.ERR_OK:
        	Toast.makeText(this, "分享成功", Toast.LENGTH_LONG).show();
            break;
            
        case WBConstants.ErrorCode.ERR_CANCEL:
        	Toast.makeText(this, "用户取消", Toast.LENGTH_LONG).show();
            break;
            
        case WBConstants.ErrorCode.ERR_FAIL:
        	Toast.makeText(this, "分享失败 ：" + resp.errMsg, Toast.LENGTH_LONG).show();
            break;
        }
	}

	@Override
	public void share(int id) {
	}
}
