
/*
 * ���� ���� Ŭ����
 * ��ſ� �ʿ��� ��� ������ ���⼭ ���� 
 */
package com.example.test;

import java.io.IOException;
import java.net.Socket;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class SocketService extends Service{
	

	//String ip=;
	int port = 8888;	
	
	private final IBinder mBinder= new LocalBinder();
	
	public class LocalBinder extends Binder{
		SocketService getService(){
			return SocketService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent){
		return mBinder;
	}
	
	public void onCreate(){
		//���� ������ ȣ��
	}
	
	public Socket getsocket(){
		Socket s=null;
		try{
			//int wifiIPAddress = info.getIpAddress();//�������� �����ڵ� �ۼ��ؾ���
			s=new Socket(ip,port);//���� ����			  
			
		}
		catch(Exception e){
			if(s!=null){
				try{
					s.close();				
				}
				catch(IOException ee){}				
			}			
			e.printStackTrace();
		}		
		return s;//������ ���� ��ȯ
	}
}
