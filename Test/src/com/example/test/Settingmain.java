/*
 * ȯ�漳�� ���� 
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
        id=intent_01.getStringExtra("���̵�");
    }

    public void logininfo(View v){
        Intent intent=new Intent(getApplicationContext(), removelog.class);
        intent.putExtra("���̵�", id);
        startActivity(intent);
     }//�������� ����

  
    public void pushalarm(View v){
        Intent intent=new Intent(getApplicationContext(), settingpush.class);
        intent.putExtra("���̵�", id);
        startActivity(intent);
    }//Ǫ�þ˸� ����

    public void appupdate(View v){
        Toast.makeText(getApplicationContext(), "OK", Toast.LENGTH_SHORT).show();
    }//�� ������Ʈ


    public void downloadlink(View v){
        Toast.makeText(getApplicationContext(), "OK", Toast.LENGTH_SHORT).show();
        }//������ �� ��ġ ��ũ �����ϱ�

    public void home(View v)
    {
        Intent intent=new Intent(getApplicationContext(), Mainselect.class);
        intent.putExtra("���̵�", id);
        startActivity(intent);
    }
    
    public void setting(View v)
    {
    	Intent intent=new Intent(getApplicationContext(), Settingmain.class);
        intent.putExtra("���̵�", id);
        startActivity(intent);
    }
}