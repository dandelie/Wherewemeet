/*
 * 방만들기 화면2-2 - 정기 모임일 때 시간 선택
 */
package com.example.test;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Calendar;

public class newgroup2 extends Activity {

	String ID,roomname;
	ArrayList<String> memberlist=new ArrayList<String>();//방멤버 리스트
	int daycheck;
	String day;
	int starttime;
	int dayreturnvalue;
	
    private TextView mDateDisplay;
    private TextView mTimeDisplay;
    private Button mPickDate;
    private Button mPickTime;
    private int mYear;
    private int mMonth;
    private int mDay;
    private int mHour;
    private int mMinute;


    static final int DATE_DIALOG_ID = 1;
    static final int TIME_DIALOG_ID = 0;

    RadioButton rb1,rb2,rb3,rb4;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newgroup2);

        MainActivity ma=new MainActivity();
        Typeface mTypeface=Typeface.createFromAsset(getAssets(), "fonts/hun.ttf");
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content); 
        ma.setGlobalFont(root,mTypeface);
        
        Intent intent_01 = getIntent();
        ID = intent_01.getStringExtra("아이디");
        roomname = intent_01.getStringExtra("방이름");
        memberlist=intent_01.getStringArrayListExtra("멤버리스트");
        daycheck=intent_01.getIntExtra("daycheck", -1);
        
        // capture our View elements
        mDateDisplay = (TextView) findViewById(R.id.dateDisplay);
        mTimeDisplay = (TextView) findViewById(R.id.new2cEntry);
        mPickDate = (Button) findViewById(R.id.pickDate);
        mPickTime = (Button) findViewById(R.id.makemeetgo);

        rb1=(RadioButton)findViewById(R.id.radio01);
        rb2=(RadioButton)findViewById(R.id.radio02);
        rb3=(RadioButton)findViewById(R.id.radio03);
        rb4=(RadioButton)findViewById(R.id.radio04);
        rb3.setChecked(true);//기본선택
        
        // add a click listener to the button
        mPickDate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(DATE_DIALOG_ID);
            }
        });

        mPickTime.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(TIME_DIALOG_ID);
            }
        });
        // get the current date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get (Calendar.MINUTE);


        // display the current date (this method is below)
        updateDisplay();
        updateDisplay2();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                    return new DatePickerDialog(this,
                          mDateSetListener,
                           mYear, mMonth, mDay);

            case TIME_DIALOG_ID:
                return new TimePickerDialog(this,
                        mTimeSetListener, mHour, mMinute, false);
        }
        return null;

    }



    // updates the date we display in the TextView
    private void updateDisplay() {
        mDateDisplay.setText(
                new StringBuilder()
                        // Month is 0 based so add 1
                        .append(mYear).append("년 ")
                        .append(mMonth+1).append("월 ")
                        .append(mDay).append("일")
        );        
        String newm=pad(mMonth+1);
        String newd=pad(mDay);
        day=mYear+"-"+newm+"-"+newd;//2015-06-02 형식으로 바꿔서 넣기
    }

    private void updateDisplay2() {
        mTimeDisplay.setText(
                new StringBuilder()
                        .append(mHour).append("시 ")//.append(mMinute).append("분")
        );        
        starttime=mHour;
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

private TimePickerDialog.OnTimeSetListener mTimeSetListener =
        new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                mHour = hourOfDay;
                mMinute = minute;
                updateDisplay2();
            }
        };

    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }

    public void newnext(View v){
    	if(rb1.isChecked()){
    		dayreturnvalue=1;
    	}
    	else if(rb2.isChecked()){
    		dayreturnvalue=2;
    	}
    	else if(rb3.isChecked()){
    		dayreturnvalue=3;
    	}
    	else if(rb4.isChecked()){
    		dayreturnvalue=4;
    	}
    	
        Intent intent=new Intent(getApplicationContext(), newgroup4.class);
        
        intent.putExtra("daycheck", daycheck);
        intent.putExtra("아이디", ID);
        intent.putExtra("방이름", roomname);
		intent.putExtra("멤버리스트", memberlist);
		intent.putExtra("day", day);
		intent.putExtra("starttime",starttime);
		intent.putExtra("dayreturnvalue",dayreturnvalue);
        startActivity(intent);
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