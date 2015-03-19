package com.eshore.socialsharedemo;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class MainActivity extends ShareActivity implements OnClickListener {	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.main_layout);
		
		findViewById(R.id.share).setOnClickListener(this);
	}
	
	@Override
	public void share(int id) {
		socialShare.shareText(id, "测试文字分享");
		/*socialShare.shareWebPage(
				id, 
				"shareDemo", 
				"shareDemo分享网页", 
				BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher), 
				"http://7u2jt1.com1.z0.glb.clouddn.com/20150128083244.png", 
				"http://news.sina.com.cn/c/2013-10-22/021928494669.shtml");*/
	}
	
	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.share:			
			socialShare.show();		
			break;	
		}
	};
}
