package com.sun.tweetfiltrr.tweetprocessor.impl;


import android.util.Log;

import com.sun.tweetfiltrr.parcelable.ParcelableTimeLineEntry;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by Sundeep on 17/12/13.
 */
public class MentionsTweetProcessor extends DateBasedTweetProcessor {
    private static final String TAG = MentionsTweetProcessor.class.getName();

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
    public MentionsTweetProcessor(ThreadLocal<SimpleDateFormat> dateFormat_) {
        super(dateFormat_);
    }


    /**
     * We just want to process the tweet java.lang.Stringand indicate that its a keyword tweet
     * @param tweetToProcess_
     */
    @Override
    protected void processTweet(ParcelableTimeLineEntry tweetToProcess_) {
        tweetToProcess_.setIsMention(true);
    }

    @Override
    public void cacheLastIDs(ParcelableUser user_) {
        final List<ParcelableTimeLineEntry> timeLine = user_.getUserTimeLine();
        if (!timeLine.isEmpty()) {
            ParcelableTimeLineEntry timelineFirst = timeLine.get(timeLine.size() - 1);
            ParcelableTimeLineEntry timelineLast = timeLine.get(0);
            Log.v(TAG, "Setting new maxID " + timelineLast.getTweetID());
            user_.setSinceIdForMentions(timelineLast.getTweetID());
            user_.setMaxIdForMentions( timelineFirst.getTweetID());
        }
        user_.setTotalTweetCount(user_.getTotalTweetCount()+ timeLine.size());

    }
}
