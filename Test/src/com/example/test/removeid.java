/*
 * 회원 탈퇴
 */
package com.example.test;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.Socket;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

public class removeid extends Activity {
	String id,pw;
	Socket client;
	
	Thread thread;
	LoginThread loginThread;
    
	private final RemoveIDHandler RIDhandler=new RemoveIDHandler(this);

	private static class RemoveIDHandler extends Handler{
		private final WeakReference<removeid> aActivity;
		public RemoveIDHandler(removeid activity){
			aActivity = new WeakReference<removeid>(activity);
		}
		@Override
		public void handleMessage(Message msg){
			removeid activity=aActivity.get();
			if(activity!=null){
				activity.handleMessage(msg);
			}
		}
	}
	
	private void handleMessage(Message msg){
		Bundle bundle = msg.getData();
		int result=bundle.getInt("result");
		if(result==1){
			try 
			{
				client.close();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
			
			File myfile=getDir("myfile", Context.MODE_PRIVATE);
		    String path= myfile.getAbsolutePath();
		    File file=new File(path+"/flag.txt");
		    file.delete();
		    
			Toast.makeText(getApplicationContext(), "탈퇴가 완료 되었습니다.", Toast.LENGTH_SHORT).show();
	        Intent intent=new Intent(getApplicationContext(), login.class);
	        startActivity(intent);//첫 화면으로
		}//탈퇴 완료
		else if(result==2){
			try 
			{
				client.close();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
			Toast.makeText(getApplicationContext(), "회원 정보가 맞지 않습니다.", Toast.LENGTH_SHORT).show();
		}//회원 정보 오류
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.removeid);

        MainActivity ma=new MainActivity();
        Typeface mTypeface=Typeface.createFromAsset(getAssets(), "fonts/hun.ttf");
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content); 
        ma.setGlobalFont(root,mTypeface);
        
        Intent intent_01 = getIntent();
        id = intent_01.getStringExtra("아이디");
    }


    public void removeidnot(View v){
        Intent intent=new Intent(getApplicationContext(), Settingmain.class);
        intent.putExtra("아이디", id);
        startActivity(intent);
    }//돌아가기


    public void removeidgo(View v){
    	EditText text_id = (EditText) findViewById(R.id.userEntry);
        EditText text_pw = (EditText) findViewById(R.id.passwordEntry);
        id = text_id.getText().toString();
        pw = text_pw.getText().toString();
        
        if(id.contains("/")==true)
			Toast.makeText(getApplicationContext(), "/는 들어가면 안되요.", Toast.LENGTH_SHORT).show();
        
        if(id.length()>15){
        	Toast.makeText(getApplicationContext(), "아이디가 너무 길어요", Toast.LENGTH_SHORT).show();
			//아이디가 너무 길때 경고메시지			
		}
		else if(id.length()<1){
			Toast.makeText(getApplicationContext(), "아이디를 입력하세요", Toast.LENGTH_SHORT).show();			
			//아무것도 안 입력했을때 경고메시지
		}
        
        if(pw.contains("/")==true)
			Toast.makeText(getApplicationContext(), "/는 들어가면 안되요.", Toast.LENGTH_SHORT).show();
		if(pw.length()>15){
			Toast.makeText(getApplicationContext(), "비밀번호가 너무 길어요", Toast.LENGTH_SHORT).show();
			//비번이 길때 경고메시지
		}
		else if(pw.length()<1){
			Toast.makeText(getApplicationContext(), "비밀번호를 입력하세요", Toast.LENGTH_SHORT).show();
			//아무것도 안 입력했을때 경고메시지
		}		
		else if(id.length()<=15&&id.length()>0 && id.contains("/")==false && pw.contains("/")==false){
			thread = new Thread(){
				public void run() {
					super.run();			
					SocketService s=new SocketService();
					client = s.getsocket();			
					loginThread = new LoginThread(client, RIDhandler, id, pw,2);
					loginThread.start();
				}
			};
			thread.start();
		}
    }//탈퇴
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