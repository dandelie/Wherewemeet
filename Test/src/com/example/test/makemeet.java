/*
 * 약속 생성 화면
 */
package com.example.test;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;
import android.os.Message;

public class makemeet extends Activity {

    String ID,roomname;    
    int roomid,placecheck;
    Socket client;
	Thread thread;
	MeetThread meetThread;
    ArrayList<String> memberlist=new ArrayList<String>();
    
    private TextView mDateDisplay;
    private Button mPickDate;
    private EditText rangenum;
    
    int mYear,gu;
    int mMonth;
    int mDay;
    static final int DATE_DIALOG_ID = 0;
    
	private final MakemeetHandler mmhandler=new MakemeetHandler(this);
	  
	private static class MakemeetHandler extends Handler{
		private final WeakReference<makemeet> aActivity;
		public MakemeetHandler(makemeet activity){
			aActivity = new WeakReference<makemeet>(activity);
		}
		@Override
		public void handleMessage(Message msg){
			makemeet activity=aActivity.get();
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
		if(res==1||res==3){
			Toast.makeText(getApplicationContext(), "약속생성이 완료되었습니다.", Toast.LENGTH_SHORT).show();
			try {
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			Intent intent_01 = new Intent(getApplicationContext(),meet.class);
	        intent_01.putExtra("아이디", ID); 
	        intent_01.putExtra("방아이디", roomid);
	        intent_01.putExtra("방이름",roomname);
	        intent_01.putExtra("멤버리스트",memberlist);
	        intent_01.putExtra("placecheck",placecheck);
	        intent_01.putExtra("gu",gu);
	        startActivity(intent_01);
		}//약속생성 혹은 onCreate시 확인-정기 모임일시
		
		else if(res==4){	        
	        mDateDisplay = (TextView) findViewById(R.id.dateDisplay);
	        mPickDate = (Button) findViewById(R.id.pickDate);
	       
	        
	        // add a click listener to the button
	        mPickDate.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	                showDialog(DATE_DIALOG_ID);
	            }
	        });

	        // get the current date
	        

	        try
	        {
	        	final Calendar c = Calendar.getInstance();
		        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
		        String now=format.format(c.getTime());//현재 날짜 String
		    	Date now2=format.parse(now);//현재 날짜 date
		        mYear = c.get(Calendar.YEAR);
		        mMonth = c.get(Calendar.MONTH);
		        mDay = c.get(Calendar.DAY_OF_MONTH);
		        String startdate=Integer.toString(mYear)+"-"+Integer.toString(mMonth)+"-"+Integer.toString(mDay);
		        Date start=format.parse(startdate);//시작 날짜 date
		        
		        if(start.before(now2)==true)
		        {
		        	Toast.makeText(getApplicationContext(), "오늘 이후의 날짜로 다시 선택하세요.", Toast.LENGTH_SHORT).show();
		        }
		        else
		        {
		        	updateDisplay();
		       	}
	        }
	        catch(Exception e)
	        {
	        	
	        }
	        // display the current date (this method is below)	        
		}//onCreate시 확인-비정기 모임일시
	}//서버에서 받아온 친구 아이디 조회 결과 출력
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.makemeet);

        MainActivity ma=new MainActivity();
        Typeface mTypeface=Typeface.createFromAsset(getAssets(), "fonts/hun.ttf");
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content); 
        ma.setGlobalFont(root,mTypeface);
        
        Intent intent_01 = getIntent(); 
        
        roomname = intent_01.getStringExtra("방이름");//사용자 아이디
        ID=intent_01.getStringExtra("아이디");
        roomid=intent_01.getIntExtra("방아이디",-1);
        memberlist=intent_01.getStringArrayListExtra("멤버리스트");
        placecheck=intent_01.getIntExtra("placecheck", -1);
        gu=intent_01.getIntExtra("gu", 0);
        
        thread = new Thread(){
 			public void run() {
 				super.run();				
 					SocketService s=new SocketService();
 					client = s.getsocket();			
 					meetThread = new MeetThread(client, mmhandler,roomid,4);
 					meetThread.start();
 			}
    	 };

  		thread.start();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                return new DatePickerDialog(this,
                        mDateSetListener,
                        mYear, mMonth, mDay);
        }
        return null;
    }

    // updates the date we display in the TextView
    private void updateDisplay() {
        mDateDisplay.setText(
                new StringBuilder()
                        // Month is 0 based so add 1
                        .append(mYear).append("년 ")
                        .append(mMonth + 1).append("월 ")
                        .append(mDay).append("일")
        );
    }

    // the callback received when the user "sets" the date in the dialog
    private DatePickerDialog.OnDateSetListener mDateSetListener =
            new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker view, int year,
                                      int monthOfYear, int dayOfMonth) {
                    mYear = year;
                    mMonth = monthOfYear;
                    mDay = dayOfMonth;
                    updateDisplay();
                }
            };
            
    public void makemeetgo(View v){
    	 rangenum=(EditText)findViewById(R.id.rangenum);
    	 final String startdate=Integer.toString(mYear)+"-"+Integer.toString(mMonth+1)+"-"+Integer.toString(mDay);
    	 
    	 if(rangenum.getText().toString().length()!=0)
    	 {
    		 try
    		 {
	    		 final int range=Integer.parseInt(rangenum.getText().toString());
	    	        
	             if(range>0 && range<=14)
	             {
	    	    	 thread = new Thread(){
	    	 			public void run() {
	    	 				super.run();				
	    	 					SocketService s=new SocketService();
	    	 					client = s.getsocket();			
	    	 					meetThread = new MeetThread(client, mmhandler, roomid,startdate,range,ID,0);
	    	 					meetThread.start();
	    	 			}
	    	    	 };
	
	    	  		thread.start();
	             }
	             
	             else if(range>14)
	             {
	            	 Toast.makeText(getApplicationContext(), "1일에서 14 사이에서 입력하시오!", Toast.LENGTH_SHORT).show();
	            	 rangenum.setText("");
	             }
    		 }
    		 
    		 catch(Exception e)
    		 {
    			 Toast.makeText(getApplicationContext(), "숫자를 입력하세요.", Toast.LENGTH_SHORT).show();
            	 rangenum.setText("");
    		 }
    	 }
         
    	 else
    		 Toast.makeText(getApplicationContext(), "범위를 입력하세요.", Toast.LENGTH_SHORT).show();
        	 
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