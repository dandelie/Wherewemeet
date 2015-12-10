/*
 * 일정 선택 화면 
 */
package com.example.test;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Date_main_activity extends Activity {

	String ID,roomname;
	int roomid;
	int range;
	int num=0;
	String startdate;
	ArrayList<Button> buttonlist=new ArrayList<Button>();
	ArrayList<String> memberlist=new ArrayList<String>();
	ArrayList<String> dateselectlist=new ArrayList<String>();//서버에 최종으로 보낼 리스트
	LinearLayout.LayoutParams Lp;
	LinearLayout ll;//리니어 레이아웃
	Socket client;
	Thread thread;
	MeetThread meetThread;
	String msg;
	Typeface mTypeface;
	
	private final dateHandler dhandler=new dateHandler(this);
	  
	private static class dateHandler extends Handler{
		private final WeakReference<Date_main_activity> aActivity;
		public dateHandler(Date_main_activity activity){
			aActivity = new WeakReference<Date_main_activity>(activity);
		}
		@Override
		public void handleMessage(Message msg){
			Date_main_activity activity=aActivity.get();
			if(activity!=null){
				activity.handleMessage(msg);
			}
		}
	}
	
    private void handleMessage(Message msg){
		Bundle bundle = msg.getData();
		String info=bundle.getString("info");		
		String[] value=info.split("[/]");
		
		int res=Integer.parseInt(value[0]);		
		
		if(res==1){
			startdate=value[1];
			range=Integer.parseInt(value[2]);
			try
	        {
	        	Calendar c=Calendar.getInstance();
	        	SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
	        	String now=format.format(c.getTime());//현재 날짜 String
	        	Date now2=format.parse(now);//현재 날짜 date
	        	Date start=format.parse(startdate);//시작 날짜 date
	        
	        	c.setTime(start);
	        	
	        	ll=(LinearLayout) findViewById(R.id.linearLayout);
	        	for(int i=0;i<range;i++)
	        	{
	        		String d=format.format(c.getTime());
	        		Date d2=format.parse(d);//선택 SCOPE 날짜 date
	        		c.add(c.DATE,+1);//하나 증가
	        		
	        		Button b=new Button(this);
	        		

	                mTypeface=Typeface.createFromAsset(getAssets(), "fonts/hun.ttf");
	        		b.setTypeface(mTypeface);
	                b.setBackgroundResource(R.drawable.buttonw);
	            	b.setText(d);
	            	if(d2.getTime() >= now2.getTime()){//지금 날짜거나 그것보다 나중일 때만 클릭리스너 활성화-> 즉 유효한 날짜만 선택 가능하게 함
	            		b.setOnClickListener(new OnClickListener() {
	            			@Override
	            			public void onClick(View v) {
	            				Button clickedButton = (Button) v;
	            				Button tempButton;
	            				String datetext=clickedButton.getText().toString();
	            				int k;
	            				for (k=0;k<buttonlist.size();k++) {
	            					tempButton=buttonlist.get(k);
	            					if (tempButton == clickedButton) {
	            						break;
	            					}
	            				}
	            				
	            				Log.v("k",Integer.toString(k));
	            				Intent intent_01 = new Intent(getApplicationContext(),time_activity.class);
	            				intent_01.putExtra("아이디", ID);
	            				intent_01.putExtra("방아이디",roomid);
	            				intent_01.putExtra("방이름",roomname);
	            				intent_01.putExtra("멤버리스트",memberlist);
	            				intent_01.putExtra("날짜",datetext);
	            				intent_01.putExtra("버튼숫자", k);
	            				startActivity(intent_01);  
	            			}
	            		});
	            		num++;
	            		Lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
	            		ll.addView(b,Lp);//버튼 추가
	            	}
	            	
	            	buttonlist.add(b); 
	        	}
	        	c.add(c.DATE,+1);
	        }
	        
	        catch(Exception e)
	        {
	        	e.printStackTrace();
	        }
			try 
			{
				client.close();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
		else if(res==2){
			Toast.makeText(getApplicationContext(), "저장 완료! 다른 친구들의 선택을 기다려주세요~", Toast.LENGTH_SHORT).show();
			try 
			{
				client.close();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
			Intent intent_01 = new Intent(getApplicationContext(),meet.class);
	         intent_01.putExtra("아이디", ID);
	         intent_01.putExtra("방아이디",roomid);
	         intent_01.putExtra("방이름",roomname);
	         intent_01.putExtra("멤버리스트",memberlist);	
	         startActivity(intent_01); 
		}//아직 다른애들이 선택 안했을때
		else if(res==3)
		{
			Toast.makeText(getApplicationContext(), "친구와 맞는 시간이 없어요... 다시 선택해 주세요.", Toast.LENGTH_SHORT).show();
			try 
			{
				client.close();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}//날짜 없음
		
		else if(res==4)
		{			
			try 
			{
				client.close();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
			Toast.makeText(getApplicationContext(), "저장 완료! 만나는 날짜를 보여드릴게요~", Toast.LENGTH_SHORT).show();
			Toast.makeText(getApplicationContext(), "날짜: "+value[1]+"\n만나는 시간: "+value[2]+"\n끝나는 시간: "+value[3], Toast.LENGTH_SHORT).show();
			
		    for(int i=0;i<range;i++){
	    		String filename= ID+roomid+i;//파일 이름
	    		SharedPreferences prefs = getSharedPreferences(filename, MODE_PRIVATE);
	    	    Editor ed = prefs.edit();
	    	    ed.clear();//모두 지워버리기
	    	    ed.commit();
	    	}//sharedpreference 지워버리기
		    
		    Intent intent_01 = new Intent(getApplicationContext(),meet.class);
	         intent_01.putExtra("아이디", ID);
	         intent_01.putExtra("방아이디",roomid);
	         intent_01.putExtra("방이름",roomname);
	         intent_01.putExtra("멤버리스트",memberlist);	
	         startActivity(intent_01); 
		    
		}//날짜가 정해짐
		/*
		else if(res==5){
			Toast.makeText(getApplicationContext(), "저장 완료! 결과가 많아 투표를 진행합니다.", Toast.LENGTH_SHORT).show();

		}//투표해야할시*/
	}
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.date_main_activity);

        MainActivity ma=new MainActivity();
        mTypeface=Typeface.createFromAsset(getAssets(), "fonts/hun.ttf");
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content); 
        ma.setGlobalFont(root,mTypeface);
        
        Intent intent_01 = getIntent(); 
        roomid=intent_01.getIntExtra("방아이디",-1);
        ID=intent_01.getStringExtra("아이디");
        roomname=intent_01.getStringExtra("방이름");
        memberlist=intent_01.getStringArrayListExtra("멤버리스트");
        
        thread = new Thread(){
 			public void run() {
 				super.run();				
 					SocketService s=new SocketService();
 					client = s.getsocket();			
 					meetThread = new MeetThread(client, dhandler, roomid,2);
 					meetThread.start();
 			}
    	 };

  		thread.start();
    }
    
    public void datesave(View v){
    	msg="";
    	SharedPreferences date_time;
    	
    	for(int i=0;i<range-num;i++)
    	{
    		for(int j=1;j<17;j++){
					msg=msg+0;
			}
    		msg=msg+"/";
    	}
    	
    	for(int i=range-num;i<range;i++){
    		String filename= ID+roomid+i;//파일 이름
    		try{
    			date_time = getSharedPreferences(filename, Activity.MODE_PRIVATE); 
    			for(int j=1;j<17;j++){
    				if(date_time.getBoolean("check"+j, false)==true){
    					msg=msg+1;
    				}//체크가 되어있으면 1
    				else{
    					msg=msg+0;
    				}
    			}
    			msg=msg+"/";
    			
    			Log.v("msg",msg);
    		}
    		catch(Exception e){
    			 Toast.makeText(getApplicationContext(), "파일이상발생", Toast.LENGTH_SHORT).show();
    		}
    	}//msg 생성
    	msg=startdate+"/"+range+"/"+msg;
    	
    	 thread = new Thread(){
 			public void run() {
 				super.run();				
 					SocketService s=new SocketService();
 					client = s.getsocket();			
 					meetThread = new MeetThread(client, dhandler,roomid,ID,msg,3);
 					meetThread.start();				
 			}
 		};
 		
 		thread.start();
    	
    	
    }//선택완료 버튼
    
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
