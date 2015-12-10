/*
 * �������� ����� ���� ���� ������ ȭ��
 */
package com.example.test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class selectmeet extends Activity{
    ListView listView;
	ArrayAdapter<String> myAdapter; 
    Thread thread;
    Socket client;
	MeetThread meetThread;
	
    String roomname;
    int roomid,meetnum,placecheck,gu;
    String ID;
    Typeface mTypeface;
    LinearLayout.LayoutParams Lp;
    LinearLayout l2;
    int flag=0;
    
    String restaurant,food;
    
    ArrayList<String> memberlist;
	ArrayList<String> meetlist=new ArrayList<String>();
	ArrayList<String> meetprintlist=new ArrayList<String>();
	ArrayList<String> IDlist=new ArrayList<String>();
	ArrayList<String> NNlist=new ArrayList<String>();
	ArrayList<Integer> meetidlist=new ArrayList<Integer>();
	
	private final selectmeetHandler smhandler=new selectmeetHandler(this);
	  
	private static class selectmeetHandler extends Handler{
		private final WeakReference<selectmeet> aActivity;
		public selectmeetHandler(selectmeet activity){
			aActivity = new WeakReference<selectmeet>(activity);
		}
		@Override
		public void handleMessage(Message msg){
			selectmeet activity=aActivity.get();
			if(activity!=null){
				activity.handleMessage(msg);
			}
		}
	}
	
	private void handleMessage(Message msg){

		Bundle bundle = msg.getData();

		String m=bundle.getString("info");

		String[] value=m.split("[/]");
		int res=Integer.parseInt(value[0]);
		int num=Integer.parseInt(value[2]);
		l2=(LinearLayout)findViewById(R.id.editText2);
		if(res==1)
		{
			try{
				Calendar cal=Calendar.getInstance();	
				SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
				String now=format.format(cal.getTime());//���ó�¥
				Date now2=format.parse(now);
				for(int i=0;i<num;i++)
				{					
					Date dt2=format.parse(value[4+i*6]);//���� ��� ��¥
					if(dt2.getTime() >= now2.getTime()) {//���� ������� �ٰ����� ������� �Ǻ�
						flag=1;
					}//�ٰ����� ����� ��
					
					if(flag==0){
						meetidlist.add(Integer.parseInt(value[3+i*6]));						
					}
					else{
						TextView t=new TextView(this);
						t.setText("���� : "+value[4+i*6]+"   "+value[5+i*6]+"��");
						t.setTypeface(mTypeface);
						t.setTextSize(17);
						l2.addView(t,Lp);
						
						TextView t1=new TextView(this);
						restaurant=value[7+i*6];
						t1.setText("��� : "+value[7+i*6]);
						t1.setTypeface(mTypeface);
						t1.setTextSize(17);
						l2.addView(t1,Lp);
						
						
						TextView t2=new TextView(this);
						food=value[8+i*6];
						t2.setText("���� : "+value[8+i*6]);
						t2.setTypeface(mTypeface);
						t2.setTextSize(17);
						l2.addView(t2,Lp);
					}
					

					String meetmsg="";					
					String meetprintmsg=value[4+i*6]+" "+value[5+i*6]+"��  "+value[7+i*6]+"����";
					
					for(int j=0;j<5;j++)
					{
						if(value[4+i*6+j].length()>0)
						{
							meetmsg=meetmsg+value[4+i*6+j]+"    ";
						}
					}
					if(flag==0){
						meetlist.add(meetmsg);
						meetprintlist.add(meetprintmsg);
					}
				}
			}
			catch(Exception e){
				Toast.makeText(getApplicationContext(), "�����߻�!", Toast.LENGTH_SHORT).show();
			}

			myAdapter.notifyDataSetChanged();
			
			try {
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}//������Ӹ���Ʈ  ���
	
    @Override
    public void onCreate(Bundle savedInstanceState) 
{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selectmeet);

        final MainActivity ma=new MainActivity();
        mTypeface=Typeface.createFromAsset(getAssets(), "fonts/hun.ttf");
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content); 
        ma.setGlobalFont(root,mTypeface);
        
        memberlist=new ArrayList<String>();
        
        Intent intent_01 = getIntent();
        roomname = intent_01.getStringExtra("���̸�");//����� ���̵�
        ID=intent_01.getStringExtra("���̵�");
        roomid=intent_01.getIntExtra("����̵�",-1);
		memberlist=intent_01.getStringArrayListExtra("�������Ʈ");
		placecheck=intent_01.getIntExtra("placecheck", -1);
		gu=intent_01.getIntExtra("gu", 0);

        TextView name=(TextView)findViewById(R.id.selectmeet);
       
        name.setText("< "+roomname+" >");
        
        LinearLayout l=(LinearLayout)findViewById(R.id.roomfriendname);
    	Lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);

        for(int i=0;i<memberlist.size();i++)
        {
        	TextView v=new TextView(this);
        	v.setText(memberlist.get(i));
        	v.setTypeface(mTypeface);
        	l.addView(v,Lp);//��ư �߰�
        }
		
        thread = new Thread(){
			public void run() {
				super.run();				
					SocketService s=new SocketService();
					client = s.getsocket();			
					meetThread = new MeetThread(client, smhandler,roomid,1);
					meetThread.start();				
			}
		};
		
		thread.start();
		
        listView = (ListView)findViewById(R.id.editText); //xml���� �����
        myAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,meetprintlist)
        		{
        	@Override
        	public View getView(int position,View convertView,ViewGroup parent)
        	{
        		ma.setGlobalFont(parent, mTypeface);
	            return super.getView(position, convertView, parent);
        	}
        	};
        listView.setAdapter(myAdapter); //����Ʈ �並 ����Ϳ� ��ü
        listView.setOnItemClickListener(new OnItemClickListener(){
        	public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
        		final LinearLayout linear=(LinearLayout)View.inflate(selectmeet.this,R.layout.custom2, null);
        		
        		Builder builder = new AlertDialog.Builder(selectmeet.this);
        		builder.setTitle("��� �� ����");
        		builder.setView(linear);
        		
        		Button b=(Button)findViewById(R.id.button);
         		b.setOnClickListener(new OnClickListener(){
          		   
          		   public void onClick(View v)
          		   {
          			  final LinearLayout linear=(LinearLayout)View.inflate(selectmeet.this,R.layout.activity_web, null);
	          			Builder builder = new AlertDialog.Builder(selectmeet.this);
	            		builder.setTitle("��� �� ����");
	            		builder.setView(linear);
	            		builder.setNegativeButton("Ȯ��", new DialogInterface.OnClickListener() {
	            			public void onClick(DialogInterface dialog, int which) {
	            	    		AdapterView.AdapterContextMenuInfo menuInfo;
	            		    	
	            			}
	            		});
	            		builder.show();
          		   }
          			
          		});
        		builder.setNegativeButton("Ȯ��", new DialogInterface.OnClickListener() {
        			public void onClick(DialogInterface dialog, int which) {
        	    		AdapterView.AdapterContextMenuInfo menuInfo;
        		    	
        			}
        		});
        		builder.show();
        	}
        });
    }
    public void makemeetgo(View v)
    {
    	if(flag==0)
    	{
	    	Intent intent=new Intent(getApplicationContext(),makemeet.class);
	    	
	    	intent.putExtra("���̵�", ID);
	    	intent.putExtra("����̵�", roomid);
	    	intent.putExtra("���̸�", roomname);
	    	intent.putExtra("�������Ʈ", memberlist);
	    	intent.putExtra("placecheck",placecheck);
	    	intent.putExtra("gu",gu);
	    	startActivity(intent);
    	}
    	else
    	{
    		Toast.makeText(getApplicationContext(), "�ٰ����� ����� �־��.", Toast.LENGTH_SHORT).show();
    	}
    	
    }
   
    public void toselectgroup(View v)
    {
    	Intent intent=new Intent(getApplicationContext(),Selectgroup.class);
    	intent.putExtra("���̵�", ID);
    	startActivity(intent);
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

    public void websearch(View v)
    {
    	
    	if(restaurant!=""&&food!=""){
    		restaurant=restaurant+" "+food;
    	String finalword="";
    	byte[] b=null;
    	try {
    		b=restaurant.getBytes("UTF-8");
    	} catch (UnsupportedEncodingException e) {
    		e.printStackTrace();
    	}
    	
    	for(int i=0;i<b.length;i++){
    		if(Integer.toHexString(b[i]).length()>3){
    			finalword=finalword+"%"+Integer.toHexString(b[i]).substring(6,8);
    		}
    		else{
    			finalword=finalword+"%"+Integer.toHexString(b[i]);
    		}
    	}
        
    	Intent intent_01 = new Intent(getApplicationContext(), Web.class);
    		intent_01.putExtra("�˻�����", finalword);  
    		intent_01.putExtra("���̵�", ID);
            startActivity(intent_01);
    	}
    	}

    
}