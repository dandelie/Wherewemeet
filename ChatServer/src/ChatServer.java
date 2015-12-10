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
	
	//서버소켓 - 클라이언트의 접속을 받아들이는 클래스
	int port=8888;
	ServerSocket server;
	int sum2=0;	
	//서버 클라이언트 연결 변수

	String fileName="DBConnect.txt";//DB 연결 정보 들어있는 파일 접근
	Scanner inputStream= null;
	String url="";
	
	public ChatServer() {				
		try{
			inputStream=new Scanner(new File(fileName));
		}//파일 연결
		catch(FileNotFoundException e){
			System.out.println("DBConnect File open Error!\n");
			System.exit(1);
		}//DBConnect파일 오류시		
		while(inputStream.hasNextLine()){
			url=inputStream.nextLine();
		}//파일 읽고 url 저장
		inputStream.close();//파일 연결 끊기
		
		try {
			//서버객체 생성
			server = new ServerSocket(port);
			while(true){
				//접속자가 접속하는지 확인
				//접속자가 있을때까지 대기,지연상태에 있다.
				Socket client = server.accept();
				InetAddress inet = client.getInetAddress();
				String ip = inet.getHostAddress();
				System.out.println(ip+"-접속자 발견");
				
				//대화용 쓰레드 생성 및 소켓
				ServerThread serverThread = new ServerThread(client);
				System.out.println("start");
				serverThread.start();				
		}
		} 
		catch (IOException e) {
			e.printStackTrace();
		}//접속 확인
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
	//DB관련 변수
	
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
				//connection 연결				
				conn=DriverManager.getConnection(url);
				stmt=conn.createStatement();
				
				String msg = buffer.readLine();
				String[] value=msg.split("[/]");
				
				if(Integer.parseInt(value[0])==0){
					String ID=value[1];
					String PW=value[2];
					String NN=value[3];
					// 클라이언트로부터 아이디와 패스워드 닉네임 정보 받아오기
					System.out.println(NN+"\n");
					conn.setAutoCommit(false);//DB작업시작
					
					subquery="SELECT * FROM Login WHERE ID='"+ID+"'";
					//이미 존재하는 ID인지 확인
								
					rs=stmt.executeQuery(subquery);
					
					if(rs.next()){
						System.out.println("이미 존재하는 아이디 입니다.");
						
						int result=1;
						bufferWriter.write(Integer.toString(result)+"\n");
						bufferWriter.flush();//클라이언트에 결과 보내기
						
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
						bufferWriter.flush();//클라이언트에 결과 보내기
					}
					rs.close();
					conn.commit();//DB 작업 완료
				}//회원가입 기능
				
				else if(Integer.parseInt(value[0])==1){
					String ID=value[1];
					String PW=value[2];
					// 클라이언트로부터 아이디와 패스워드 정보 받아오기
					conn.setAutoCommit(false);//DB작업시작
					subquery="SELECT * FROM Login WHERE ID='"+ID+"' AND PW='"+PW+"'";
					//ID와 PW 확인				
					rs=stmt.executeQuery(subquery);
					
					if(rs.next()){						
						System.out.println("로그인 완료.");
						int result=3;
						bufferWriter.write(Integer.toString(result)+"\n");
						bufferWriter.flush();//클라이언트에 결과 보내기						
					}//로그인 완료
					else{						
						System.out.println("로그인 오류");
						int result=4;
						bufferWriter.write(Integer.toString(result)+"\n");
						bufferWriter.flush();//클라이언트에 결과 보내기
					}//로그인 정보가 맞지 않음(ID,PW 오류)	
					rs.close();
					conn.commit();//DB 작업 완료					
				}//로그인 기능					
				
				else if(Integer.parseInt(value[0])==2){
					String ID=value[1];
					// 클라이언트로부터 아이디정보 받아오기
					conn.setAutoCommit(false);//DB작업시작
					subquery="SELECT * FROM Login WHERE ID='"+ID+"'";
					//ID 조회			
					rs=stmt.executeQuery(subquery);
					
					if(rs.next()){						
						System.out.println("ID 있음");
						rs.close();
						query="SELECT Name FROM Login WHERE ID='"+ID+"'";
						rs=stmt.executeQuery(query);
						if(rs.next()){
							String NN=rs.getString(1);//닉네임 가져오기
							System.out.print(NN+"\n");
							int result=2;
							bufferWriter.write(Integer.toString(result)+"/"+NN+"\n");
							rs.close();
						}//닉네임이 있을시
						else{
							int result=3;
							bufferWriter.write(Integer.toString(result)+"\n");
						}//닉네임이 없을시						
						bufferWriter.flush();//클라이언트에 결과 보내기	
					}//아이디 있음
					else{						
						System.out.println("ID 없음");
						int result=1;
						bufferWriter.write(Integer.toString(result)+"\n");
						bufferWriter.flush();//클라이언트에 결과 보내기
					}//아이디 없음				
					conn.commit();//DB 작업 완료					
				}//친구찾기 기능 중 id검색 기능
				
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
					}//방 멤버들 입력
					
					int daycheck=Integer.parseInt(value[num+3]);
					int placecheck=Integer.parseInt(value[num+4]);
					
					if(daycheck==1){
						day=value[num+5];
						starttime=Integer.parseInt(value[num+6]);
						dayreturnvalue=Integer.parseInt(value[num+7]);
						
						if(placecheck==0){							
							place=value[num+8];
							
							conn.setAutoCommit(false);//DB작업시작

							query="INSERT INTO Room(Name,Number,Daycheck,Daystart,Daystarttime,Dayreturnvalue,Placecheck,Placedefault) VALUES(?,?,?,?,?,?,?,?)";//방생성
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
							
						}//장소 고정
						else if(placecheck==1||placecheck==2){
							selectgu=Integer.parseInt(value[num+8]);	

							conn.setAutoCommit(false);//DB작업시작

							query="INSERT INTO Room(Name,Number,Daycheck,Daystart,Daystarttime,Dayreturnvalue,Placecheck,Placerangedefault) VALUES(?,?,?,?,?,?,?,?)";//방생성
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
						}//지역 근처
						else if(placecheck==3||placecheck==4){
							
							conn.setAutoCommit(false);//DB작업시작

							query="INSERT INTO Room(Name,Number,Daycheck,Daystart,Daystarttime,Dayreturnvalue,Placecheck) VALUES(?,?,?,?,?,?,?)";//방생성
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
						}//먼장소
					}//정기모임일 시
					else {
						if(placecheck==0){
							place=value[num+5];	

							conn.setAutoCommit(false);//DB작업시작

							query="INSERT INTO Room(Name,Number,Daycheck,Placecheck,Placedefault) VALUES(?,?,?,?,?)";//방생성
							pstm=conn.prepareStatement(query);
							pstm.setString(1,roomname);
							pstm.setInt(2,num);
							pstm.setInt(3,daycheck);
							pstm.setInt(4,placecheck);
							pstm.setString(5,place);
							pstm.executeUpdate();
							pstm.close();
							
						}//장소 고정
						else if(placecheck==1||placecheck==2){
							selectgu=Integer.parseInt(value[num+5]);	

							conn.setAutoCommit(false);//DB작업시작

							query="INSERT INTO Room(Name,Number,Daycheck,Placecheck,Placerangedefault) VALUES(?,?,?,?,?)";//방생성
							pstm=conn.prepareStatement(query);
							pstm.setString(1,roomname);
							pstm.setInt(2,num);
							pstm.setInt(3,daycheck);
							pstm.setInt(4,placecheck);
							pstm.setInt(5,selectgu);
							pstm.executeUpdate();
							pstm.close();
						}//지역 근처

						else if(placecheck==3||placecheck==4){	

							conn.setAutoCommit(false);//DB작업시작

							query="INSERT INTO Room(Name,Number,Daycheck,Placecheck) VALUES(?,?,?,?)";//방생성
							pstm=conn.prepareStatement(query);
							pstm.setString(1,roomname);
							pstm.setInt(2,num);
							pstm.setInt(3,daycheck);
							pstm.setInt(4,placecheck);
							pstm.executeUpdate();
							pstm.close();
						}//먼장소
					}//비정기모임일시
										
					int roomid=-1;
					query="SELECT LAST_INSERT_ID();";//방아이디 가져오기				
					rs=stmt.executeQuery(query);
					if(rs.next()){
						roomid=rs.getInt(1);
						rs.close();
					}
					
					for(int j=0;j<mlist.size();j++)
					{
						subquery="SELECT * FROM Login WHERE ID='"+mlist.get(j)+"'";//아이디 유효 검사					
						rs=stmt.executeQuery(subquery);
						
						if(rs.next())
						{
							query="INSERT INTO Roommember VALUES("+roomid+",'"+mlist.get(j)+"',TRUE)";			
							stmt.executeUpdate(query);
							rs.close();
							
							if(j!=0)  //내가 아닌 다른 방 멤버에게 방 생성됐다고 알림.
							{
								System.out.println("다른 친구 아이디 : "+mlist.get(j));
								query="SELECT regID FROM Login WHERE ID='"+mlist.get(j)+"'";
								rs=stmt.executeQuery(query);
								
								if(rs.next())
								{
									gcmserver gcm=new gcmserver(rs.getString(1),0);
									gcm.sendMessage();
								}
							}
							
						}//방 멤버 등록
					}
					if(rs!=null){
						rs.close();
					}
					
					int result=1;
					bufferWriter.write(Integer.toString(result)+"/"+Integer.toString(roomid)+"\n");
					bufferWriter.flush();//클라이언트에 결과 보내기	
					conn.commit();//DB작업 끝
				}//방 기능 중 방 생성 기능
				
				
				else if(Integer.parseInt(value[0])==4)
				{
					String ID=value[1];					
					//아이디를 받아왔고, 이제 디비에서 그 아이디의 방목록을 찾아서 클라이언트로 보내주면 됨.
					
					ArrayList<Integer> roomlist=new ArrayList<Integer>();
					ArrayList<String> roomnamelist=new ArrayList<String>();
					
					conn.setAutoCommit(false);//DB작업시작
					
					
					query="SELECT RoomID FROM Roommember WHERE ID='"+ID+"'";
					rs=stmt.executeQuery(query);	
					
					while(rs.next()){	
						roomlist.add(rs.getInt(1));
					}//방 id 리스트 가져오기
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
							}//방이름 정보가 있다면
							else{
								roomnamelist.add("방 정보 없음");
							}//방이름 정보가 없다면
						}					
						pstm.close(); 
						
						info="1/"+Integer.toString(roomsize)+"/";
						for(int i=0;i<roomsize;i++){
							info=info+Integer.toString(roomlist.get(i))+"/";
						}
						for(int i=0;i<roomsize;i++){
							info=info+roomnamelist.get(i)+"/";
						}
					}//사용자가 속한 방이 있는 경우
					else
					{
						info="3/";
					}//사용자가 속한 방이 없는 경우

					query="SELECT regID FROM Login WHERE ID='sungkyu'";
					rs=stmt.executeQuery(query);
					
					if(rs.next())  //내가 아닌 다른 방 멤버에게 방 생성됐다고 알림.
					{
							gcmserver gcm=new gcmserver(rs.getString(1),0);
							gcm.sendMessage();
					}
					
					bufferWriter.write(info+"\n");
					bufferWriter.flush();//클라이언트에 결과 보내기	
					conn.commit();//DB작업 끝					
				}//아이디 조회해서 방 목록 보여주기
				
				else if(Integer.parseInt(value[0])==5)
				{
					int roomid=Integer.parseInt(value[1]);
					
					ArrayList<String> memberlist=new ArrayList<String>();
					
					conn.setAutoCommit(false);//DB작업시작
					
					query="SELECT ID FROM Roommember WHERE RoomID='"+roomid+"'";
					rs=stmt.executeQuery(query);
					
					while(rs.next()){	
						memberlist.add(rs.getString(1));
					}//방에 속한 멤버 아이디 리스트 가져오기

					if(rs!=null){
						rs.close();
					}
					
					int placecheck=0,gu=0;
					
					query="SELECT Placecheck FROM Room WHERE RoomID='"+roomid+"'";
					rs=stmt.executeQuery(query);
					
					if(rs.next()){
						placecheck=rs.getInt(1);
					}//방의 장소 정보 가져오기

					if(placecheck==1 || placecheck==2)
					{
						query="SELECT Placerangedefault FROM Room WHERE RoomID='"+roomid+"'";
						rs=stmt.executeQuery(query);
						if(rs.next()){
							gu=rs.getInt(1);
						}//방에서 선택한 구 정보 가져오기
					}
					else
					{
						gu=0;
					}
					
					if(rs!=null){
						rs.close();
					}
					
					int num=memberlist.size();//멤버수
					
					query="SELECT Finished FROM Meet WHERE RoomID='"+roomid+"'";
					rs=stmt.executeQuery(query);
					
					int flag=0;  //약속이 없을때
					while(rs.next()){
						if(rs.getBoolean(1)==false)
							flag=1;   //진행중인 약속이 있을 때
					}//현재 방에 있는 약속 개수 찾기

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
										
					bufferWriter.flush();//클라이언트에 결과 보내기	
					conn.commit();//DB작업 끝		
				}//방id로 방정보 조회
				
				else if(Integer.parseInt(value[0])==6)
				{					
					int roomid=Integer.parseInt(value[1]);
					String startdate=value[2];
					int range=Integer.parseInt(value[3]);
					String ID=value[4];//부른사람
					int newmeetid=1;

					
					conn.setAutoCommit(false);//DB작업시작
					
					query="SELECT Max(MeetID) FROM Meet WHERE RoomID="+roomid;
					rs=stmt.executeQuery(query);	
					int maxmeetid=-1;
					if(rs.next()){
						maxmeetid=rs.getInt(1);
						newmeetid=maxmeetid+1;
						rs.close();
					}//최대값 가져옴
										
					query="INSERT INTO Meet(RoomID,MeetId,StartDate,DateRange) VALUES(?,?,?,?)";//약속생성
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
						
						
						if(id.equals(ID)==false)    //내가 아닌 다른 친구들에게 약속이 생성되었다고 보냄.
						{
							System.out.println("다른 친구 아이디 : "+id);
							pstm2.setString(1,id);
							rs2=pstm2.executeQuery();
							
							if(rs2.next())
							{
								gcmserver gcm=new gcmserver(rs2.getString(1),2);
								gcm.sendMessage();
							}
							
						}
					}// 선택 db에 삽입.
					
					pstm.close();
					pstm2.close();
					if(rs2!=null){
						rs2.close();
					}
					if(rs!=null){
						rs.close();
					}
					
					bufferWriter.write("1/\n");
					bufferWriter.flush();//클라이언트에 결과 보내기	
					conn.commit();//DB작업 끝	
				}//약속 생성시.
				
				else if(Integer.parseInt(value[0])==7)
				{				
					conn.setAutoCommit(false);//DB작업시작
					String finfo="";
					int flag=1;
					String ID;
					for(int i=1;i<value.length;i++){
						ID=value[i];
						subquery="SELECT * FROM Login WHERE ID='"+ID+"'";
						//ID 조회
						rs=stmt.executeQuery(subquery);						
						if(rs.next()){							
							System.out.println("ID 있음");
							rs.close();
						}//아이디 있음
						else{
							flag=2;
							System.out.println("ID 없음");
							finfo=finfo+ID+"/";							
						}//아이디 없음			
					}
					
					bufferWriter.write(Integer.toString(flag)+"/"+finfo+"\n");
					bufferWriter.flush();//클라이언트에 결과 보내기
					conn.commit();//DB 작업 완료				
				}//친구 리스트 갱신시
				
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
					conn.setAutoCommit(false);//DB작업시작
				
					query="SELECT Choice FROM Meet WHERE RoomID='"+roomid+"'"+" AND Finished=0";
					rs=stmt.executeQuery(query);	
					
					if(rs.next()){
						choice=rs.getInt(1);
						rs.close();
					}//현재진행중인 약속이 있을시 값 가져옴
					
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
						}//현재진행중인 약속이 있을시 값 가져옴
						
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
						}//현재진행중인 약속이 있을시 값 가져옴
						
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
					bufferWriter.flush();//클라이언트에 결과 보내기	
					conn.commit();//DB작업 끝
				}//지난약속 검색
				
				else if(Integer.parseInt(value[0])==9){
					String ID=value[1];
					String PW=value[2];
					conn.setAutoCommit(false);//DB작업시작
					subquery="SELECT * FROM Login WHERE ID='"+ID+"' AND PW='"+PW+"'";
					//ID와 PW 확인				
					rs=stmt.executeQuery(subquery);
					if(rs.next()){						
						System.out.println("회원 정보 확인 완료.");
						rs.close();
						query="SELECT RoomID FROM Roommember WHERE ID='"+ID+"'";//roomid검색
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
								query="UPDATE Room SET Number=Number-1 WHERE RoomID="+roomidarr.get(i);//참가한 방에 인원 한명씩 감소시킴
								stmt.executeUpdate(query);
							}
						}//참가한 방이 있음
						query="DELETE FROM Login WHERE ID='"+ID+"'";//로그인 테이블에서 삭제
						stmt.executeUpdate(query);
						int result=1;
						bufferWriter.write(Integer.toString(result)+"\n");
						bufferWriter.flush();//클라이언트에 결과 보내기						
					}//회원 정보 확인 후 탈퇴
					else{						
						System.out.println("회원 정보 오류");
						int result=2;
						bufferWriter.write(Integer.toString(result)+"\n");
						bufferWriter.flush();//클라이언트에 결과 보내기
					}//정보가 맞지 않음(ID,PW 오류)	
					conn.commit();//DB 작업 완료	
				}//회원탈퇴
				
				else if(Integer.parseInt(value[0])==10){
					String ID=value[1];
					String PW=value[2];
					String newPW=value[3];
					conn.setAutoCommit(false);//DB작업시작
					subquery="SELECT * FROM Login WHERE ID='"+ID+"' AND PW='"+PW+"'";
					//ID와 PW 확인				
					rs=stmt.executeQuery(subquery);
					if(rs.next()){						
						System.out.println("회원 정보 확인 완료.");
						rs.close();
						query="UPDATE Login SET PW='"+newPW+"' WHERE ID='"+ID+"'";//비번 변경
						stmt.executeUpdate(query);
						
						int result=1;
						bufferWriter.write(Integer.toString(result)+"\n");
						bufferWriter.flush();//클라이언트에 결과 보내기						
					}//회원 정보 확인 후 패스워드 변경
					else{						
						System.out.println("회원 정보 오류");
						int result=2;
						bufferWriter.write(Integer.toString(result)+"\n");
						bufferWriter.flush();//클라이언트에 결과 보내기
					}//정보가 맞지 않음(ID,PW 오류)	
					conn.commit();//DB 작업 완료	
				}//비번 변경
				
				else if(Integer.parseInt(value[0])==11){
					int roomid=Integer.parseInt(value[1]);
					String startdate="";
					int range=0;
					
					conn.setAutoCommit(false);//DB작업시작
					subquery="SELECT StartDate,DateRange FROM Meet WHERE RoomID='"+roomid+"' AND Finished=0";
					//ID와 PW 확인		
					
					rs=stmt.executeQuery(subquery);
					
					if(rs.next()){
						startdate=rs.getString(1);
						range=rs.getInt(2);
						rs.close();
					}
					
					bufferWriter.write("1/"+startdate+"/"+Integer.toString(range)+"/\n");
					bufferWriter.flush();//클라이언트에 결과 보내기	
					conn.commit();//DB작업 끝		
				}//시작 날짜와 날짜범위 받아오기.
			
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
					
					conn.setAutoCommit(false);//DB작업시작
					subquery="SELECT MeetID FROM Meet WHERE RoomID="+roomid+" AND Finished=0";
					rs=stmt.executeQuery(subquery);
					if(rs.next()){
						meetid=rs.getInt(1);
						rs.close();
					}			
					
					query="UPDATE MeetIDChoice SET Selected=TRUE, SelectValue='"+choicemsg+"' WHERE RoomID="+roomid+" AND MeetID="+meetid+" AND ID='"+ID+"'";
					stmt.executeUpdate(query);//사용자 선택 집어넣음
					System.out.println("저장완료!\n");
					
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
						}//Selected값이 false이면 빠져나옴.						
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
						}//초기화 작업
						
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
						}//결과를 더함
						rs.close();
						
						int flag2=0;//초기상태 : 0  전 값이 3일 때 : 1 전 값이 3이 아닐때:  0 
						ArrayList<String> timelist= new ArrayList<String>();
						//날짜/시작시간/끝나는시간 형태로 저장 예)2015-03-28/3/6
						
						Calendar cal=Calendar.getInstance();						
			        	SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
			        	String now=format.format(cal.getTime());//오늘날짜
			        	
			        	Date start=format.parse(startdate);
			        	Date now2=format.parse(now);
			        	cal.setTime(start);		        	
	        		
						for(int i=0;i<range;i++)//날짜
						{
				        	String dt=format.format(cal.getTime());//현재 scope 날짜
				        	Date dt2=format.parse(dt);//현재 scope 날짜
				        	cal.add(cal.DATE,+1);//하나 증가
				        	int s=0,e=0;//시작시간 끝나는시간
				        	
							if(dt2.getTime() >= now2.getTime()) {//지금 날짜거나 그것보다 나중일 때만 계산-> 즉 유효한 날짜만 계산 가능하게 함
				                 System.out.println("유효 범위 안에 있음");
				                 for(int j=0;j<16;j++){//시간									
				                	 if(flag2==0){
				                		 if(d[i][j]==num){
				                			 s=j+9;//시작 시간 입력
				                			 e=s;//시작시간과 끝나는시간 같게 설정
				                			 flag2=1;
				                			 if(j==15){
				                				 timelist.add(dt+"/"+s+"/"+e);
				                			 }
				                		 }//지금값이 3일때
				                	 }//전 값이 3이 아닐때
				                	 else{
				                		 if(d[i][j]==num){
				                			 e++;//끝나는 시간을 하나 더한다
				                			 if(j==15){
				                				 timelist.add(dt+"/"+s+"/"+e);
				                			 }
				                		 }//지금값이 3일 때
				                		 else{
				                			 timelist.add(dt+"/"+s+"/"+e);
				                			 flag2=0;
				                		 }//지금값이 3이 아닐때
				                	 }//전 값이 3일 때
				                 }
				            } 
				        	else {
				        		System.out.println("유효범위 밖에 있음");
				        		continue;//생략하고 다음으로 넘어감
				            }						        	
						}//범위 계산하여 timelist에 넣음
						
						query="UPDATE MeetIDChoice SET Selected=FALSE, SelectValue='' WHERE RoomID="+roomid+" AND MeetID="+meetid;
						stmt.executeUpdate(query);//초기화 함.
						
						if(timelist.size()<1)
						{							
							System.out.println("날짜없음!");
							
							query="SELECT ID FROM Roommember WHERE RoomID='"+roomid+"'";
							rs=stmt.executeQuery(query);
							
							while(rs.next())
							{
								String id=rs.getString(1);
								if(id.equals(ID)==false)    //내가 아닌 다른 친구들에게 날짜 다시 정하라고 보냄.
								{
									System.out.println("다른 친구 아이디 : "+id);
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
						}//날짜 없음- meetidchoice 초기화
						
						else if(timelist.size()==1){
							System.out.println("날짜가 딱 하나!");
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
								
								if(id.equals(ID)==false)    //내가 아닌 다른 친구들에게 날짜 정해졌다고 보냄.
								{
									System.out.println("다른 친구 아이디 : "+id);
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
							stmt.executeUpdate(query);//약속 정보 저장
							
							bufferWriter.write("4/"+date+"/"+stime+"/"+etime+"\n");
							
						}//날짜가 딱 하나- meetidchoice 초기화 & meet에 날짜 정보 등록 및 choice를 2로 바꾸기& 클라이언트에게 결과 보내기
						
						else{
							System.out.println("날짜많음 투표해야함!");
							
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
							}//최장시간 만날 수 있는 날짜 선택
							
							query="UPDATE Meet SET Day='"+date+"', Starttime="+stime+", Endtime="+etime+",Choice=2 WHERE RoomID="+roomid+" AND MeetID="+meetid;
							stmt.executeUpdate(query);//약속 정보 저장
						
							bufferWriter.write("4/"+date+"/"+stime+"/"+etime+"\n");
						}//날짜가 많음- meetidchoice 초기화 & meet에 날짜 정보 등록 및 choice를 2로 바꾸기& 클라이언트에게 결과 보내기					
						
						bufferWriter.flush();//클라이언트에 결과 보내기	
						conn.commit();
					}//모든 사람이 선택 했을시 계산.
					
					else
					{
						bufferWriter.write("2/\n");
						bufferWriter.flush();//클라이언트에 결과 보내기
						conn.commit();	
						System.out.println("기다려야함!\n");
					}//모든 사람이 선택하진 않았을 때
				}//일정 저장
				
				else if(Integer.parseInt(value[0])==13){
					int roomid=Integer.parseInt(value[1]);
					int daycheck=-1;
					int placecheck=-1;			
					
					conn.setAutoCommit(false);//DB작업시작
				
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
											
						choice=2;//choice를 2로 자동으로 올려줌
						if(dayreturnvalue==1){
							dayreturnvalue_val=7;
						}//1주
						else if(dayreturnvalue==2){
							dayreturnvalue_val=14;
						}//2주
						else if(dayreturnvalue==3){
							dayreturnvalue_val=1;
						}//한달
						else if(dayreturnvalue==4){
							dayreturnvalue_val=2;
						}//두달
						
						query="SELECT Max(MeetID) FROM Meet WHERE RoomID="+roomid;
						rs=stmt.executeQuery(query);	
						int maxmeetid=-1;
						if(rs.next()){
							maxmeetid=rs.getInt(1);
							newmeetid=maxmeetid+1;
							rs.close();
						}//최대값 가져옴
						
						query="SELECT Day FROM Meet WHERE RoomID="+roomid+" AND MeetID="+maxmeetid;
						rs=stmt.executeQuery(query);
						
						String maxday="";
						
						if(rs.next()){
							maxday=rs.getString(1);
							rs.close();
						}//최대날짜 가져옴
						
						
						Calendar cal=Calendar.getInstance();						
			        	SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
			        	String now=format.format(cal.getTime());//오늘날짜
			        	
			        	Date start=format.parse(daystart);//처음모임 시작 날짜
			        	Date now2=format.parse(now);//오늘날짜
			        	
			        	if(maxday.length()>0){
			        		Date recent=format.parse(maxday);//제일 최근 날짜
			        		cal.setTime(recent);
			        		while(recent.getTime() <= now2.getTime()) {
			        			if(dayreturnvalue==1||dayreturnvalue==2){
			        				cal.add(cal.DATE,+dayreturnvalue_val);//주기만큼 증가
			        			}
			        			else{
			        				cal.add(cal.MONTH,+dayreturnvalue_val);//주기만큼 증가
			        			}
			        			String dt=format.format(cal.getTime());//현재 scope 날짜
					        	recent=format.parse(dt);//현재 scope 날짜
					        	
			        		}
			        	}
			        	else{
			        		cal.setTime(start);
			        		while(start.getTime() <= now2.getTime()) {					        	
			        			if(dayreturnvalue==1||dayreturnvalue==2){
			        				cal.add(cal.DATE,+dayreturnvalue_val);//주기만큼 증가
			        			}
			        			else{
			        				cal.add(cal.MONTH,+dayreturnvalue_val);//주기만큼 증가
			        			}
			        			String dt=format.format(cal.getTime());//현재 scope 날짜
					        	start=format.parse(dt);//현재 scope 날짜
			        		}
			        	}
			        	
			        	newday=format.format(cal.getTime());//새 날짜
						
			        	query="INSERT INTO Meet(RoomID,MeetId,Day,Starttime,Choice) VALUES(?,?,?,?,?)";//약속생성
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
						}//정기 모임이고 장소 정해짐
						
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
						}// 선택 db에 삽입.
						
						if(rs!=null){
							rs.close();
						}
						
						bufferWriter.write("3/\n");
					}//정기모임일 시만 meet 자동 생성					
					else{
						bufferWriter.write("4/\n");	
					}//비정기 모임일시..					

					bufferWriter.flush();//클라이언트에 결과 보내기	
					conn.commit();//DB작업 끝					
				}//방정보조회후 방생성 혹은 그냥 리턴
				
				else if(Integer.parseInt(value[0])==14)
				{
					int gu=Integer.parseInt(value[1]);
					int theme=Integer.parseInt(value[2]);
					String m="";
					int i=0;
					
					conn.setAutoCommit(false);//DB작업시작
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
					bufferWriter.flush();//클라이언트에 결과 보내기	
					conn.commit();//DB작업 끝
				}//구 번호와 테마 번호를 이용해서 장소 찾기.
				
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
					
					conn.setAutoCommit(false);//DB작업시작
					
					subquery="SELECT MeetID FROM Meet WHERE RoomID="+roomid+" AND Finished=0";
					rs=stmt.executeQuery(subquery);
					if(rs.next()){
						meetid=rs.getInt(1);
						rs.close();
					}			
					
					query="UPDATE MeetIDChoice SET Selected=TRUE, SelectValue='"+selectmsg+"' WHERE RoomID="+roomid+" AND MeetID="+meetid+" AND ID='"+id+"'";
					stmt.executeUpdate(query);//사용자 선택 집어넣음
					System.out.println("저장완료!\n");
					
					subquery="SELECT Selected FROM MeetIDChoice WHERE RoomID='"+roomid+"' AND MeetID="+meetid;
					rs=stmt.executeQuery(subquery);
					int flag=0;
					Boolean c;
					while(rs.next()){					
						c=rs.getBoolean(1);
						if(!c){
							flag=1;
							break;
						}//Selected값이 false이면 빠져나옴.						
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
						}//결과를 더함
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
						stmt.executeUpdate(query);//초기화 함.
						
						
						if(place.size()==1)
						{
							String placevalue=place.get(0);
							query="SELECT ID FROM Roommember WHERE RoomID='"+roomid+"'";
							rs=stmt.executeQuery(query);
							
							while(rs.next())
							{
								String fid=rs.getString(1);
								
								if(fid!=id)    //내가 아닌 다른 친구들에게 장소 정해졌다고 보냄.
								{
									System.out.println("다른 친구 아이디 : "+fid);
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
							stmt.executeUpdate(query);//약속 정보 저장
							
							bufferWriter.write("2/"+placevalue+"/\n");
							bufferWriter.flush();//클라이언트에 결과 보내기	
							conn.commit();//DB작업 끝
							
						}//가장 많이 고른 장소가 하나일때.
						
						else if(place.size()>1)
						{
							int placevalue=(int)((Math.random())*place.size());
							String selectedplace=place.get(placevalue);
							
							query="UPDATE Meet SET Place='"+selectedplace+"',Choice=3 WHERE RoomID="+roomid+" AND MeetID="+meetid;
							stmt.executeUpdate(query);//약속 정보 저장
							
							query="SELECT ID FROM Roommember WHERE RoomID='"+roomid+"'";
							rs=stmt.executeQuery(query);
							
							while(rs.next())
							{
								String fid=rs.getString(1);
								
								if(fid!=id)    //내가 아닌 다른 친구들에게 랜덤으로 장소 정해졌다고 보냄.
								{
									System.out.println("다른 친구 아이디 : "+fid);
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
							bufferWriter.flush();//클라이언트에 결과 보내기	
							conn.commit();//DB작업 끝
						}//제일 많이 고른 장소가 여럿일때.
						
						else
						{
							query="SELECT ID FROM Roommember WHERE RoomID='"+roomid+"'";
							rs=stmt.executeQuery(query);
														
							bufferWriter.write("3/\n");
							bufferWriter.flush();//클라이언트에 결과 보내기
							conn.commit();
						}//결과가 없을때.
					}//모든 사람이 선택 했을시 계산.
					
					else
					{
						bufferWriter.write("4/\n");
						bufferWriter.flush();//클라이언트에 결과 보내기
						conn.commit();	
						System.out.println("기다려야함!\n");
					}//모든 사람이 선택하진 않았을 때
					
				}//장소 저장
				
				else if(Integer.parseInt(value[0])==16)
				{
					int theme=Integer.parseInt(value[1]);
					int subway=Integer.parseInt(value[2]);
					String m="";
					int i=0;
					int flag=0;
					
					conn.setAutoCommit(false);//DB작업시작
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
					bufferWriter.flush();//클라이언트에 결과 보내기	
					conn.commit();//DB작업 끝
				}//지하철 호선과 테마번호로 장소 검색.
				
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
					
					conn.setAutoCommit(false);//DB작업시작
					
					subquery="SELECT MeetID FROM Meet WHERE RoomID="+roomid+" AND Finished=0";
					rs=stmt.executeQuery(subquery);
					if(rs.next()){
						meetid=rs.getInt(1);
						rs.close();
					}//약속아이디 검색	
														
					
					query="UPDATE MeetIDChoice SET Selected=TRUE, SelectValue='"+choicemsg+"' WHERE RoomID="+roomid+" AND MeetID="+meetid+" AND ID='"+ID+"'";
					stmt.executeUpdate(query);//사용자 선택 집어넣음
					System.out.println("저장완료!\n");
					
					subquery="SELECT Selected FROM MeetIDChoice WHERE RoomID='"+roomid+"' AND MeetID="+meetid;
					rs=stmt.executeQuery(subquery);
					
					int flag=0;
					Boolean c;
					while(rs.next()){
						c=rs.getBoolean(1);
						if(!c){
							flag=1;
							break;
						}//Selected값이 false이면 빠져나옴.						
					}
					rs.close();
					
					if(flag!=1)
					{
						query="SELECT SelectValue FROM MeetIDChoice WHERE RoomID='"+roomid+"'";
						rs=stmt.executeQuery(query);
						
						ArrayList<String> gfoodlist=new ArrayList<String>();//좋은 음식 저장리스트
						ArrayList<String> bfoodlist=new ArrayList<String>();//싫은 음식 저장리스트
						ArrayList<Integer> gselectnum=new ArrayList<Integer>();//좋은 음식 선택 갯수(다수가 선택한 것 고르기 위해)						
						
						int i=0;
						
						while(rs.next())
						{
							String selected=rs.getString(1);
							String[] v=selected.split("[/]");
							
							if(i==0)
							{
								int gnum=Integer.parseInt(v[0]);//좋은 음식 갯수 저장
								for(int j=1;j<1+gnum;j++){
									gfoodlist.add(v[j]);
									gselectnum.add(1);
								}
								int bnum=Integer.parseInt(v[1+gnum]);//싫은 음식 갯수 저장
								for(int j=2+gnum;j<2+gnum+bnum;j++){
									bfoodlist.add(v[j]);									
								}
							}//첫 사람 선택지 저장시
							else
							{
								int gnum=Integer.parseInt(v[0]);//좋은 음식 갯수 저장
								for(int j=1;j<1+gnum;j++){
									if(gfoodlist.contains(v[j])){
										int index=gfoodlist.indexOf(v[j]);
										int a=gselectnum.get(index)+1;
										gselectnum.set(index, a);										
									}//이미 선호 리스트에 있을때. 선호도를 하나 증가시켜줌
									else{
										gfoodlist.add(v[j]);
										gselectnum.add(1);
									}//새로운 선호 음식일 때								
								}
								
								int bnum=Integer.parseInt(v[1+gnum]);//싫은 음식 갯수 저장
								for(int j=2+gnum;j<2+gnum+bnum;j++){
									if(bfoodlist.contains(v[j])==false){
										bfoodlist.add(v[j]);										
									}//새로운 비선호 음식일 때.							
								}								
							}
							i++;//사람 수 세기
						}//결과를 더함
						rs.close();

						for(int k=0;k<gfoodlist.size();k++){
							String f=gfoodlist.get(k);
							if(bfoodlist.contains(f)){
								gfoodlist.remove(k);
								gselectnum.remove(k);
							}
						}// 비선호음식으로 선택된 음식은 제외
						
						
						int max=-1;
						ArrayList<String> food=new ArrayList<String>();
						
						for(int j=0;j<gfoodlist.size();j++)
						{
							if(max<gselectnum.get(j))
							{
								food.removeAll(food);//리스트 갱신
								max=gselectnum.get(j);
								food.add(gfoodlist.get(j));
							}//최대갑 변경
							else if(max==gselectnum.get(j))
							{
								food.add(gfoodlist.get(j));
							}
						}//최대 인원이 선호하는 음식 계산

						query="UPDATE MeetIDChoice SET Selected=FALSE, SelectValue='' WHERE RoomID="+roomid+" AND MeetID="+meetid;
						stmt.executeUpdate(query);//초기화 함.
						
						
						if(food.size()==1)
						{
							String foodvalue=food.get(0);
							/*
							query="SELECT ID FROM Roommember WHERE RoomID='"+roomid+"'";
							rs=stmt.executeQuery(query);
							
							while(rs.next())
							{
								String fid=rs.getString(1);
								
								if(fid!=ID)    //내가 아닌 다른 친구들에게 장소 정해졌다고 보냄.
								{
									System.out.println("다른 친구 아이디 : "+fid);
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
							stmt.executeUpdate(query);//약속 정보 저장
							
							bufferWriter.write("2/"+foodvalue+"/\n");
							bufferWriter.flush();//클라이언트에 결과 보내기	
							conn.commit();//DB작업 끝
							
						}//가장 많이 고른 음식이 하나일때.
						
						else if(food.size()>1)
						{
							int foodvalue=(int)((Math.random())*food.size());
							String selectedfood=food.get(foodvalue);
							
							query="UPDATE Meet SET food='"+selectedfood+"',Choice=4, Finished=1 WHERE RoomID="+roomid+" AND MeetID="+meetid;
							stmt.executeUpdate(query);//약속 정보 저장
							
							/*
							query="SELECT ID FROM Roommember WHERE RoomID='"+roomid+"'";
							rs=stmt.executeQuery(query);
							
							while(rs.next())
							{
								String fid=rs.getString(1);
								
								if(fid!=id)    //내가 아닌 다른 친구들에게 랜덤으로 장소 정해졌다고 보냄.
								{
									System.out.println("다른 친구 아이디 : "+fid);
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
							bufferWriter.flush();//클라이언트에 결과 보내기	
							conn.commit();//DB작업 끝
						}//제일 많이 고른 음식이 여럿일때.
						
						else
						{
							System.out.println("음식없음!");							
							bufferWriter.write("4/\n");						
							bufferWriter.flush();//클라이언트에 결과 보내기
							conn.commit();					
						}//결과가 없을때.						
					}//모든 사람이 선택했을 때.
					else{
						conn.commit();
						bufferWriter.write("3/\n");
						bufferWriter.flush();//클라이언트에 결과 보내기	
					}//기다려야 할때
				}//음식 저장.
				
				else if(Integer.parseInt(value[0])==18){
					int roomid=Integer.parseInt(value[1]);
					String place="";
					conn.setAutoCommit(false);//DB작업시작
					
					subquery="SELECT Placedefault FROM Room WHERE RoomID="+roomid;
					rs=stmt.executeQuery(subquery);
					
					if(rs.next()){
						place=rs.getString(1);
						rs.close();
					}
					
					query="UPDATE Meet SET Place='"+place+"', Choice=3 WHERE RoomID="+roomid+" AND Finished=0";
					stmt.executeUpdate(query);//사용자 선택 집어넣음
					System.out.println("저장완료!\n");
					
					conn.commit();//DB작업 끝
					
					bufferWriter.write("2/\n");
					bufferWriter.flush();//클라이언트에 결과 보내기	
					
				}//장소 고정일시 meet 테이블에 place값 삽입
				
				else if(Integer.parseInt(value[0])==19){
					String ID=value[1];
					String regID=value[2];
					conn.setAutoCommit(false);//DB작업시작
					
					query="UPDATE Login SET regID='"+regID+"' WHERE ID='"+ID+"'";//비번 변경
					stmt.executeUpdate(query);
					
					int result=5;
					bufferWriter.write(Integer.toString(result)+"\n");
					bufferWriter.flush();//클라이언트에 결과 보내기			
					
					conn.commit();//DB 작업 완료
					
					
				}//로그인할때 기기 아이디 등록.
				
				else if(Integer.parseInt(value[0])==20){
					conn.setAutoCommit(false);//DB작업시작
					int roomid,range,choice;
					String startdate;
					
					System.out.println("알람시작!!!");
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
				        	String now=format.format(cal.getTime());//오늘날짜
				        	
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
				        	String now=format.format(cal.getTime());//오늘날짜
				        	
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
							System.out.println("성공적으로 지워졌어요!!");
							

							query="SELECT regID FROM login where ID IN (SELECT ID From roommember where RoomID=?)";
							pstm=conn.prepareStatement(query);
							pstm.setInt(1, roomid);
							ResultSet rs2=pstm.executeQuery();
							
							while(rs2.next())
							{								
								System.out.println("다른 친구 아이디 : "+rs2.getString(1));
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
					System.out.println("알람 보내기 성공");
					 
					int result=5;
					bufferWriter.write(Integer.toString(result)+"\n");
					bufferWriter.flush();//클라이언트에 결과 보내기			
					
					conn.commit();//DB 작업 완료
					
					
				}//로그인할때 기기 아이디 등록.
				else if(Integer.parseInt(value[0])==21){
					int category=Integer.parseInt(value[1]);
					String m="";
					int i=0;
					
					conn.setAutoCommit(false);//DB작업시작
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
					bufferWriter.flush();//클라이언트에 결과 보내기	
					conn.commit();//DB작업 끝
				}//음식 카테고리 검색

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
			//리소스 반환 작업
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