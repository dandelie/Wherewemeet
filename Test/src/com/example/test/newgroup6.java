/*
 * �游��� ȭ��4-2 - ������ ��� ����(��) �Է�
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
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class newgroup6 extends Activity implements
        AdapterView.OnItemSelectedListener {
    /** Called when the activity is first created. */

	String ID,roomname;
	ArrayList<String> IDlist=new ArrayList<String>();
	ArrayList<String> NNlist=new ArrayList<String>();
	ArrayList<String> memberlist=new ArrayList<String>();//���� ����Ʈ
	ArrayList<String> memberlist2=new ArrayList<String>();//���� ����Ʈ
	int daycheck,placecheck;
	String day;
	int starttime,dayreturnvalue;
	
	Socket client;
	Thread thread;
	RoomThread roomThread;
	
    String[] items = { "����(����, ����, ����, ���)", "������(���빮, �߶�, ����, ����)", "����(����, ����)", "����(����, ����)","������(����, ����, ��õ)","����(����, ��õ, ������, ����)","������(����, ����, ���빮)","����(����, �߱�, ���)" };

   // TextView selection;

    Spinner spin;
    int selectgu=1,gu;

    private final new6roomHandler n6_handler=new new6roomHandler(this);
	  
	private static class new6roomHandler extends Handler{
		private final WeakReference<newgroup6> aActivity;
		public new6roomHandler(newgroup6 activity){
			aActivity = new WeakReference<newgroup6>(activity);
		}
		@Override
		public void handleMessage(Message msg){
			newgroup6 activity=aActivity.get();
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
			
	        Log.v("������ó��?",Integer.toString(placecheck));
			Intent intent_01 = new Intent(getApplicationContext(),selectmeet.class);
			intent_01.putExtra("���̵�", ID);
			intent_01.putExtra("����̵�", roomid);
			intent_01.putExtra("���̸�", roomname);
			intent_01.putExtra("�������Ʈ", memberlist2);
			intent_01.putExtra("placecheck",placecheck);
			intent_01.putExtra("gu",gu);
			startActivity(intent_01);

		}//�� ���� ������
	 }
    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newgroup6);

        MainActivity ma=new MainActivity();
        Typeface mTypeface=Typeface.createFromAsset(getAssets(), "fonts/hun.ttf");
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content); 
        ma.setGlobalFont(root,mTypeface);
        //selection = (TextView) findViewById(R.id.new6sp);
        spin = (Spinner) findViewById(R.id.spinner);
        spin.setOnItemSelectedListener(this);

        ArrayAdapter<String> aa= new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, items);
        aa.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        spin.setAdapter(aa);
        
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
    }
    
    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
                               long arg3) {
        // spinner 
       // selection.setText(items[arg2]);
        selectgu=arg2+1;//�� ���� �־���
        gu=selectgu;
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        //selection.setText(null);
    }
    
    public void newfind(View v){
    	thread = new Thread(){
			public void run() {
				super.run();
				SocketService s=new SocketService();
				client = s.getsocket();	
				if(daycheck==0){
					roomThread = new RoomThread(client, n6_handler, roomname, memberlist,daycheck,placecheck,selectgu,4);
				}//���������
				else{
					roomThread = new RoomThread(client, n6_handler, roomname, memberlist,daycheck,placecheck,day,starttime,dayreturnvalue,selectgu,5);
				}//�������
				roomThread.start();
			}
		};
		thread.start();
    }

    public void home(View v)
    {
        Intent intent=new Intent(getApplicationContext(), Mainselect.class);
        intent.putExtra("���̵�", ID);
        startActivity(intent);
    }
    
    public void setting(View v)
    {
    	Intent intent=new Intent(getApplicationContext(), Settingmain.class);
        intent.putExtra("���̵�", ID);
        startActivity(intent);
    }
}