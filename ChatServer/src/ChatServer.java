import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.io.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;

public class ChatServer {	
	
	//�������� - Ŭ���̾�Ʈ�� ������ �޾Ƶ��̴� Ŭ����
	int port=8888;
	ServerSocket server;
	int sum2=0;	
	//���� Ŭ���̾�Ʈ ���� ����

	String fileName="DBConnect.txt";//DB ���� ���� ����ִ� ���� ����
	Scanner inputStream= null;
	String url="";
	
	public ChatServer() {				
		try{
			inputStream=new Scanner(new File(fileName));
		}//���� ����
		catch(FileNotFoundException e){
			System.out.println("DBConnect File open Error!\n");
			System.exit(1);
		}//DBConnect���� ������		
		while(inputStream.hasNextLine()){
			url=inputStream.nextLine();
		}//���� �а� url ����
		inputStream.close();//���� ���� ����
		
		try {
			//������ü ����
			server = new ServerSocket(port);
			while(true){
				//�����ڰ� �����ϴ��� Ȯ��
				//�����ڰ� ���������� ���,�������¿� �ִ�.
				Socket client = server.accept();
				InetAddress inet = client.getInetAddress();
				String ip = inet.getHostAddress();
				System.out.println(ip+"-������ �߰�");
				
				//��ȭ�� ������ ���� �� ����
				ServerThread serverThread = new ServerThread(client);
				System.out.println("start");
				serverThread.start();				
		}
		} 
		catch (IOException e) {
			e.printStackTrace();
		}//���� Ȯ��
		}
	

public class ServerThread extends Thread{
	Socket client;
	BufferedReader buffer;
	BufferedWriter bufferWriter;	
	
	Connection conn=null;	
	Statement stmt=null;
	PreparedStatement pstm=null;
	PreparedStatement pstm2=null;
	String query, subquery;
	ResultSet rs=null;
	//DB���� ����
	
