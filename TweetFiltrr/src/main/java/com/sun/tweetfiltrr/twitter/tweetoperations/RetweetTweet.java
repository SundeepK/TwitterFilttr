package com.sun.tweetfiltrr.twitter.tweetoperations;

import android.util.Log;

import com.sun.tweetfiltrr.parcelable.ParcelableTweet;
import com.sun.tweetfiltrr.twitter.tweetoperations.api.IOnTweetOperationFail;
import com.sun.tweetfiltrr.twitter.tweetoperations.api.ITweetOperation;
import com.sun.tweetfiltrr.utils.TwitterUtil;

import java.text.SimpleDateFormat;

import twitter4j.Twitter;
import twitter4j.TwitterException;

/**
 * Created by Sundeep on 01/01/14.
 */
public  class RetweetTweet implements ITweetOperation {

    private static final String TAG = RetweetTweet.class.getName() ;


    public RetweetTweet(){
    }

    @Override
    public ParcelableTweet performTwitterOperation(ParcelableTweet tweets_,IOnTweetOperationFail failLister_ ) {
        Twitter twitter = TwitterUtil.getInstance().getTwitter();
        ParcelableTweet tweet = null;
        SimpleDateFormat format = TwitterUtil.getInstance().getSimpleDateFormatThreadLocal().get();

        try {
            twitter4j.Status status = twitter.retweetStatus(tweets_.getTweetID());
            tweet = new ParcelableTweet(status, format.format(status.getCreatedAt()), status.getUser().getId());
            tweet.setIsRetweeted(status.isRetweeted());
        } catch (TwitterException e) {
            e.printStackTrace();
            Log.v(TAG, "Error occured whilst trying to re-tweet");
            failLister_.onTweetOperationFail(e, this);
        }

        return tweet;
    }

    @Override
    public TweetOperationType getTweetOperationType() {
        return TweetOperationType.RETWEET;
    }


}
