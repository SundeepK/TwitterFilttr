package com.sun.tweetfiltrr.twitter.tweetoperations.impl;

import android.util.Log;

import com.sun.tweetfiltrr.parcelable.ParcelableTweet;
import com.sun.tweetfiltrr.parcelable.parcelable.api.ICachedUser;
import com.sun.tweetfiltrr.twitter.api.ITwitterAPICall;
import com.sun.tweetfiltrr.twitter.api.ITwitterAPICallStatus;
import com.sun.tweetfiltrr.utils.TwitterUtil;

import java.text.SimpleDateFormat;

import twitter4j.Twitter;
import twitter4j.TwitterException;

/**
 * Created by Sundeep on 01/01/14.
 */
public  class RetweetTweet implements ITwitterAPICall<ParcelableTweet> {

    private static final String TAG = RetweetTweet.class.getName() ;


    public RetweetTweet(){
    }

    @Override
    public ParcelableTweet retrieveTwitterData(ICachedUser user_, ITwitterAPICallStatus failLister_ ) {
        Twitter twitter = TwitterUtil.getInstance().getTwitter();
        ParcelableTweet tweet = null;
        SimpleDateFormat format = TwitterUtil.getInstance().getSimpleDateFormatThreadLocal().get();

        try {
            ParcelableTweet tweetToProcess =  user_.getUser().getUserTimeLine().iterator().next();
            twitter4j.Status status = twitter.retweetStatus(tweetToProcess.getTweetID());
            tweet = new ParcelableTweet(status, format.format(status.getCreatedAt()), status.getUser().getId());
            tweet.setIsRetweeted(status.isRetweeted());
            tweetToProcess.setIsRetweeted(true);
        } catch (TwitterException e) {
            e.printStackTrace();
            Log.v(TAG, "Error occured whilst trying to re-tweet");
            failLister_.onTwitterApiCallFail(user_.getUser(),e, this);
        }

        return tweet;
    }

    @Override
    public ITwitterAPICall.TwitterAPICallType getTweetOperationType() {
        return TwitterAPICallType.POST_RETWEET;
    }


}
