package com.sun.tweetfiltrr.twitter.tweetprocessor.impl;


import android.util.Log;

import com.sun.tweetfiltrr.parcelable.ParcelableTweet;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;
import com.sun.tweetfiltrr.parcelable.parcelable.api.ICachedUser;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import twitter4j.Status;

/**
 * Created by Sundeep on 17/12/13.
 */
@Singleton
public class PlainTweetProcessor extends ATweetProcessor {
    private static final String TAG = PlainTweetProcessor.class.getName();

    /**
     * <p/>
     * Base class for runnable's which need to retrieve tweets from twitter. It provides default functionality to convert
     * tweets to {@link com.sun.tweetfiltrr.parcelable.ParcelableTweet} and add them to the current {@link com.sun.tweetfiltrr.parcelable.ParcelableUser} so that they can later
     * be updated in the database.
     *
     * This class ensures that we do not filter tweets on dates, so we essentially keep all tweets that is returned via
     * twitter api.
     *
     * @param dateFormat_    The {@link java.text.SimpleDateFormat} that is used to manipulate {@link java.util.Date}
     */
    @Inject
    public PlainTweetProcessor(ThreadLocal<SimpleDateFormat> dateFormat_) {
        super(dateFormat_);
    }

    @Override
    public void cacheLastIDs(ParcelableUser user_) {
        final List<ParcelableTweet> timeLine = user_.getUserTimeLine();
        if (!timeLine.isEmpty()) {
            ParcelableTweet timelineFirst = timeLine.get(timeLine.size() - 1);
            ParcelableTweet timelineLast = timeLine.get(0);
            Log.v(TAG, "Setting new maxID " + timelineLast.getTweetID());
            user_.setSinceId(timelineLast.getTweetID());
            user_.setMaxId( timelineFirst.getTweetID());
        }
        //update the total tweets recieved
        user_.setTotalTweetCount(user_.getTotalTweetCount()+ timeLine.size());
        Log.v(TAG, "Setting new tweet total to  " + user_.getTotalTweetCount()+ " for user " + user_.getScreenName());
    }
    /**
     *
     * This doesn't take tweet date into consideration so it will process all tweets
     *
     * @param iterator_ {@link java.util.Iterator} which contains the {@link twitter4j.Status} to process and extract tweets from
     * @param friend_ {@link com.sun.tweetfiltrr.parcelable.ParcelableUser} to associate the tweet to.
     * @param today_ the date to check tweets against
     * @return
     */
    @Override
    public Collection<ParcelableUser> processTimeLine(Iterator<Status> iterator_, ICachedUser friend_, Date today_){
        return super.processTimeLine(iterator_,friend_,null);
    }

    @Override
    protected void processTweet(ParcelableTweet tweetToProcess_) {

    }

}
