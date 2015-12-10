/*
 * 진행중인 약속이 있을 때의 약정방 화면
 */
package com.example.test;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.Socket;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;
import android.os.Handler;
import android.os.Message;

import com.example.test.SocketService;

public class meet extends Activity {
	ArrayList<LinearLayout> list=new ArrayList<LinearLayout>();
	ArrayList<String> memberlist=new ArrayList<String>();
	ArrayList<String> meetlist=new ArrayList<String>();
	ArrayList<String> meetprintlist=new ArrayList<String>();
	ArrayList<Integer> meetidlist=new ArrayList<Integer>();
	
	String ID,roomname,check;
	Thread thread;
    Socket client;
	MeetThread meetThread;
	int flag,roomid,choice,num,gu,placecheck;
	ListView myListView;
	TextView tv;
	LinearLayout.LayoutParams Lp;
	LinearLayout l1;
	Typeface mTypeface;
	
	private ArrayAdapter<String> myAdapter; 
	
	private final MeetHandler mhandler=new MeetHandler(this);
	  
	private static class MeetHandler extends Handler{
		private final WeakReference<meet> aActivity;
		public MeetHandler(meet activity){
			aActivity = new WeakReference<meet>(activity);
		}
		@Override
		public void handleMessage(Message msg){
			meet activity=aActivity.get();
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
			choice=Integer.parseInt(value[1]);
			
//			Button b1=(Button)findViewById(R.id.button1);
	//		Button b2=(Button)findViewById(R.id.button2);
		//	Button b3=(Button)findViewById(R.id.button3);
			
			if(choice==1)
			{
				Toast.makeText(getApplicationContext(), "일정을 선택할 차례에요.", Toast.LENGTH_SHORT).show();
				//b1.setBackgroundResource(R.drawable.button_round1);
				num=Integer.parseInt(value[2]);
				
				TextView v=new TextView(this);
				v.setText("1step. 일정 : 진행중");
				v.setTypeface(mTypeface);
				v.setTextSize(17);
				v.setTextColor(Color.parseColor("#800080"));
				l1.addView(v,Lp);
				
				for(int i=0;i<num;i++)
				{
					meetidlist.add(Integer.parseInt(value[3+i*6]));
					
					String meetmsg="";
					String meetprintmsg=value[4+i*6]+" "+value[5+i*6]+"시  "+value[7+i*6]+"에서";
					
					for(int j=0;j<5;j++)
					{
						if(value[4+i*6+j].length()>0)
						{
							meetmsg=meetmsg+value[4+i*6+j]+"    ";
						}
					}
					meetlist.add(meetmsg);
					meetprintlist.add(meetprintmsg);
				}
			}
			
			else if(choice==2){
				Toast.makeText(getApplicationContext(), "장소를 선택할 차례에요.", Toast.LENGTH_SHORT).show();
				//b2.setBackgroundResource(R.drawable.button_round1);
				num=Integer.parseInt(value[4]);
				
				TextView v=new TextView(this);
				v.setText("1step. 일정 : "+value[2]+"   "+value[3]+"시");
				v.setTypeface(mTypeface);
				l1.addView(v,Lp);
				
				TextView v1=new TextView(this);
				v1.setText("2step. 장소 : 진행중");
				v1.setTypeface(mTypeface);
				v1.setTextColor(Color.parseColor("#800080"));
				l1.addView(v1,Lp);
				
				for(int i=0;i<num;i++)
				{
					meetidlist.add(Integer.parseInt(value[5+i*6]));
					
					String meetmsg="";
					String meetprintmsg=value[6+i*6]+" "+value[7+i*6]+"시  "+value[8+i*6]+"에서";
					
					for(int j=0;j<5;j++)
					{
						if(value[6+i*6+j].length()>0)
						{
							meetmsg=meetmsg+value[6+i*6+j]+"    ";
						}
					}
					meetlist.add(meetmsg);
					meetprintlist.add(meetprintmsg);
				}
			}
			else if(choice==3){
				Toast.makeText(getApplicationContext(), "음식를 선택할 차례에요.", Toast.LENGTH_SHORT).show();
				//b3.setBackgroundResource(R.drawable.button_round1);
				num=Integer.parseInt(value[5]);
				
				TextView v=new TextView(this);
				v.setText("1step. 일정 : "+value[2]+"   "+value[3]+"시");
				v.setTypeface(mTypeface);
				l1.addView(v,Lp);
				
				TextView v1=new TextView(this);
				v1.setText("2step. 장소 : "+value[4]);
				v1.setTypeface(mTypeface);
				l1.addView(v1,Lp);
				
				TextView v2=new TextView(this);
				v2.setText("3step. 음식 : 진행중");
				v2.setTypeface(mTypeface);
				v2.setTextColor(Color.parseColor("#800080"));
				l1.addView(v2,Lp);
				
				
				for(int i=0;i<num;i++)
				{
					meetidlist.add(Integer.parseInt(value[6+i*6]));
					
					String meetmsg="";
					String meetprintmsg=value[7+i*6]+" "+value[8+i*6]+"시  "+value[9+i*6]+"에서";
					
					for(int j=0;j<5;j++)
					{
						if(value[7+i*6+j].length()>0)
						{
							meetmsg=meetmsg+value[7+i*6+j]+"    ";
						}
					}
					meetlist.add(meetmsg);
					meetprintlist.add(meetprintmsg);
				}
			}
			myAdapter.notifyDataSetChanged();
			try {
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		else if(res==2)
		{
			try {
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		else if(res==3)
		{
			Intent intent01 = new Intent(getApplicationContext(),selectmeet.class);
			intent01.putExtra("아이디", ID);
			intent01.putExtra("방아이디",roomid);
			intent01.putExtra("방이름",roomname);
			intent01.putExtra("멤버리스트",memberlist);
			intent01.putExtra("placecheck",placecheck);
			intent01.putExtra("gu",gu);
			startActivity(intent01);
			
			//Toast.makeText(getApplicationContext(), "약속을 너무 오래 정하셨군요. 기한 날짜가 지나 약속이 취소되었어요", Toast.LENGTH_SHORT).show();
			try {
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}//지난약속리스트  출력
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meet);

        final MainActivity ma=new MainActivity();
        mTypeface=Typeface.createFromAsset(getAssets(), "fonts/hun.ttf");
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content); 
        ma.setGlobalFont(root,mTypeface);
        
        Intent intent_01 = getIntent(); 
        roomname = intent_01.getStringExtra("방이름");
        roomid=intent_01.getIntExtra("방아이디",-1);
        ID=intent_01.getStringExtra("아이디");
        memberlist=intent_01.getStringArrayListExtra("멤버리스트");
        placecheck=intent_01.getIntExtra("placecheck",-1);
        gu=intent_01.getIntExtra("gu",0);
        
        TextView name=(TextView)findViewById(R.id.selectmeet);
        l1=(LinearLayout)findViewById(R.id.editText2);
        
        name.setText("< "+roomname+" >");
        
        thread = new Thread(){
			public void run() {
				super.run();				
					SocketService s=new SocketService();
					client = s.getsocket();			
					meetThread = new MeetThread(client, mhandler,roomid,1);
					meetThread.start();				
			}
		};
		
		thread.start();
		
		 LinearLayout l=(LinearLayout)findViewById(R.id.roomfriendname);
	    	Lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);

	        for(int i=0;i<memberlist.size();i++)
	        {
	        	TextView v=new TextView(this);
	        	v.setText(memberlist.get(i));
	        	v.setTypeface(mTypeface);
	        	Log.v("태그",memberlist.get(i));
	        	l.addView(v,Lp);//버튼 추가
	        }
		
    	 myListView = (ListView)findViewById(R.id.editText); //xml과의 연결고리
         
         myAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, meetprintlist)
        		 {
         	@Override
         	public View getView(int position,View convertView,ViewGroup parent)
         	{
         		ma.setGlobalFont(parent, mTypeface);
 	            return super.getView(position, convertView, parent);
         	}
         	};
         myListView.setAdapter(myAdapter); //리스트 뷰를 어댑터와 합체
         myListView.setOnItemClickListener(new OnItemClickListener(){
         	public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
         		final LinearLayout linear=(LinearLayout)View.inflate(meet.this,R.layout.custom2, null);
         		
         		Builder builder = new AlertDialog.Builder(meet.this);
         		builder.setTitle("약속 상세 정보");
         		builder.setView(linear);
         		Button b=(Button)findViewById(R.id.button);
         		b.setOnClickListener(new OnClickListener(){
         		   
         		   public void onClick(View v)
         		   {
         			  final LinearLayout linear=(LinearLayout)View.inflate(meet.this,R.layout.activity_web, null);
         		   }
         			
         		});
         		builder.setNegativeButton("확인", new DialogInterface.OnClickListener() {
         			public void onClick(DialogInterface dialog, int which) {
         	    		AdapterView.AdapterContextMenuInfo menuInfo;
         		    	
         			}
         		});
         		builder.show();
         	}
         });
         
    }
    
