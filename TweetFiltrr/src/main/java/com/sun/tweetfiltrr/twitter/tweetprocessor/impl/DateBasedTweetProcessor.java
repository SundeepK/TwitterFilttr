package com.sun.tweetfiltrr.twitter.tweetprocessor.impl;


import com.sun.tweetfiltrr.parcelable.ParcelableTweet;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;
import com.sun.tweetfiltrr.parcelable.parcelable.api.ICachedUser;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import javax.inject.Inject;
import javax.inject.Singleton;

import twitter4j.Status;

/**
 * Created by Sundeep on 17/12/13.
 */
@Singleton
public class DateBasedTweetProcessor extends PlainTweetProcessor {
    private static final String  TAG  = DateBasedTweetProcessor.class.getName();

    /**
     * Threadsafe class
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
    public DateBasedTweetProcessor(ThreadLocal<SimpleDateFormat> dateFormat_) {
        super(dateFormat_);
    }

    /**
     * Takes into account dates
     *
     * @param iterator_ {@link java.util.Iterator} which contains the {@link twitter4j.Status} to process and extract tweets from
     * @param friend_ {@link com.sun.tweetfiltrr.parcelable.ParcelableUser} to associate the tweet to.
     * @param today_ the date to check tweets against
     * @return
     */
    @Override
    public Collection<ParcelableUser> processTimeLine(Iterator<Status> iterator_, ICachedUser friend_, Date today_){
        return super.processTimeLine(iterator_,friend_,today_);
    }

    /**
     * NO-OP
     * @param tweetToProcess_
     */
    @Override
    protected void processTweet(ParcelableTweet tweetToProcess_) {
        
    }

}
