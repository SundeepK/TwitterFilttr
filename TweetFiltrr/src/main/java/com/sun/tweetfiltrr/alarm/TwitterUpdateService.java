package com.sun.tweetfiltrr.alarm;

import android.app.IntentService;
import android.content.Intent;

import com.sun.tweetfiltrr.twitter.tweetoperations.KeywordTweetUpdateRetriever;
import com.sun.tweetfiltrr.utils.TwitterUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TwitterUpdateService extends IntentService {

	private static final String TAG = TwitterUpdateService.class.getName();
    KeywordTweetUpdateRetriever _keywordTweetUpdateRetriever;

	public TwitterUpdateService() {
		super("Twitter update service");
    }

    private void initService(){
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        _keywordTweetUpdateRetriever = new KeywordTweetUpdateRetriever(executorService, getContentResolver());
    }


	@Override
	protected void onHandleIntent(Intent intent) {
        initService();
		if (TwitterUtil.hasInternetConnection(getApplicationContext())) {
            _keywordTweetUpdateRetriever.searchForKeywordTweetUpdates(this);
        }
	}

}
