/*
 * 회원가입 정보 출력 및 회원가입 후 로그인 화면
 */
package com.example.test;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class lognewidtest extends Activity {
	
	String id,pw,nn;
		
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lognewidtest);

        MainActivity ma=new MainActivity();
        Typeface mTypeface=Typeface.createFromAsset(getAssets(), "fonts/hun.ttf");
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content); 
        ma.setGlobalFont(root,mTypeface);
        
        TextView textView_id = (TextView) findViewById(R.id.newID);
        TextView textView_nn = (TextView) findViewById(R.id.newnick);

        Intent intent_01 = getIntent();
        
        id = intent_01.getStringExtra("아이디");
        nn = intent_01.getStringExtra("닉네임");

        textView_id.setText(String.valueOf(id));
        textView_nn.setText(String.valueOf(nn));//아이디와 닉네임 보여줌
    }


    public void Mainselect(View v) {
    	Intent intent = new Intent(getBaseContext(), Mainselect.class);
		intent.putExtra("아이디", id);
        startActivity(intent);
    }//로그인
}