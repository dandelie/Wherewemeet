/*
 * ȸ������ ȭ��
 */
package com.example.test;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.content.Intent;
import android.graphics.Typeface;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.Socket;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.test.SocketService;
import com.google.android.gcm.GCMRegistrar;

public class lognewid extends Activity {
	
	EditText text_id,text_pw,text_nn;
	String ID,PW,NN;
	
	Socket client;
	Thread thread;
	LoginThread loginThread;
	
	private final LognewHandler lognewhandler=new LognewHandler(this);
	  
	private static class LognewHandler extends Handler{
		private final WeakReference<lognewid> aActivity;
		public LognewHandler(lognewid activity){
			aActivity = new WeakReference<lognewid>(activity);
		}
		@Override
		public void handleMessage(Message msg){
			lognewid activity=aActivity.get();
			if(activity!=null){
				activity.handleMessage(msg);
			}
		}
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lognewid);

        MainActivity ma=new MainActivity();
        Typeface mTypeface=Typeface.createFromAsset(getAssets(), "fonts/hun.ttf");
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content); 
        ma.setGlobalFont(root,mTypeface);
        
        text_id = (EditText) findViewById(R.id.newid);
        text_pw = (EditText) findViewById(R.id.newpassword);
        text_nn= (EditText) findViewById(R.id.niknameEntry);
    }

    private void handleMessage(Message msg){
		Bundle bundle = msg.getData();
		int result=bundle.getInt("result");		
		if(result==1){
			Toast.makeText(getApplicationContext(), "�ߺ� ���̵� �Դϴ�. ���̵� �ٽ� �Է����ּ���.", Toast.LENGTH_SHORT).show();		
			text_id.setText("");
			try {
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}//�ߺ� ���̵� �������
		else if(result==2){
			Toast.makeText(getApplicationContext(), "ȸ������ �Ϸ�.", Toast.LENGTH_SHORT).show();
			
			try {
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			registerGcm();     //��� id ��� ����.
			
			Intent intent_01 = new Intent(getApplicationContext(), lognewidtest.class);
	        intent_01.putExtra("���̵�", ID);
	        intent_01.putExtra("�г���", NN);
	        startActivity(intent_01);
			//���� ȭ������ �Ѿ��
		}//ȸ������ �Ϸ�� ���	
	}//�������� �޾ƿ� ȸ�� ���� ��� ���
    
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
    
    public void logewid(View v){
        ID = text_id.getText().toString();
        PW = text_pw.getText().toString();
        NN= text_nn.getText().toString();

        
        if(ID.contains("/")==true)
        {
        	Toast.makeText(getApplicationContext(), "Ư�����ڴ� ���� �ȵǿ�~", Toast.LENGTH_SHORT).show();
        }
        
        if(ID.length()>15){
        	Toast.makeText(getApplicationContext(), "���̵� �ʹ� ����", Toast.LENGTH_SHORT).show();
			//���̵� �ʹ� �涧 ���޽���			
		}
		else if(ID.length()<1){
			Toast.makeText(getApplicationContext(), "���̵� �Է��ϼ���", Toast.LENGTH_SHORT).show();			
			//�ƹ��͵� �� �Է������� ���޽���
		}
		
        if(PW.contains("/")==true)
        {
        	Toast.makeText(getApplicationContext(), "'/'�� ���� �ȵǿ�~", Toast.LENGTH_SHORT).show();
        }
		if(PW.length()>15){
			Toast.makeText(getApplicationContext(), "��й�ȣ�� �ʹ� ����", Toast.LENGTH_SHORT).show();
			//����� �涧 ���޽���
		}
		else if(PW.length()<1){
			Toast.makeText(getApplicationContext(), "��й�ȣ�� �Է��ϼ���", Toast.LENGTH_SHORT).show();
			//�ƹ��͵� �� �Է������� ���޽���
		}
		
		if(NN.contains("/")==true){
	      	Toast.makeText(getApplicationContext(), "'/'�� ���� �ȵǿ�~", Toast.LENGTH_SHORT).show();
			//����� �涧 ���޽���
		}
		
		if(NN.length()>15){
			Toast.makeText(getApplicationContext(), "�г����� �ʹ� ����", Toast.LENGTH_SHORT).show();
			//����� �涧 ���޽���
		}
		
		else if(ID.length()<=15&&ID.length()>0&&PW.length()<=15&&PW.length()>0 && ID.contains("/")==false && PW.contains("/")==false &&NN.contains("/")==false){
			thread = new Thread(){
			public void run() {
				super.run();				
					SocketService s=new SocketService();
					client = s.getsocket();			
					loginThread = new LoginThread(client, lognewhandler, ID, PW, NN);
					loginThread.start();				
			}
		};
		
		thread.start();
		}   
    }
}