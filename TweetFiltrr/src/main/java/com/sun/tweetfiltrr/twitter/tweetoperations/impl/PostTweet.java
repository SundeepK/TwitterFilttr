package com.sun.tweetfiltrr.twitter.tweetoperations.impl;

import android.util.Log;

import com.sun.tweetfiltrr.parcelable.ParcelableTweet;
import com.sun.tweetfiltrr.parcelable.parcelable.api.ICachedUser;
import com.sun.tweetfiltrr.twitter.api.ITwitterAPICall;
import com.sun.tweetfiltrr.twitter.api.ITwitterAPICallStatus;
import com.sun.tweetfiltrr.utils.TwitterUtil;

import java.text.SimpleDateFormat;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

/**
 * Created by Sundeep on 01/01/14.
 */
public  class PostTweet implements ITwitterAPICall<ParcelableTweet> {

    private static final String TAG = PostTweet.class.getName() ;


    public PostTweet(){
    }

    @Override
    public ParcelableTweet retrieveTwitterData(ICachedUser user_, ITwitterAPICallStatus failLister_) {
        Twitter twitter = TwitterUtil.getInstance().getTwitter();
        ParcelableTweet tweet = null;
        ParcelableTweet tweetToProcess =  user_.getUser().getUserTimeLine().iterator().next();
        SimpleDateFormat format = TwitterUtil.getInstance().getSimpleDateFormatThreadLocal().get();

        try {
           Status status = twitter.updateStatus(tweetToProcess.getTweetText());
            tweet = new ParcelableTweet(status, format.format(status.getCreatedAt()), status.getUser().getId());
        } catch (TwitterException e) {
            e.printStackTrace();
            Log.v(TAG, "Error occured whilst trying to post a tweet");
            failLister_.onTwitterApiCallFail(user_.getUser(),e, this);
        }
        return tweet;
    }

    @Override
    public TwitterAPICallType getTweetOperationType() {
        return TwitterAPICallType.POST_TWEET;

    }


}
