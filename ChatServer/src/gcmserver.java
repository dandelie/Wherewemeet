import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.MulticastResult;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;

public class gcmserver {

	String regID,date,place;
	int flag,starttime;
	
	public gcmserver(String regID,int flag,String date,int starttime)
	{
		this.regID=regID;
		this.flag=flag;
		this.date=date;
		this.starttime=starttime;	
	}//일정 정해졌다고 알릴때 (flag==1)
	
	public gcmserver(String regID,int flag,String place)
	{
		this.regID=regID;
		this.flag=flag;
		this.place=place;
	}//장소 정해졌다고 알릴때 (flag==4)
	
	public gcmserver(String regID,int flag)
	{
		this.regID=regID;
		this.flag=flag;
	}//방생성했을때 (flag==0), 약속 생성(flag==2), 일정 겹치는 거 없을때(flag==3)
	
	public void sendMessage() throws IOException {
		 
		 String temp="";
		Sender sender = new Sender("AIzaSyA1g-vHoxoq0DEhes-QJQpK0SE5cbXQibk");
		Message message = new Message.Builder().addData("msg", temp).build();
		
		if(flag==0)
		{
			temp = "방 생성이 완료되었습니다.";
			 // String test = URLEncoder.encode(temp, "euc-kr");
			message = new Message.Builder().addData("msg", "Successfully made room").build();
			  //message = new Message.Builder().addData("msg", "방생성이 완료되었습니다").build();
		}
		else if(flag==1)
		{
			//message = new Message.Builder().addData("msg", date+"  "+Integer.toString(starttime)+"  로 정해졌습니다. 그 때 만나요.").build();
			message = new Message.Builder().addData("msg", "Date : "+date+"  Time : "+Integer.toString(starttime)).build();
		}
		 
		else if(flag==2)
		{
			message = new Message.Builder().addData("msg", "Successfully made meet").build();
		}
		
		else if(flag==3)
		{
			message = new Message.Builder().addData("msg", "No meet date, Again choose please").build();
		}
		
		else if(flag==4)
		{
			message = new Message.Builder().addData("msg", "Place : "+place).build();
		}
		
		else if(flag==5)
		{
			message = new Message.Builder().addData("msg", "Place is chosen randomly. => Place : "+place).build();
		}
		
		else if(flag==6)
		{
			message = new Message.Builder().addData("msg", "Alarm success").build();
		}
		
		List<String> list = new ArrayList<String>();
		list.add(regID);
		 
		MulticastResult multiResult = sender.send(message, list, 5);
		
		if (multiResult != null) {
		 
		List<Result> resultList = multiResult.getResults();
		 
		for (Result result : resultList) {
		 
		System.out.println(result.getMessageId());
		 
		}
		 
		}
		 
		}

	public static void main(String[] args) throws Exception {
	}

}
