/*
 * 로그인 관련기능 thread
 * 회원가입 및 로그인
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


public class LoginThread extends Thread{
	

	BufferedReader bufferR;
	BufferedWriter bufferW;
	Socket client;
	
	Handler handler;
	String id,pw,nn,newpw,regID;
	int flag;

	public LoginThread(){}
	public LoginThread(Socket client, Handler handler, String id, String pw, String nn) {
		this.handler = handler;
		try {
			this.client = client;
			this.id=id;
			this.pw=pw;
			this.nn=nn;
			this.flag=0;//회원가입용
			
			//연결된 소켓으로부터 대화를 나눌 스트림을 얻음
			bufferR = new BufferedReader(new InputStreamReader(client.getInputStream(),"MS949"));
			bufferW = new BufferedWriter(new OutputStreamWriter(client.getOutputStream(),"MS949"));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}//회원가입용 생성자
	
	public LoginThread(Socket client, Handler handler, String id, String pw) {
		this.handler = handler;
		try {
			this.client = client;
			this.id=id;
			this.pw=pw;
			this.flag=1; //로그인용
			
			//연결된 소켓으로부터 대화를 나눌 스트림을 얻음
			bufferR = new BufferedReader(new InputStreamReader(client.getInputStream(),"MS949"));
			bufferW = new BufferedWriter(new OutputStreamWriter(client.getOutputStream(),"MS949"));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}//로그인 용 생성자
	
	public LoginThread(Socket client, Handler handler, String var1, String var2, int flag) {
		this.handler = handler;
		this.flag=flag;
		try {
			this.client = client;
			if(flag==2){
				id=var1;
				pw=var2;
			}//회원 탈퇴용
			
			else if(flag==4)
			{
				id=var1;
				regID=var2;
			}
			//연결된 소켓으로부터 대화를 나눌 스트림을 얻음
			bufferR = new BufferedReader(new InputStreamReader(client.getInputStream(),"MS949"));
			bufferW = new BufferedWriter(new OutputStreamWriter(client.getOutputStream(),"MS949"));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}//회원 정보 변경 생성자
	
	public LoginThread(Socket client, Handler handler, String var1, String var2, String var3, int flag) {
		this.handler = handler;
		this.flag=flag;
		try {
			this.client = client;
			if(flag==3){
				id=var1;
				pw=var2;
				newpw=var3;
			}//비번 변경용
			
			bufferR = new BufferedReader(new InputStreamReader(client.getInputStream(),"MS949"));
			bufferW = new BufferedWriter(new OutputStreamWriter(client.getOutputStream(),"MS949"));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}//닉네임 변경 생성자

	
	public void listen(){
		try{
			String msg="";
			if(flag==0){
				msg="0/"+id+"/"+pw+"/"+nn;
			}
			else if(flag==1){
				msg="1/"+id+"/"+pw;
			}
			else if(flag==2){
				msg="9/"+id+"/"+pw;
			}
			else if(flag==3){
				msg="10/"+id+"/"+pw+"/"+newpw;
			}
			
			else if(flag==4)
			{
				msg="19/"+id+"/"+regID;
			}
			
			bufferW.write(msg+"\n");
			bufferW.flush();//아이디 패스워드 혹은 닉네임 정보를 보내고
			
			String result = bufferR.readLine();//서버로부터 회원가입 성공 여부를 받는다
			int res=Integer.parseInt(result);
			
			Message m = new Message();
			Bundle bundle = new Bundle();
			bundle.putInt("result", res);
			m.setData(bundle);
			handler.sendMessage(m);
			//핸들러로 메인액티비티에 성공 여부를 보낸다
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		
		super.run();
		listen();
		
	}
	
}