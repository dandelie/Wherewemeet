/*
 * 자동 로그인 결정 화면- 로그인 화면으로 가거나 자동로그인 화면으로
 */
package com.example.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class autologin extends Activity {

    String ID="";
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.autologin);
        
        MainActivity ma=new MainActivity();
        Typeface mTypeface=Typeface.createFromAsset(getAssets(), "fonts/hun.ttf");
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content); 
        ma.setGlobalFont(root,mTypeface);
        
    	File myfile=getDir("myfile", Context.MODE_PRIVATE);
        String path= myfile.getAbsolutePath();
        final File file=new File(path+"/flag.txt");//ID로 텍스트 파일 만들기
        
        try
    	{
    		FileInputStream fis = new FileInputStream(file);
    		BufferedReader bufferReader = new BufferedReader(new InputStreamReader(fis,"MS949"));
    		String str;
    		while ((str = bufferReader.readLine()) != null) 
    		{
    			ID=str;
    		}//리스트에 추가
    		bufferReader.close();
    		fis.close();
    		

            Button b=(Button)findViewById(R.id.auto_login_button);
    		b.setText(ID+" 로 자동 로그인");
    	}
    	catch(Exception e)
    	{
    		Intent intent = new Intent(getBaseContext(), login.class);//에러나면 로그인하는 클래스로
            startActivity(intent);
    	}
        
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {  
        if(keyCode == KeyEvent.KEYCODE_BACK) {
        	switch (keyCode) {
        	case KeyEvent.KEYCODE_BACK:        

               new AlertDialog.Builder(this)

               .setTitle("프로그램 종료")

               .setMessage("프로그램을 종료 하시겠습니까?")

               .setPositiveButton("예", new DialogInterface.OnClickListener() {     

                   @Override

                   public void onClick(DialogInterface dialog, int which) {

                       // 프로세스 종료.

                       android.os.Process.killProcess(android.os.Process.myPid());

                   }

               })

               .setNegativeButton("아니오", null)

               .show();         

               break;    

           default:

               break;

           }

              return true;

             } 

        return super.onKeyDown(keyCode, event);  
    }
    
    public void auto_login(View v){
    		Intent intent = new Intent(getBaseContext(), Mainselect.class);
    		intent.putExtra("아이디", ID);
            startActivity(intent);
    }//로그인 버튼

    public void diff_login(View v){
        Intent intent_01 = new Intent(getApplicationContext(), login.class);
        startActivity(intent_01);
    }//다른 아이디로 로그인 버튼

}