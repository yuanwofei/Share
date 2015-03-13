package com.eshore.socialsharedemo.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.eshore.socialsharedemo.WXShare;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;

/**
 * 处理微信分享返回结果
 * 
 * @author fayuan
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

	WXShare wxShare;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		wxShare = new WXShare(this);
		wxShare.getApi().handleIntent(getIntent(), this);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		
        setIntent(intent);
        wxShare.getApi().handleIntent(intent, this);
	}
	
	@Override
	public void onReq(BaseReq req) {
	}

	// 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
	@Override
	public void onResp(BaseResp resp) {
		switch (resp.errCode) {
		case BaseResp.ErrCode.ERR_OK:
			Toast.makeText(this, "分享成功", Toast.LENGTH_LONG).show();
			break;
		case BaseResp.ErrCode.ERR_USER_CANCEL:
			Toast.makeText(this, "用户取消", Toast.LENGTH_LONG).show();
			break;
		case BaseResp.ErrCode.ERR_AUTH_DENIED:
			Toast.makeText(this, "验证拒绝" + resp.errCode, Toast.LENGTH_LONG).show();
			break;
		default:
			break;			
		}
		finish();
	}
}
