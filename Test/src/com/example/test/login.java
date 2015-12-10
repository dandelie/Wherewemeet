/*
 * 로그인 화면
 */
package com.example.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.Socket;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.test.SocketService;
import com.google.android.gcm.GCMRegistrar;

public class login extends Activity {

	String id,pw;
	Socket client;
	
	Thread thread;
	LoginThread loginThread;
	CheckBox chk;
	TextView talk;
	
	private final LoginHandler lhandler=new LoginHandler(this);

	private static class LoginHandler extends Handler{
		private final WeakReference<login> aActivity;
		public LoginHandler(login activity){
			aActivity = new WeakReference<login>(activity);
		}
		@Override
		public void handleMessage(Message msg){
			login activity=aActivity.get();
			if(activity!=null){
				activity.handleMessage(msg);
			}
		}
	}
	
	private void handleMessage(Message msg){
		Bundle bundle = msg.getData();
		int result=bundle.getInt("result");		
		if(result==3){
			Toast.makeText(getApplicationContext(), "로그인 완료", Toast.LENGTH_SHORT).show();
			if(chk.isChecked()==true)
			{
				try
				 {
					File myfile=getDir("myfile", Context.MODE_PRIVATE);
			        String path= myfile.getAbsolutePath();
			        File file=new File(path+"/flag.txt");			        
			        FileOutputStream fos=new FileOutputStream(file,false);//덮어 쓰기			
			        
			        fos.write(id.getBytes());
					fos.close();
				 }//자동 로그인 아이디 정보 기록
				 catch(IOException e)
				 {
					 e.printStackTrace();
				 }
			}//자동 로그인 체크되었을 경우	
			try 
			{
				client.close();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
			
			registerGcm();     //기기 id 등록 과정.
			
			Intent intent_01 = new Intent(getApplicationContext(), Mainselect.class);
			intent_01.putExtra("아이디", id);       
			startActivity(intent_01);
		}//로그인 완료시		
		
		else if(result==4){
			Toast.makeText(getApplicationContext(), "로그인 오류!!", Toast.LENGTH_SHORT).show();
			try {
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}//로그인 오류시
	}//서버에서 받아온 로그인 결과 출력
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        MainActivity ma=new MainActivity();
        Typeface mTypeface=Typeface.createFromAsset(getAssets(), "fonts/hun.ttf");
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content); 
        ma.setGlobalFont(root,mTypeface);
        
        
        chk=(CheckBox)findViewById(R.id.autologin);
    }
    
    public void registerGcm() 
   	{
   		GCMRegistrar.checkDevice(this);
   		GCMRegistrar.checkManifest(this);
   		 
   		final String regId = GCMRegistrar.getRegistrationId(this);
   		 
   		if (regId.equals("")) 
   		{
   			GCMRegistrar.register(this, "938564819292");
   		} 
   		else 
   		{	
   			Log.e("id", regId);
   		}
   	}
    public void OnLogin(View v){
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
					loginThread = new LoginThread(client, lhandler, id, pw);
					loginThread.start();
				}
			};		
			thread.start();		
		}	
    }//로그인 버튼

    public void makenewid(View v){
        Intent intent_01 = new Intent(getApplicationContext(), lognewid.class);
        startActivity(intent_01);
    }//회원가입하기 버튼

}