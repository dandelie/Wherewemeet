package com.example.test;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.Socket;
import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.graphics.Typeface;
import android.app.Activity;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class food_main_activity extends Activity{
	
	Spinner categorySpinner;
	String ID,roomname;
	Thread thread;
    Socket client;
    int roomid;
    PlaceThread placethread;
    LinearLayout ll;
	LinearLayout.LayoutParams Lp;
    Typeface mTypeface;
    String msg;
    
	ArrayList<CheckBox> gChecklist=new ArrayList<CheckBox>();//üũ�ڽ� ����Ʈ
	ArrayList<CheckBox> bChecklist=new ArrayList<CheckBox>();//üũ�ڽ� ����Ʈ
	ArrayList<String> memberlist=new ArrayList<String>();
	ArrayList<String> gfoodlist=new ArrayList<String>();//üũ�ڽ� ����Ʈ	
	ArrayList<String> bfoodlist=new ArrayList<String>();//üũ�ڽ� ����Ʈ	
	
	private final PlaceHandler fhandler=new PlaceHandler(this);
	  
	private static class PlaceHandler extends Handler{
		private final WeakReference<food_main_activity> aActivity;
		public PlaceHandler(food_main_activity activity){
			aActivity = new WeakReference<food_main_activity>(activity);
		}
		@Override
		public void handleMessage(Message msg){
			food_main_activity activity=aActivity.get();
			if(activity!=null){
				activity.handleMessage(msg);
			}
		}
	}
	
	private void handleMessage(Message msg)
	{
		Bundle bundle = msg.getData();
		String m=bundle.getString("info");
		String[] value=m.split("[/]");
		int res=Integer.parseInt(value[0]);
		ll=(LinearLayout) findViewById(R.id.linearLayout4);

		if(res==1)
		{			
			int num=Integer.parseInt(value[1]);//���� ����
			ll.removeAllViews();//üũ�ڽ� �� ����
			TextView t=(TextView)findViewById(R.id.t3);
			t.setText(" ��ȣ   ��ȣ");
			
			gChecklist.removeAll(gChecklist);
			bChecklist.removeAll(bChecklist);
			
			for(int i=2;i<2+num;i++)
			{				
	        	final CheckBox goodchk=new CheckBox(this);
	        	final CheckBox badchk=new CheckBox(this);
	        	badchk.setText(value[i]);
        		badchk.setTypeface(mTypeface);
        		goodchk.setTag(value[i]);
        		
	        	goodchk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
	                public void onCheckedChanged(CompoundButton buttonView,
	                        boolean isChecked) 
	                {

                		String foodname= goodchk.getTag().toString();
                		//CheckBox c= (CheckBox)buttonView;
	                	if (isChecked) 
	                	{
	                		if(gfoodlist.size()<3 && gfoodlist.contains(foodname)==false)
	                		{
	                			gfoodlist.add(foodname);//��ȣ ����Ʈ�� �߰�
	                			int num=gChecklist.indexOf(goodchk);//üũ����Ʈ�� �ε��� ��ȯ
	                			Log.e("eee", Integer.toString(num));
	                			bChecklist.get(num).setChecked(false);;//��ȣ üũ�ڽ� üũ ����
	                			if(bfoodlist.contains(foodname))
		                    	{
			                    	int index=bfoodlist.indexOf(foodname);
			                    	bfoodlist.remove(index);
			                    	showview(bfoodlist);
		                    	}//���� ��ȣ ����Ʈ�� �ִٸ� ����
	                			showview(gfoodlist);//����Ʈ ����
	                		}
	                		else if(gfoodlist.size()>=3)
	                		{
	                			Toast.makeText(getApplicationContext(), "3�������� �� �� �־��!",Toast.LENGTH_SHORT).show();
	                			goodchk.setChecked(false);
	                		}
	                		else if(gfoodlist.contains(foodname))
	                		{
	                			Toast.makeText(getApplicationContext(), "�̹� �����ϼ̾��!", Toast.LENGTH_SHORT).show();
	                			goodchk.setChecked(false);
	                		}
	                    }// ��ȣ üũ�ڽ� ���� 
	                    else
	                    {
	                    	if(gfoodlist.contains(foodname))
	                    	{
		                    	int index=gfoodlist.indexOf(foodname);
		                    	gfoodlist.remove(index);
		                    	showview(gfoodlist);
	                    	}
	                    }// ��ȣ üũ�ڽ� ��������
	                }
	            });

	        	badchk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
	                public void onCheckedChanged(CompoundButton buttonView,
	                        boolean isChecked) 
	                {

                		String foodname= badchk.getText().toString();
	                	if (isChecked) 
	                	{
	                		if(bfoodlist.size()<3 && bfoodlist.contains(foodname)==false)
	                		{
	                			bfoodlist.add(foodname);//��ȣ ����Ʈ�� �߰�
	                			int index=bChecklist.indexOf(badchk);//��ȣ üũ�ڽ��� �ε��� �˻�.��ȣüũ�ڽ��� �ؽ�Ʈ�� �˻��ȵǾ� �̷��� ó��.
	                			gChecklist.get(index).setChecked(false);//��ȣ üũ�ڽ� �ҷ��� üũ ����
	                			if(gfoodlist.contains(foodname))
		                    	{
			                    	int index2=gfoodlist.indexOf(foodname);
			                    	gfoodlist.remove(index2);
			                    	showview(gfoodlist);
		                    	}//���� ��ȣ ����Ʈ�� �ִٸ� ����
	                			showview(bfoodlist);//����Ʈ ����
	                		}
	                		else if(bfoodlist.size()>=3)
	                		{
	                			Toast.makeText(getApplicationContext(), "3�������� �� �� �־��!", Toast.LENGTH_SHORT).show();
	                			badchk.setChecked(false);
	                		}
	                		else if(bfoodlist.contains(foodname))
	                		{
	                			Toast.makeText(getApplicationContext(), "�̹� �����ϼ̾��!", Toast.LENGTH_SHORT).show();
	                			badchk.setChecked(false);
	                		}
	                    }// ��ȣ üũ�ڽ� ���� 
	                    else
	                    {
	                    	if(bfoodlist.contains(foodname))
	                    	{
		                    	int index3=bfoodlist.indexOf(foodname);
		                    	bfoodlist.remove(index3);
		                    	showview(bfoodlist);
	                    	}
	                    }// ��ȣ üũ�ڽ� ��������
	                }
	            });

	        	
	        	gChecklist.add(goodchk);
	        	bChecklist.add(badchk);
	        	LinearLayout littlel= new LinearLayout(this);
	        	Lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.HORIZONTAL);
	        	littlel.addView(goodchk,Lp);
	        	littlel.addView(badchk,Lp);
	        	ll.addView(littlel);//üũ�ڽ� �߰�
			}
		}//���� �˻��� ���
		
		else if(res==2)
		{
			String food=value[1];
			Toast.makeText(getApplicationContext(), food+"�� �����Ǿ����ϴ�!", Toast.LENGTH_SHORT).show();
			
			Intent intent=new Intent(getApplicationContext(), selectmeet.class);
	        intent.putExtra("���̵�", ID);
	    	intent.putExtra("����̵�", roomid);
	    	intent.putExtra("���̸�", roomname);
	    	intent.putExtra("�������Ʈ", memberlist);
	    	startActivity(intent);
			
		}
		
		else if(res==3)
		{
			Toast.makeText(getApplicationContext(), "�ٸ� ������� ������ ��ٷ��ּ���", Toast.LENGTH_SHORT).show();
			Intent intent=new Intent(getApplicationContext(), meet.class);
	        intent.putExtra("���̵�", ID);
	    	intent.putExtra("����̵�", roomid);
	    	intent.putExtra("���̸�", roomname);
	    	intent.putExtra("�������Ʈ", memberlist);
	    	startActivity(intent);
		}//���� �������϶�
		else if(res==4)
		{
			Toast.makeText(getApplicationContext(), "��ġ�� ������ ���׿�. �ٽ� ����ּ���~", Toast.LENGTH_SHORT).show();
		}//��ġ�� ������ ������
		try {
			client.close();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}

	 protected void showview(final ArrayList<String> foodlist) {
		 if(foodlist==gfoodlist){
			final LinearLayout l=(LinearLayout)findViewById(R.id.linearLayout3);
			 
			Lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		    if(l.getChildCount() > 0){
		        l.removeAllViews();
		    }
		    
		    for(int i=0;i<gfoodlist.size();i++)
	    	{
	    		LinearLayout l2=new LinearLayout(this);
	    		
	    		final TextView b=new TextView(this);
	    		final TextView c=new TextView(this);
	    		b.setText(gfoodlist.get(i));
	    		b.setTypeface(mTypeface);
	    		c.setText("    ");
	    		
	    		l2.addView(b,Lp);
	    		l2.addView(c,Lp);
	    		
	    		l.addView(l2);
	    		
	    		b.setOnClickListener(new TextView.OnClickListener() {
	    			   @Override
	    			   public void onClick(View v) {
	    				   int k=gfoodlist.indexOf(b.getText().toString());
	    				   gfoodlist.remove(k);
	    				   l.removeView((View) b.getParent());
	    				   showview(gfoodlist);
	    			   }
	    		});
	    	}    		    	
		 }//��ȣ���� ����
		 else{
			 final LinearLayout l=(LinearLayout)findViewById(R.id.linearLayout3_2);
			 
			 Lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		    	if(l.getChildCount() > 0){
		    	    l.removeAllViews();
		    	}
		    	for(int i=0;i<bfoodlist.size();i++)
		    	{
		    		LinearLayout l2=new LinearLayout(this);
		    		
		    		final TextView b=new TextView(this);
		    		final TextView c=new TextView(this);
		    		b.setText(bfoodlist.get(i));
		    		b.setTypeface(mTypeface);
		    		c.setText("    ");
		    		
		    		l2.addView(b,Lp);
		    		l2.addView(c,Lp);
		    		
		    		l.addView(l2,Lp);
		    		
		    		b.setOnClickListener(new TextView.OnClickListener() {
		    			   @Override
		    			   public void onClick(View v) {
		    				   int k=bfoodlist.indexOf(b.getText().toString());
		    				   bfoodlist.remove(k);
		    				   l.removeView((View) b.getParent());
		    				   showview(bfoodlist);
		    			   }
		    		});
		    	}    		    	
		    	
		 }//��ȣ ���� ����	    	
		}//���� ������ ��ȣ ��ȣ ���� ǥ���ϴ� ��.
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.food_main_activity);
       
        mTypeface=Typeface.createFromAsset(getAssets(), "fonts/hun.ttf");
        MainActivity ma=new MainActivity();
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content); 
        ma.setGlobalFont(root,mTypeface);
        
        Intent intent_01 = getIntent(); 
        roomname = intent_01.getStringExtra("���̸�");
        roomid=intent_01.getIntExtra("����̵�",-1);
        ID=intent_01.getStringExtra("���̵�");
        memberlist=intent_01.getStringArrayListExtra("�������Ʈ");
        
        categorySpinner = (Spinner) findViewById(R.id.food);
        ArrayAdapter<CharSequence> categoryadapter = ArrayAdapter.createFromResource(this, R.array.food,
                android.R.layout.simple_spinner_item);
        categoryadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryadapter);
    }
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
    
    public void categorygo(View v)
    {  
    	final int category=categorySpinner.getSelectedItemPosition();
    	
    	
    	if(category!=0)
    	{
	    	thread = new Thread(){
				public void run() {
					super.run();				
						SocketService s=new SocketService();
						client = s.getsocket();			
						placethread = new PlaceThread(client, fhandler,category,4);
						placethread.start();				
				}
			};
			thread.start();
    	}
    	else
    	{
			TextView t=(TextView)findViewById(R.id.t3);
			t.setText("");
    		Toast.makeText(getApplicationContext(), "ī�װ��� �����ϼ���!", Toast.LENGTH_SHORT).show();
    	}
    }//������ ī�װ��� ������ ��������
    

    public void newnext(View v){
    	if(gfoodlist.size()<1)
		{
			Toast.makeText(getApplicationContext(), "��ȣ�ϴ� ������ ������!", Toast.LENGTH_SHORT).show();
		}
    	else{
    		msg="";   	

    		msg=Integer.toString(gfoodlist.size())+"/";
    		for(int j=0;j<gfoodlist.size();j++){
    			msg=msg+gfoodlist.get(j)+"/";
    		}		

    		msg=msg+Integer.toString(bfoodlist.size())+"/";
    		for(int j=0;j<bfoodlist.size();j++){
    			msg=msg+bfoodlist.get(j)+"/";
    		}

    		thread = new Thread(){
    			public void run() {
    				super.run();				
    				SocketService s=new SocketService();
    				client = s.getsocket();			
    				placethread = new PlaceThread(client, fhandler,roomid,ID,msg,3);
    				placethread.start();				
    			}
    		};

    		thread.start();
    	}
    }
    
}
