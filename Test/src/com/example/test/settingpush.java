/*
 * 푸시알림 설정
 */
package com.example.test;

import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;

public class settingpush extends Activity {
	RadioButton r1;
    RadioButton r2;
    RadioButton r3;
    RadioButton r4;
    RadioButton r5;
    
    RadioGroup rg;
    String id;
    Switch s;
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	 super.onCreate(savedInstanceState);
         setContentView(R.layout.settingpush);
         r1=(RadioButton)findViewById(R.id.radio1);
         r2=(RadioButton)findViewById(R.id.radio2);
         r3=(RadioButton)findViewById(R.id.radio3);
         r4=(RadioButton)findViewById(R.id.radio4);
         r5=(RadioButton)findViewById(R.id.radio5);
         rg=(RadioGroup)findViewById(R.id.radiogroup);
         s=(Switch)findViewById(R.id.switch1);
         MainActivity ma=new MainActivity();
         Typeface mTypeface=Typeface.createFromAsset(getAssets(), "fonts/hun.ttf");
         ViewGroup root = (ViewGroup) findViewById(android.R.id.content); 
         ma.setGlobalFont(root,mTypeface);
         
         Intent intent_01 = getIntent();
         id=intent_01.getStringExtra("아이디");
         
         s.setOnCheckedChangeListener(new OnCheckedChangeListener() {
             public void onCheckedChanged(CompoundButton buttonView,
                     boolean isChecked) 
             {
            	 if(isChecked)
            	 {
            		 r1.setClickable(true);
            		 r2.setClickable(true);
            		 r3.setClickable(true);
            		 r4.setClickable(true);
            		 r5.setClickable(true);
            	 }
            	 else
            	 {
            		 r1.setClickable(false);
            		 r2.setClickable(false);
            		 r3.setClickable(false);
            		 r4.setClickable(false);
            		 r5.setClickable(false);
            	 }
             }

         });
    }
    
    public void complete(View v)
	{
    	GregorianCalendar mCalendar;
    	TimerTask mTask;
	    Timer mTimer;
	
	    mTask = new TimerTask() {
	        @Override
	        public void run() {
	            Intent intent = new Intent(getApplicationContext()
	                    , Settingmain.class);
	            startActivity(intent);
	        }
	    };
	     
	    mTimer = new Timer();         
	    mTimer.schedule(mTask, 1000);
	
	    AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
	    mCalendar = new GregorianCalendar();
	    
    	if(s.isChecked()==true)
    	{
    		if(r1.isChecked()==true)
    		{
    	        mCalendar.set (2015, 8, 28, 9, 0,0);
    			alarm.setRepeating(AlarmManager.RTC_WAKEUP, mCalendar.getTimeInMillis(),1000*60*60*3, getPendingIntent());
    	    }
    	   
    		
    		else if(r2.isChecked()==true)
    		{
    			mCalendar.set (2015, 8, 28, 9, 0,0);
     			alarm.setRepeating(AlarmManager.RTC_WAKEUP, mCalendar.getTimeInMillis(),1000*60*60*6, getPendingIntent());
    		}
    		
    		else if(r3.isChecked()==true)
    		{
    			mCalendar.set (2015, 8, 28, 12, 0,0);
     			alarm.setRepeating(AlarmManager.RTC_WAKEUP, mCalendar.getTimeInMillis(),1000*60*60*6, getPendingIntent());
    		}
    		
    		else if(r4.isChecked()==true)
    		{
    			mCalendar.set (2015, 8, 28, 9, 0,0);
     			alarm.setRepeating(AlarmManager.RTC_WAKEUP, mCalendar.getTimeInMillis(),1000*60*60*12, getPendingIntent());
    		}
    		
    		else if(r5.isChecked()==true)
    		{
    			mCalendar.set (2015, 8, 28, 12, 0,0);
     			alarm.setRepeating(AlarmManager.RTC_WAKEUP, mCalendar.getTimeInMillis(),1000*60*60*12, getPendingIntent());
    		}

    	}
    }
    
    private PendingIntent getPendingIntent()
    {
    	Intent intent = new Intent(getBaseContext(),AlarmReceive.class);

		PendingIntent pender = PendingIntent.getBroadcast(settingpush.this, 0, intent, 0);

		return pender;
    }
    
    public void home(View v)
    {
        Intent intent=new Intent(getApplicationContext(), Mainselect.class);
        intent.putExtra("아이디", id);
        startActivity(intent);
    }
    
    public void setting(View v)
    {
    	Intent intent=new Intent(getApplicationContext(), Settingmain.class);
        intent.putExtra("아이디", id);
        startActivity(intent);
    }
}