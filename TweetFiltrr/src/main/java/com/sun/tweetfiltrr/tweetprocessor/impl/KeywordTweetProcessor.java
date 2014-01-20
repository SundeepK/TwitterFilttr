package com.sun.tweetfiltrr.tweetprocessor.impl;


import com.sun.tweetfiltrr.parcelable.ParcelableTimeLineEntry;

import java.text.SimpleDateFormat;

/**
 * Created by Sundeep on 17/12/13.
 */
public class KeywordTweetProcessor extends DateBasedTweetProcessor {
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
    public KeywordTweetProcessor(ThreadLocal<SimpleDateFormat> dateFormat_) {
        super(dateFormat_);
    }


    /**
     * We just want to process the tweet and indicate that its a keyword tweet
     * @param tweetToProcess_
     */
    @Override
    protected void processTweet(ParcelableTimeLineEntry tweetToProcess_) {
        tweetToProcess_.setIsKeyWordSearedTweet(true);
    }



}
