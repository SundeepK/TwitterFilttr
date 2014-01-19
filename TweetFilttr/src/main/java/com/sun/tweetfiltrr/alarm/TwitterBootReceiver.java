package com.sun.tweetfiltrr.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.sun.tweetfiltrr.utils.TwitterConstants;


public class TwitterBootReceiver extends BroadcastReceiver {

	private static final String TAG = BroadcastReceiver.class.getName();
	
	@Override
	public void onReceive(Context context, Intent intent) {
	      Log.i(TAG, "TwitterBootReceiver invoked, configuring AlarmManager");
	      AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
	      PendingIntent pendingIntent =
	               PendingIntent.getBroadcast(context, 0, new Intent(context, TwitterUpdateReceiver.class), 0);

	      // use inexact repeating which is easier on battery (system can phase events and not wake at exact times)
	      alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, TwitterConstants.ALARM_TRIGGER_AT_TIME,
	    		  TwitterConstants.ALARM_INTERVAL, pendingIntent);
		
	}

}
