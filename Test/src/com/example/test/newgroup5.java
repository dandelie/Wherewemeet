/*
 * �游��� ȭ��4-1 - ������ ��� �Է�
 */
package com.example.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.Socket;
import java.util.ArrayList;

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

public class newgroup5 extends Activity {
	String ID,roomname;
	ArrayList<String> IDlist=new ArrayList<String>();
	ArrayList<String> NNlist=new ArrayList<String>();
	ArrayList<String> memberlist=new ArrayList<String>();//���� ����Ʈ
	ArrayList<String> memberlist2=new ArrayList<String>();//���� ����Ʈ
	int daycheck,placecheck;
	EditText place;
	String day;
	int starttime,dayreturnvalue;
	String p;
	
	Socket client;
	Thread thread;
	RoomThread roomThread;
	
	private final new5roomHandler n5_handler=new new5roomHandler(this);
	  
	private static class new5roomHandler extends Handler{
		private final WeakReference<newgroup5> aActivity;
		public new5roomHandler(newgroup5 activity){
			aActivity = new WeakReference<newgroup5>(activity);
		}
		@Override
		public void handleMessage(Message msg){
			newgroup5 activity=aActivity.get();
			if(activity!=null){
				activity.handleMessage(msg);
			}
		}
	}
	
	private void handleMessage(Message msg){
		Bundle bundle = msg.getData();	
		int result=bundle.getInt("result");

		if(result==1){
			int roomid=bundle.getInt("roomid");
			Toast.makeText(getApplicationContext(), "������� �Ϸ�Ǿ����ϴ�.", Toast.LENGTH_SHORT).show();
			try {
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}				

			String f="/"+ID+"friend.txt";
	        File myfile=getDir("myfile", Context.MODE_PRIVATE);
	        String path= myfile.getAbsolutePath();
	        File file=new File(path+f);//ID�� �ؽ�Ʈ ���� �����

	        try {
				 FileInputStream fis = new FileInputStream(file);
				 BufferedReader bufferReader = new BufferedReader(new InputStreamReader(fis,"UTF-8"));
				 String str;
				 while ((str = bufferReader.readLine()) != null) 
				 {
						 String[] v=str.split("[(]");
						 String[] v2=v[1].split("[)]");							 
						 IDlist.add(v2[0]);
						 NNlist.add(v[0]);
				 }//����Ʈ�� �߰�
				 bufferReader.close();
				 fis.close();
	        }
	        catch(Exception e) {
	        	e.printStackTrace();
			}
	        for(int i=0;i<memberlist.size();i++){
	        	String tempp=memberlist.get(i);
	        	if(ID.equals(tempp)==false){
	        		int n=IDlist.indexOf(tempp);
					if(n!=-1){
						memberlist2.add(NNlist.get(n));
					}//ģ����� �Ǿ����� ��
					else{
						memberlist2.add(tempp);
					}//�ȵǾ����� ��
				}
	        }
			
			Intent intent_01 = new Intent(getApplicationContext(),selectmeet.class);
			intent_01.putExtra("���̵�", ID);
			intent_01.putExtra("����̵�", roomid);
			intent_01.putExtra("���̸�", roomname);
			intent_01.putExtra("�������Ʈ", memberlist2);
			intent_01.putExtra("placecheck",placecheck);
			startActivity(intent_01);
		}//�� ���� ������
	 }
	 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newgroup5);

        MainActivity ma=new MainActivity();
        Typeface mTypeface=Typeface.createFromAsset(getAssets(), "fonts/hun.ttf");
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content); 
        ma.setGlobalFont(root,mTypeface);
        
        Intent intent_01 = getIntent();
        ID = intent_01.getStringExtra("���̵�");
        roomname = intent_01.getStringExtra("���̸�");
        memberlist=intent_01.getStringArrayListExtra("�������Ʈ");
        daycheck=intent_01.getIntExtra("daycheck", -1);
        placecheck=intent_01.getIntExtra("placecheck", -1);
        if(daycheck==1){
        	day=intent_01.getStringExtra("day");
        	starttime=intent_01.getIntExtra("starttime", -1);
        	dayreturnvalue=intent_01.getIntExtra("dayreturnvalue", -1);
        }//��������̶��        
        
        place=(EditText)findViewById(R.id.new5aEntry);
    }

    public void newnext(View v){
    	p=place.getText().toString();
    	if(p.length()<1){
    		Toast.makeText(getApplicationContext(), "��Ҹ� �Է��ϼ���!", Toast.LENGTH_SHORT).show();
    	}
    	else if(p.length()>30){
    		Toast.makeText(getApplicationContext(), "��Ұ� �ʹ� ����!", Toast.LENGTH_SHORT).show();
    		place.setText("");
    	}
    	else if(p.contains("/")){
    		Toast.makeText(getApplicationContext(), "/�� ���� �ȵǿ�~", Toast.LENGTH_SHORT).show();
    		place.setText("");
    	}
    	else{
    		thread = new Thread(){
    			public void run() {
    				super.run();
    				SocketService s=new SocketService();
    				client = s.getsocket();	
    				if(daycheck==0){
    					roomThread = new RoomThread(client, n5_handler, roomname, memberlist,daycheck,placecheck,p,0);
    				}//���������
    				else{
    					roomThread = new RoomThread(client, n5_handler, roomname, memberlist,daycheck,placecheck,day,starttime,dayreturnvalue,p,3);
    				}//�������
    				roomThread.start();
    			}
    		};
    		thread.start();
        }
    }

}