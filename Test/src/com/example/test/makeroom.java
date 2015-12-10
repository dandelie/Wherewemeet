/*
 * 방만들기 화면1 - 방이름과 친구 선택
 */
package com.example.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class makeroom extends Activity {

	String ID;//사용자 id
	String roomname;//방이름
	EditText name;//방이름
	LinearLayout ll;//리니어 레이아웃
	FriendThread friendThread;
	LinearLayout.LayoutParams Lp;
	
	ArrayList<String> IDNNlist=new ArrayList<String>();//ID(NN) 리스트
    ArrayList<String> IDlist=new ArrayList<String>();//ID 리스트
	ArrayList<CheckBox> Checklist=new ArrayList<CheckBox>();//체크박스 리스트
	ArrayList<String> memberlist=new ArrayList<String>();//방멤버 리스트
	
	Socket client;
	Thread thread;
	RoomThread roomThread;
	
	private final MakeroomHandler mrhandler=new MakeroomHandler(this);
	  
	private static class MakeroomHandler extends Handler{
		private final WeakReference<makeroom> aActivity;
		public MakeroomHandler(makeroom activity){
			aActivity = new WeakReference<makeroom>(activity);
		}
		@Override
		public void handleMessage(Message msg){
			makeroom activity=aActivity.get();
			if(activity!=null){
				activity.handleMessage(msg);
			}
		}
	}
	
    private void handleMessage(Message msg){
		Bundle bundle = msg.getData();	
		int result=bundle.getInt("result");
		
		if(result==4){
			ArrayList<String> fnlist=bundle.getStringArrayList("없는친구리스트");//닉네임 저장
			int num;
			for(int i=0;i<fnlist.size();i++){
				num=IDlist.indexOf(fnlist.get(i));
				IDlist.remove(num);
				IDNNlist.remove(num);	
	        	ll.removeView(Checklist.get(num));
	        	Checklist.remove(num);//체크박스 삭제
			}
			try
			 {
				 File myfile=getDir("myfile", Context.MODE_PRIVATE);
				 String path= myfile.getAbsolutePath();
				 File file=new File(path+"/"+ID+"friend.txt");
				 
				 FileOutputStream fos=new FileOutputStream(file,false);//덮어 쓰기			
				 for(int i=0;i<IDNNlist.size();i++){
					 String info=IDNNlist.get(i)+"\n";
					 fos.write(info.getBytes());
				 }
				 fos.close();
			 }	// 친구 정보 쓰기
			 catch(IOException e)
			 {
				 e.printStackTrace();
			 }
			try {
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Toast.makeText(getApplicationContext(), "친구리스트 갱신 완료", Toast.LENGTH_SHORT).show();
		}//아이디 갱신완료
		
		else if(result==5){
			try {
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Toast.makeText(getApplicationContext(), "친구리스트 갱신 완료", Toast.LENGTH_SHORT).show();
		}//갱신될 아이디가 없을 시
	}//핸들러 작업
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.makeroom);

        MainActivity ma=new MainActivity();
        Typeface mTypeface=Typeface.createFromAsset(getAssets(), "fonts/hun.ttf");
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content); 
        ma.setGlobalFont(root,mTypeface);
        
        Intent intent_01 = getIntent();
        ID = intent_01.getStringExtra("아이디");
        
        name = (EditText) findViewById(R.id.roomname);  
        ll=(LinearLayout) findViewById(R.id.linearLayout);
        
        String f="/"+ID+"friend.txt";
        File myfile=getDir("myfile", Context.MODE_PRIVATE);
        String path= myfile.getAbsolutePath();
        File file=new File(path+f);//ID로 텍스트 파일 만들기
        
        try {
			 FileInputStream fis = new FileInputStream(file);
			 BufferedReader bufferReader = new BufferedReader(new InputStreamReader(fis));
			 String str;
			 while ((str = bufferReader.readLine()) != null) 
				 {
					 IDNNlist.add(str);
				 }//리스트에 추가
			 bufferReader.close();
			 fis.close();
			 }
			 catch(Exception e) {
				 e.printStackTrace();
		}
        
        Collections.sort(IDNNlist);//오름차순 정렬
       
        for(int i=0;i<IDNNlist.size();i++)
        {
        	String[] value=IDNNlist.get(i).split("[(]");
        	String[] value2=value[1].split("[)]");
        	IDlist.add(value2[0]);//IDNN이 정렬된 순서대로 IDlist를 정렬시킴
        	
        	CheckBox chk=new CheckBox(this);

            chk.setTypeface(mTypeface);
        	chk.setText(IDNNlist.get(i));
        	Checklist.add(chk); 
        	Lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        	ll.addView(chk,Lp);//체크박스 추가
        }        
    }
    public void home(View v)
    {
        Intent intent=new Intent(getApplicationContext(), Mainselect.class);
        intent.putExtra("아이디", ID);
        startActivity(intent);
    }
    
    public void setting(View v)
    {
    	Intent intent=new Intent(getApplicationContext(), Settingmain.class);
        intent.putExtra("아이디", ID);
        startActivity(intent);
    }
    public void makeroomnext(View v){
    	roomname=name.getText().toString();//이름 가져오기
    	
    	if(roomname.length()>15){
    		Toast.makeText(getApplicationContext(), "방 이름이 너무 길어요!", Toast.LENGTH_SHORT).show();
    		name.setText("");
    	}
    	else if(roomname.contains("/"))
    	{
    		Toast.makeText(getApplicationContext(), "/는 들어가면 안되요~", Toast.LENGTH_SHORT).show();
    		name.setText("");
    	}
    	else if(roomname.length()<1){
    		Toast.makeText(getApplicationContext(), "방 이름을 입력하세요!", Toast.LENGTH_SHORT).show();
    	}
    	
    	else{
    		memberlist.add(ID);
    		for(int i=0;i<IDNNlist.size();i++){
            	if(Checklist.get(i).isChecked()){
            		memberlist.add(IDlist.get(i).toString());
            	}
            }
    		if(memberlist.size()<1){
    			Toast.makeText(getApplicationContext(), "방 멤버가 없어요..", Toast.LENGTH_SHORT).show();
    		}//방 멤버를 선택 안했을시
    		else{	
    			Intent intent_01 = new Intent(getApplicationContext(),newgroup1.class);
    	        intent_01.putExtra("아이디", ID);
    	        intent_01.putExtra("방이름", roomname);
    			intent_01.putExtra("멤버리스트", memberlist);
    	        startActivity(intent_01);
    		}
    	}
    }//방생성 버튼
    
    public void makeroomrefresh(View v){
    	thread = new Thread(){
			 public void run() {
				 super.run();
				 SocketService s=new SocketService();
				 client = s.getsocket();
				 friendThread = new FriendThread(client, mrhandler, IDlist);
				 friendThread.start();
			}
		 };			
		 thread.start();
    }//새로고침 버튼
    
}