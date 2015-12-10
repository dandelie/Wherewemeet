
/*
 * 소켓 여는 클래스
 * 통신에 필요한 모든 소켓은 여기서 연다 
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
		//서비스 생성시 호출
	}
	
	public Socket getsocket(){
		Socket s=null;
		try{
			//int wifiIPAddress = info.getIpAddress();//와이파이 관련코드 작성해야함
			s=new Socket(ip,port);//소켓 생성			  
			
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
		return s;//생성한 소켓 반환
	}
}
