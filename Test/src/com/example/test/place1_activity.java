
package com.example.test;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.Socket;
import java.util.ArrayList;

public class place1_activity extends Activity {

	String ID,roomname;
	Thread thread;
    Socket client;
	int roomid,gu,theme,placecheck;
	String[] items = { "강북(도봉, 강북, 성북, 노원)", "동서울(동대문, 중랑, 성동, 광진)", "동남(강동, 송파)", "강남(서초, 강남)","남서울(동작, 관악, 금천)","서남(강서, 양천, 영등포, 구로)","서서울(은평, 마포, 서대문)","도심(종로, 중구, 용산)" };
	PlaceThread placethread,placethread2;
	LinearLayout.LayoutParams Lp;
	Typeface mTypeface;
	
	ArrayList<CheckBox> Checklist=new ArrayList<CheckBox>();//체크박스 리스트
	ArrayList<String> memberlist=new ArrayList<String>();
	ArrayList<String> Placelist=new ArrayList<String>();//체크박스 리스트
	
	LinearLayout ll,l;
	Spinner spinner;
	
	private final PlaceHandler p1handler=new PlaceHandler(this);
	  
	private static class PlaceHandler extends Handler{
		private final WeakReference<place1_activity> aActivity;
		public PlaceHandler(place1_activity activity){
			aActivity = new WeakReference<place1_activity>(activity);
		}
		@Override
		public void handleMessage(Message msg){
			place1_activity activity=aActivity.get();
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
		ll=(LinearLayout) findViewById(R.id.linearLayout2);
        
		if(res==1)
		{
			int num=Integer.parseInt(value[1]);
			ll.removeAllViews();
			for(int i=2;i<2+num;i++)
			{
				Checklist.removeAll(Checklist);
	        	final CheckBox chk=new CheckBox(this);
	        	chk.setText(value[i]);
        		chk.setTypeface(mTypeface);
        		
	        	chk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
	                public void onCheckedChanged(CompoundButton buttonView,
	                        boolean isChecked) 
	                {
	                	if (isChecked) 
	                	{
	                		if(Placelist.size()<3 && Placelist.contains(chk.getText().toString())==false)
	                		{
	                			Placelist.add(chk.getText().toString());
	                			showview(Placelist);
	                		}
	                		else if(Placelist.size()>=3)
	                		{
	                			Toast.makeText(getApplicationContext(), "3개까지만 고를 수 있어요!", Toast.LENGTH_SHORT).show();
	                			chk.setChecked(false);
	                		}
	                		else if(Placelist.contains(chk.getText().toString())==true)
	                		{
	                			Toast.makeText(getApplicationContext(), "이미 선택하셨어요!", Toast.LENGTH_SHORT).show();
	                			chk.setChecked(false);
	                		}
	                    } 
	                    else
	                    {
	                    	if(Placelist.contains(chk.getText().toString()))
	                    	{
		                    	int index=Placelist.indexOf(chk.getText().toString());
		                    	Placelist.remove(index);
		                    	showview(Placelist);
	                    	}
	                    }
	                }
	            });

	        	Checklist.add(chk);
	        	
	        	Lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
	        	ll.addView(chk,Lp);//체크박스 추가
			}
		}
		else if(res==2)
		{
			String place=value[1];
			Toast.makeText(getApplicationContext(), place, Toast.LENGTH_SHORT).show();
	        Intent intent=new Intent(getApplicationContext(), meet.class);
	        intent.putExtra("아이디", ID);
	    	intent.putExtra("방아이디", roomid);
	    	intent.putExtra("방이름", roomname);
	    	intent.putExtra("멤버리스트", memberlist);
	    	intent.putExtra("gu", gu);
	    	intent.putExtra("placecheck", placecheck);
	    	startActivity(intent);
		}
		
		else if(res==3)
		{
			Toast.makeText(getApplicationContext(), "겹치는 결과가 없어요. 다시 골라주세요", Toast.LENGTH_SHORT).show();
		}
		
		else
		{
			Toast.makeText(getApplicationContext(), "다른 사람의 선택을 기다려주세요", Toast.LENGTH_SHORT).show();
			Intent intent=new Intent(getApplicationContext(), meet.class);
	        intent.putExtra("아이디", ID);
	    	intent.putExtra("방아이디", roomid);
	    	intent.putExtra("방이름", roomname);
	    	intent.putExtra("멤버리스트", memberlist);
	    	intent.putExtra("gu", gu);
	    	intent.putExtra("placecheck", placecheck);
	    	startActivity(intent);
		
		}
		
		try {
			client.close();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}

	
    protected void showview(final ArrayList<String> placelist) 
    {
    	final LinearLayout l=(LinearLayout)findViewById(R.id.linearf);
    	Lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
    	if(l.getChildCount() > 0)
    	    l.removeAllViews();
    	
    	for(int i=0;i<placelist.size();i++)
    	{
    		LinearLayout l2=new LinearLayout(this);
    		
    		final TextView b=new TextView(this);
    		final TextView c=new TextView(this);
    		b.setText(placelist.get(i));
    		b.setTypeface(mTypeface);
    		c.setText("    ");
    		l2.addView(b,Lp);
    		l2.addView(c,Lp);
    		
    		l.addView(l2,Lp);
    		
    		b.setOnClickListener(new TextView.OnClickListener() {
    			   @Override
    			   public void onClick(View v) {
    				   int k=placelist.indexOf(b.getText().toString());
    				   placelist.remove(k);
    				   l.removeView((View) b.getParent());
    			   }
    		});
    	}    	
	}


	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.place1_activity);

        MainActivity ma=new MainActivity();
        mTypeface=Typeface.createFromAsset(getAssets(), "fonts/hun.ttf");
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content); 
        ma.setGlobalFont(root,mTypeface);
        
        Intent intent_01 = getIntent(); 
        roomname = intent_01.getStringExtra("방이름");
        roomid=intent_01.getIntExtra("방아이디",-1);
        ID=intent_01.getStringExtra("아이디");
        memberlist=intent_01.getStringArrayListExtra("멤버리스트");
        gu=intent_01.getIntExtra("gu",-1);
        placecheck=intent_01.getIntExtra("placecheck",-1);
        
        String msg=items[gu-1];
        TextView gu_info=(TextView)findViewById(R.id.gu_info);
        gu_info.setText(msg+" 에서 만납니다.");
        
        spinner = (Spinner) findViewById(R.id.place);
        ArrayAdapter<CharSequence> Dayadapter = ArrayAdapter.createFromResource(this, R.array.place2,
                android.R.layout.simple_spinner_item);
        Dayadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(Dayadapter);
        
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
    public void placego(View v)
    {  
    	final int theme=spinner.getSelectedItemPosition()+1;
    	thread = new Thread(){
			public void run() {
				super.run();				
					SocketService s=new SocketService();
					client = s.getsocket();			
					placethread = new PlaceThread(client, p1handler,gu,theme,0);
					placethread.start();				
			}
		};
		
		thread.start();
    }
    
    public void newnext(View v){
    	
    	thread = new Thread(){
			public void run() {
				super.run();	
				String selectedplace="";
		    	for(int i=0;i<Placelist.size();i++)
		    	{
		    		selectedplace=selectedplace+Placelist.get(i)+"/";
		    	}
		    	
				SocketService s=new SocketService();
				client = s.getsocket();			
				placethread2 = new PlaceThread(client, p1handler,roomid,ID,Placelist.size(),selectedplace,1);
				placethread2.start();				
			}
		};
		
		thread.start();
    }
    
}