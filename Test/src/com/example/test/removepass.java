/*
 * �н����� ����
 */
package com.example.test;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.Socket;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;


public class removepass extends Activity {
	
	String id,pw,newpw,newpw2;
	Thread thread;
	Socket client;
	LoginThread loginThread;
	
	private final rmvpassHandler rmvpasshandler=new rmvpassHandler(this);

	private static class rmvpassHandler extends Handler{
		private final WeakReference<removepass> aActivity;
		public rmvpassHandler(removepass activity){
			aActivity = new WeakReference<removepass>(activity);
		}
		@Override
		public void handleMessage(Message msg){
			removepass activity=aActivity.get();
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
			Toast.makeText(getApplicationContext(), "��й�ȣ ������ �Ϸ� �Ǿ����ϴ�.", Toast.LENGTH_SHORT).show();
	        Intent intent=new Intent(getApplicationContext(), removelog.class);
	        intent.putExtra("���̵�", id);
	        startActivity(intent);//ù ȭ������
		}//������� �� �������� �������� ����
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
		}//ȸ������ ����
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.removepass);

        MainActivity ma=new MainActivity();
        Typeface mTypeface=Typeface.createFromAsset(getAssets(), "fonts/hun.ttf");
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content); 
        ma.setGlobalFont(root,mTypeface);
        Intent intent_01 = getIntent();
        id=intent_01.getStringExtra("���̵�");
    }

    public void removepassword(View v){
        EditText text_pw = (EditText) findViewById(R.id.passwordEntry);
        EditText text_npw = (EditText) findViewById(R.id.newpasswordEntry);
        EditText text_npw2 = (EditText) findViewById(R.id.newpasswordEntry2);
        pw = text_pw.getText().toString();
        newpw=text_npw.getText().toString();
        newpw2=text_npw2.getText().toString();
        
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
        
        if(newpw.contains("/")==true)
			Toast.makeText(getApplicationContext(), "/�� ���� �ȵǿ�.", Toast.LENGTH_SHORT).show();
		if(newpw.length()>15){
			Toast.makeText(getApplicationContext(), "��й�ȣ�� �ʹ� ����", Toast.LENGTH_SHORT).show();
			text_npw.setText("");
			text_npw2.setText("");
			//����� �涧 ���޽���
		}
		else if(newpw.length()<1){
			Toast.makeText(getApplicationContext(), "��й�ȣ�� �Է��ϼ���", Toast.LENGTH_SHORT).show();
			//�ƹ��͵� �� �Է������� ���޽���
		}
		else if(newpw.equals(newpw2)==false){
			Toast.makeText(getApplicationContext(), "�� ��й�ȣ Ȯ�� �����Դϴ�",Toast.LENGTH_SHORT).show();
			text_npw.setText("");
			text_npw2.setText("");
			//�� ��й�ȣ Ȯ���� �ȵǾ�����
		}
		else if(pw.length()<=15&&pw.length()>0 && pw.contains("/")==false && newpw.contains("/")==false){
			thread = new Thread(){
				public void run() {
					super.run();
					SocketService s=new SocketService();
					client = s.getsocket();			
					loginThread = new LoginThread(client, rmvpasshandler, id, pw, newpw,3);
					loginThread.start();
				}
			};		
			thread.start();		
		}	
    }

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