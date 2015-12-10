package com.example.test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class Web extends Activity {
	
	private WebView mWebView;
	String finalword,ID;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web);
		
		Intent intent_01 = getIntent(); 
        finalword= intent_01.getStringExtra("검색인자");
        ID=intent_01.getStringExtra("아이디");
       String url="http://search.naver.com/search.naver?where=nexearch&query="+finalword+"&sm=top_hty&fbm=1&ie=utf8";
        mWebView=(WebView)findViewById(R.id.webView);
        WebSettings set=mWebView.getSettings();
        set.setJavaScriptEnabled(true);//자바스크립트 켜기
        set.setLoadWithOverviewMode(true);
        set.setUseWideViewPort(true);
        
       mWebView.loadUrl(url);
		mWebView.setWebViewClient(new WebViewClientClass());
		mWebView.setVerticalScrollBarEnabled(true);
	}
	
	private class WebViewClientClass extends WebViewClient{
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url){
			view.loadUrl(url);
			return true;
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event){
		if((keyCode==KeyEvent.KEYCODE_BACK)&&mWebView.canGoBack()){
			mWebView.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}//웹의 뒤로가기버튼 구현

}
