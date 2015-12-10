/*
 * ���� ���� ȭ�� 
 */
package com.example.test;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Date_main_activity extends Activity {

	String ID,roomname;
	int roomid;
	int range;
	int num=0;
	String startdate;
	ArrayList<Button> buttonlist=new ArrayList<Button>();
	ArrayList<String> memberlist=new ArrayList<String>();
	ArrayList<String> dateselectlist=new ArrayList<String>();//������ �������� ���� ����Ʈ
	LinearLayout.LayoutParams Lp;
	LinearLayout ll;//���Ͼ� ���̾ƿ�
	Socket client;
	Thread thread;
	MeetThread meetThread;
	String msg;
	Typeface mTypeface;
	
	private final dateHandler dhandler=new dateHandler(this);
	  
	private static class dateHandler extends Handler{
		private final WeakReference<Date_main_activity> aActivity;
		public dateHandler(Date_main_activity activity){
			aActivity = new WeakReference<Date_main_activity>(activity);
		}
		@Override
		public void handleMessage(Message msg){
			Date_main_activity activity=aActivity.get();
			if(activity!=null){
				activity.handleMessage(msg);
			}
		}
	}
	
    private void handleMessage(Message msg){
		Bundle bundle = msg.getData();
		String info=bundle.getString("info");		
		String[] value=info.split("[/]");
		
		int res=Integer.parseInt(value[0]);		
		
		if(res==1){
			startdate=value[1];
			range=Integer.parseInt(value[2]);
			try
	        {
	        	Calendar c=Calendar.getInstance();
	        	SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
	        	String now=format.format(c.getTime());//���� ��¥ String
	        	Date now2=format.parse(now);//���� ��¥ date
	        	Date start=format.parse(startdate);//���� ��¥ date
	        
	        	c.setTime(start);
	        	
	        	ll=(LinearLayout) findViewById(R.id.linearLayout);
	        	for(int i=0;i<range;i++)
	        	{
	        		String d=format.format(c.getTime());
	        		Date d2=format.parse(d);//���� SCOPE ��¥ date
	        		c.add(c.DATE,+1);//�ϳ� ����
	        		
	        		Button b=new Button(this);
	        		

	                mTypeface=Typeface.createFromAsset(getAssets(), "fonts/hun.ttf");
	        		b.setTypeface(mTypeface);
	                b.setBackgroundResource(R.drawable.buttonw);
	            	b.setText(d);
	            	if(d2.getTime() >= now2.getTime()){//���� ��¥�ų� �װͺ��� ������ ���� Ŭ�������� Ȱ��ȭ-> �� ��ȿ�� ��¥�� ���� �����ϰ� ��
	            		b.setOnClickListener(new OnClickListener() {
	            			@Override
	            			public void onClick(View v) {
	            				Button clickedButton = (Button) v;
	            				Button tempButton;
	            				String datetext=clickedButton.getText().toString();
	            				int k;
	            				for (k=0;k<buttonlist.size();k++) {
	            					tempButton=buttonlist.get(k);
	            					if (tempButton == clickedButton) {
	            						break;
	            					}
	            				}
	            				
	            				Log.v("k",Integer.toString(k));
	            				Intent intent_01 = new Intent(getApplicationContext(),time_activity.class);
	            				intent_01.putExtra("���̵�", ID);
	            				intent_01.putExtra("����̵�",roomid);
	            				intent_01.putExtra("���̸�",roomname);
	            				intent_01.putExtra("�������Ʈ",memberlist);
	            				intent_01.putExtra("��¥",datetext);
	            				intent_01.putExtra("��ư����", k);
	            				startActivity(intent_01);  
	            			}
	            		});
	            		num++;
	            		Lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
	            		ll.addView(b,Lp);//��ư �߰�
	            	}
	            	
	            	buttonlist.add(b); 
	        	}
	        	c.add(c.DATE,+1);
	        }
	        
	        catch(Exception e)
	        {
	        	e.printStackTrace();
	        }
			try 
			{
				client.close();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
		else if(res==2){
			Toast.makeText(getApplicationContext(), "���� �Ϸ�! �ٸ� ģ������ ������ ��ٷ��ּ���~", Toast.LENGTH_SHORT).show();
			try 
			{
				client.close();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
			Intent intent_01 = new Intent(getApplicationContext(),meet.class);
	         intent_01.putExtra("���̵�", ID);
	         intent_01.putExtra("����̵�",roomid);
	         intent_01.putExtra("���̸�",roomname);
	         intent_01.putExtra("�������Ʈ",memberlist);	
	         startActivity(intent_01); 
		}//���� �ٸ��ֵ��� ���� ��������
		else if(res==3)
		{
			Toast.makeText(getApplicationContext(), "ģ���� �´� �ð��� �����... �ٽ� ������ �ּ���.", Toast.LENGTH_SHORT).show();
			try 
			{
				client.close();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}//��¥ ����
		
		else if(res==4)
		{			
			try 
			{
				client.close();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
			Toast.makeText(getApplicationContext(), "���� �Ϸ�! ������ ��¥�� �����帱�Կ�~", Toast.LENGTH_SHORT).show();
			Toast.makeText(getApplicationContext(), "��¥: "+value[1]+"\n������ �ð�: "+value[2]+"\n������ �ð�: "+value[3], Toast.LENGTH_SHORT).show();
			
		    for(int i=0;i<range;i++){
	    		String filename= ID+roomid+i;//���� �̸�
	    		SharedPreferences prefs = getSharedPreferences(filename, MODE_PRIVATE);
	    	    Editor ed = prefs.edit();
	    	    ed.clear();//��� ����������
	    	    ed.commit();
	    	}//sharedpreference ����������
		    
		    Intent intent_01 = new Intent(getApplicationContext(),meet.class);
	         intent_01.putExtra("���̵�", ID);
	         intent_01.putExtra("����̵�",roomid);
	         intent_01.putExtra("���̸�",roomname);
	         intent_01.putExtra("�������Ʈ",memberlist);	
	         startActivity(intent_01); 
		    
		}//��¥�� ������
		/*
		else if(res==5){
			Toast.makeText(getApplicationContext(), "���� �Ϸ�! ����� ���� ��ǥ�� �����մϴ�.", Toast.LENGTH_SHORT).show();

		}//��ǥ�ؾ��ҽ�*/
	}
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.date_main_activity);

        MainActivity ma=new MainActivity();
        mTypeface=Typeface.createFromAsset(getAssets(), "fonts/hun.ttf");
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content); 
        ma.setGlobalFont(root,mTypeface);
        
        Intent intent_01 = getIntent(); 
        roomid=intent_01.getIntExtra("����̵�",-1);
        ID=intent_01.getStringExtra("���̵�");
        roomname=intent_01.getStringExtra("���̸�");
        memberlist=intent_01.getStringArrayListExtra("�������Ʈ");
        
        thread = new Thread(){
 			public void run() {
 				super.run();				
 					SocketService s=new SocketService();
 					client = s.getsocket();			
 					meetThread = new MeetThread(client, dhandler, roomid,2);
 					meetThread.start();
 			}
    	 };

  		thread.start();
    }
    
    public void datesave(View v){
    	msg="";
    	SharedPreferences date_time;
    	
    	for(int i=0;i<range-num;i++)
    	{
    		for(int j=1;j<17;j++){
					msg=msg+0;
			}
    		msg=msg+"/";
    	}
    	
    	for(int i=range-num;i<range;i++){
    		String filename= ID+roomid+i;//���� �̸�
    		try{
    			date_time = getSharedPreferences(filename, Activity.MODE_PRIVATE); 
    			for(int j=1;j<17;j++){
    				if(date_time.getBoolean("check"+j, false)==true){
    					msg=msg+1;
    				}//üũ�� �Ǿ������� 1
    				else{
    					msg=msg+0;
    				}
    			}
    			msg=msg+"/";
    			
    			Log.v("msg",msg);
    		}
    		catch(Exception e){
    			 Toast.makeText(getApplicationContext(), "�����̻�߻�", Toast.LENGTH_SHORT).show();
    		}
    	}//msg ����
    	msg=startdate+"/"+range+"/"+msg;
    	
    	 thread = new Thread(){
 			public void run() {
 				super.run();				
 					SocketService s=new SocketService();
 					client = s.getsocket();			
 					meetThread = new MeetThread(client, dhandler,roomid,ID,msg,3);
 					meetThread.start();				
 			}
 		};
 		
 		thread.start();
    	
    	
    }//���ÿϷ� ��ư
    
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
