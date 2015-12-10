/*
 * �α��� ȭ��
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
			Toast.makeText(getApplicationContext(), "�α��� �Ϸ�", Toast.LENGTH_SHORT).show();
			if(chk.isChecked()==true)
			{
				try
				 {
					File myfile=getDir("myfile", Context.MODE_PRIVATE);
			        String path= myfile.getAbsolutePath();
			        File file=new File(path+"/flag.txt");			        
			        FileOutputStream fos=new FileOutputStream(file,false);//���� ����			
			        
			        fos.write(id.getBytes());
					fos.close();
				 }//�ڵ� �α��� ���̵� ���� ���
				 catch(IOException e)
				 {
					 e.printStackTrace();
				 }
			}//�ڵ� �α��� üũ�Ǿ��� ���	
			try 
			{
				client.close();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
			
			registerGcm();     //��� id ��� ����.
			
			Intent intent_01 = new Intent(getApplicationContext(), Mainselect.class);
			intent_01.putExtra("���̵�", id);       
			startActivity(intent_01);
		}//�α��� �Ϸ��		
		
		else if(result==4){
			Toast.makeText(getApplicationContext(), "�α��� ����!!", Toast.LENGTH_SHORT).show();
			try {
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}//�α��� ������
	}//�������� �޾ƿ� �α��� ��� ���
	
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
			Toast.makeText(getApplicationContext(), "/�� ���� �ȵǿ�.", Toast.LENGTH_SHORT).show();
        if(id.length()>15){
        	Toast.makeText(getApplicationContext(), "���̵� �ʹ� ����", Toast.LENGTH_SHORT).show();
			//���̵� �ʹ� �涧 ���޽���			
		}
		else if(id.length()<1){
			Toast.makeText(getApplicationContext(), "���̵� �Է��ϼ���", Toast.LENGTH_SHORT).show();			
			//�ƹ��͵� �� �Է������� ���޽���
		}
        
        if(pw.contains("/")==true)
			Toast.makeText(getApplicationContext(), "/�� ���� �ȵǿ�.", Toast.LENGTH_SHORT).show();
		if(pw.length()>15){
			Toast.makeText(getApplicationContext(), "��й�ȣ�� �ʹ� ����", Toast.LENGTH_SHORT).show();
			//����� �涧 ���޽���
		}
		else if(pw.length()<1){
			Toast.makeText(getApplicationContext(), "��й�ȣ�� �Է��ϼ���", Toast.LENGTH_SHORT).show();
			//�ƹ��͵� �� �Է������� ���޽���
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
    }//�α��� ��ư

    public void makenewid(View v){
        Intent intent_01 = new Intent(getApplicationContext(), lognewid.class);
        startActivity(intent_01);
    }//ȸ�������ϱ� ��ư

}