/*
 * 계정정보 변경 화면
 */
package com.example.test;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


public class removelog extends Activity {
	String id;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.removelog);

        MainActivity ma=new MainActivity();
        Typeface mTypeface=Typeface.createFromAsset(getAssets(), "fonts/hun.ttf");
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content); 
        ma.setGlobalFont(root,mTypeface);
        Intent intent_01 = getIntent();
        id=intent_01.getStringExtra("아이디");
    }

    public void changepw(View v){
        Intent intent=new Intent(getApplicationContext(), removepass.class);
        intent.putExtra("아이디", id); 
        startActivity(intent);
    }//패스워드 변경    
    
    public void logout(View v){
    	File myfile=getDir("myfile", Context.MODE_PRIVATE);
	    String path= myfile.getAbsolutePath();
	    File file=new File(path+"/flag.txt");
	    file.delete();
        Toast.makeText(getApplicationContext(), "로그아웃 완료.", Toast.LENGTH_SHORT).show();
    	Intent intent=new Intent(getApplicationContext(), login.class);
        startActivity(intent);
    }//로그아웃
    
    public void removeid(View v){
    	Intent intent=new Intent(getApplicationContext(), removeid.class);
        intent.putExtra("아이디", id); 
        startActivity(intent);
    }//회원 탈퇴
    
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