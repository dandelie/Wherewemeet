/*
 * 환경설정 메인 
 */
package com.example.test;
 
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


public class Settingmain extends Activity {
	String id;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settingmain);

        MainActivity ma=new MainActivity();
        Typeface mTypeface=Typeface.createFromAsset(getAssets(), "fonts/hun.ttf");
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content); 
        ma.setGlobalFont(root,mTypeface);
         Intent intent_01 = getIntent();
        id=intent_01.getStringExtra("아이디");
    }

    public void logininfo(View v){
        Intent intent=new Intent(getApplicationContext(), removelog.class);
        intent.putExtra("아이디", id);
        startActivity(intent);
     }//계정정보 변경

  
    public void pushalarm(View v){
        Intent intent=new Intent(getApplicationContext(), settingpush.class);
        intent.putExtra("아이디", id);
        startActivity(intent);
    }//푸시알림 설정

    public void appupdate(View v){
        Toast.makeText(getApplicationContext(), "OK", Toast.LENGTH_SHORT).show();
    }//앱 업데이트


    public void downloadlink(View v){
        Toast.makeText(getApplicationContext(), "OK", Toast.LENGTH_SHORT).show();
        }//약정녀 앱 설치 링크 복사하기

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