	public ServerThread(Socket socket) {
		this.client = socket;
		
		try {
			buffer = new BufferedReader(new InputStreamReader((client.getInputStream())));
			bufferWriter = new BufferedWriter(new OutputStreamWriter((client.getOutputStream())));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void run() {	
		try{	
			try{								
				Class.forName("com.mysql.jdbc.Driver");	
				//connection ����				
				conn=DriverManager.getConnection(url);
				stmt=conn.createStatement();
				
				String msg = buffer.readLine();
				String[] value=msg.split("[/]");
				
				if(Integer.parseInt(value[0])==0){
					String ID=value[1];
					String PW=value[2];
					String NN=value[3];
					// Ŭ���̾�Ʈ�κ��� ���̵�� �н����� �г��� ���� �޾ƿ���
					System.out.println(NN+"\n");
					conn.setAutoCommit(false);//DB�۾�����
					
					subquery="SELECT * FROM Login WHERE ID='"+ID+"'";
					//�̹� �����ϴ� ID���� Ȯ��
								
					rs=stmt.executeQuery(subquery);
					
					if(rs.next()){
						System.out.println("�̹� �����ϴ� ���̵� �Դϴ�.");
						
						int result=1;
						bufferWriter.write(Integer.toString(result)+"\n");
						bufferWriter.flush();//Ŭ���̾�Ʈ�� ��� ������
						
					}
					else{
						query="INSERT INTO Login(ID,PW,name) VALUES(?,?,?)";
						pstm=conn.prepareStatement(query);
						pstm.setString(1,ID);
						pstm.setString(2,PW);
						pstm.setString(3,NN);
						pstm.executeUpdate();
						pstm.close();

						System.out.println("Insert finish!");
						int result=2;
						bufferWriter.write(Integer.toString(result)+"\n");
						bufferWriter.flush();//Ŭ���̾�Ʈ�� ��� ������
					}
					rs.close();
					conn.commit();//DB �۾� �Ϸ�
				}//ȸ������ ���
				
				else if(Integer.parseInt(value[0])==1){
					String ID=value[1];
					String PW=value[2];
					// Ŭ���̾�Ʈ�κ��� ���̵�� �н����� ���� �޾ƿ���
					conn.setAutoCommit(false);//DB�۾�����
					subquery="SELECT * FROM Login WHERE ID='"+ID+"' AND PW='"+PW+"'";
					//ID�� PW Ȯ��				
					rs=stmt.executeQuery(subquery);
					
					if(rs.next()){						
						System.out.println("�α��� �Ϸ�.");
						int result=3;
						bufferWriter.write(Integer.toString(result)+"\n");
						bufferWriter.flush();//Ŭ���̾�Ʈ�� ��� ������						
					}//�α��� �Ϸ�
					else{						
						System.out.println("�α��� ����");
						int result=4;
						bufferWriter.write(Integer.toString(result)+"\n");
						bufferWriter.flush();//Ŭ���̾�Ʈ�� ��� ������
					}//�α��� ������ ���� ����(ID,PW ����)	
					rs.close();
					conn.commit();//DB �۾� �Ϸ�					
				}//�α��� ���					
				
				else if(Integer.parseInt(value[0])==2){
					String ID=value[1];
					// Ŭ���̾�Ʈ�κ��� ���̵����� �޾ƿ���
					conn.setAutoCommit(false);//DB�۾�����
					subquery="SELECT * FROM Login WHERE ID='"+ID+"'";
					//ID ��ȸ			
					rs=stmt.executeQuery(subquery);
					
					if(rs.next()){						
						System.out.println("ID ����");
						rs.close();
						query="SELECT Name FROM Login WHERE ID='"+ID+"'";
						rs=stmt.executeQuery(query);
						if(rs.next()){
							String NN=rs.getString(1);//�г��� ��������
							System.out.print(NN+"\n");
							int result=2;
							bufferWriter.write(Integer.toString(result)+"/"+NN+"\n");
							rs.close();
						}//�г����� ������
						else{
							int result=3;
							bufferWriter.write(Integer.toString(result)+"\n");
						}//�г����� ������						
						bufferWriter.flush();//Ŭ���̾�Ʈ�� ��� ������	
					}//���̵� ����
					else{						
						System.out.println("ID ����");
						int result=1;
						bufferWriter.write(Integer.toString(result)+"\n");
						bufferWriter.flush();//Ŭ���̾�Ʈ�� ��� ������
					}//���̵� ����				
					conn.commit();//DB �۾� �Ϸ�					
				}//ģ��ã�� ��� �� id�˻� ���
				
				else if(Integer.parseInt(value[0])==3)
				{
					String roomname=value[1];
					int num=Integer.parseInt(value[2]);
					ArrayList<String> mlist=new ArrayList<String>();
					String place;
					String day;
					int starttime;
					int dayreturnvalue;
					int selectgu;
					
					for(int i=3;i<num+3;i++){
						mlist.add(value[i]);
					}//�� ����� �Է�
					
					int daycheck=Integer.parseInt(value[num+3]);
					int placecheck=Integer.parseInt(value[num+4]);
					
					if(daycheck==1){
						day=value[num+5];
						starttime=Integer.parseInt(value[num+6]);
						dayreturnvalue=Integer.parseInt(value[num+7]);
						
						if(placecheck==0){							
							place=value[num+8];
							
							conn.setAutoCommit(false);//DB�۾�����

							query="INSERT INTO Room(Name,Number,Daycheck,Daystart,Daystarttime,Dayreturnvalue,Placecheck,Placedefault) VALUES(?,?,?,?,?,?,?,?)";//�����
							pstm=conn.prepareStatement(query);
							pstm.setString(1,roomname);
							pstm.setInt(2,num);
							pstm.setInt(3,daycheck);
							pstm.setString(4,day);
							pstm.setInt(5,starttime);
							pstm.setInt(6,dayreturnvalue);
							pstm.setInt(7,placecheck);
							pstm.setString(8,place);
							pstm.executeUpdate();
							pstm.close();
							
						}//��� ����
						else if(placecheck==1||placecheck==2){
							selectgu=Integer.parseInt(value[num+8]);	

							conn.setAutoCommit(false);//DB�۾�����

							query="INSERT INTO Room(Name,Number,Daycheck,Daystart,Daystarttime,Dayreturnvalue,Placecheck,Placerangedefault) VALUES(?,?,?,?,?,?,?,?)";//�����
							pstm=conn.prepareStatement(query);
							pstm.setString(1,roomname);
							pstm.setInt(2,num);
							pstm.setInt(3,daycheck);
							pstm.setString(4,day);
							pstm.setInt(5,starttime);							
							pstm.setInt(6,dayreturnvalue);
							pstm.setInt(7,placecheck);
							pstm.setInt(8,selectgu);
							pstm.executeUpdate();
							pstm.close();
						}//���� ��ó
						else if(placecheck==3||placecheck==4){
							
							conn.setAutoCommit(false);//DB�۾�����

							query="INSERT INTO Room(Name,Number,Daycheck,Daystart,Daystarttime,Dayreturnvalue,Placecheck) VALUES(?,?,?,?,?,?,?)";//�����
							pstm=conn.prepareStatement(query);
							pstm.setString(1,roomname);
							pstm.setInt(2,num);
							pstm.setInt(3,daycheck);
							pstm.setString(4,day);
							pstm.setInt(5,starttime);
							pstm.setInt(6,dayreturnvalue);
							pstm.setInt(7,placecheck);
							pstm.executeUpdate();
							pstm.close();
						}//�����
					}//��������� ��
					else {
						if(placecheck==0){
							place=value[num+5];	

							conn.setAutoCommit(false);//DB�۾�����

							query="INSERT INTO Room(Name,Number,Daycheck,Placecheck,Placedefault) VALUES(?,?,?,?,?)";//�����
							pstm=conn.prepareStatement(query);
							pstm.setString(1,roomname);
							pstm.setInt(2,num);
							pstm.setInt(3,daycheck);
							pstm.setInt(4,placecheck);
							pstm.setString(5,place);
							pstm.executeUpdate();
							pstm.close();
							
						}//��� ����
						else if(placecheck==1||placecheck==2){
							selectgu=Integer.parseInt(value[num+5]);	

							conn.setAutoCommit(false);//DB�۾�����

							query="INSERT INTO Room(Name,Number,Daycheck,Placecheck,Placerangedefault) VALUES(?,?,?,?,?)";//�����
							pstm=conn.prepareStatement(query);
							pstm.setString(1,roomname);
							pstm.setInt(2,num);
							pstm.setInt(3,daycheck);
							pstm.setInt(4,placecheck);
							pstm.setInt(5,selectgu);
							pstm.executeUpdate();
							pstm.close();
						}//���� ��ó

						else if(placecheck==3||placecheck==4){	

							conn.setAutoCommit(false);//DB�۾�����

							query="INSERT INTO Room(Name,Number,Daycheck,Placecheck) VALUES(?,?,?,?)";//�����
							pstm=conn.prepareStatement(query);
							pstm.setString(1,roomname);
							pstm.setInt(2,num);
							pstm.setInt(3,daycheck);
							pstm.setInt(4,placecheck);
							pstm.executeUpdate();
							pstm.close();
						}//�����
					}//����������Ͻ�
										
					int roomid=-1;
					query="SELECT LAST_INSERT_ID();";//����̵� ��������				
					rs=stmt.executeQuery(query);
					if(rs.next()){
						roomid=rs.getInt(1);
						rs.close();
					}
					
					for(int j=0;j<mlist.size();j++)
					{
						subquery="SELECT * FROM Login WHERE ID='"+mlist.get(j)+"'";//���̵� ��ȿ �˻�					
						rs=stmt.executeQuery(subquery);
						
						if(rs.next())
						{
							query="INSERT INTO Roommember VALUES("+roomid+",'"+mlist.get(j)+"',TRUE)";			
							stmt.executeUpdate(query);
							rs.close();
							
							if(j!=0)  //���� �ƴ� �ٸ� �� ������� �� �����ƴٰ� �˸�.
							{
								System.out.println("�ٸ� ģ�� ���̵� : "+mlist.get(j));
								query="SELECT regID FROM Login WHERE ID='"+mlist.get(j)+"'";
								rs=stmt.executeQuery(query);
								
								if(rs.next())
								{
									gcmserver gcm=new gcmserver(rs.getString(1),0);
									gcm.sendMessage();
								}
							}
							
						}//�� ��� ���
					}
					if(rs!=null){
						rs.close();
					}
					
					int result=1;
					bufferWriter.write(Integer.toString(result)+"/"+Integer.toString(roomid)+"\n");
					bufferWriter.flush();//Ŭ���̾�Ʈ�� ��� ������	
					conn.commit();//DB�۾� ��
				}//�� ��� �� �� ���� ���
				
				
				else if(Integer.parseInt(value[0])==4)
				{
					String ID=value[1];					
					//���̵� �޾ƿ԰�, ���� ��񿡼� �� ���̵��� ������ ã�Ƽ� Ŭ���̾�Ʈ�� �����ָ� ��.
					
					ArrayList<Integer> roomlist=new ArrayList<Integer>();
					ArrayList<String> roomnamelist=new ArrayList<String>();
					
					conn.setAutoCommit(false);//DB�۾�����
					
					
					query="SELECT RoomID FROM Roommember WHERE ID='"+ID+"'";
					rs=stmt.executeQuery(query);	
					
					while(rs.next()){	
						roomlist.add(rs.getInt(1));
					}//�� id ����Ʈ ��������
					if(rs!=null){
						rs.close();
					}
					
					int roomsize=roomlist.size();
					String info;
					if(roomsize!=0)
					{
						query="SELECT Name FROM Room WHERE RoomID=?";
						pstm=conn.prepareStatement(query);
						for(int i=0;i<roomsize;i++){
							pstm.setInt(1,roomlist.get(i));
							rs=pstm.executeQuery();
							if(rs.next()){
								roomnamelist.add(rs.getString(1));
								rs.close();
							}//���̸� ������ �ִٸ�
							else{
								roomnamelist.add("�� ���� ����");
							}//���̸� ������ ���ٸ�
						}					
						pstm.close(); 
						
						info="1/"+Integer.toString(roomsize)+"/";
						for(int i=0;i<roomsize;i++){
							info=info+Integer.toString(roomlist.get(i))+"/";
						}
						for(int i=0;i<roomsize;i++){
							info=info+roomnamelist.get(i)+"/";
						}
					}//����ڰ� ���� ���� �ִ� ���
					else
					{
						info="3/";
					}//����ڰ� ���� ���� ���� ���

					query="SELECT regID FROM Login WHERE ID='sungkyu'";
					rs=stmt.executeQuery(query);
					
					if(rs.next())  //���� �ƴ� �ٸ� �� ������� �� �����ƴٰ� �˸�.
					{
							gcmserver gcm=new gcmserver(rs.getString(1),0);
							gcm.sendMessage();
					}
					
					bufferWriter.write(info+"\n");
					bufferWriter.flush();//Ŭ���̾�Ʈ�� ��� ������	
					conn.commit();//DB�۾� ��					
				}//���̵� ��ȸ�ؼ� �� ��� �����ֱ�
				
				else if(Integer.parseInt(value[0])==5)
				{
					int roomid=Integer.parseInt(value[1]);
					
					ArrayList<String> memberlist=new ArrayList<String>();
					
					conn.setAutoCommit(false);//DB�۾�����
					
					query="SELECT ID FROM Roommember WHERE RoomID='"+roomid+"'";
					rs=stmt.executeQuery(query);
					
					while(rs.next()){	
						memberlist.add(rs.getString(1));
					}//�濡 ���� ��� ���̵� ����Ʈ ��������

					if(rs!=null){
						rs.close();
					}
					
					int placecheck=0,gu=0;
					
					query="SELECT Placecheck FROM Room WHERE RoomID='"+roomid+"'";
					rs=stmt.executeQuery(query);
					
					if(rs.next()){
						placecheck=rs.getInt(1);
					}//���� ��� ���� ��������

					if(placecheck==1 || placecheck==2)
					{
						query="SELECT Placerangedefault FROM Room WHERE RoomID='"+roomid+"'";
						rs=stmt.executeQuery(query);
						if(rs.next()){
							gu=rs.getInt(1);
						}//�濡�� ������ �� ���� ��������
					}
					else
					{
						gu=0;
					}
					
					if(rs!=null){
						rs.close();
					}
					
					int num=memberlist.size();//�����
					
					query="SELECT Finished FROM Meet WHERE RoomID='"+roomid+"'";
					rs=stmt.executeQuery(query);
					
					int flag=0;  //����� ������
					while(rs.next()){
						if(rs.getBoolean(1)==false)
							flag=1;   //�������� ����� ���� ��
					}//���� �濡 �ִ� ��� ���� ã��

					if(rs!=null){
						rs.close();
					}

					String info="2/"+Integer.toString(flag)+"/"+Integer.toString(placecheck)+"/"+Integer.toString(gu)+"/"+num+"/";
					
					System.out.println(info);
					for(int i=0;i<num;i++)
					{
						info=info+memberlist.get(i)+"/";
					}
					
					bufferWriter.write(info+"\n");
										
					bufferWriter.flush();//Ŭ���̾�Ʈ�� ��� ������	
					conn.commit();//DB�۾� ��		
				}//��id�� ������ ��ȸ
				
				else if(Integer.parseInt(value[0])==6)
				{					
					int roomid=Integer.parseInt(value[1]);
					String startdate=value[2];
					int range=Integer.parseInt(value[3]);
					String ID=value[4];//�θ����
					int newmeetid=1;

					
					conn.setAutoCommit(false);//DB�۾�����
					
					query="SELECT Max(MeetID) FROM Meet WHERE RoomID="+roomid;
					rs=stmt.executeQuery(query);	
					int maxmeetid=-1;
					if(rs.next()){
						maxmeetid=rs.getInt(1);
						newmeetid=maxmeetid+1;
						rs.close();
					}//�ִ밪 ������
										
					query="INSERT INTO Meet(RoomID,MeetId,StartDate,DateRange) VALUES(?,?,?,?)";//��ӻ���
					pstm=conn.prepareStatement(query);					
					pstm.setInt(1,roomid);
					pstm.setInt(2,newmeetid);
					pstm.setString(3,startdate);
					pstm.setInt(4,range);
					pstm.executeUpdate();
					pstm.close();
					
					query="select id from roommember where roomid="+roomid;
					rs=stmt.executeQuery(query);
					
					ResultSet rs2=null;
					
					query="INSERT INTO MeetIDChoice(roomid,meetid,id,selected,selectvalue) VALUES(?,?,?,?,?)";
					pstm=conn.prepareStatement(query);
					
					String query2="SELECT regID FROM Login WHERE ID=?";
					pstm2=conn.prepareStatement(query2);
					
					while(rs.next())
					{
						String id=rs.getString(1);
						System.out.println(id);
						pstm.setInt(1, roomid);
						pstm.setInt(2,newmeetid);
						pstm.setString(3, id);
						pstm.setBoolean(4, false);
						pstm.setString(5, null);
						
						pstm.executeUpdate();
						
						
						if(id.equals(ID)==false)    //���� �ƴ� �ٸ� ģ���鿡�� ����� �����Ǿ��ٰ� ����.
						{
							System.out.println("�ٸ� ģ�� ���̵� : "+id);
							pstm2.setString(1,id);
							rs2=pstm2.executeQuery();
							
							if(rs2.next())
							{
								gcmserver gcm=new gcmserver(rs2.getString(1),2);
								gcm.sendMessage();
							}
							
						}
					}// ���� db�� ����.
					
					pstm.close();
					pstm2.close();
					if(rs2!=null){
						rs2.close();
					}
					if(rs!=null){
						rs.close();
					}
					
					bufferWriter.write("1/\n");
					bufferWriter.flush();//Ŭ���̾�Ʈ�� ��� ������	
					conn.commit();//DB�۾� ��	
				}//��� ������.
				
				else if(Integer.parseInt(value[0])==7)
				{				
					conn.setAutoCommit(false);//DB�۾�����
					String finfo="";
					int flag=1;
					String ID;
					for(int i=1;i<value.length;i++){
						ID=value[i];
						subquery="SELECT * FROM Login WHERE ID='"+ID+"'";
						//ID ��ȸ
						rs=stmt.executeQuery(subquery);						
						if(rs.next()){							
							System.out.println("ID ����");
							rs.close();
						}//���̵� ����
						else{
							flag=2;
							System.out.println("ID ����");
							finfo=finfo+ID+"/";							
						}//���̵� ����			
					}
					
					bufferWriter.write(Integer.toString(flag)+"/"+finfo+"\n");
					bufferWriter.flush();//Ŭ���̾�Ʈ�� ��� ������
					conn.commit();//DB �۾� �Ϸ�				
				}//ģ�� ����Ʈ ���Ž�
				
				else if(Integer.parseInt(value[0])==8)
				{
					int roomid=Integer.parseInt(value[1]);
					int choice=0;
					String day="";
					int starttime=-1;
					String place="";
					String m="";
					String startdate="";
					int range=0;
					conn.setAutoCommit(false);//DB�۾�����
				
					query="SELECT Choice FROM Meet WHERE RoomID='"+roomid+"'"+" AND Finished=0";
					rs=stmt.executeQuery(query);	
					
					if(rs.next()){
						choice=rs.getInt(1);
						rs.close();
					}//������������ ����� ������ �� ������
					
					if(choice==1)
					{
						query="SELECT StartDate,DateRange FROM Meet WHERE RoomID='"+roomid+"'"+" AND Finished=0";
						rs=stmt.executeQuery(query);
						
						if(rs.next())
						{
							startdate=rs.getString(1);
							range=rs.getInt(2);
							rs.close();
						}
						
					}
					
					if(choice==2){					
						query="SELECT Day,Starttime FROM Meet WHERE RoomID='"+roomid+"'"+" AND Finished=0";
						rs=stmt.executeQuery(query);
						
						if(rs.next()){
							day=rs.getString(1);
							starttime=rs.getInt(2);
							rs.close();
						}//������������ ����� ������ �� ������
						
						query="SELECT Day FROM Meet WHERE RoomID='"+roomid+"'"+" AND Finished=0";
						rs=stmt.executeQuery(query);
						
						if(rs.next())
						{
							day=rs.getString(1);
							rs.close();
						}							
					}
					
					else if(choice==3){
						query="SELECT Day,Starttime,Place FROM Meet WHERE RoomID='"+roomid+"'"+" AND Finished=0";
						rs=stmt.executeQuery(query);
						
						if(rs.next()){
							day=rs.getString(1);
							starttime=rs.getInt(2);
							place=rs.getString(3);
							System.out.println(place);
							rs.close();
						}//������������ ����� ������ �� ������
						
						query="SELECT Day FROM Meet WHERE RoomID='"+roomid+"'"+" AND Finished=0";
						rs=stmt.executeQuery(query);
						
						if(rs.next())
						{
							day=rs.getString(1);
							rs.close();
						}
					}
					
					query="SELECT MeetID,Day,Starttime,Endtime,Place,Food FROM Meet WHERE RoomID='"+roomid+"'"+"AND Finished=1";
					rs=stmt.executeQuery(query);
					
					int i=0;
					while(rs.next())
					{
						i++;
						String m1=rs.getInt(1)+"/"+rs.getString(2)+"/"+rs.getInt(3)+"/"+rs.getInt(4)+"/"+rs.getString(5)+"/"+rs.getString(6)+"/";
						m=m+m1;						
					}
					
					if(rs!=null){
						rs.close();
					}
					
					if(choice==2){
						bufferWriter.write("1/"+Integer.toString(choice)+"/"+day+"/"+starttime+"/"+Integer.toString(i)+"/"+m+"/\n");
					}
					else if(choice==3){
						bufferWriter.write("1/"+Integer.toString(choice)+"/"+day+"/"+starttime+"/"+place+"/"+Integer.toString(i)+"/"+m+"/\n");

					}
					else{
					bufferWriter.write("1/"+Integer.toString(choice)+"/"+Integer.toString(i)+"/"+m+"/\n");
					}
					bufferWriter.flush();//Ŭ���̾�Ʈ�� ��� ������	
					conn.commit();//DB�۾� ��
				}//������� �˻�
				
				else if(Integer.parseInt(value[0])==9){
					String ID=value[1];
					String PW=value[2];
					conn.setAutoCommit(false);//DB�۾�����
					subquery="SELECT * FROM Login WHERE ID='"+ID+"' AND PW='"+PW+"'";
					//ID�� PW Ȯ��				
					rs=stmt.executeQuery(subquery);
					if(rs.next()){						
						System.out.println("ȸ�� ���� Ȯ�� �Ϸ�.");
						rs.close();
						query="SELECT RoomID FROM Roommember WHERE ID='"+ID+"'";//roomid�˻�
						rs=stmt.executeQuery(query);
						ArrayList<Integer> roomidarr=new ArrayList<Integer>();
						while(rs.next()){
							roomidarr.add(rs.getInt(1));
						}
						if(rs!=null){
							rs.close();
						}
						if(roomidarr.size()>0){
							for(int i=0;i<roomidarr.size();i++){
								query="UPDATE Room SET Number=Number-1 WHERE RoomID="+roomidarr.get(i);//������ �濡 �ο� �Ѹ� ���ҽ�Ŵ
								stmt.executeUpdate(query);
							}
						}//������ ���� ����
						query="DELETE FROM Login WHERE ID='"+ID+"'";//�α��� ���̺��� ����
						stmt.executeUpdate(query);
						int result=1;
						bufferWriter.write(Integer.toString(result)+"\n");
						bufferWriter.flush();//Ŭ���̾�Ʈ�� ��� ������						
					}//ȸ�� ���� Ȯ�� �� Ż��
					else{						
						System.out.println("ȸ�� ���� ����");
						int result=2;
						bufferWriter.write(Integer.toString(result)+"\n");
						bufferWriter.flush();//Ŭ���̾�Ʈ�� ��� ������
					}//������ ���� ����(ID,PW ����)	
					conn.commit();//DB �۾� �Ϸ�	
				}//ȸ��Ż��
				
				else if(Integer.parseInt(value[0])==10){
					String ID=value[1];
					String PW=value[2];
					String newPW=value[3];
					conn.setAutoCommit(false);//DB�۾�����
					subquery="SELECT * FROM Login WHERE ID='"+ID+"' AND PW='"+PW+"'";
					//ID�� PW Ȯ��				
					rs=stmt.executeQuery(subquery);
					if(rs.next()){						
						System.out.println("ȸ�� ���� Ȯ�� �Ϸ�.");
						rs.close();
						query="UPDATE Login SET PW='"+newPW+"' WHERE ID='"+ID+"'";//��� ����
						stmt.executeUpdate(query);
						
						int result=1;
						bufferWriter.write(Integer.toString(result)+"\n");
						bufferWriter.flush();//Ŭ���̾�Ʈ�� ��� ������						
					}//ȸ�� ���� Ȯ�� �� �н����� ����
					else{						
						System.out.println("ȸ�� ���� ����");
						int result=2;
						bufferWriter.write(Integer.toString(result)+"\n");
						bufferWriter.flush();//Ŭ���̾�Ʈ�� ��� ������
					}//������ ���� ����(ID,PW ����)	
					conn.commit();//DB �۾� �Ϸ�	
				}//��� ����
				
				else if(Integer.parseInt(value[0])==11){
					int roomid=Integer.parseInt(value[1]);
					String startdate="";
					int range=0;
					
					conn.setAutoCommit(false);//DB�۾�����
					subquery="SELECT StartDate,DateRange FROM Meet WHERE RoomID='"+roomid+"' AND Finished=0";
					//ID�� PW Ȯ��		
					
					rs=stmt.executeQuery(subquery);
					
					if(rs.next()){
						startdate=rs.getString(1);
						range=rs.getInt(2);
						rs.close();
					}
					
					bufferWriter.write("1/"+startdate+"/"+Integer.toString(range)+"/\n");
					bufferWriter.flush();//Ŭ���̾�Ʈ�� ��� ������	
					conn.commit();//DB�۾� ��		
				}//���� ��¥�� ��¥���� �޾ƿ���.
			
				else if(Integer.parseInt(value[0])==12)
				{
					int roomid=Integer.parseInt(value[1]);
					String ID=value[2];
					String startdate=value[3];
					int range=Integer.parseInt(value[4]);
					String choicemsg="";
					for(int j=5;j<5+range;j++){
						choicemsg=choicemsg+value[j]+"/";
					}
					int meetid=-1;
					
					conn.setAutoCommit(false);//DB�۾�����
					subquery="SELECT MeetID FROM Meet WHERE RoomID="+roomid+" AND Finished=0";
					rs=stmt.executeQuery(subquery);
					if(rs.next()){
						meetid=rs.getInt(1);
						rs.close();
					}			
					
					query="UPDATE MeetIDChoice SET Selected=TRUE, SelectValue='"+choicemsg+"' WHERE RoomID="+roomid+" AND MeetID="+meetid+" AND ID='"+ID+"'";
					stmt.executeUpdate(query);//����� ���� �������
					System.out.println("����Ϸ�!\n");
					
					subquery="SELECT Selected FROM MeetIDChoice WHERE RoomID='"+roomid+"' AND MeetID="+meetid;
					rs=stmt.executeQuery(subquery);
					int flag=0;
					int num=0;
					Boolean c;
					while(rs.next()){
						num++;
						c=rs.getBoolean(1);
						if(!c){
							flag=1;
							break;
						}//Selected���� false�̸� ��������.						
					}
					rs.close();
					
					if(flag!=1)
					{
						query="SELECT SelectValue FROM MeetIDChoice WHERE RoomID='"+roomid+"'";
						rs=stmt.executeQuery(query);
						
						int[][] d=new int[range][16];
						
						for(int i=0;i<range;i++)
						{
							for(int j=0;j<16;j++)
								d[i][j]=0;
						}//�ʱ�ȭ �۾�
						
						while(rs.next())
						{
							String v=rs.getString(1);
							String[] v2=v.split("[/]");
							
							for(int i=0;i<range;i++)
							{
								for(int j=0;j<16;j++)
								{
									int k=Integer.parseInt(String.valueOf(v2[i].charAt(j)));
									d[i][j]=d[i][j]+k;
								}
							}
						}//����� ����
						rs.close();
						
						int flag2=0;//�ʱ���� : 0  �� ���� 3�� �� : 1 �� ���� 3�� �ƴҶ�:  0 
						ArrayList<String> timelist= new ArrayList<String>();
						//��¥/���۽ð�/�����½ð� ���·� ���� ��)2015-03-28/3/6
						
						Calendar cal=Calendar.getInstance();						
			        	SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
			        	String now=format.format(cal.getTime());//���ó�¥
			        	
			        	Date start=format.parse(startdate);
			        	Date now2=format.parse(now);
			        	cal.setTime(start);		        	
	        		
						for(int i=0;i<range;i++)//��¥
						{
				        	String dt=format.format(cal.getTime());//���� scope ��¥
				        	Date dt2=format.parse(dt);//���� scope ��¥
				        	cal.add(cal.DATE,+1);//�ϳ� ����
				        	int s=0,e=0;//���۽ð� �����½ð�
				        	
							if(dt2.getTime() >= now2.getTime()) {//���� ��¥�ų� �װͺ��� ������ ���� ���-> �� ��ȿ�� ��¥�� ��� �����ϰ� ��
				                 System.out.println("��ȿ ���� �ȿ� ����");
				                 for(int j=0;j<16;j++){//�ð�									
				                	 if(flag2==0){
				                		 if(d[i][j]==num){
				                			 s=j+9;//���� �ð� �Է�
				                			 e=s;//���۽ð��� �����½ð� ���� ����
				                			 flag2=1;
				                			 if(j==15){
				                				 timelist.add(dt+"/"+s+"/"+e);
				                			 }
				                		 }//���ݰ��� 3�϶�
				                	 }//�� ���� 3�� �ƴҶ�
				                	 else{
				                		 if(d[i][j]==num){
				                			 e++;//������ �ð��� �ϳ� ���Ѵ�
				                			 if(j==15){
				                				 timelist.add(dt+"/"+s+"/"+e);
				                			 }
				                		 }//���ݰ��� 3�� ��
				                		 else{
				                			 timelist.add(dt+"/"+s+"/"+e);
				                			 flag2=0;
				                		 }//���ݰ��� 3�� �ƴҶ�
				                	 }//�� ���� 3�� ��
				                 }
				            } 
				        	else {
				        		System.out.println("��ȿ���� �ۿ� ����");
				        		continue;//�����ϰ� �������� �Ѿ
				            }						        	
						}//���� ����Ͽ� timelist�� ����
						
						query="UPDATE MeetIDChoice SET Selected=FALSE, SelectValue='' WHERE RoomID="+roomid+" AND MeetID="+meetid;
						stmt.executeUpdate(query);//�ʱ�ȭ ��.
						
						if(timelist.size()<1)
						{							
							System.out.println("��¥����!");
							
							query="SELECT ID FROM Roommember WHERE RoomID='"+roomid+"'";
							rs=stmt.executeQuery(query);
							
							while(rs.next())
							{
								String id=rs.getString(1);
								if(id.equals(ID)==false)    //���� �ƴ� �ٸ� ģ���鿡�� ��¥ �ٽ� ���϶�� ����.
								{
									System.out.println("�ٸ� ģ�� ���̵� : "+id);
									query="SELECT regID FROM Login WHERE ID='"+id+"'";
									rs=stmt.executeQuery(query);
									
									if(rs.next())
									{
										gcmserver gcm=new gcmserver(rs.getString(1),3);
										gcm.sendMessage();
									}
								}
							}
							rs.close();
							bufferWriter.write("3/\n");
						}//��¥ ����- meetidchoice �ʱ�ȭ
						
						else if(timelist.size()==1){
							System.out.println("��¥�� �� �ϳ�!");
							System.out.println(timelist.get(0));
							
							String[] result=timelist.get(0).split("[/]");
							String date=result[0];
							int stime=Integer.parseInt(result[1]);
							int etime=Integer.parseInt(result[2]);
							
							query="SELECT ID FROM Roommember WHERE RoomID='"+roomid+"'";
							rs=stmt.executeQuery(query);
							
							while(rs.next())
							{
								String id=rs.getString(1);
								
								if(id.equals(ID)==false)    //���� �ƴ� �ٸ� ģ���鿡�� ��¥ �������ٰ� ����.
								{
									System.out.println("�ٸ� ģ�� ���̵� : "+id);
									query="SELECT regID FROM Login WHERE ID='"+id+"'";
									rs=stmt.executeQuery(query);
									
									if(rs.next())
									{
										gcmserver gcm=new gcmserver(rs.getString(1),1,date,stime);
										gcm.sendMessage();
									}
								}
							}
							rs.close();
							query="UPDATE Meet SET Day='"+date+"', Starttime="+stime+", Endtime="+etime+",Choice=2 WHERE RoomID="+roomid+" AND MeetID="+meetid;
							stmt.executeUpdate(query);//��� ���� ����
							
							bufferWriter.write("4/"+date+"/"+stime+"/"+etime+"\n");
							
						}//��¥�� �� �ϳ�- meetidchoice �ʱ�ȭ & meet�� ��¥ ���� ��� �� choice�� 2�� �ٲٱ�& Ŭ���̾�Ʈ���� ��� ������
						
						else{
							System.out.println("��¥���� ��ǥ�ؾ���!");
							
							int maxtime=-1;
							int stime=0,etime=0;
							String date="";
							for(int i=0;i<timelist.size();i++){
								String[] result=timelist.get(i).split("[/]");

								int time=Integer.parseInt(result[2])-Integer.parseInt(result[1]);
								if(maxtime<time){
									stime=Integer.parseInt(result[1]);
									etime=Integer.parseInt(result[2]);
									maxtime=time;
									date=result[0];
								}
							}//����ð� ���� �� �ִ� ��¥ ����
							
							query="UPDATE Meet SET Day='"+date+"', Starttime="+stime+", Endtime="+etime+",Choice=2 WHERE RoomID="+roomid+" AND MeetID="+meetid;
							stmt.executeUpdate(query);//��� ���� ����
						
							bufferWriter.write("4/"+date+"/"+stime+"/"+etime+"\n");
						}//��¥�� ����- meetidchoice �ʱ�ȭ & meet�� ��¥ ���� ��� �� choice�� 2�� �ٲٱ�& Ŭ���̾�Ʈ���� ��� ������					
						
						bufferWriter.flush();//Ŭ���̾�Ʈ�� ��� ������	
						conn.commit();
					}//��� ����� ���� ������ ���.
					
					else
					{
						bufferWriter.write("2/\n");
						bufferWriter.flush();//Ŭ���̾�Ʈ�� ��� ������
						conn.commit();	
						System.out.println("��ٷ�����!\n");
					}//��� ����� �������� �ʾ��� ��
				}//���� ����
				
				else if(Integer.parseInt(value[0])==13){
					int roomid=Integer.parseInt(value[1]);
					int daycheck=-1;
					int placecheck=-1;			
					
					conn.setAutoCommit(false);//DB�۾�����
				
					query="SELECT Daycheck,Placecheck FROM Room WHERE RoomID="+roomid;
					rs=stmt.executeQuery(query);
					if(rs.next()){
						daycheck=rs.getInt(1);
						placecheck=rs.getInt(2);
						rs.close();
					}
					
					if(daycheck==1){
						String daystart="";
						int daystarttime=-1;
						int dayreturnvalue=0;
						String placedefault="";

						int choice=0;
						int newmeetid=1;
						int dayreturnvalue_val=-1;
						String newday="";
						
						query="SELECT Daystart,Daystarttime,Dayreturnvalue FROM Room WHERE RoomID="+roomid;
						rs=stmt.executeQuery(query);
						if(rs.next()){
							daystart=rs.getString(1);
							daystarttime=rs.getInt(2);
							dayreturnvalue=rs.getInt(3);
							rs.close();
						}
											
						choice=2;//choice�� 2�� �ڵ����� �÷���
						if(dayreturnvalue==1){
							dayreturnvalue_val=7;
						}//1��
						else if(dayreturnvalue==2){
							dayreturnvalue_val=14;
						}//2��
						else if(dayreturnvalue==3){
							dayreturnvalue_val=1;
						}//�Ѵ�
						else if(dayreturnvalue==4){
							dayreturnvalue_val=2;
						}//�δ�
						
						query="SELECT Max(MeetID) FROM Meet WHERE RoomID="+roomid;
						rs=stmt.executeQuery(query);	
						int maxmeetid=-1;
						if(rs.next()){
							maxmeetid=rs.getInt(1);
							newmeetid=maxmeetid+1;
							rs.close();
						}//�ִ밪 ������
						
						query="SELECT Day FROM Meet WHERE RoomID="+roomid+" AND MeetID="+maxmeetid;
						rs=stmt.executeQuery(query);
						
						String maxday="";
						
						if(rs.next()){
							maxday=rs.getString(1);
							rs.close();
						}//�ִ볯¥ ������
						
						
						Calendar cal=Calendar.getInstance();						
			        	SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
			        	String now=format.format(cal.getTime());//���ó�¥
			        	
			        	Date start=format.parse(daystart);//ó������ ���� ��¥
			        	Date now2=format.parse(now);//���ó�¥
			        	
			        	if(maxday.length()>0){
			        		Date recent=format.parse(maxday);//���� �ֱ� ��¥
			        		cal.setTime(recent);
			        		while(recent.getTime() <= now2.getTime()) {
			        			if(dayreturnvalue==1||dayreturnvalue==2){
			        				cal.add(cal.DATE,+dayreturnvalue_val);//�ֱ⸸ŭ ����
			        			}
			        			else{
			        				cal.add(cal.MONTH,+dayreturnvalue_val);//�ֱ⸸ŭ ����
			        			}
			        			String dt=format.format(cal.getTime());//���� scope ��¥
					        	recent=format.parse(dt);//���� scope ��¥
					        	
			        		}
			        	}
			        	else{
			        		cal.setTime(start);
			        		while(start.getTime() <= now2.getTime()) {					        	
			        			if(dayreturnvalue==1||dayreturnvalue==2){
			        				cal.add(cal.DATE,+dayreturnvalue_val);//�ֱ⸸ŭ ����
			        			}
			        			else{
			        				cal.add(cal.MONTH,+dayreturnvalue_val);//�ֱ⸸ŭ ����
			        			}
			        			String dt=format.format(cal.getTime());//���� scope ��¥
					        	start=format.parse(dt);//���� scope ��¥
			        		}
			        	}
			        	
			        	newday=format.format(cal.getTime());//�� ��¥
						
			        	query="INSERT INTO Meet(RoomID,MeetId,Day,Starttime,Choice) VALUES(?,?,?,?,?)";//��ӻ���
						pstm=conn.prepareStatement(query);						
						pstm.setInt(1,roomid);
						pstm.setInt(2,newmeetid);
						pstm.setString(3,newday);
						pstm.setInt(4,daystarttime);
						pstm.setInt(5,choice);
						pstm.executeUpdate();
						pstm.close();
						
						if(placecheck==0){
							choice=3;
							query="SELECT Placedefault FROM Room WHERE RoomID="+roomid;
							rs=stmt.executeQuery(query);
							if(rs.next()){
								placedefault=rs.getString(1);
								rs.close();
							}
							
							query="UPDATE Meet SET Place='"+placedefault+"',Choice=3 WHERE RoomID="+roomid+" AND MeetID="+newmeetid;
							stmt.executeUpdate(query);
						}//���� �����̰� ��� ������
						
						query="select id from roommember where roomid='"+roomid+"'";
						rs=stmt.executeQuery(query);
						
						while(rs.next())
						{
							String id=rs.getString(1);
							
							query="INSERT INTO MeetIDChoice(roomid,meetid,id,selected,selectvalue) VALUES(?,?,?,?,?)";
							pstm=conn.prepareStatement(query);
							pstm.setInt(1, roomid);
							pstm.setInt(2,newmeetid);
							pstm.setString(3, id);
							pstm.setBoolean(4, false);
							pstm.setString(5, null);
							
							pstm.executeUpdate();
							pstm.close();
						}// ���� db�� ����.
						
						if(rs!=null){
							rs.close();
						}
						
						bufferWriter.write("3/\n");
					}//��������� �ø� meet �ڵ� ����					
					else{
						bufferWriter.write("4/\n");	
					}//������ �����Ͻ�..					

					bufferWriter.flush();//Ŭ���̾�Ʈ�� ��� ������	
					conn.commit();//DB�۾� ��					
				}//��������ȸ�� ����� Ȥ�� �׳� ����
				
				else if(Integer.parseInt(value[0])==14)
				{
					int gu=Integer.parseInt(value[1]);
					int theme=Integer.parseInt(value[2]);
					String m="";
					int i=0;
					
					conn.setAutoCommit(false);//DB�۾�����
					query="SELECT Stationname FROM Station WHERE Gu="+Integer.toString(gu)+" ";
					
					switch(theme)
					{
						case 1 :query=query+"AND ThemeParkcheck=1"; break;
						case 2 : query=query+"AND ThemeCulturecheck=1";break;
						case 3 : query=query+"AND ThemeMallcheck=1";break;
						case 4 : query=query+"AND ThemeRoadshopcheck=1";break;
						case 5 : query=query+"AND ThemeEatcheck=1";break;
						case 6 : query=query+"AND ThemeHangangcheck=1";break;
						case 7 : query=query+"AND ThemeUnivcheck=1";break;
						case 8 : query=query+"AND ThemeChunggyecheck=1";break;
					}
					
					rs=stmt.executeQuery(query);
					while(rs.next())
					{
						i++;
						m=m+rs.getString(1)+"/";						
					}
					
					if(rs!=null){
						rs.close();
					}					
					bufferWriter.write("1/"+Integer.toString(i)+"/"+m+"\n");
					bufferWriter.flush();//Ŭ���̾�Ʈ�� ��� ������	
					conn.commit();//DB�۾� ��
				}//�� ��ȣ�� �׸� ��ȣ�� �̿��ؼ� ��� ã��.
				
				else if(Integer.parseInt(value[0])==15)
				{
					int roomid=Integer.parseInt(value[1]);
					String id=value[2];
					int num=Integer.parseInt(value[3]);
					int meetid=0;
					String selectmsg="";
					
					for(int i=4;i<4+num;i++)
					{
						selectmsg+=value[i]+"/";
					}
					
					conn.setAutoCommit(false);//DB�۾�����
					
					subquery="SELECT MeetID FROM Meet WHERE RoomID="+roomid+" AND Finished=0";
					rs=stmt.executeQuery(subquery);
					if(rs.next()){
						meetid=rs.getInt(1);
						rs.close();
					}			
					
					query="UPDATE MeetIDChoice SET Selected=TRUE, SelectValue='"+selectmsg+"' WHERE RoomID="+roomid+" AND MeetID="+meetid+" AND ID='"+id+"'";
					stmt.executeUpdate(query);//����� ���� �������
					System.out.println("����Ϸ�!\n");
					
					subquery="SELECT Selected FROM MeetIDChoice WHERE RoomID='"+roomid+"' AND MeetID="+meetid;
					rs=stmt.executeQuery(subquery);
					int flag=0;
					Boolean c;
					while(rs.next()){					
						c=rs.getBoolean(1);
						if(!c){
							flag=1;
							break;
						}//Selected���� false�̸� ��������.						
					}
					rs.close();
					
					if(flag!=1)
					{
						query="SELECT SelectValue FROM MeetIDChoice WHERE RoomID='"+roomid+"'";
						rs=stmt.executeQuery(query);
						
						ArrayList<Integer> v=new ArrayList<Integer>();
						ArrayList<String> v1=new ArrayList<String>();
						int i=0;
						
						while(rs.next())
						{
							String selected=rs.getString(1);
							String[] v2=selected.split("[/]");
							
							if(i==0)
							{
								for(int j=0;j<v2.length;j++)
								{
									v1.add(v2[j]);
									v.add(1);
								}
							}
							else
							{
								for(int j=0;j<v2.length;j++)
								{
									if(v1.contains(v2[j])==false)
									{
										v1.add(v2[j]);
										v.add(1);
									}
									else
									{
										int index=v1.indexOf(v2[j]);
										int a=v.get(index)+1;
										v.set(index,a);
									}
								}
							}
							i++;
						}//����� ����
						rs.close();

						int max=-1;
						ArrayList<String> place=new ArrayList<String>();
						
						for(int j=0;j<v.size();j++)
						{
							if(max<v.get(j))
							{
								place.removeAll(place);
								max=v.get(j);
								place.add(v1.get(j));
							}
							else if(max==v.get(j))
							{
								place.add(v1.get(j));
							}
						}

						query="UPDATE MeetIDChoice SET Selected=FALSE, SelectValue='' WHERE RoomID="+roomid+" AND MeetID="+meetid;
						stmt.executeUpdate(query);//�ʱ�ȭ ��.
						
						
						if(place.size()==1)
						{
							String placevalue=place.get(0);
							query="SELECT ID FROM Roommember WHERE RoomID='"+roomid+"'";
							rs=stmt.executeQuery(query);
							
							while(rs.next())
							{
								String fid=rs.getString(1);
								
								if(fid!=id)    //���� �ƴ� �ٸ� ģ���鿡�� ��� �������ٰ� ����.
								{
									System.out.println("�ٸ� ģ�� ���̵� : "+fid);
									query="SELECT regID FROM Login WHERE ID='"+fid+"'";
									rs=stmt.executeQuery(query);
									
									if(rs.next())
									{
										gcmserver gcm=new gcmserver(rs.getString(1),4,placevalue);
										gcm.sendMessage();
									}
								}
							}
							rs.close();
							
							query="UPDATE Meet SET Place='"+placevalue+"',Choice=3 WHERE RoomID="+roomid+" AND MeetID="+meetid;
							stmt.executeUpdate(query);//��� ���� ����
							
							bufferWriter.write("2/"+placevalue+"/\n");
							bufferWriter.flush();//Ŭ���̾�Ʈ�� ��� ������	
							conn.commit();//DB�۾� ��
							
						}//���� ���� �� ��Ұ� �ϳ��϶�.
						
						else if(place.size()>1)
						{
							int placevalue=(int)((Math.random())*place.size());
							String selectedplace=place.get(placevalue);
							
							query="UPDATE Meet SET Place='"+selectedplace+"',Choice=3 WHERE RoomID="+roomid+" AND MeetID="+meetid;
							stmt.executeUpdate(query);//��� ���� ����
							
							query="SELECT ID FROM Roommember WHERE RoomID='"+roomid+"'";
							rs=stmt.executeQuery(query);
							
							while(rs.next())
							{
								String fid=rs.getString(1);
								
								if(fid!=id)    //���� �ƴ� �ٸ� ģ���鿡�� �������� ��� �������ٰ� ����.
								{
									System.out.println("�ٸ� ģ�� ���̵� : "+fid);
									query="SELECT regID FROM Login WHERE ID='"+fid+"'";
									rs=stmt.executeQuery(query);
									
									if(rs.next())
									{
										gcmserver gcm=new gcmserver(rs.getString(1),5,selectedplace);
										gcm.sendMessage();
									}
								}
							}
							rs.close();
							
							bufferWriter.write("2/"+selectedplace+"/\n");
							bufferWriter.flush();//Ŭ���̾�Ʈ�� ��� ������	
							conn.commit();//DB�۾� ��
						}//���� ���� �� ��Ұ� �����϶�.
						
						else
						{
							query="SELECT ID FROM Roommember WHERE RoomID='"+roomid+"'";
							rs=stmt.executeQuery(query);
														
							bufferWriter.write("3/\n");
							bufferWriter.flush();//Ŭ���̾�Ʈ�� ��� ������
							conn.commit();
						}//����� ������.
					}//��� ����� ���� ������ ���.
					
					else
					{
						bufferWriter.write("4/\n");
						bufferWriter.flush();//Ŭ���̾�Ʈ�� ��� ������
						conn.commit();	
						System.out.println("��ٷ�����!\n");
					}//��� ����� �������� �ʾ��� ��
					
				}//��� ����
				
				else if(Integer.parseInt(value[0])==16)
				{
					int theme=Integer.parseInt(value[1]);
					int subway=Integer.parseInt(value[2]);
					String m="";
					int i=0;
					int flag=0;
					
					conn.setAutoCommit(false);//DB�۾�����
					query="SELECT Stationname FROM Station WHERE ";
					
					switch(theme)
					{
						case 0 : flag=1;break;
						case 1 : query=query+"ThemeParkcheck=1"; break;
						case 2 : query=query+"ThemeCulturecheck=1";break;
						case 3 : query=query+"ThemeMallcheck=1";break;
						case 4 : query=query+"ThemeRoadshopcheck=1";break;
						case 5 : query=query+"ThemeEatcheck=1";break;
						case 6 : query=query+"ThemeHangangcheck=1";break;
						case 7 : query=query+"ThemeUnivcheck=1";break;
						case 8 : query=query+"ThemeChunggyecheck=1";break;
					}
					
					if(flag==0)
					{
						switch(subway)
						{
							case 0 : break;
							case 1 :query=query+" AND Line1check=1"; break;
							case 2 : query=query+" AND Line2check=1";break;
							case 3 : query=query+" AND Line3check=1";break;
							case 4 : query=query+" AND Line4check=1";break;
							case 5 : query=query+" AND Line5check=1";break;
							case 6 : query=query+" AND Line6check=1";break;
							case 7 : query=query+" AND Line7check=1";break;
							case 8 : query=query+" AND Line8check=1";break;
							case 9 : query=query+" AND Line9check=1";break;
							case 10 : query=query+" AND Linemidcheck=1";break;
							case 11 : query=query+" AND Linebundangcheck=1";break;
							case 12 : query=query+" AND Lineshinbundangcheck=1";break;
						}
					}
					
					else
					{
						switch(subway)
						{
							case 0 : break;
							case 1 :query=query+"Line1check=1"; break;
							case 2 : query=query+"Line2check=1";break;
							case 3 : query=query+"Line3check=1";break;
							case 4 : query=query+"Line4check=1";break;
							case 5 : query=query+"Line5check=1";break;
							case 6 : query=query+"Line6check=1";break;
							case 7 : query=query+"Line7check=1";break;
							case 8 : query=query+"Line8check=1";break;
							case 9 : query=query+"Line9check=1";break;
							case 10 : query=query+"Linemidcheck=1";break;
							case 11 : query=query+"Linebundangcheck=1";break;
							case 12 : query=query+"Lineshinbundangcheck=1";break;
						}
					}
					
					rs=stmt.executeQuery(query);
					while(rs.next())
					{
						i++;
						m=m+rs.getString(1)+"/";						
					}
					
					if(rs!=null){
						rs.close();
					}					
					bufferWriter.write("1/"+Integer.toString(i)+"/"+m+"\n");
					bufferWriter.flush();//Ŭ���̾�Ʈ�� ��� ������	
					conn.commit();//DB�۾� ��
				}//����ö ȣ���� �׸���ȣ�� ��� �˻�.
				
				else if(Integer.parseInt(value[0])==17)
				{
					int roomid=Integer.parseInt(value[1]);
					String ID=value[2];
					String choicemsg;
					int gfoodnum=Integer.parseInt(value[3]);
					choicemsg=Integer.toString(gfoodnum)+"/";
					for(int i=4;i<4+gfoodnum;i++){
						choicemsg=choicemsg+value[i]+"/";
					}
					int bfoodnum=Integer.parseInt(value[4+gfoodnum]);
					choicemsg=choicemsg+Integer.toString(bfoodnum)+"/";
					for(int i=5+gfoodnum;i<5+gfoodnum+bfoodnum;i++){
							choicemsg=choicemsg+value[i]+"/";					
					}
										
					int meetid=-1;
					
					conn.setAutoCommit(false);//DB�۾�����
					
					subquery="SELECT MeetID FROM Meet WHERE RoomID="+roomid+" AND Finished=0";
					rs=stmt.executeQuery(subquery);
					if(rs.next()){
						meetid=rs.getInt(1);
						rs.close();
					}//��Ӿ��̵� �˻�	
														
					
					query="UPDATE MeetIDChoice SET Selected=TRUE, SelectValue='"+choicemsg+"' WHERE RoomID="+roomid+" AND MeetID="+meetid+" AND ID='"+ID+"'";
					stmt.executeUpdate(query);//����� ���� �������
					System.out.println("����Ϸ�!\n");
					
					subquery="SELECT Selected FROM MeetIDChoice WHERE RoomID='"+roomid+"' AND MeetID="+meetid;
					rs=stmt.executeQuery(subquery);
					
					int flag=0;
					Boolean c;
					while(rs.next()){
						c=rs.getBoolean(1);
						if(!c){
							flag=1;
							break;
						}//Selected���� false�̸� ��������.						
					}
					rs.close();
					
					if(flag!=1)
					{
						query="SELECT SelectValue FROM MeetIDChoice WHERE RoomID='"+roomid+"'";
						rs=stmt.executeQuery(query);
						
						ArrayList<String> gfoodlist=new ArrayList<String>();//���� ���� ���帮��Ʈ
						ArrayList<String> bfoodlist=new ArrayList<String>();//���� ���� ���帮��Ʈ
						ArrayList<Integer> gselectnum=new ArrayList<Integer>();//���� ���� ���� ����(�ټ��� ������ �� ���� ����)						
						
						int i=0;
						
						while(rs.next())
						{
							String selected=rs.getString(1);
							String[] v=selected.split("[/]");
							
							if(i==0)
							{
								int gnum=Integer.parseInt(v[0]);//���� ���� ���� ����
								for(int j=1;j<1+gnum;j++){
									gfoodlist.add(v[j]);
									gselectnum.add(1);
								}
								int bnum=Integer.parseInt(v[1+gnum]);//���� ���� ���� ����
								for(int j=2+gnum;j<2+gnum+bnum;j++){
									bfoodlist.add(v[j]);									
								}
							}//ù ��� ������ �����
							else
							{
								int gnum=Integer.parseInt(v[0]);//���� ���� ���� ����
								for(int j=1;j<1+gnum;j++){
									if(gfoodlist.contains(v[j])){
										int index=gfoodlist.indexOf(v[j]);
										int a=gselectnum.get(index)+1;
										gselectnum.set(index, a);										
									}//�̹� ��ȣ ����Ʈ�� ������. ��ȣ���� �ϳ� ����������
									else{
										gfoodlist.add(v[j]);
										gselectnum.add(1);
									}//���ο� ��ȣ ������ ��								
								}
								
								int bnum=Integer.parseInt(v[1+gnum]);//���� ���� ���� ����
								for(int j=2+gnum;j<2+gnum+bnum;j++){
									if(bfoodlist.contains(v[j])==false){
										bfoodlist.add(v[j]);										
									}//���ο� ��ȣ ������ ��.							
								}								
							}
							i++;//��� �� ����
						}//����� ����
						rs.close();

						for(int k=0;k<gfoodlist.size();k++){
							String f=gfoodlist.get(k);
							if(bfoodlist.contains(f)){
								gfoodlist.remove(k);
								gselectnum.remove(k);
							}
						}// ��ȣ�������� ���õ� ������ ����
						
						
						int max=-1;
						ArrayList<String> food=new ArrayList<String>();
						
						for(int j=0;j<gfoodlist.size();j++)
						{
							if(max<gselectnum.get(j))
							{
								food.removeAll(food);//����Ʈ ����
								max=gselectnum.get(j);
								food.add(gfoodlist.get(j));
							}//�ִ방 ����
							else if(max==gselectnum.get(j))
							{
								food.add(gfoodlist.get(j));
							}
						}//�ִ� �ο��� ��ȣ�ϴ� ���� ���

						query="UPDATE MeetIDChoice SET Selected=FALSE, SelectValue='' WHERE RoomID="+roomid+" AND MeetID="+meetid;
						stmt.executeUpdate(query);//�ʱ�ȭ ��.
						
						
						if(food.size()==1)
						{
							String foodvalue=food.get(0);
							/*
							query="SELECT ID FROM Roommember WHERE RoomID='"+roomid+"'";
							rs=stmt.executeQuery(query);
							
							while(rs.next())
							{
								String fid=rs.getString(1);
								
								if(fid!=ID)    //���� �ƴ� �ٸ� ģ���鿡�� ��� �������ٰ� ����.
								{
									System.out.println("�ٸ� ģ�� ���̵� : "+fid);
									query="SELECT regID FROM Login WHERE ID=?";
									pstm=conn.prepareStatement(query);
									pstm.setString(1,fid);
									ResultSet rs2=pstm.executeQuery();									
									
									if(rs2.next())
									{
										gcmserver gcm=new gcmserver(rs2.getString(1),4,placevalue);
										gcm.sendMessage();
										rs2.close();
									}
								}
							}
							rs.close();
							*/
							query="UPDATE Meet SET Food='"+foodvalue+"',Choice=4, Finished=1 WHERE RoomID="+roomid+" AND MeetID="+meetid;
							stmt.executeUpdate(query);//��� ���� ����
							
							bufferWriter.write("2/"+foodvalue+"/\n");
							bufferWriter.flush();//Ŭ���̾�Ʈ�� ��� ������	
							conn.commit();//DB�۾� ��
							
						}//���� ���� �� ������ �ϳ��϶�.
						
						else if(food.size()>1)
						{
							int foodvalue=(int)((Math.random())*food.size());
							String selectedfood=food.get(foodvalue);
							
							query="UPDATE Meet SET food='"+selectedfood+"',Choice=4, Finished=1 WHERE RoomID="+roomid+" AND MeetID="+meetid;
							stmt.executeUpdate(query);//��� ���� ����
							
							/*
							query="SELECT ID FROM Roommember WHERE RoomID='"+roomid+"'";
							rs=stmt.executeQuery(query);
							
							while(rs.next())
							{
								String fid=rs.getString(1);
								
								if(fid!=id)    //���� �ƴ� �ٸ� ģ���鿡�� �������� ��� �������ٰ� ����.
								{
									System.out.println("�ٸ� ģ�� ���̵� : "+fid);
									query="SELECT regID FROM Login WHERE ID='"+fid+"'";
									rs=stmt.executeQuery(query);
									
									if(rs.next())
									{
										gcmserver gcm=new gcmserver(rs.getString(1),5,selectedplace);
										gcm.sendMessage();
									}
								}
							}
							rs.close();
							*/
							bufferWriter.write("2/"+selectedfood+"/\n");
							bufferWriter.flush();//Ŭ���̾�Ʈ�� ��� ������	
							conn.commit();//DB�۾� ��
						}//���� ���� �� ������ �����϶�.
						
						else
						{
							System.out.println("���ľ���!");							
							bufferWriter.write("4/\n");						
							bufferWriter.flush();//Ŭ���̾�Ʈ�� ��� ������
							conn.commit();					
						}//����� ������.						
					}//��� ����� �������� ��.
					else{
						conn.commit();
						bufferWriter.write("3/\n");
						bufferWriter.flush();//Ŭ���̾�Ʈ�� ��� ������	
					}//��ٷ��� �Ҷ�
				}//���� ����.
				
				else if(Integer.parseInt(value[0])==18){
					int roomid=Integer.parseInt(value[1]);
					String place="";
					conn.setAutoCommit(false);//DB�۾�����
					
					subquery="SELECT Placedefault FROM Room WHERE RoomID="+roomid;
					rs=stmt.executeQuery(subquery);
					
					if(rs.next()){
						place=rs.getString(1);
						rs.close();
					}
					
					query="UPDATE Meet SET Place='"+place+"', Choice=3 WHERE RoomID="+roomid+" AND Finished=0";
					stmt.executeUpdate(query);//����� ���� �������
					System.out.println("����Ϸ�!\n");
					
					conn.commit();//DB�۾� ��
					
					bufferWriter.write("2/\n");
					bufferWriter.flush();//Ŭ���̾�Ʈ�� ��� ������	
					
				}//��� �����Ͻ� meet ���̺� place�� ����
				
				else if(Integer.parseInt(value[0])==19){
					String ID=value[1];
					String regID=value[2];
					conn.setAutoCommit(false);//DB�۾�����
					
					query="UPDATE Login SET regID='"+regID+"' WHERE ID='"+ID+"'";//��� ����
					stmt.executeUpdate(query);
					
					int result=5;
					bufferWriter.write(Integer.toString(result)+"\n");
					bufferWriter.flush();//Ŭ���̾�Ʈ�� ��� ������			
					
					conn.commit();//DB �۾� �Ϸ�
					
					
				}//�α����Ҷ� ��� ���̵� ���.
				
				else if(Integer.parseInt(value[0])==20){
					conn.setAutoCommit(false);//DB�۾�����
					int roomid,range,choice;
					String startdate;
					
					System.out.println("�˶�����!!!");
					query="SELECT roomid,choice,StartDate,DateRange FROM Meet where finished=0";
					rs=stmt.executeQuery(query);
					
					while(rs.next())
					{
						roomid=rs.getInt(1);
						choice=rs.getInt(2);
						startdate=rs.getString(3);
						range=rs.getInt(4);
						
						Date now2;
						Date day2;
						if(choice==1)
						{
							Calendar cal=Calendar.getInstance();						
				        	SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
				        	String now=format.format(cal.getTime());//���ó�¥
				        	
				        	Date start=format.parse(startdate);
				        	now2=format.parse(now);
				        	cal.setTime(start);
				        	
				        	cal.add(cal.DATE,+range);
				        	String dt=format.format(cal.getTime());//startdate+range
				        	day2=format.parse(dt);
						}
						
						else
						{
							query="SELECT Day FROM Meet WHERE RoomID=? AND Finished=0";
							pstm=conn.prepareStatement(query);
							pstm.setInt(1,roomid);
							ResultSet rs2=pstm.executeQuery();
							String day="";
							
							if(rs2.next())
							{
								day=rs2.getString(1);
								rs2.close();
							}
							pstm.close();
							
							Calendar cal=Calendar.getInstance();						
				        	SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
				        	String now=format.format(cal.getTime());//���ó�¥
				        	
				        	now2=format.parse(now);
				        	day2=format.parse(day);
				        	
						}
						
						if(day2.getTime() <= now2.getTime())
						{
							query="DELETE FROM Meet WHERE RoomID=? AND Finished=0";
							pstm=conn.prepareStatement(query);
							pstm.setInt(1,roomid);
							pstm.executeUpdate();
							pstm.close();
							System.out.println("���������� ���������!!");
							

							query="SELECT regID FROM login where ID IN (SELECT ID From roommember where RoomID=?)";
							pstm=conn.prepareStatement(query);
							pstm.setInt(1, roomid);
							ResultSet rs2=pstm.executeQuery();
							
							while(rs2.next())
							{								
								System.out.println("�ٸ� ģ�� ���̵� : "+rs2.getString(1));
								gcmserver gcm=new gcmserver(rs2.getString(1),6);
								gcm.sendMessage();
							}
							if(rs2!=null){
								rs2.close();
							}
							pstm.close();
						}
					}
					rs.close();
					System.out.println("�˶� ������ ����");
					 
					int result=5;
					bufferWriter.write(Integer.toString(result)+"\n");
					bufferWriter.flush();//Ŭ���̾�Ʈ�� ��� ������			
					
					conn.commit();//DB �۾� �Ϸ�
					
					
				}//�α����Ҷ� ��� ���̵� ���.
				else if(Integer.parseInt(value[0])==21){
					int category=Integer.parseInt(value[1]);
					String m="";
					int i=0;
					
					conn.setAutoCommit(false);//DB�۾�����
					query="SELECT foodname FROM food WHERE ";
					
					switch(category)
					{
						case 1 : query=query+"category=1"; break;
						case 2 : query=query+"category=2";break;
						case 3 : query=query+"category=3";break;
						case 4 : query=query+"category=4";break;
						case 5 : query=query+"category=5";break;
						case 6 : query=query+"category=6";break;
						case 7 : query=query+"category=7";break;
						case 8 : query=query+"category=8";break;
						case 9 : query=query+"category=9";break;
						case 10 : query=query+"category=10";break;
						case 11 : query=query+"category=11";break;
						case 12 : query=query+"category=12";break;
					}
					
					rs=stmt.executeQuery(query);
					while(rs.next())
					{
						i++;
						m=m+rs.getString(1)+"/";						
					}
					
					if(rs!=null){
						rs.close();
					}					
					bufferWriter.write("1/"+Integer.toString(i)+"/"+m+"\n");
					bufferWriter.flush();//Ŭ���̾�Ʈ�� ��� ������	
					conn.commit();//DB�۾� ��
				}//���� ī�װ� �˻�

			}
			catch(ClassNotFoundException e){
					e.printStackTrace();
				}
			catch(IOException e){
				e.printStackTrace();
			}
				catch(Exception e){
					conn.rollback();
					e.printStackTrace();
				}	
					
			finally{
			//���ҽ� ��ȯ �۾�
			if(rs!=null){
				try{
					rs.close();					
				}
				catch(SQLException e){
					e.printStackTrace();
				}
			}
			rs=null;
			
			if(stmt!=null){
				try{
					stmt.close();
				}
				catch(SQLException e){
					e.printStackTrace();
				}
			}
			stmt=null;
			
			if(conn!=null){
				try{
					conn.close();
				}
				catch(SQLException e){
					e.printStackTrace();
				}
			}
			conn=null;
		}
			}
	catch(SQLException e){
		e.printStackTrace();
	}
			
			
		}					
	}

	
	public static void main(String[] args) {
		
		new ChatServer();
		
		//server = new ServerSocket(port);
		
			
	}
}