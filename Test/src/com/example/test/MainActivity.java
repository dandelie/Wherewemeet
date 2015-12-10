/*
 * 완전 초기화면
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
import android.widget.TextView;

public class MainActivity extends Activity {

    public static final int REQUEST_CODE_ANOTHER =1001;
    Typeface mTypeface;
    private GregorianCalendar mCalendar;
    
    public void setGlobalFont(ViewGroup root,Typeface mTypeface)
    { 
	
	    for(int i=0; i< root.getChildCount(); i++)
	    { 
	    	View child = root.getChildAt(i); 
		    if(child instanceof TextView)((TextView)child).setTypeface(mTypeface); 
		    else if(child instanceof ViewGroup) setGlobalFont((ViewGroup)child,mTypeface); 
	
	    } 
	
	 } 


    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	//getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xff359AC4));

    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.activity_main);
    	mTypeface= Typeface.createFromAsset(getAssets(), "fonts/hun.ttf");
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content); 
        setGlobalFont(root,mTypeface);
        TimerTask mTask;
        Timer mTimer;

        mTask = new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext()
                        , autologin.class);
                startActivity(intent);
            }
        };
         
        mTimer = new Timer();         
        mTimer.schedule(mTask, 1000);

         
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        mCalendar = new GregorianCalendar();
        mCalendar.set (2015, 8, 24, 12, 14,0);
		alarm.setRepeating(AlarmManager.RTC_WAKEUP, mCalendar.getTimeInMillis(),1000*60*60*24, getPendingIntent());
    }
    
    private PendingIntent getPendingIntent()
    {
    	Intent intent = new Intent(getBaseContext(),AlarmReceive.class);

		PendingIntent pender = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);

		return pender;
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent Data) {
        super.onActivityResult(requestCode, resultCode, Data);

        if (requestCode == REQUEST_CODE_ANOTHER){
            //액티비티에서 보내온 데이터에 대한 처리
        }
    }

}