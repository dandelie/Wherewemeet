/*
 * 전체 약정방 보기 화면
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;


public class Selectgroup extends Activity {
    ListView listView;
	private ArrayAdapter<String> myAdapter; 
	
	String ID;
    Socket client;
	Thread thread;
	RoomThread roomThread;

	int roomid,flag,placecheck,gu;
	String roomname;
	Typeface mTypeface;
	
	ArrayList<Integer> roomlist=new ArrayList<Integer>();
	ArrayList<String> roomnamelist=new ArrayList<String>();	
	
	ArrayList<String> memberlist=new ArrayList<String>();
	ArrayList<String> IDlist=new ArrayList<String>();
	ArrayList<String> NNlist=new ArrayList<String>();
	
	private final SelectgroupHandler sghandler=new SelectgroupHandler(this);
	  
	private static class SelectgroupHandler extends Handler{
		private final WeakReference<Selectgroup> aActivity;
		public SelectgroupHandler(Selectgroup activity){
			aActivity = new WeakReference<Selectgroup>(activity);
		}
		@Override
		public void handleMessage(Message msg){
			Selectgroup activity=aActivity.get();
			if(activity!=null){
				activity.handleMessage(msg);
			}
		}
	}
	
	 private void handleMessage(Message msg){

			Bundle bundle = msg.getData();			
			String m=bundle.getString("info");
			
			String[] value=m.split("[/]");
			int res=Integer.parseInt(value[0]);
			
			if(res==1)
			{
				int roomnum=Integer.parseInt(value[1]);//방 개수

				for(int i=2;i<2+roomnum;i++)
				{
					roomlist.add(Integer.parseInt(value[i]));
				}
				for(int i=2+roomnum;i<2+2*roomnum;i++)
				{
					roomnamelist.add(value[i]);
				}
				try {
					client.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				myAdapter.notifyDataSetChanged();	
			}//방리스트가 있을 때
			
			else if(res==2)
			{
				try {
					client.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				flag=Integer.parseInt(value[1]);
				placecheck=Integer.parseInt(value[2]);
				gu=Integer.parseInt(value[3]);
				int num=Integer.parseInt(value[4]);
				
				memberlist.removeAll(memberlist);
				
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
		        
			    for(int i=5;i<5+num;i++)
				{
					if(ID.equals(value[i])==false)
					{
						int n=IDlist.indexOf(value[i]);
						if(n!=-1){
							memberlist.add(NNlist.get(n));
						}
						else{
							memberlist.add(value[i]);
						}
					}//내아이디가 아닌 참가자의 리스트 추가
				}
				
				if(flag==0)
				{
					Intent intent=new Intent(getApplicationContext(), selectmeet.class);
					intent.putExtra("아이디", ID);
					intent.putExtra("방아이디",roomid);
					intent.putExtra("방이름",roomname);
					intent.putExtra("멤버리스트", memberlist);
					intent.putExtra("placecheck",placecheck);
					intent.putExtra("gu",gu);
					
					startActivity(intent);
				}//진행중인 약속이 없을 때
				
				else
				{
					Intent intent=new Intent(getApplicationContext(), meet.class);
					intent.putExtra("아이디", ID);
					intent.putExtra("방아이디",roomid);
					intent.putExtra("방이름",roomname);
					intent.putExtra("멤버리스트", memberlist);
					intent.putExtra("placecheck",placecheck);
					intent.putExtra("gu",gu);
					
					startActivity(intent);
				}//진행중인 약속이 있을 때
			}//방 id로 방정보 검색 후 방 입장		
			else if(res==3){
	        	Toast.makeText(getApplicationContext(), "참여하는 방이 없습니다.", Toast.LENGTH_SHORT).show();
	        	Intent intent=new Intent(getApplicationContext(), Mainselect.class);
				intent.putExtra("아이디", ID);
				startActivity(intent);
			}//방이 없을 때
			
			myAdapter.notifyDataSetChanged();
		}//서버에서 받아온 방목록 출력
	 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selectgroup);

        final MainActivity ma=new MainActivity();
        mTypeface=Typeface.createFromAsset(getAssets(), "fonts/hun.ttf");
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content); 
        ma.setGlobalFont(root,mTypeface);
        
        Intent intent_01 = getIntent(); 
        ID = intent_01.getStringExtra("아이디");//사용자 아이디
		
        thread = new Thread(){
			public void run() {
				super.run();				
					SocketService s=new SocketService();
					client = s.getsocket();			
					roomThread = new RoomThread(client, sghandler, ID,1);
					roomThread.start();				
			}
		};
		
		thread.start();
		
        listView = (ListView)findViewById(R.id.room_listview); //xml과의 연결고리

        myAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,roomnamelist)
        		{
        	@Override
        	public View getView(int position,View convertView,ViewGroup parent)
        	{
        		ma.setGlobalFont(parent, mTypeface);
	            return super.getView(position, convertView, parent);
        	}
        	};
        listView.setAdapter(myAdapter); //리스트 뷰를 어댑터와 합체		
        listView.setOnItemClickListener(listener);        
    }
    
    OnItemClickListener listener= new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, final int position,
				long id) {
			
			roomid=roomlist.get(position);
			roomname=roomnamelist.get(position);
	        thread = new Thread(){
				public void run() {
					super.run();				
						SocketService s=new SocketService();
						client = s.getsocket();			
						roomThread = new RoomThread(client, sghandler, roomid,2);
						roomThread.start();				
				}
			};
			thread.start();			
		}
    };
    
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
}