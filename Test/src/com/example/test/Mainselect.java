/*
 * 메인 메뉴 화면
 */
package com.example.test;

import java.lang.ref.WeakReference;
import java.net.Socket;

import com.google.android.gcm.GCMRegistrar;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

public class Mainselect extends Activity {
	
	String id;
	Socket client;
	Thread thread;
	LoginThread loginThread;
	
	private final mainHandler ghandler=new mainHandler(this);

	private static class mainHandler extends Handler{
		private final WeakReference<Mainselect> aActivity;
		public mainHandler(Mainselect activity){
			aActivity = new WeakReference<Mainselect>(activity);
		}
		@Override
		public void handleMessage(Message msg){
			Mainselect  activity=aActivity.get();
			if(activity!=null){
				activity.handleMessage(msg);
			}
		}
	}
	private void handleMessage(Message msg){
		Bundle bundle = msg.getData();
		int result=bundle.getInt("result");		
		if(result==5)
		{
			Log.v("태그","기기 등록 잘 됐음.");
		}
			
		}//기기등록.
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainselect);

        MainActivity ma=new MainActivity();
        Typeface mTypeface=Typeface.createFromAsset(getAssets(), "fonts/hun.ttf");
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content); 
        ma.setGlobalFont(root,mTypeface);
        
        registerGcm();
        
        Intent intent_01 = getIntent();
        id = intent_01.getStringExtra("아이디");
    }
    
    public void registerGcm() 
   	{
   		GCMRegistrar.checkDevice(this);
   		GCMRegistrar.checkManifest(this);
   		 
   		final String regId = GCMRegistrar.getRegistrationId(this);
   		 
   		if(regId.equals("")) 
   		{
   			GCMRegistrar.register(this, "938564819292");
   		} 
   		else 
   		{
   			thread = new Thread(){
				public void run() {
					super.run();
					SocketService s=new SocketService();
					client = s.getsocket();			
					loginThread = new LoginThread(client, ghandler, id, regId,4);
					loginThread.start();
				}
			};		
			thread.start();		
			Log.e("id", regId);
   		}
   		 
   	}
    public void select1(View v){
        Intent intent=new Intent(getApplicationContext(), makeroom.class);
        intent.putExtra("아이디", id);
        startActivity(intent);
    }//방생성 메뉴
    

    public void select2(View v){
        Intent intent=new Intent(getApplicationContext(), Selectgroup.class);
        intent.putExtra("아이디", id);
        startActivity(intent);
    }//전체 약정방 보기 메뉴


    public void select3(View v){
        Intent intent=new Intent(getApplicationContext(), findmyfriend.class);
        intent.putExtra("아이디", id);
        startActivity(intent);
    }//친구 관리 메뉴

    public void select4(View v){
        Intent intent=new Intent(getApplicationContext(), Settingmain.class);
        intent.putExtra("아이디", id);
        startActivity(intent);
    }//세팅 메뉴
    
    public void home(View v)
    {
        Intent intent=new Intent(getApplicationContext(), Mainselect.class);
        intent.putExtra("아이디", id);
        startActivity(intent);
    }
    
    public void setting(View v)
    {
    	Intent intent=new Intent(getApplicationContext(), Settingmain.class);
        intent.putExtra("아이디", id);
        startActivity(intent);
    }
}