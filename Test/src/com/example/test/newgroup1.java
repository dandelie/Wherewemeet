/*
 * 방만들기 화면2 - 정기/비정기 모임 선택
 */
package com.example.test;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

public class newgroup1 extends Activity {

	String ID,roomname;
	ArrayList<String> memberlist=new ArrayList<String>();//방멤버 리스트
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newgroup1);

        MainActivity ma=new MainActivity();
        Typeface mTypeface=Typeface.createFromAsset(getAssets(), "fonts/hun.ttf");
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content); 
        ma.setGlobalFont(root,mTypeface);
        
        Intent intent_01 = getIntent();
        ID = intent_01.getStringExtra("아이디");
        roomname = intent_01.getStringExtra("방이름");
        memberlist=intent_01.getStringArrayListExtra("멤버리스트");
    }
    
    public void new1y(View v){
        Intent intent=new Intent(getApplicationContext(), newgroup2.class);
        intent.putExtra("daycheck", 1);
        intent.putExtra("아이디", ID);
        intent.putExtra("방이름", roomname);
		intent.putExtra("멤버리스트", memberlist);
        startActivity(intent);
    }//정기모임
    
    public void new1n(View v){
        Intent intent=new Intent(getApplicationContext(), newgroup4.class);
        intent.putExtra("daycheck", 0);
        intent.putExtra("아이디", ID);
        intent.putExtra("방이름", roomname);
		intent.putExtra("멤버리스트", memberlist);
        startActivity(intent);
    }//비정기모임
    
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