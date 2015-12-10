package com.example.test;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.Socket;
import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.graphics.Typeface;
import android.app.Activity;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class food_main_activity extends Activity{
	
	Spinner categorySpinner;
	String ID,roomname;
	Thread thread;
    Socket client;
    int roomid;
    PlaceThread placethread;
    LinearLayout ll;
	LinearLayout.LayoutParams Lp;
    Typeface mTypeface;
    String msg;
    
	ArrayList<CheckBox> gChecklist=new ArrayList<CheckBox>();//체크박스 리스트
	ArrayList<CheckBox> bChecklist=new ArrayList<CheckBox>();//체크박스 리스트
	ArrayList<String> memberlist=new ArrayList<String>();
	ArrayList<String> gfoodlist=new ArrayList<String>();//체크박스 리스트	
	ArrayList<String> bfoodlist=new ArrayList<String>();//체크박스 리스트	
	
	private final PlaceHandler fhandler=new PlaceHandler(this);
	  
	private static class PlaceHandler extends Handler{
		private final WeakReference<food_main_activity> aActivity;
		public PlaceHandler(food_main_activity activity){
			aActivity = new WeakReference<food_main_activity>(activity);
		}
		@Override
		public void handleMessage(Message msg){
			food_main_activity activity=aActivity.get();
			if(activity!=null){
				activity.handleMessage(msg);
			}
		}
	}
	
	private void handleMessage(Message msg)
	{
		Bundle bundle = msg.getData();
		String m=bundle.getString("info");
		String[] value=m.split("[/]");
		int res=Integer.parseInt(value[0]);
		ll=(LinearLayout) findViewById(R.id.linearLayout4);

		if(res==1)
		{			
			int num=Integer.parseInt(value[1]);//음식 갯수
			ll.removeAllViews();//체크박스 다 삭제
			TextView t=(TextView)findViewById(R.id.t3);
			t.setText(" 선호   비선호");
			
			gChecklist.removeAll(gChecklist);
			bChecklist.removeAll(bChecklist);
			
			for(int i=2;i<2+num;i++)
			{				
	        	final CheckBox goodchk=new CheckBox(this);
	        	final CheckBox badchk=new CheckBox(this);
	        	badchk.setText(value[i]);
        		badchk.setTypeface(mTypeface);
        		goodchk.setTag(value[i]);
        		
	        	goodchk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
	                public void onCheckedChanged(CompoundButton buttonView,
	                        boolean isChecked) 
	                {

                		String foodname= goodchk.getTag().toString();
                		//CheckBox c= (CheckBox)buttonView;
	                	if (isChecked) 
	                	{
	                		if(gfoodlist.size()<3 && gfoodlist.contains(foodname)==false)
	                		{
	                			gfoodlist.add(foodname);//선호 리스트에 추가
	                			int num=gChecklist.indexOf(goodchk);//체크리스트의 인덱스 반환
	                			Log.e("eee", Integer.toString(num));
	                			bChecklist.get(num).setChecked(false);;//비선호 체크박스 체크 해제
	                			if(bfoodlist.contains(foodname))
		                    	{
			                    	int index=bfoodlist.indexOf(foodname);
			                    	bfoodlist.remove(index);
			                    	showview(bfoodlist);
		                    	}//만약 비선호 리스트에 있다면 삭제
	                			showview(gfoodlist);//리스트 갱신
	                		}
	                		else if(gfoodlist.size()>=3)
	                		{
	                			Toast.makeText(getApplicationContext(), "3개까지만 고를 수 있어요!",Toast.LENGTH_SHORT).show();
	                			goodchk.setChecked(false);
	                		}
	                		else if(gfoodlist.contains(foodname))
	                		{
	                			Toast.makeText(getApplicationContext(), "이미 선택하셨어요!", Toast.LENGTH_SHORT).show();
	                			goodchk.setChecked(false);
	                		}
	                    }// 선호 체크박스 선택 
	                    else
	                    {
	                    	if(gfoodlist.contains(foodname))
	                    	{
		                    	int index=gfoodlist.indexOf(foodname);
		                    	gfoodlist.remove(index);
		                    	showview(gfoodlist);
	                    	}
	                    }// 선호 체크박스 선택해제
	                }
	            });

	        	badchk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
	                public void onCheckedChanged(CompoundButton buttonView,
	                        boolean isChecked) 
	                {

                		String foodname= badchk.getText().toString();
	                	if (isChecked) 
	                	{
	                		if(bfoodlist.size()<3 && bfoodlist.contains(foodname)==false)
	                		{
	                			bfoodlist.add(foodname);//비선호 리스트에 추가
	                			int index=bChecklist.indexOf(badchk);//비선호 체크박스의 인덱스 검색.선호체크박스가 텍스트로 검색안되어 이렇게 처리.
	                			gChecklist.get(index).setChecked(false);//선호 체크박스 불러와 체크 해제
	                			if(gfoodlist.contains(foodname))
		                    	{
			                    	int index2=gfoodlist.indexOf(foodname);
			                    	gfoodlist.remove(index2);
			                    	showview(gfoodlist);
		                    	}//만약 선호 리스트에 있다면 삭제
	                			showview(bfoodlist);//리스트 갱신
	                		}
	                		else if(bfoodlist.size()>=3)
	                		{
	                			Toast.makeText(getApplicationContext(), "3개까지만 고를 수 있어요!", Toast.LENGTH_SHORT).show();
	                			badchk.setChecked(false);
	                		}
	                		else if(bfoodlist.contains(foodname))
	                		{
	                			Toast.makeText(getApplicationContext(), "이미 선택하셨어요!", Toast.LENGTH_SHORT).show();
	                			badchk.setChecked(false);
	                		}
	                    }// 비선호 체크박스 선택 
	                    else
	                    {
	                    	if(bfoodlist.contains(foodname))
	                    	{
		                    	int index3=bfoodlist.indexOf(foodname);
		                    	bfoodlist.remove(index3);
		                    	showview(bfoodlist);
	                    	}
	                    }// 비선호 체크박스 선택해제
	                }
	            });

	        	
	        	gChecklist.add(goodchk);
	        	bChecklist.add(badchk);
	        	LinearLayout littlel= new LinearLayout(this);
	        	Lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.HORIZONTAL);
	        	littlel.addView(goodchk,Lp);
	        	littlel.addView(badchk,Lp);
	        	ll.addView(littlel);//체크박스 추가
			}
		}//음식 검색후 출력
		
		else if(res==2)
		{
			String food=value[1];
			Toast.makeText(getApplicationContext(), food+"로 결정되었습니다!", Toast.LENGTH_SHORT).show();
			
			Intent intent=new Intent(getApplicationContext(), selectmeet.class);
	        intent.putExtra("아이디", ID);
	    	intent.putExtra("방아이디", roomid);
	    	intent.putExtra("방이름", roomname);
	    	intent.putExtra("멤버리스트", memberlist);
	    	startActivity(intent);
			
		}
		
		else if(res==3)
		{
			Toast.makeText(getApplicationContext(), "다른 사람들의 선택을 기다려주세요", Toast.LENGTH_SHORT).show();
			Intent intent=new Intent(getApplicationContext(), meet.class);
	        intent.putExtra("아이디", ID);
	    	intent.putExtra("방아이디", roomid);
	    	intent.putExtra("방이름", roomname);
	    	intent.putExtra("멤버리스트", memberlist);
	    	startActivity(intent);
		}//아직 선택중일때
		else if(res==4)
		{
			Toast.makeText(getApplicationContext(), "겹치는 음식이 없네요. 다시 골라주세요~", Toast.LENGTH_SHORT).show();
		}//겹치는 음식이 없을때
		try {
			client.close();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}

	 protected void showview(final ArrayList<String> foodlist) {
		 if(foodlist==gfoodlist){
			final LinearLayout l=(LinearLayout)findViewById(R.id.linearLayout3);
			 
			Lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		    if(l.getChildCount() > 0){
		        l.removeAllViews();
		    }
		    
		    for(int i=0;i<gfoodlist.size();i++)
	    	{
	    		LinearLayout l2=new LinearLayout(this);
	    		
	    		final TextView b=new TextView(this);
	    		final TextView c=new TextView(this);
	    		b.setText(gfoodlist.get(i));
	    		b.setTypeface(mTypeface);
	    		c.setText("    ");
	    		
	    		l2.addView(b,Lp);
	    		l2.addView(c,Lp);
	    		
	    		l.addView(l2);
	    		
	    		b.setOnClickListener(new TextView.OnClickListener() {
	    			   @Override
	    			   public void onClick(View v) {
	    				   int k=gfoodlist.indexOf(b.getText().toString());
	    				   gfoodlist.remove(k);
	    				   l.removeView((View) b.getParent());
	    				   showview(gfoodlist);
	    			   }
	    		});
	    	}    		    	
		 }//선호음식 갱신
		 else{
			 final LinearLayout l=(LinearLayout)findViewById(R.id.linearLayout3_2);
			 
			 Lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		    	if(l.getChildCount() > 0){
		    	    l.removeAllViews();
		    	}
		    	for(int i=0;i<bfoodlist.size();i++)
		    	{
		    		LinearLayout l2=new LinearLayout(this);
		    		
		    		final TextView b=new TextView(this);
		    		final TextView c=new TextView(this);
		    		b.setText(bfoodlist.get(i));
		    		b.setTypeface(mTypeface);
		    		c.setText("    ");
		    		
		    		l2.addView(b,Lp);
		    		l2.addView(c,Lp);
		    		
		    		l.addView(l2,Lp);
		    		
		    		b.setOnClickListener(new TextView.OnClickListener() {
		    			   @Override
		    			   public void onClick(View v) {
		    				   int k=bfoodlist.indexOf(b.getText().toString());
		    				   bfoodlist.remove(k);
		    				   l.removeView((View) b.getParent());
		    				   showview(bfoodlist);
		    			   }
		    		});
		    	}    		    	
		    	
		 }//비선호 음식 갱신	    	
		}//내가 선택한 선호 비선호 음식 표시하는 곳.
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.food_main_activity);
       
        mTypeface=Typeface.createFromAsset(getAssets(), "fonts/hun.ttf");
        MainActivity ma=new MainActivity();
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content); 
        ma.setGlobalFont(root,mTypeface);
        
        Intent intent_01 = getIntent(); 
        roomname = intent_01.getStringExtra("방이름");
        roomid=intent_01.getIntExtra("방아이디",-1);
        ID=intent_01.getStringExtra("아이디");
        memberlist=intent_01.getStringArrayListExtra("멤버리스트");
        
        categorySpinner = (Spinner) findViewById(R.id.food);
        ArrayAdapter<CharSequence> categoryadapter = ArrayAdapter.createFromResource(this, R.array.food,
                android.R.layout.simple_spinner_item);
        categoryadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryadapter);
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
    
    public void categorygo(View v)
    {  
    	final int category=categorySpinner.getSelectedItemPosition();
    	
    	
    	if(category!=0)
    	{
	    	thread = new Thread(){
				public void run() {
					super.run();				
						SocketService s=new SocketService();
						client = s.getsocket();			
						placethread = new PlaceThread(client, fhandler,category,4);
						placethread.start();				
				}
			};
			thread.start();
    	}
    	else
    	{
			TextView t=(TextView)findViewById(R.id.t3);
			t.setText("");
    		Toast.makeText(getApplicationContext(), "카테고리를 선택하세요!", Toast.LENGTH_SHORT).show();
    	}
    }//선택한 카테고리의 데이터 가져오기
    

    public void newnext(View v){
    	if(gfoodlist.size()<1)
		{
			Toast.makeText(getApplicationContext(), "선호하는 음식을 고르세요!", Toast.LENGTH_SHORT).show();
		}
    	else{
    		msg="";   	

    		msg=Integer.toString(gfoodlist.size())+"/";
    		for(int j=0;j<gfoodlist.size();j++){
    			msg=msg+gfoodlist.get(j)+"/";
    		}		

    		msg=msg+Integer.toString(bfoodlist.size())+"/";
    		for(int j=0;j<bfoodlist.size();j++){
    			msg=msg+bfoodlist.get(j)+"/";
    		}

    		thread = new Thread(){
    			public void run() {
    				super.run();				
    				SocketService s=new SocketService();
    				client = s.getsocket();			
    				placethread = new PlaceThread(client, fhandler,roomid,ID,msg,3);
    				placethread.start();				
    			}
    		};

    		thread.start();
    	}
    }
    
}
