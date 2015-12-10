/*
 * �游��� ȭ��2 - ����/������ ���� ����
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
	ArrayList<String> memberlist=new ArrayList<String>();//���� ����Ʈ
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newgroup1);

        MainActivity ma=new MainActivity();
        Typeface mTypeface=Typeface.createFromAsset(getAssets(), "fonts/hun.ttf");
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content); 
        ma.setGlobalFont(root,mTypeface);
        
        Intent intent_01 = getIntent();
        ID = intent_01.getStringExtra("���̵�");
        roomname = intent_01.getStringExtra("���̸�");
        memberlist=intent_01.getStringArrayListExtra("�������Ʈ");
    }
    
    public void new1y(View v){
        Intent intent=new Intent(getApplicationContext(), newgroup2.class);
        intent.putExtra("daycheck", 1);
        intent.putExtra("���̵�", ID);
        intent.putExtra("���̸�", roomname);
		intent.putExtra("�������Ʈ", memberlist);
        startActivity(intent);
    }//�������
    
    public void new1n(View v){
        Intent intent=new Intent(getApplicationContext(), newgroup4.class);
        intent.putExtra("daycheck", 0);
        intent.putExtra("���̵�", ID);
        intent.putExtra("���̸�", roomname);
		intent.putExtra("�������Ʈ", memberlist);
        startActivity(intent);
    }//���������
    
    public void home(View v)
    {
        Intent intent=new Intent(getApplicationContext(), Mainselect.class);
        intent.putExtra("���̵�", ID);
        startActivity(intent);
    }
    
    public void setting(View v)
    {
    	Intent intent=new Intent(getApplicationContext(), Settingmain.class);
        intent.putExtra("���̵�", ID);
        startActivity(intent);
    }

}