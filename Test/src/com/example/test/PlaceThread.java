/*
 * 친구관리 관련 Thread
 * 친구 추가시 친구 id조회, 친구 목록 refresh
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

public class PlaceThread extends Thread{

	BufferedReader bufferR;
	BufferedWriter bufferW;
	Socket client;
	Handler handler;
	
	String id,msg,selected;
	int flag,gu,theme,roomid,num,subway,category;
	
	public PlaceThread(Socket client, Handler handler, int gu, int theme, int flag) {
		this.handler = handler;
		this.client = client;
		this.flag=flag;
		
		if(flag==0)
		{
			this.gu=gu;
			this.theme=theme;
		}
		else if(flag==2)
		{
			this.theme=gu;
			this.subway=theme;
		}
		try {
			//연결된 소켓으로부터 대화를 나눌 스트림을 얻음
			bufferR = new BufferedReader(new InputStreamReader(client.getInputStream(),"MS949"));
			bufferW = new BufferedWriter(new OutputStreamWriter(client.getOutputStream(),"MS949"));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}	
	public PlaceThread(Socket client, Handler handler,int roomid,String ID,int num,String selectedplace,int flag) {
		this.selected=selectedplace;
		this.handler = handler;
		this.client = client;
		this.id=ID;
		this.roomid=roomid;
		this.num=num;
		this.flag=flag;
		
		try {
			//연결된 소켓으로부터 대화를 나눌 스트림을 얻음
			bufferR = new BufferedReader(new InputStreamReader(client.getInputStream(),"MS949"));
			bufferW = new BufferedWriter(new OutputStreamWriter(client.getOutputStream(),"MS949"));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}	
	
	public PlaceThread(Socket client, Handler handler,int category, int flag) {
		this.handler = handler;
		this.client = client;
		this.category=category;
		this.flag=flag;
		
		try {
			//연결된 소켓으로부터 대화를 나눌 스트림을 얻음
			bufferR = new BufferedReader(new InputStreamReader(client.getInputStream(),"MS949"));
			bufferW = new BufferedWriter(new OutputStreamWriter(client.getOutputStream(),"MS949"));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}//음식 카테고리 검색	
	
	public PlaceThread(Socket client, Handler handler,int roomid,String ID,String msg,int flag) {
		this.selected=msg;
		this.handler = handler;
		this.client = client;
		this.id=ID;
		this.roomid=roomid;
		this.flag=flag;
		
		try {
			//연결된 소켓으로부터 대화를 나눌 스트림을 얻음
			bufferR = new BufferedReader(new InputStreamReader(client.getInputStream(),"MS949"));
			bufferW = new BufferedWriter(new OutputStreamWriter(client.getOutputStream(),"MS949"));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}//음식저장	
	
	public void listen(){
		try{
			if(flag==0)
			{
				msg="14/"+Integer.toString(gu)+"/"+Integer.toString(theme)+"/";
				
				bufferW.write(msg+"\n");
				bufferW.flush();
	
				String info = bufferR.readLine();
				
				Message m = new Message();
				Bundle bundle = new Bundle();
				bundle.putString("info", info);
							
				m.setData(bundle);
				handler.sendMessage(m);
				//핸들러로 메인액티비티에 성공 여부를 보낸다
			}
			
			else if(flag==1)
			{
				msg="15/"+Integer.toString(roomid)+"/"+id+"/"+Integer.toString(num)+"/"+selected;
				
				bufferW.write(msg+"\n");
				bufferW.flush();
	
				String info = bufferR.readLine();
				
				Message m = new Message();
				Bundle bundle = new Bundle();
				bundle.putString("info", info);
							
				m.setData(bundle);
				handler.sendMessage(m);
				//핸들러로 메인액티비티에 성공 여부를 보낸다
			}
			
			else if(flag==2)
			{

				msg="16/"+Integer.toString(theme)+"/"+Integer.toString(subway)+"/";
				
				bufferW.write(msg+"\n");
				bufferW.flush();
	
				String info = bufferR.readLine();
				
				Message m = new Message();
				Bundle bundle = new Bundle();
				bundle.putString("info", info);
							
				m.setData(bundle);
				handler.sendMessage(m);
				//핸들러로 메인액티비티에 성공 여부를 보낸다
			}
			
			else if(flag==3)
			{

				msg="17/"+Integer.toString(roomid)+"/"+id+"/"+selected+"/";
				
				bufferW.write(msg+"\n");
				bufferW.flush();
	
				String info = bufferR.readLine();
				
				Message m = new Message();
				Bundle bundle = new Bundle();
				bundle.putString("info", info);
							
				m.setData(bundle);
				handler.sendMessage(m);
				//핸들러로 메인액티비티에 성공 여부를 보낸다
			}//음식저장
			else if(flag==4)
			{

				msg="21/"+Integer.toString(category);
				
				bufferW.write(msg+"\n");
				bufferW.flush();
	
				String info = bufferR.readLine();
				
				Message m = new Message();
				Bundle bundle = new Bundle();
				bundle.putString("info", info);
							
				m.setData(bundle);
				handler.sendMessage(m);
				//핸들러로 메인액티비티에 성공 여부를 보낸다
			}//음식 카테고리 검색
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		
		super.run();
		listen();
		
	}
	
}