/*
 * ȸ������ ���� ��� �� ȸ������ �� �α��� ȭ��
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
        
        id = intent_01.getStringExtra("���̵�");
        nn = intent_01.getStringExtra("�г���");

        textView_id.setText(String.valueOf(id));
        textView_nn.setText(String.valueOf(nn));//���̵�� �г��� ������
    }


    public void Mainselect(View v) {
    	Intent intent = new Intent(getBaseContext(), Mainselect.class);
		intent.putExtra("���̵�", id);
        startActivity(intent);
    }//�α���
}