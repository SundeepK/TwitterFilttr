package com.sun.tweetfiltrr.tweetprocessor.api;


import com.sun.tweetfiltrr.parcelable.ParcelableUser;

import java.util.Date;
import java.util.Iterator;

import twitter4j.Status;

/**
 * Created by Sundeep on 16/12/13.
 */
public interface ITweetProcessor {

    /**
     * Processes a {@link twitter4j.Status}, convert them into {@link com.sun.tweetfiltrr.parcelable.ParcelableTimeLineEntry}
     * timeline and adds to the {@link com.sun.tweetfiltrr.parcelable.ParcelableUser}'s timeline {@link java.util.Collection}.
     * @param iterator_ {@link java.util.Iterator} which contains the {@link twitter4j.Status} to process and extract tweets from
     * @param friend_ {@link com.sun.tweetfiltrr.parcelable.ParcelableUser} to associate the tweet to
     * @param today_ the date to check tweets against
     * @return boolean if {@link #processTimeLine(java.util.Iterator, com.sun.tweetfiltrr.parcelable.ParcelableUser, java.util.Date)}
     *         continue to process more {@link twitter4j.Status}
     *
     */
     public boolean processTimeLine(Iterator<Status> iterator_, ParcelableUser friend_, Date today_, boolean shouldRunOnce_);


    }
