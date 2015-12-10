/*
 * ģ������ ���� Thread
 * ģ�� �߰��� ģ�� id��ȸ, ģ�� ��� refresh
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
			
			//����� �������κ��� ��ȭ�� ���� ��Ʈ���� ����
			bufferR = new BufferedReader(new InputStreamReader(client.getInputStream(),"MS949"));
			bufferW = new BufferedWriter(new OutputStreamWriter(client.getOutputStream(),"MS949"));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}//ģ���߰��� ������	
	
	public FriendThread(Socket client, Handler handler, ArrayList<String> fidlist) {
		flag=2;
		this.handler = handler;
		this.fidlist=fidlist;
		try {
			this.client = client;
			
			//����� �������κ��� ��ȭ�� ���� ��Ʈ���� ����
			bufferR = new BufferedReader(new InputStreamReader(client.getInputStream(),"MS949"));
			bufferW = new BufferedWriter(new OutputStreamWriter(client.getOutputStream(),"MS949"));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}//refresh�� ������
	
	public void listen(){
		try{
			if(flag==1)
			{
				String msg="2/"+id;
				
				bufferW.write(msg+"\n");
				bufferW.flush();//���̵� ������ ������
				
				String resultNN = bufferR.readLine();//�����κ��� ��ȿ�� ���̵����� ���θ� �޴´�			
				String[] value=resultNN.split("[/]");
				int res=Integer.parseInt(value[0]);
				
				Message m = new Message();
				Bundle bundle = new Bundle();
				
				if(res==1)
				{
					bundle.putInt("result", res);
				}//���̵� ����
				else if(res==2)
				{
					bundle.putInt("result", res);
					bundle.putString("Nick",value[1]);
				}//���̵� �ְ� �г��� ����
				else if(res==3)
				{
					bundle.putInt("result", res);
				}//���̵� �ְ� �г��� ����
				m.setData(bundle);
				handler.sendMessage(m);
			//�ڵ鷯�� ���ξ�Ƽ��Ƽ�� ���� ���θ� ������
			}
			
			else if(flag==2){
				Message m = new Message();
				Bundle bundle = new Bundle();
				String msg="7/";
				for(int i=0;i<fidlist.size();i++){
					msg=msg+fidlist.get(i)+"/";
					}
					
					bufferW.write(msg+"\n");
					bufferW.flush();//���̵� ������ ������

					String resultNN = bufferR.readLine();//�����κ��� ��ȿ�� ���̵����� ���θ� �޴´�	
					String[] value=resultNN.split("[/]");
					if(Integer.parseInt(value[0])==1){
						bundle.putInt("result",5);
					}//���̵� ��ȭ�� �� ����		
					else if(Integer.parseInt(value[0])==2){						
						newfidnnlist=new ArrayList<String>();
						for(int i=1;i<value.length;i++){
							newfidnnlist.add(value[i]);
						}
						bundle.putStringArrayList("����ģ������Ʈ",newfidnnlist);
						bundle.putInt("result",4);
					}//���̵� ��ȭ�� �� ����
					m.setData(bundle);
					handler.sendMessage(m);
				//�ڵ鷯�� ���ξ�Ƽ��Ƽ�� ���� ���θ� ������
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