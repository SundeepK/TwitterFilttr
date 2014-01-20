package com.sun.tweetfiltrr.alarm;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class TwitterUpdateReceiver extends BroadcastReceiver {

	private static final String TAG = TwitterUpdateReceiver.class.getName();
	
	@Override
	public void onReceive(Context content_, Intent arg1) {
	      Log.v(TAG, "TwitterUpdateReceiver invoked, starting DealService in background");
	      content_.startService(new Intent(content_, TwitterUpdateService.class));
		
	}

}
