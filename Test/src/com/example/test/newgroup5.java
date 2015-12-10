/*
 * 방만들기 화면4-1 - 모임의 장소 입력
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
	ArrayList<String> memberlist=new ArrayList<String>();//방멤버 리스트
	ArrayList<String> memberlist2=new ArrayList<String>();//방멤버 리스트
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
			Toast.makeText(getApplicationContext(), "방생성이 완료되었습니다.", Toast.LENGTH_SHORT).show();
			try {
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}				

			String f="/"+ID+"friend.txt";
	        File myfile=getDir("myfile", Context.MODE_PRIVATE);
	        String path= myfile.getAbsolutePath();
	        File file=new File(path+f);//ID로 텍스트 파일 만들기

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
				 }//리스트에 추가
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
					}//친구등록 되어있을 때
					else{
						memberlist2.add(tempp);
					}//안되어있을 때
				}
	        }
			
			Intent intent_01 = new Intent(getApplicationContext(),selectmeet.class);
			intent_01.putExtra("아이디", ID);
			intent_01.putExtra("방아이디", roomid);
			intent_01.putExtra("방이름", roomname);
			intent_01.putExtra("멤버리스트", memberlist2);
			intent_01.putExtra("placecheck",placecheck);
			startActivity(intent_01);
		}//방 생성 성공시
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
        ID = intent_01.getStringExtra("아이디");
        roomname = intent_01.getStringExtra("방이름");
        memberlist=intent_01.getStringArrayListExtra("멤버리스트");
        daycheck=intent_01.getIntExtra("daycheck", -1);
        placecheck=intent_01.getIntExtra("placecheck", -1);
        if(daycheck==1){
        	day=intent_01.getStringExtra("day");
        	starttime=intent_01.getIntExtra("starttime", -1);
        	dayreturnvalue=intent_01.getIntExtra("dayreturnvalue", -1);
        }//정기모임이라면        
        
        place=(EditText)findViewById(R.id.new5aEntry);
    }

    public void newnext(View v){
    	p=place.getText().toString();
    	if(p.length()<1){
    		Toast.makeText(getApplicationContext(), "장소를 입력하세요!", Toast.LENGTH_SHORT).show();
    	}
    	else if(p.length()>30){
    		Toast.makeText(getApplicationContext(), "장소가 너무 길어요!", Toast.LENGTH_SHORT).show();
    		place.setText("");
    	}
    	else if(p.contains("/")){
    		Toast.makeText(getApplicationContext(), "/는 들어가면 안되요~", Toast.LENGTH_SHORT).show();
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
    				}//비정기모임
    				else{
    					roomThread = new RoomThread(client, n5_handler, roomname, memberlist,daycheck,placecheck,day,starttime,dayreturnvalue,p,3);
    				}//정기모임
    				roomThread.start();
    			}
    		};
    		thread.start();
        }
    }

}