    public void selectresult(View v)
    {
    	if(choice==1)
    	{
    		Intent intent_01 = new Intent(getApplicationContext(),Date_main_activity.class);
    		intent_01.putExtra("아이디", ID);
    		intent_01.putExtra("방아이디",roomid);
    		intent_01.putExtra("방이름",roomname);
    		intent_01.putExtra("멤버리스트",memberlist);
    		startActivity(intent_01);
    	}//일정 선택 중일 시		
    	
    	else if(choice==2)
    	{
    		if(placecheck==0){
    			 thread = new Thread(){
    					public void run() {
    						super.run();				
    							SocketService s=new SocketService();
    							client = s.getsocket();			
    							meetThread = new MeetThread(client, mhandler,roomid,5);
    							meetThread.start();				
    					}
    				};
    				thread.start();
    		}
    		else if(placecheck==1)
    		{
    			Intent intent01 = new Intent(getApplicationContext(),place1_activity.class);
    			intent01.putExtra("아이디", ID);
    			intent01.putExtra("방아이디",roomid);
    			intent01.putExtra("방이름",roomname);
    			intent01.putExtra("멤버리스트",memberlist);
    			intent01.putExtra("gu",gu);
    			intent01.putExtra("placecheck",placecheck);
    			startActivity(intent01);
    		}
    		else if(placecheck==3)
    		{
    			Intent intent01 = new Intent(getApplicationContext(),place2_activity.class);
    			intent01.putExtra("아이디", ID);
    			intent01.putExtra("방아이디",roomid);
    			intent01.putExtra("방이름",roomname);
    			intent01.putExtra("멤버리스트",memberlist);
    			intent01.putExtra("placecheck",placecheck);
    			intent01.putExtra("gu",gu);
    			startActivity(intent01);
    		}
    	}
    	else if(choice==3)
    	{
			Intent intent01 = new Intent(getApplicationContext(),food_main_activity.class);
			intent01.putExtra("아이디", ID);
			intent01.putExtra("방아이디",roomid);
			intent01.putExtra("방이름",roomname);
			intent01.putExtra("멤버리스트",memberlist);
			startActivity(intent01);
    	}//음식 선택 중일시
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
    
}