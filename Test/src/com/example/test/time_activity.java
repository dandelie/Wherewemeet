/*
 * 시간 선택하기
 */
package com.example.test;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

public class time_activity extends Activity{
	
	int roomid,dateid;
	String ID,roomname,datetext;
	ArrayList<String> memberlist=new ArrayList<String>();
    String filename;
    TextView tv;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.time_activity);

        MainActivity ma=new MainActivity();
        Typeface mTypeface=Typeface.createFromAsset(getAssets(), "fonts/hun.ttf");
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content); 
        ma.setGlobalFont(root,mTypeface);
        
        Intent intent_01=getIntent();
        roomid=intent_01.getIntExtra("방아이디",-1);
        ID=intent_01.getStringExtra("아이디");
        roomname=intent_01.getStringExtra("방이름");
        memberlist=intent_01.getStringArrayListExtra("멤버리스트");
        dateid=intent_01.getIntExtra("버튼숫자",-1);
        datetext=intent_01.getStringExtra("날짜");
        
        filename=ID+roomid+dateid;
        
        tv=(TextView) findViewById(R.id.timeact); 
        tv.setText(datetext);

        SharedPreferences date_time = getSharedPreferences(filename, Activity.MODE_PRIVATE);
        
        CheckBox morning=(CheckBox)findViewById(R.id.timeact1);
        CheckBox afternoon=(CheckBox)findViewById(R.id.timeact2);
        CheckBox dinner=(CheckBox)findViewById(R.id.timeact3);

        final CheckBox[] datecheckbox=new CheckBox[16];
        datecheckbox[0]=(CheckBox)findViewById(R.id.datecheckBox1);
        datecheckbox[1]=(CheckBox)findViewById(R.id.datecheckBox2);
        datecheckbox[2]=(CheckBox)findViewById(R.id.datecheckBox3);
        datecheckbox[3]=(CheckBox)findViewById(R.id.datecheckBox4);
        datecheckbox[4]=(CheckBox)findViewById(R.id.datecheckBox5);
        datecheckbox[5]=(CheckBox)findViewById(R.id.datecheckBox6);
        datecheckbox[6]=(CheckBox)findViewById(R.id.datecheckBox7);
        datecheckbox[7]=(CheckBox)findViewById(R.id.datecheckBox8);
        datecheckbox[8]=(CheckBox)findViewById(R.id.datecheckBox9);
        datecheckbox[9]=(CheckBox)findViewById(R.id.datecheckBox10);
        datecheckbox[10]=(CheckBox)findViewById(R.id.datecheckBox11);
        datecheckbox[11]=(CheckBox)findViewById(R.id.datecheckBox12);
        datecheckbox[12]=(CheckBox)findViewById(R.id.datecheckBox13);
        datecheckbox[13]=(CheckBox)findViewById(R.id.datecheckBox14);
        datecheckbox[14]=(CheckBox)findViewById(R.id.datecheckBox15);
		datecheckbox[15]=(CheckBox)findViewById(R.id.datecheckBox16);
		
		Boolean[] chk=new Boolean[16];
		
		for(int i=0;i<16;i++)
			chk[i] = date_time.getBoolean("check"+Integer.toString(i+1), false);
		
		for(int i=0;i<16;i++)
			datecheckbox[i].setChecked(chk[i]);
		
        morning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
            	if(v instanceof CheckBox)
            	{
            		CheckBox c=(CheckBox)v;
            		if(c.isChecked()==true)
            		{
            			for(int i=0;i<4;i++)
            				datecheckbox[i].setChecked(true);
            		}
            		else
            		{
            			for(int i=0;i<4;i++)
            				datecheckbox[i].setChecked(false);
            		}
            	}
            }
        });
        
        afternoon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
            	if(v instanceof CheckBox)
            	{
            		CheckBox c=(CheckBox)v;
            		if(c.isChecked()==true)
            		{
            			for(int i=4;i<10;i++)
            				datecheckbox[i].setChecked(true);
            		}
            		else
            		{
            			for(int i=4;i<10;i++)
            				datecheckbox[i].setChecked(false);
            		}
            	}
            }
        });
        
        dinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
            	if(v instanceof CheckBox)
            	{
            		CheckBox c=(CheckBox)v;
            		if(c.isChecked()==true)
            		{
            			for(int i=10;i<16;i++)
            				datecheckbox[i].setChecked(true);
            		}
            		else
            		{
            			for(int i=10;i<16;i++)
            				datecheckbox[i].setChecked(false);
            		}
            	}
            }
        });


    }
    
    public void datecheckdone(View v){
    	 SharedPreferences date_time = getSharedPreferences(filename, Activity.MODE_PRIVATE);
         // UI 
         SharedPreferences.Editor editor = date_time.edit();
         // Editor

         final CheckBox[] datecheckbox=new CheckBox[16];
         datecheckbox[0]=(CheckBox)findViewById(R.id.datecheckBox1);
         datecheckbox[1]=(CheckBox)findViewById(R.id.datecheckBox2);
         datecheckbox[2]=(CheckBox)findViewById(R.id.datecheckBox3);
         datecheckbox[3]=(CheckBox)findViewById(R.id.datecheckBox4);
         datecheckbox[4]=(CheckBox)findViewById(R.id.datecheckBox5);
         datecheckbox[5]=(CheckBox)findViewById(R.id.datecheckBox6);
         datecheckbox[6]=(CheckBox)findViewById(R.id.datecheckBox7);
         datecheckbox[7]=(CheckBox)findViewById(R.id.datecheckBox8);
         datecheckbox[8]=(CheckBox)findViewById(R.id.datecheckBox9);
         datecheckbox[9]=(CheckBox)findViewById(R.id.datecheckBox10);
         datecheckbox[10]=(CheckBox)findViewById(R.id.datecheckBox11);
         datecheckbox[11]=(CheckBox)findViewById(R.id.datecheckBox12);
         datecheckbox[12]=(CheckBox)findViewById(R.id.datecheckBox13);
         datecheckbox[13]=(CheckBox)findViewById(R.id.datecheckBox14);
         datecheckbox[14]=(CheckBox)findViewById(R.id.datecheckBox15);
 		datecheckbox[15]=(CheckBox)findViewById(R.id.datecheckBox16);
 		
         for(int i=0;i<16;i++)
        	 editor.putBoolean("check"+Integer.toString(i+1), datecheckbox[i].isChecked());

         editor.commit();
         
         Intent intent_01 = new Intent(getApplicationContext(),Date_main_activity.class);
         intent_01.putExtra("아이디", ID);
         intent_01.putExtra("방아이디",roomid);
         intent_01.putExtra("방이름",roomname);
         intent_01.putExtra("멤버리스트",memberlist);	
         startActivity(intent_01);
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
