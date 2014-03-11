package com.sun.tweetfiltrr.alarm;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.sun.tweetfiltrr.R;
import com.sun.tweetfiltrr.activity.activities.UserHomeActivity;
import com.sun.tweetfiltrr.application.TweetFiltrrApplication;
import com.sun.tweetfiltrr.twitter.tweetoperations.impl.KeywordTweetUpdateRetriever;
import com.sun.tweetfiltrr.utils.TwitterUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dagger.ObjectGraph;

public class TwitterUpdateService extends IntentService {

	private static final String TAG = TwitterUpdateService.class.getName();
    private KeywordTweetUpdateRetriever _keywordTweetUpdateRetriever;
    private static final int NOTIFICATION_ID = 0x90;

	public TwitterUpdateService() {
		super("TweetFiltrr update service");
    }

    private void initService(){
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        ObjectGraph graph =((TweetFiltrrApplication) getApplication()).getObjectGraph();
        _keywordTweetUpdateRetriever = new KeywordTweetUpdateRetriever(executorService, graph);
    }


    private void displayNotification(int newTweetCount_){
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this)
                        .setContentTitle("TweetFiltrr")
                        .setSmallIcon(R.drawable.twitter_logo_blue_transparent)
                        .setContentText(newTweetCount_ + " new tweets found");
        Intent resultIntent = new Intent(this, UserHomeActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(UserHomeActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent( 0, PendingIntent.FLAG_UPDATE_CURRENT );
        notificationBuilder.setContentIntent(resultPendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());

    }



	@Override
	protected void onHandleIntent(Intent intent) {
        initService();
		if (TwitterUtil.hasInternetConnection(this)) {
            int totalUpdates =  _keywordTweetUpdateRetriever.searchForKeywordTweetUpdates(this);
            if(totalUpdates > 0){
                displayNotification(totalUpdates);
            }
        }
	}

}
