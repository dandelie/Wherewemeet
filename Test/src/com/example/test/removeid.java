/*
 * ȸ�� Ż��
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
		    
			Toast.makeText(getApplicationContext(), "Ż�� �Ϸ� �Ǿ����ϴ�.", Toast.LENGTH_SHORT).show();
	        Intent intent=new Intent(getApplicationContext(), login.class);
	        startActivity(intent);//ù ȭ������
		}//Ż�� �Ϸ�
		else if(result==2){
			try 
			{
				client.close();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
			Toast.makeText(getApplicationContext(), "ȸ�� ������ ���� �ʽ��ϴ�.", Toast.LENGTH_SHORT).show();
		}//ȸ�� ���� ����
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
        id = intent_01.getStringExtra("���̵�");
    }


    public void removeidnot(View v){
        Intent intent=new Intent(getApplicationContext(), Settingmain.class);
        intent.putExtra("���̵�", id);
        startActivity(intent);
    }//���ư���


    public void removeidgo(View v){
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
					loginThread = new LoginThread(client, RIDhandler, id, pw,2);
					loginThread.start();
				}
			};
			thread.start();
		}
    }//Ż��
    public void home(View v)
    {
        Intent intent=new Intent(getApplicationContext(), Mainselect.class);
        intent.putExtra("���̵�", id);
        startActivity(intent);
    }
    
    public void setting(View v)
    {
    	Intent intent=new Intent(getApplicationContext(), Settingmain.class);
        intent.putExtra("���̵�", id);
        startActivity(intent);
    }
}