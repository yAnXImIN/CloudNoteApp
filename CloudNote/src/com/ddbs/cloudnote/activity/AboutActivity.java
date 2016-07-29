package com.ddbs.cloudnote.activity;

import com.ddbs.cloudnote.R;
import com.ddbs.cloudnote.util.ActivityCollector;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebView;
/**
 * 关于界面
 * @author yanximin
 * */
public class AboutActivity extends BaseActivity {
	private WebView webView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.about);
		webView = (WebView) findViewById(R.id.webView);
		webView.getSettings().setDefaultTextEncodingName("GBK");
		webView.loadUrl("file:///android_asset/about.html");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	
}
