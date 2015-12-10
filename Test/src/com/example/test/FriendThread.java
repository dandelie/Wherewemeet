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
import java.util.ArrayList;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class FriendThread extends Thread{

	BufferedReader bufferR;
	BufferedWriter bufferW;
	Socket client;
	Handler handler;
	
	String id;
	int flag;
	
	ArrayList<String> fidlist;
	ArrayList<String> newfidnnlist;
	
	public FriendThread(){}
	
	public FriendThread(Socket client, Handler handler, String id) {
		flag=1;
		this.handler = handler;
		try {
			this.client = client;
			this.id=id;
			
			//연결된 소켓으로부터 대화를 나눌 스트림을 얻음
			bufferR = new BufferedReader(new InputStreamReader(client.getInputStream(),"MS949"));
			bufferW = new BufferedWriter(new OutputStreamWriter(client.getOutputStream(),"MS949"));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}//친구추가용 생성자	
	
	public FriendThread(Socket client, Handler handler, ArrayList<String> fidlist) {
		flag=2;
		this.handler = handler;
		this.fidlist=fidlist;
		try {
			this.client = client;
			
			//연결된 소켓으로부터 대화를 나눌 스트림을 얻음
			bufferR = new BufferedReader(new InputStreamReader(client.getInputStream(),"MS949"));
			bufferW = new BufferedWriter(new OutputStreamWriter(client.getOutputStream(),"MS949"));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}//refresh용 생성자
	
	public void listen(){
		try{
			if(flag==1)
			{
				String msg="2/"+id;
				
				bufferW.write(msg+"\n");
				bufferW.flush();//아이디 정보를 보내고
				
				String resultNN = bufferR.readLine();//서버로부터 유효한 아이디인지 여부를 받는다			
				String[] value=resultNN.split("[/]");
				int res=Integer.parseInt(value[0]);
				
				Message m = new Message();
				Bundle bundle = new Bundle();
				
				if(res==1)
				{
					bundle.putInt("result", res);
				}//아이디 없음
				else if(res==2)
				{
					bundle.putInt("result", res);
					bundle.putString("Nick",value[1]);
				}//아이디 있고 닉네임 있음
				else if(res==3)
				{
					bundle.putInt("result", res);
				}//아이디 있고 닉네임 없음
				m.setData(bundle);
				handler.sendMessage(m);
			//핸들러로 메인액티비티에 성공 여부를 보낸다
			}
			
			else if(flag==2){
				Message m = new Message();
				Bundle bundle = new Bundle();
				String msg="7/";
				for(int i=0;i<fidlist.size();i++){
					msg=msg+fidlist.get(i)+"/";
					}
					
					bufferW.write(msg+"\n");
					bufferW.flush();//아이디 정보를 보내고

					String resultNN = bufferR.readLine();//서버로부터 유효한 아이디인지 여부를 받는다	
					String[] value=resultNN.split("[/]");
					if(Integer.parseInt(value[0])==1){
						bundle.putInt("result",5);
					}//아이디 변화된 것 없음		
					else if(Integer.parseInt(value[0])==2){						
						newfidnnlist=new ArrayList<String>();
						for(int i=1;i<value.length;i++){
							newfidnnlist.add(value[i]);
						}
						bundle.putStringArrayList("없는친구리스트",newfidnnlist);
						bundle.putInt("result",4);
					}//아이디 변화된 거 있음
					m.setData(bundle);
					handler.sendMessage(m);
				//핸들러로 메인액티비티에 성공 여부를 보낸다
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