/*
 * 방 관련기능 thread
 * 방 생성
 */
package com.example.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;


public class RoomThread extends Thread{
	
	BufferedReader bufferR;
	BufferedWriter bufferW;
	
	Socket client;
	
	Handler handler;
	String roomname;//방이름
	ArrayList<String> memberlist=new ArrayList<String>();//방멤버 리스트
	
	int num;//멤버 수
	String id,placedefault;
	int flag;
	int roomid,daycheck,placecheck;
	int dayreturnvalue;
	int selectgu;
	String day;
	int starttime;
	//연결된 소켓으로부터 대화를 나눌 스트림을 얻음
	
	public RoomThread(){}
	
	public RoomThread(Socket client, Handler handler, String roomname, ArrayList<String> mlist,int daycheck,int placecheck,String placedefault,int flag) {
		this.handler = handler;
		try {
			this.client = client;
			this.roomname=roomname;
			memberlist=mlist;
			num=memberlist.size();
			this.placedefault=placedefault;
			this.daycheck=daycheck;
			this.placecheck=placecheck;
			
			this.flag=flag;
			
			bufferR = new BufferedReader(new InputStreamReader(client.getInputStream(),"MS949"));
			bufferW = new BufferedWriter(new OutputStreamWriter(client.getOutputStream(),"MS949"));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}//방 생성용 생성자 - 비정기 모임 & 장소 정해짐
	
	public RoomThread(Socket client, Handler handler, String roomname, ArrayList<String> mlist,int daycheck,int placecheck,String day,int starttime,int dayreturnval, String placedefault,int flag) {
		this.handler = handler;
		try {
			this.client = client;
			this.roomname=roomname;
			memberlist=mlist;
			num=memberlist.size();
			this.placedefault=placedefault;
			this.daycheck=daycheck;
			this.placecheck=placecheck;
			this.day=day;
			this.starttime=starttime;
			this.dayreturnvalue=dayreturnval;
			this.flag=flag;
			
			bufferR = new BufferedReader(new InputStreamReader(client.getInputStream(),"MS949"));
			bufferW = new BufferedWriter(new OutputStreamWriter(client.getOutputStream(),"MS949"));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}//방 생성용 생성자 - 정기 모임 & 장소 정해짐	
	
	public RoomThread(Socket client, Handler handler, String roomname, ArrayList<String> mlist,int daycheck,int placecheck,int selectgu,int flag) {
		this.handler = handler;
		try {
			this.client = client;
			this.roomname=roomname;
			memberlist=mlist;
			num=memberlist.size();
			this.selectgu=selectgu;		
			this.daycheck=daycheck;
			this.placecheck=placecheck;
			
			this.flag=flag;
			
			bufferR = new BufferedReader(new InputStreamReader(client.getInputStream(),"MS949"));
			bufferW = new BufferedWriter(new OutputStreamWriter(client.getOutputStream(),"MS949"));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}//방 생성용 생성자 - 비정기 모임 & 구 정해짐 
	
	public RoomThread(Socket client, Handler handler, String roomname, ArrayList<String> mlist,int daycheck,int placecheck,String day,int starttime,int dayreturnvalue,int selectgu,int flag) {
		this.handler = handler;
		try {
			this.client = client;
			this.roomname=roomname;
			memberlist=mlist;
			num=memberlist.size();
			this.selectgu=selectgu;
			this.daycheck=daycheck;
			this.placecheck=placecheck;
			this.day=day;
			this.starttime=starttime;
			this.dayreturnvalue=dayreturnvalue;
			this.flag=flag;
			
			bufferR = new BufferedReader(new InputStreamReader(client.getInputStream(),"MS949"));
			bufferW = new BufferedWriter(new OutputStreamWriter(client.getOutputStream(),"MS949"));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}//방 생성용 생성자 - 정기 모임 & 구 정해짐	
	
	public RoomThread(Socket client, Handler handler, String roomname, ArrayList<String> mlist,int daycheck,int placecheck,int flag) {
		this.handler = handler;
		try {
			this.client = client;
			this.roomname=roomname;
			memberlist=mlist;
			num=memberlist.size();
			this.daycheck=daycheck;
			this.placecheck=placecheck;
			
			this.flag=flag;
			
			bufferR = new BufferedReader(new InputStreamReader(client.getInputStream(),"MS949"));
			bufferW = new BufferedWriter(new OutputStreamWriter(client.getOutputStream(),"MS949"));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}//방 생성용 생성자 - 비정기 모임 & 장소 안정해짐
	
	public RoomThread(Socket client, Handler handler, String roomname, ArrayList<String> mlist,int daycheck,int placecheck,String day,int starttime,int dayreturnval,int flag) {
		this.handler = handler;
		try {
			this.client = client;
			this.roomname=roomname;
			memberlist=mlist;
			num=memberlist.size();
			this.day=day;
			this.starttime=starttime;
			this.dayreturnvalue=dayreturnval;

			this.daycheck=daycheck;
			this.placecheck=placecheck;
			
			this.flag=flag;
			
			bufferR = new BufferedReader(new InputStreamReader(client.getInputStream(),"MS949"));
			bufferW = new BufferedWriter(new OutputStreamWriter(client.getOutputStream(),"MS949"));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}//방 생성용 생성자 - 정기모임 & 장소안정해짐
	
	public RoomThread(Socket client,Handler handler,String ID,int flag)
	{
		this.handler=handler;
		try
		{
			this.client=client;
			this.flag=flag;
			this.id=ID;
		
			bufferR = new BufferedReader(new InputStreamReader(client.getInputStream(),"MS949"));
			bufferW = new BufferedWriter(new OutputStreamWriter(client.getOutputStream(),"MS949"));
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}//특정 사용자가 속한 방 조회 생성자
	
	public RoomThread(Socket client,Handler handler,int roomid,int flag)
	{
		this.handler=handler;
		this.client=client;
		this.flag=flag;
		this.roomid=roomid;
		try
		{
			bufferR = new BufferedReader(new InputStreamReader(client.getInputStream(),"MS949"));
			bufferW = new BufferedWriter(new OutputStreamWriter(client.getOutputStream(),"MS949"));
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}//방정보 조회 생성자
	
	public void listen(){
		try{
			String msg="";
			if(flag==0)
			{
				msg="3/"+roomname+"/"+num+"/";
				for(int i=0;i<num;i++){
					msg=msg+memberlist.get(i)+"/";
				}
				
				msg=msg+Integer.toString(daycheck)+"/"+Integer.toString(placecheck)+"/"+placedefault+"/";
				bufferW.write(msg+"\n");
				bufferW.flush();//방이름,방장,멤버수,멤버아이디,기타 정보를 보내고
				
				String info = bufferR.readLine();
				String[] value=info.split("[/]");
				int res=Integer.parseInt(value[0]);
				int roomid=Integer.parseInt(value[1]);//서버로부터 방 생성 성공 여부와 방 id를 받는다
				
				Message m = new Message();
				Bundle bundle = new Bundle();
				bundle.putInt("result", res);
				bundle.putInt("roomid", roomid);
				m.setData(bundle);
				handler.sendMessage(m);
			}//방 생성 일시 - 비정기 모임 & 장소 정해짐
			
			else if(flag==1) 
			{
				msg="4/"+id;
				
				bufferW.write(msg+"\n");
				bufferW.flush();


				String info = bufferR.readLine();
				
				Message m = new Message();
				Bundle bundle = new Bundle();
				bundle.putString("info", info);
							
				m.setData(bundle);
				handler.sendMessage(m);
			}//아이디 조회해서 방 목록 보여주기
			
			else if(flag==2)
			{
				msg="5/"+roomid;
				
				bufferW.write(msg+"\n");
				bufferW.flush();
				
				String info=bufferR.readLine();
				
				Message m=new Message();
				Bundle bundle=new Bundle();
				bundle.putString("info", info);
				
				m.setData(bundle);
				handler.sendMessage(m);
			}//방 id로 방정보 검색
			
			else if(flag==3){
				msg="3/"+roomname+"/"+num+"/";
				for(int i=0;i<num;i++){
					msg=msg+memberlist.get(i)+"/";
				}
				msg=msg+Integer.toString(daycheck)+"/"+Integer.toString(placecheck)+"/"+day+"/"+starttime+"/"+dayreturnvalue+"/"+placedefault;
				bufferW.write(msg+"\n");
				bufferW.flush();//방이름,방장,멤버수,멤버아이디,기타 정보를 보내고
				
				String info = bufferR.readLine();
				String[] value=info.split("[/]");
				int res=Integer.parseInt(value[0]);
				int roomid=Integer.parseInt(value[1]);//서버로부터 방 생성 성공 여부와 방 id를 받는다
				
				Message m = new Message();
				Bundle bundle = new Bundle();
				bundle.putInt("result", res);
				bundle.putInt("roomid", roomid);
				m.setData(bundle);
				handler.sendMessage(m);
			}//방 생성 일시 - 정기 모임 & 장소 정해짐	
			else if(flag==4)
			{
				msg="3/"+roomname+"/"+num+"/";
				for(int i=0;i<num;i++){
					msg=msg+memberlist.get(i)+"/";
				}
				
				msg=msg+Integer.toString(daycheck)+"/"+Integer.toString(placecheck)+"/"+selectgu+"/";
				bufferW.write(msg+"\n");
				bufferW.flush();//방이름,방장,멤버수,멤버아이디,기타 정보를 보내고
				
				String info = bufferR.readLine();
				String[] value=info.split("[/]");
				int res=Integer.parseInt(value[0]);
				int roomid=Integer.parseInt(value[1]);//서버로부터 방 생성 성공 여부와 방 id를 받는다
				
				Message m = new Message();
				Bundle bundle = new Bundle();
				bundle.putInt("result", res);
				bundle.putInt("roomid", roomid);
				m.setData(bundle);
				handler.sendMessage(m);
			}//방 생성 일시 - 비정기 모임 & 구 정해짐
			else if(flag==5){
				msg="3/"+roomname+"/"+num+"/";
				for(int i=0;i<num;i++){
					msg=msg+memberlist.get(i)+"/";
				}
				msg=msg+Integer.toString(daycheck)+"/"+Integer.toString(placecheck)+"/"+day+"/"+starttime+"/"+dayreturnvalue+"/"+selectgu;
				bufferW.write(msg+"\n");
				bufferW.flush();//방이름,방장,멤버수,멤버아이디,기타 정보를 보내고
				
				String info = bufferR.readLine();
				String[] value=info.split("[/]");
				int res=Integer.parseInt(value[0]);
				int roomid=Integer.parseInt(value[1]);//서버로부터 방 생성 성공 여부와 방 id를 받는다
				
				Message m = new Message();
				Bundle bundle = new Bundle();
				bundle.putInt("result", res);
				bundle.putInt("roomid", roomid);
				m.setData(bundle);
				handler.sendMessage(m);
			}//방 생성 일시 - 정기 모임 & 구 정해짐	
			else if(flag==6)
			{
				msg="3/"+roomname+"/"+num+"/";
				for(int i=0;i<num;i++){
					msg=msg+memberlist.get(i)+"/";
				}
				
				msg=msg+Integer.toString(daycheck)+"/"+Integer.toString(placecheck);
				bufferW.write(msg+"\n");
				bufferW.flush();//방이름,방장,멤버수,멤버아이디,기타 정보를 보내고
				
				String info = bufferR.readLine();
				String[] value=info.split("[/]");
				int res=Integer.parseInt(value[0]);
				int roomid=Integer.parseInt(value[1]);//서버로부터 방 생성 성공 여부와 방 id를 받는다
				
				Message m = new Message();
				Bundle bundle = new Bundle();
				bundle.putInt("result", res);
				bundle.putInt("roomid", roomid);
				m.setData(bundle);
				handler.sendMessage(m);
			}//방 생성 일시 - 비정기 모임 & 장소 안정해짐
			else if(flag==7){
				msg="3/"+roomname+"/"+num+"/";
				for(int i=0;i<num;i++){
					msg=msg+memberlist.get(i)+"/";
				}
				msg=msg+Integer.toString(daycheck)+"/"+Integer.toString(placecheck)+"/"+day+"/"+starttime+"/"+dayreturnvalue+"/";
				bufferW.write(msg+"\n");
				bufferW.flush();//방이름,방장,멤버수,멤버아이디,기타 정보를 보내고
				
				String info = bufferR.readLine();
				String[] value=info.split("[/]");
				int res=Integer.parseInt(value[0]);
				int roomid=Integer.parseInt(value[1]);//서버로부터 방 생성 성공 여부와 방 id를 받는다
				
				Message m = new Message();
				Bundle bundle = new Bundle();
				bundle.putInt("result", res);
				bundle.putInt("roomid", roomid);
				m.setData(bundle);
				handler.sendMessage(m);
			}//방 생성 일시 - 정기 모임 & 장소 안정해짐
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		
		super.run();
		listen();
		
	}
	
}