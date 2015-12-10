/*
 * �α��� ���ñ�� thread
 * ȸ������ �� �α���
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
			this.flag=0;//ȸ�����Կ�
			
			//����� �������κ��� ��ȭ�� ���� ��Ʈ���� ����
			bufferR = new BufferedReader(new InputStreamReader(client.getInputStream(),"MS949"));
			bufferW = new BufferedWriter(new OutputStreamWriter(client.getOutputStream(),"MS949"));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}//ȸ�����Կ� ������
	
	public LoginThread(Socket client, Handler handler, String id, String pw) {
		this.handler = handler;
		try {
			this.client = client;
			this.id=id;
			this.pw=pw;
			this.flag=1; //�α��ο�
			
			//����� �������κ��� ��ȭ�� ���� ��Ʈ���� ����
			bufferR = new BufferedReader(new InputStreamReader(client.getInputStream(),"MS949"));
			bufferW = new BufferedWriter(new OutputStreamWriter(client.getOutputStream(),"MS949"));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}//�α��� �� ������
	
	public LoginThread(Socket client, Handler handler, String var1, String var2, int flag) {
		this.handler = handler;
		this.flag=flag;
		try {
			this.client = client;
			if(flag==2){
				id=var1;
				pw=var2;
			}//ȸ�� Ż���
			
			else if(flag==4)
			{
				id=var1;
				regID=var2;
			}
			//����� �������κ��� ��ȭ�� ���� ��Ʈ���� ����
			bufferR = new BufferedReader(new InputStreamReader(client.getInputStream(),"MS949"));
			bufferW = new BufferedWriter(new OutputStreamWriter(client.getOutputStream(),"MS949"));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}//ȸ�� ���� ���� ������
	
	public LoginThread(Socket client, Handler handler, String var1, String var2, String var3, int flag) {
		this.handler = handler;
		this.flag=flag;
		try {
			this.client = client;
			if(flag==3){
				id=var1;
				pw=var2;
				newpw=var3;
			}//��� �����
			
			bufferR = new BufferedReader(new InputStreamReader(client.getInputStream(),"MS949"));
			bufferW = new BufferedWriter(new OutputStreamWriter(client.getOutputStream(),"MS949"));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}//�г��� ���� ������

	
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
			bufferW.flush();//���̵� �н����� Ȥ�� �г��� ������ ������
			
			String result = bufferR.readLine();//�����κ��� ȸ������ ���� ���θ� �޴´�
			int res=Integer.parseInt(result);
			
			Message m = new Message();
			Bundle bundle = new Bundle();
			bundle.putInt("result", res);
			m.setData(bundle);
			handler.sendMessage(m);
			//�ڵ鷯�� ���ξ�Ƽ��Ƽ�� ���� ���θ� ������
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		
		super.run();
		listen();
		
	}
	
}