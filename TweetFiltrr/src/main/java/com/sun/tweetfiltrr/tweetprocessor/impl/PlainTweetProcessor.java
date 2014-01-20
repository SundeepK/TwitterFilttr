package com.sun.tweetfiltrr.tweetprocessor.impl;


import com.sun.tweetfiltrr.parcelable.ParcelableTimeLineEntry;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import twitter4j.Status;

/**
 * Created by Sundeep on 17/12/13.
 */
public class PlainTweetProcessor extends ATweetProcessor {
    /**
     * Threadsafe class
     * <p/>
     * Base class for runnable's which need to retrieve tweets from twitter. It provides default functionality to convert
     * tweets to {@link com.sun.tweetfiltrr.parcelable.ParcelableTimeLineEntry} and add them to the current {@link com.sun.tweetfiltrr.parcelable.ParcelableUser} so that they can later
     * be updated in the database.
     *
     * This class ensures that we do not filter tweets on dates, so we essentially keep all tweets that is returned via
     * twitter api.
     *
     * @param dateFormat_    The {@link java.text.SimpleDateFormat} that is used to manipulate {@link java.util.Date}
     */
    public PlainTweetProcessor(ThreadLocal<SimpleDateFormat> dateFormat_) {
        super(dateFormat_);
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
    public boolean processTimeLine(Iterator<Status> iterator_, ParcelableUser friend_, Date today_, boolean shouldRunOnce_){
        return super.processTimeLine(iterator_,friend_,null, shouldRunOnce_);
    }

    @Override
    protected void processTweet(ParcelableTimeLineEntry tweetToProcess_) {

    }

}
