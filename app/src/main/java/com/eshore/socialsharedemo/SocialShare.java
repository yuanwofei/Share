package com.eshore.socialsharedemo;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;

import com.eshore.socialsharedemo.R;

/***
 * 社会分享,目前只支持分享文字到新浪微博、微信好友、微信朋友圈、qq好友和qq空间</br>
 * 
 * 用法:</br>
 * 1.用到分享的Activity都有继承ShareActivity基类</br>
 * <p>
 * 2.在用到分享的Activity中实现{@link SocialShare.ShareCallback}接口</br>
 *  然后在void share(int id);方法中调用socialShare.share(id, "要分享的文字");</br>
 *  执行分享。</br>
 * <p>
 * 3.创建一个SocialShare实例，然后在分享点击事件中调用socialShare.show()来显示分享对话框,进行分享
 * 
 * @author fayuan
 */
public class SocialShare {
	
	/**微信好友*/
	private static final int PLATFORM_WEIXIN = 0;
	/**微信朋友圈*/
	private static final int PLATFORM_WEIXIN_TIMELINE = 1;
	/**新浪微博*/
	private static final int PLATFORM_SINA_WEIBO = 2;
	/**QQ*/
	private static final int PLATFORM_QQ = 3;	
	/**QQ空间*/
	private static final int PLATFORM_QQ_ZONE = 4;	
	
	private Context context;
	
	private List<ShareItem> shareItems;
	
	private ShareCallback shareCallback;
	
	private TencentQQShare tencentQQShare;
	
	private WXShare wxShare;
	
	private WeiboShare weiboShare;
	
	public SocialShare(Context context, ShareCallback shareCallback) {
		this.context = context;
		this.shareCallback = shareCallback;
		this.shareItems = new ArrayList<SocialShare.ShareItem>();
		init();
		
		wxShare = new WXShare(context);
		tencentQQShare = new TencentQQShare(context);
		weiboShare = new WeiboShare(context);
	}

	/**初始化分享平台的信息，可以通过更改这里的平台添加顺序来改变各个平台的顺序*/
	private void init() {
		shareItems.add(new ShareItem(
				R.drawable.share_weixin_icon, PLATFORM_WEIXIN, "微信好友") {
			@Override
			public void onClick(int id) {
				if (shareCallback != null) {
					shareCallback.share(id);
				}				
			}
		});
		
		shareItems.add(new ShareItem(
				R.drawable.share_weixin_timeline_icon, PLATFORM_WEIXIN_TIMELINE, "微信朋友圈") {
			@Override
			public void onClick(int id) {
				if (shareCallback != null) {
					shareCallback.share(id);
				}	
			}
		});		
		
		shareItems.add(new ShareItem(
				R.drawable.share_sina_icon, PLATFORM_SINA_WEIBO, "新浪微博") {
			@Override
			public void onClick(int id) {
				if (shareCallback != null) {
					shareCallback.share(id);
				}	
			}
		});	
		
		shareItems.add(new ShareItem(
				R.drawable.share_qq, PLATFORM_QQ, "QQ好友") {
			@Override
			public void onClick(int id) {
				if (shareCallback != null) {
					shareCallback.share(id);
				}	
			}
		});		
		
		shareItems.add(new ShareItem(
				R.drawable.share_qzone, PLATFORM_QQ_ZONE, "QQ空间") {
			@Override
			public void onClick(int id) {
				if (shareCallback != null) {
					shareCallback.share(id);
				}	
			}
		});			
	}
	
    /**
     * 分享文字
     * 
     * @param id
     * @param text
     */
	public void shareText(int id, String text) {
		switch (id) {
		case PLATFORM_WEIXIN:
			wxShare.shareText(text, false);
			break;
			
		case PLATFORM_WEIXIN_TIMELINE:
			wxShare.shareText(text, true);
			break;
			
		case PLATFORM_SINA_WEIBO:
			weiboShare.shareText(text);
			break;
		case PLATFORM_QQ:
			tencentQQShare.shareText(text, false);
			break;
			
		case PLATFORM_QQ_ZONE:
			tencentQQShare.shareText(text, true);
			break;			
		}
	}
	
	/**
	 * 分享网页
	 * 
	 * @param title 标题（新浪微博不支持此字段）
	 * @param content 文字内容
	 * @param bitamp （qq和qq空间不支持此字段，而是用ImageUrl）
	 * @param imageUrl qq和qq空间的imageUrl
	 * @param url 点击分享的内容，跳到的地址
	 */
	public void shareWebPage(int id, String title, String content,
			Bitmap bitmap, String imageUrl, String url) {
		switch (id) {
		case PLATFORM_WEIXIN:
			wxShare.shareWebPage(title, content, url, bitmap, false);
			break;
			
		case PLATFORM_WEIXIN_TIMELINE:
			wxShare.shareWebPage(title, content, url, bitmap, true);
			break;
			
		case PLATFORM_SINA_WEIBO:
			weiboShare.shareWebpage(title, content, bitmap, url);
			break;
			
		case PLATFORM_QQ:
			tencentQQShare.shareWebpage(title, content, imageUrl, url, false);
			break;
			
		case PLATFORM_QQ_ZONE:
			tencentQQShare.shareWebpage(title, content, imageUrl, url, true);
			break;
		}		
	}
	
	/**显示第三方平台分享对话框，目前只支持分享文字到新浪微博、微信好友、微信朋友圈、qq好友和qq空间*/
	public void show() {
		ShareDialog.show(context, shareItems);
	}
	
	/**
	 * 需要在分享的Activity中实现的回调接口
	 */
	public interface ShareCallback {
		/**
		 * 在该方法中调用share(int id, String text)方法来分享文字
		 *  
		 * @param id 第三方平台id
		 */
		void share(int id);
	}
	
	public class ShareItem {
		/**第三方平台资源图标*/
		public int logo;
		/**第三方平台id*/
		public int id;		
		/**第三方平台名称*/
		public String name;		
		
		/**
		 * @param logo 第三方平台资源图标
		 * @param id   第三方平台id
		 * @param name 第三方平台名称
		 */
		public ShareItem(int logo, int id, String name) {
			this.logo = logo;
			this.id = id;
			this.name = name;
		}
		
		/**
		 * ShareItem被点击的回调方法
		 *  
		 * @param id 第三方平台id
		 */
		public void onClick(int id) {
		}
	}

	public TencentQQShare getTencentQQShare() {
		return tencentQQShare;
	}

	public WeiboShare getWeiboShare() {
		return weiboShare;
	}
}
