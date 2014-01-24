package com.sun.tweetfiltrr.tweetoperations;

import android.util.Log;

import com.sun.tweetfiltrr.parcelable.ParcelableTimeLineEntry;
import com.sun.tweetfiltrr.tweetoperations.api.ITwitterOperation;
import com.sun.tweetfiltrr.utils.TwitterUtil;

import twitter4j.Twitter;
import twitter4j.TwitterException;

/**
 * Created by Sundeep on 01/01/14.
 */
public  class PostTweet implements ITwitterOperation {

    private static final String TAG = PostTweet.class.getName() ;


    public PostTweet(){
    }

    @Override
    public void performTwitterOperation(ParcelableTimeLineEntry tweets_) {
        Twitter twitter = TwitterUtil.getInstance().getTwitter();
        try {
            twitter.updateStatus(tweets_.getTweetText());
        } catch (TwitterException e) {
            e.printStackTrace();
            Log.v(TAG, "Error occured whilst trying to re-tweet");
        }
    }


}
