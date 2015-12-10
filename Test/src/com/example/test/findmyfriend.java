/*
 * 친구 관리 화면
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
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.AlertDialog;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

public class findmyfriend extends Activity {
	
	ListView myListView;
	private ArrayAdapter<String> myAdapter; 
	
    ArrayList<String> IDNNlist=new ArrayList<String>();//리스트에 출력할 배열 ID(NN)형태
    ArrayList<String> IDlist=new ArrayList<String>();//ID만 있는 배열
    
	String fID, ID, fNN;
	EditText text_id;
	
	Socket client;
	Thread thread;
	FriendThread friendThread;
	
	private final FindmyfriendHandler fmfhandler=new FindmyfriendHandler(this);
	  
	private static class FindmyfriendHandler extends Handler{
		private final WeakReference<findmyfriend> aActivity;
		public FindmyfriendHandler(findmyfriend activity){
			aActivity = new WeakReference<findmyfriend>(activity);
		}
		@Override
		public void handleMessage(Message msg){
			findmyfriend activity=aActivity.get();
			if(activity!=null){
				activity.handleMessage(msg);
			}
		}
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.findmyfriend);

        final MainActivity ma=new MainActivity();
        final Typeface mTypeface=Typeface.createFromAsset(getAssets(), "fonts/hun.ttf");
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content); 
        ma.setGlobalFont(root,mTypeface);
        
        Intent intent_01 = getIntent();
        
        ID = intent_01.getStringExtra("아이디");
        
        text_id = (EditText) findViewById(R.id.friend);         
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
					 IDNNlist.add(str);
				 }//리스트에 추가
			 bufferReader.close();
			 fis.close();
			 }
			 catch(Exception e) {
				 e.printStackTrace();
			}
        Collections.sort(IDNNlist);//오름차순 정렬
        
        for(int i=0;i<IDNNlist.size();i++){
        	String[] value=IDNNlist.get(i).split("[(]");
        	String[] value2=value[1].split("[)]");
        	IDlist.add(value2[0]);//IDNN이 정렬된 순서대로 IDlist를 정렬시킴
        }
        
        myListView = (ListView)findViewById(R.id.friendlist); //xml과의 연결고리
        
        myAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, IDNNlist)
        {
        	@Override
        	public View getView(int position,View convertView,ViewGroup parent)
        	{
        		ma.setGlobalFont(parent, mTypeface);
	            return super.getView(position, convertView, parent);
        	}
        	};

        myListView.setAdapter(myAdapter); //리스트 뷰를 어댑터와 합체
         
        registerForContextMenu(myListView);
    }
 
    public void onCreateContextMenu(ContextMenu menu,View v, ContextMenuInfo menuInfo)
    {
    	super.onCreateContextMenu(menu,v, menuInfo);
    	menu.add(0,1,0,"친구 이름 변경");
    	menu.add(0,2,0,"친구 삭제");
    	
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
    public boolean onContextItemSelected(final MenuItem item)
    {
    	switch(item.getItemId())
    	{
    	case 1: 
    		final LinearLayout linear=(LinearLayout)View.inflate(findmyfriend.this,R.layout.custom, null);
    		
    		
    		Builder builder = new AlertDialog.Builder(findmyfriend.this);
    		builder.setTitle("닉네임 변경.");
    		builder.setView(linear);
    		builder.setNegativeButton("확인", new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int which) {
    	    		AdapterView.AdapterContextMenuInfo menuInfo;
    		    	int index;
    		    	
					EditText new_nn=(EditText)linear.findViewById(R.id.new_nickname);
					String nn=new_nn.getText().toString();

		    		menuInfo=(AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
		    		index=menuInfo.position;
		    		
					 String str=IDNNlist.get(index);
					 String[] value=str.split("[(]");
					 String[] value2=value[1].split("[)]");
					 IDNNlist.set(index, nn+"("+value2[0]+")");
					 
					 Collections.sort(IDNNlist);//오름차순 정렬
					 
					 IDlist.removeAll(IDlist);//아이디 리스트 없애고
					 
					 for(int i=0;i<IDNNlist.size();i++){
				        	String[] value3=IDNNlist.get(i).split("[(]");
				        	String[] value4=value3[1].split("[)]");
				        	IDlist.add(value4[0]);//IDNN이 정렬된 순서대로 IDlist를 정렬시킴
					 }
					 myAdapter.notifyDataSetChanged();
					 Toast.makeText(getApplicationContext(), "수정되었습니다", Toast.LENGTH_SHORT).show();
				}
			});
    		builder.setPositiveButton("취소", new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int which) {
    				dialog.cancel();
    			}
    		});
    		builder.show();
    		
    		return true;
    		
    	case 2: 
    		AdapterView.AdapterContextMenuInfo menuInfo;
	    	int index;
	    	
    		menuInfo=(AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
    		index=menuInfo.position;
    		IDNNlist.remove(index);
			IDlist.remove(index);
			myAdapter.notifyDataSetChanged();
			Toast.makeText(getApplicationContext(), "삭제되었습니다", Toast.LENGTH_SHORT).show();
    		
		default:
    		return super.onContextItemSelected(item);
    	}
    		
    }
    
    public void onStop(){
    	super.onStop();
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
    }
    
    private void handleMessage(Message msg){
		Bundle bundle = msg.getData();
		int result=bundle.getInt("result");
		if(result==1){
			Toast.makeText(getApplicationContext(), "조회된 아이디가 없습니다.", Toast.LENGTH_SHORT).show();
		}//조회된 친구 아이디가 없을경우
		
		else if(result==2)
		{
			fNN=bundle.getString("Nick");//닉네임 저장
			
			String info=fNN+"("+fID+")";
			IDNNlist.add(info);//리스트에 저장
	        Collections.sort(IDNNlist);//오름차순 정렬
	        IDlist.removeAll(IDlist);//아이디 리스트 없애고
			for(int i=0;i<IDNNlist.size();i++){
		        	String[] value3=IDNNlist.get(i).split("[(]");
		        	String[] value4=value3[1].split("[)]");
		        	IDlist.add(value4[0]);//IDNN이 정렬된 순서대로 IDlist를 정렬시킴
		     }

			myAdapter.notifyDataSetChanged();
       
			Toast.makeText(getApplicationContext(), fNN+" 친구 추가 완료", Toast.LENGTH_SHORT).show();
		}//친구 아이디와 닉네임이 있을 경우	
		
		else if(result==3)
		{
			fNN=fID;
			String info=fID+"("+fID+")";
			IDNNlist.add(info);//리스트에 저장
			IDlist.add(fID);//리스트에 저장
	        
			Collections.sort(IDNNlist);//오름차순 정렬
			IDlist.removeAll(IDlist);//아이디 리스트 없애고
			for(int i=0;i<IDNNlist.size();i++){
	        	String[] value=IDNNlist.get(i).split("[(]");
	        	String[] value2=value[1].split("[)]");
	        	IDlist.add(value2[0]);//IDNN이 정렬된 순서대로 IDlist를 정렬시킴
	        }
			myAdapter.notifyDataSetChanged();//리스트 뷰 갱신
			Toast.makeText(getApplicationContext(), fID+" 친구 추가 완료", Toast.LENGTH_SHORT).show();
		}//친구 아이디는 있으나 닉네임 없을 경우
		
		else if(result==4){
			ArrayList<String> fnlist=bundle.getStringArrayList("없는친구리스트");//닉네임 저장
			int num;
			for(int i=0;i<fnlist.size();i++){
				num=IDlist.indexOf(fnlist.get(i));
				IDlist.remove(num);
				IDNNlist.remove(num);				
			}
			myAdapter.notifyDataSetChanged();//리스트 뷰 갱신
			Toast.makeText(getApplicationContext(), "친구리스트 갱신 완료", Toast.LENGTH_SHORT).show();
		}//아이디 갱신완료
		else if(result==5){
			Toast.makeText(getApplicationContext(), "친구리스트 갱신 완료", Toast.LENGTH_SHORT).show();
		}//갱신될 아이디가 없을 시
		try {
			client.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		text_id.setText("");//입력 화면 초기화
	}//서버에서 받아온 친구 아이디 조회 결과 출력

    public void friendsave(View v){
    	fID = text_id.getText().toString();
    	
    	if(fID.length()<1){
			Toast.makeText(getApplicationContext(), "아이디를 입력하세요", Toast.LENGTH_SHORT).show();			
		}//아무것도 안 입력했을때 경고메시지
    	else if(fID.length()>15){
    		Toast.makeText(getApplicationContext(), "아이디가 너무 길어요", Toast.LENGTH_SHORT).show();
    	}//아이디가 너무 길때 경고메시지
    	else if(fID.equals(ID)){
    		Toast.makeText(getApplicationContext(), "내 아이디 입니다!", Toast.LENGTH_SHORT).show();
    	}//내 아이디를 입력했을 시
    	else if(IDlist.contains(fID)){
    		Toast.makeText(getApplicationContext(), "이미 추가된 아이디 입니다!", Toast.LENGTH_SHORT).show();
    	}//이미 친구 추가된 아이디를 입력했을 시
    	
    	else if(fID.contains("/")==true)
    	{
    		Toast.makeText(getApplicationContext(), "/는 들어가면 안되요.", Toast.LENGTH_SHORT).show();
    	}
    	else{
    		thread = new Thread(){
    			public void run() {
    				super.run();				
    					SocketService s=new SocketService();
    					client = s.getsocket();			
    					friendThread = new FriendThread(client, fmfhandler, fID);
    					friendThread.start();				
    			}
    		};
    		
    		thread.start();
    		}   
    	}//친구 저장
    
    
    public void refresh(View v){
    	thread = new Thread(){
			 public void run() {
				 super.run();
				 SocketService s=new SocketService();
				 client = s.getsocket();
				 friendThread = new FriendThread(client, fmfhandler, IDlist);
				 friendThread.start();
			}
		 };			
		 thread.start();
    }//친구 목록 새로 고침
    
    public void tomain(View v){
    	Intent intent_01 = new Intent(getApplicationContext(),Mainselect.class);
        intent_01.putExtra("아이디", ID);
        startActivity(intent_01);
    }//메인화면으로
}