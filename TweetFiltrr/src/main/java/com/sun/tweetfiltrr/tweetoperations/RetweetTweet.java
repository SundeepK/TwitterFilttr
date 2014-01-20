package com.sun.tweetfiltrr.tweetoperations;

import android.util.Log;

import com.sun.tweetfiltrr.database.dao.ADBDao;
import com.sun.tweetfiltrr.parcelable.ParcelableTimeLineEntry;
import com.sun.tweetfiltrr.smoothprogressbarwrapper.SmoothProgressBarWrapper;

import twitter4j.Twitter;
import twitter4j.TwitterException;

/**
 * Created by Sundeep on 01/01/14.
 */
public  class RetweetTweet extends SimpleTweetOperation {

    private static final String TAG = RetweetTweet.class.getName() ;


    public RetweetTweet(SmoothProgressBarWrapper progressBar_, ADBDao<ParcelableTimeLineEntry> timelineDao_){
        super(progressBar_, timelineDao_);
    }

    @Override
    protected void performSimpleOperation(ParcelableTimeLineEntry tweets_, Twitter twitter_) {
        try {
            twitter4j.Status status = twitter_.retweetStatus(tweets_.getTweetID());
            tweets_.setIsRetweeted(status.isRetweeted());
        } catch (TwitterException e) {
            e.printStackTrace();
            Log.v(TAG, "Error occured whilst trying to re-tweet");
        }
    }


}
