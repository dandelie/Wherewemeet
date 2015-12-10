package com.example.test;

import java.io.UnsupportedEncodingException;
import com.google.android.gcm.GCMBaseIntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;



public class GCMIntentService extends GCMBaseIntentService 
{		
	private static void generateNotification(Context context, String message) throws UnsupportedEncodingException 
	{
		int icon = R.drawable.ic_launcher;
		long when = System.currentTimeMillis();
		String title = "약속 정해주는 여자";
		
		Intent notificationIntent = new Intent(context, MainActivity.class);
		PendingIntent intent = PendingIntent.getActivity(context, 0,notificationIntent, 0);
		
		NotificationCompat.Builder builder = new NotificationCompat.Builder(
                context);
		Notification notification = builder.setContentIntent(intent)
                .setSmallIcon(icon).setTicker(title).setWhen(when)
                .setAutoCancel(true).setContentTitle(title)
                .setContentText(message).build();

 
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		
		
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		
		
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notificationManager.notify(0, notification);
	}
	
	@Override
	protected void onError(Context arg0, String arg1) {
	 
	}

	@Override
	protected void onMessage(Context context, Intent intent) {

		String msg= intent.getStringExtra("msg");
		try {
			generateNotification(context,msg);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	 
	protected void onRegistered(Context context, String reg_id) {
	}
	 
	@Override
	 
	protected void onUnregistered(Context arg0, String arg1) {
	}
 
}
