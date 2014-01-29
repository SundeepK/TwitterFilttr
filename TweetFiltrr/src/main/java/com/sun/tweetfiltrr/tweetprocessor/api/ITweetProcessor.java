package com.sun.tweetfiltrr.tweetprocessor.api;


import com.sun.tweetfiltrr.parcelable.ParcelableUser;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import twitter4j.Status;

/**
 * Created by Sundeep on 16/12/13.
 */
public interface ITweetProcessor {

    /**
     * Processes a {@link twitter4j.Status}, convert them into {@link com.sun.tweetfiltrr.parcelable.ParcelableTweet}
     * timeline and adds to the user's timeline {@link java.util.Collection}.
     *
     * The friend's last updatedate time will also be set to the date passed in, so we know when the last update occured
     *
     * @param iterator_ {@link java.util.Iterator} which contains the {@link twitter4j.Status} to process and extract tweets from
     * @param friend_ {@link com.sun.tweetfiltrr.parcelable.ParcelableUser} to associate the tweet to.
     * @param today_ the date to check tweets against
     * @return {@link java.util.Collection<com.sun.tweetfiltrr.parcelable.ParcelableUser>}} of users with updated timelines.
     *
     */
     public Collection<ParcelableUser> processTimeLine(Iterator<Status> iterator_, ParcelableUser friend_, Date today_);


    }
