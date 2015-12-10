/*
 * 약속 관련기능 thread
 * 약속 생성
 */
package com.example.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class MeetThread extends Thread{
	
	BufferedReader bufferR;
	BufferedWriter bufferW;
	
	Socket client;
	
	Handler handler;//방장 id와 방이름
	String id,checkmsg;
	int flag;
	int roomid;
	int range;
	String startdate;
	String ID;
	String choicemsg;
	String day;
	int starttime;
	//연결된 소켓으로부터 대화를 나눌 스트림을 얻음
	
	public MeetThread(Socket client, Handler handler, int roomid,String startdate,int range,String id,int flag) {
		this.handler = handler;
		this.client=client;
		this.roomid=roomid;
		this.flag=flag;
		this.range=range;
		this.startdate=startdate;
		this.ID=id;
		
		try {
			bufferR = new BufferedReader(new InputStreamReader(client.getInputStream(),"MS949"));
			bufferW = new BufferedWriter(new OutputStreamWriter(client.getOutputStream(),"MS949"));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}//악속 생성용 생성자
	
	public MeetThread(Socket client, Handler handler, int roomid,int flag) {
		this.handler = handler;
		this.client=client;
		this.roomid=roomid;
		this.flag=flag;
		
		try {
			bufferR = new BufferedReader(new InputStreamReader(client.getInputStream(),"MS949"));
			bufferW = new BufferedWriter(new OutputStreamWriter(client.getOutputStream(),"MS949"));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}//방 정보 조회 생성자
	
	public MeetThread(Socket client, Handler handler, int roomid, String ID, String msg, int flag) {
		this.handler = handler;
		this.client=client;
		this.roomid=roomid;
		this.ID=ID;
		this.flag=flag;
		this.choicemsg=msg;
		try {
			bufferR = new BufferedReader(new InputStreamReader(client.getInputStream(),"MS949"));
			bufferW = new BufferedWriter(new OutputStreamWriter(client.getOutputStream(),"MS949"));
			
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}//사용자 선택(일정,장소,음식,이벤트)를 저장하는 생성자

	public MeetThread(Socket client, Handler handler,int flag)
	{
		this.flag=flag;
		this.handler = handler;
		try {
			
			bufferR = new BufferedReader(new InputStreamReader(client.getInputStream(),"MS949"));
			bufferW = new BufferedWriter(new OutputStreamWriter(client.getOutputStream(),"MS949"));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void listen(){
		try{
			String msg="";
			if(flag==0)
			{
				msg="6/"+roomid+"/"+startdate+"/"+Integer.toString(range)+"/"+ID;
				
				bufferW.write(msg+"\n");
				bufferW.flush();//방아이디, checkmsg를 보낸다.
				
				String info = bufferR.readLine();
				
				Message m = new Message();
				Bundle bundle = new Bundle();
				bundle.putString("info", info);
				m.setData(bundle);
				handler.sendMessage(m);
			}//약속 생성 일시
			
			else if(flag==1)
			{
				msg="8/"+Integer.toString(roomid)+"/";
				
				bufferW.write(msg+"\n");
				bufferW.flush();//방아이디를 보낸다.
				
				String info = bufferR.readLine();
				
				Message m = new Message();
				Bundle bundle = new Bundle();
				bundle.putString("info", info);
				m.setData(bundle);
				handler.sendMessage(m);
			}//방정보 가져오기1
			
			else if(flag==2)
			{
				msg="11/"+Integer.toString(roomid)+"/";
				
				bufferW.write(msg+"\n");
				bufferW.flush();//방아이디를 보낸다.
				
				String info = bufferR.readLine();
				
				Message m = new Message();
				Bundle bundle = new Bundle();
				bundle.putString("info", info);
				m.setData(bundle);
				handler.sendMessage(m);
			}
			else if(flag==3){
				msg="12/"+roomid+"/"+ID+"/"+choicemsg;
				
				bufferW.write(msg+"\n");
				bufferW.flush();
				
				String info = bufferR.readLine();
				
				Message m = new Message();
				Bundle bundle = new Bundle();
				bundle.putString("info", info);
				m.setData(bundle);
				handler.sendMessage(m);
			}
			else if(flag==4){
				msg="13/"+roomid;
				
				bufferW.write(msg+"\n");
				bufferW.flush();
				
				String info = bufferR.readLine();
				
				Message m = new Message();
				Bundle bundle = new Bundle();
				bundle.putString("info", info);
				m.setData(bundle);
				handler.sendMessage(m);
			}//방정보 가져오기2
			else if(flag==5){
				msg="18/"+roomid;
				
				bufferW.write(msg+"\n");
				bufferW.flush();
				
				String info = bufferR.readLine();
				
				Message m = new Message();
				Bundle bundle = new Bundle();
				bundle.putString("info", info);
				m.setData(bundle);
				handler.sendMessage(m);
			}//장소고정일시 자동 완성
			
			else if(flag==6)
			{
				msg="20/";
				bufferW.write(msg+"\n");
				bufferW.flush();
				
				String info = bufferR.readLine();
				
				Message m = new Message();
				Bundle bundle = new Bundle();
				bundle.putString("info", info);
				m.setData(bundle);
				handler.sendMessage(m);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		
		super.run();
		listen();
		
	}
	
}