package com.example.test;

import java.lang.ref.WeakReference;
import java.net.Socket;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class AlarmReceive extends BroadcastReceiver {

	String id;
	Socket client;
	Thread thread;
	MeetThread meetThread;
	
	private final alarmHandler ahandler=new alarmHandler(this);

	private static class alarmHandler extends Handler{
		private final WeakReference<AlarmReceive> aActivity;
		public alarmHandler(AlarmReceive activity){
			aActivity = new WeakReference<AlarmReceive>(activity);
		}
		@Override
		public void handleMessage(Message msg){
			AlarmReceive  activity=aActivity.get();
			if(activity!=null){
				activity.handleMessage(msg);
			}
		}
	}
	
	private void handleMessage(Message msg){
		Bundle bundle = msg.getData();
		String res=bundle.getString("info");		
		if(Integer.parseInt(res)==5)
		{
		//알람 잘 갔음	
		}
	}//기기등록.
	
	public void onReceive(Context context, Intent intent) {		
		
		thread = new Thread(){
			public void run() {
				super.run();
				SocketService s=new SocketService();
				client = s.getsocket();			
				meetThread = new MeetThread(client, ahandler,6);
				meetThread.start();
			}
		};		
		thread.start();		
	}
}
