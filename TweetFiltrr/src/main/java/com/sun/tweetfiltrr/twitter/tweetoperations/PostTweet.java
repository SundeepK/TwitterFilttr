package com.sun.tweetfiltrr.twitter.tweetoperations;

import android.util.Log;

import com.sun.tweetfiltrr.parcelable.ParcelableTweet;
import com.sun.tweetfiltrr.twitter.tweetoperations.api.IOnTweetOperationFail;
import com.sun.tweetfiltrr.twitter.tweetoperations.api.ITwitterOperation;
import com.sun.tweetfiltrr.utils.TwitterUtil;

import java.text.SimpleDateFormat;

import twitter4j.Status;
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
    public ParcelableTweet performTwitterOperation(ParcelableTweet tweets_, IOnTweetOperationFail failLister_) {
        Twitter twitter = TwitterUtil.getInstance().getTwitter();
        ParcelableTweet tweet = null;
        SimpleDateFormat format = TwitterUtil.getInstance().getSimpleDateFormatThreadLocal().get();

        try {
           Status status = twitter.updateStatus(tweets_.getTweetText());
            tweet = new ParcelableTweet(status, format.format(status.getCreatedAt()), status.getUser().getId());
        } catch (TwitterException e) {
            e.printStackTrace();
            Log.v(TAG, "Error occured whilst trying to post a tweet");
            failLister_.onTweetOperationFail(e);
        }
        return tweet;
    }


}
