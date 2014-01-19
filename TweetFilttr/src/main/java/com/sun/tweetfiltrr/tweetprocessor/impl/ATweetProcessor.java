package com.sun.tweetfiltrr.tweetprocessor.impl;

import android.util.Log;

import com.sun.tweetfiltrr.asyncretriever.api.ATweetRetiever;
import com.sun.tweetfiltrr.parcelable.ParcelableTimeLineEntry;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;
import com.sun.tweetfiltrr.tweetprocessor.api.ITweetProcessor;
import com.sun.tweetfiltrr.utils.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import twitter4j.Status;

/**
 * Created by Sundeep on 16/12/13.
 * ThreadSafe
 */
public abstract class ATweetProcessor implements ITweetProcessor {

    private static final String TAG = ATweetRetiever.class.getName();
    private ThreadLocal<SimpleDateFormat> _simpleDateFormatThreadLocal;


    /**
     * Threadsafe calss
     *
     * Base class for runnable's which need to retrieve tweets from twitter. It provides default functionality to convert
     * tweets to {@link ParcelableTimeLineEntry} and add them to the current {@link com.sun.tweetfiltrr.parcelable.ParcelableUser} so that they can later
     * be updated in the database.

     * @param dateFormat_
     *          The {@link java.text.SimpleDateFormat} that is used to manipulate {@link java.util.Date}
     */
    public ATweetProcessor(final ThreadLocal<SimpleDateFormat> dateFormat_) {
        _simpleDateFormatThreadLocal = dateFormat_;   //  new SimpleDateFormat(TwitterConstants.SIMPLE_DATE_FORMATE);
    }


    /**
     * Processes a {@link twitter4j.Status}, convert them into {@link com.sun.tweetfiltrr.parcelable.ParcelableTimeLineEntry}
     * timeline and adds to the user's timeline {@link java.util.Collection}.
     *
     * The friend's last updatedate time will also be set to the date passed in, so we know when the last update occured
     *
     * @param iterator_ {@link java.util.Iterator} which contains the {@link twitter4j.Status} to process and extract tweets from
     * @param friend_ {@link com.sun.tweetfiltrr.parcelable.ParcelableUser} to associate the tweet to.
     * @param today_ the date to check tweets against
     * @return boolean if {@link #processTimeLine(java.util.Iterator, com.sun.tweetfiltrr.parcelable.ParcelableUser, java.util.Date)}
     *         continue to process more {@link twitter4j.Status}
     *
     */
    public boolean processTimeLine(Iterator<Status> iterator_, ParcelableUser friend_, Date today_, boolean shouldRunOnce_){
            SimpleDateFormat dateFormat = _simpleDateFormatThreadLocal.get();
            while (iterator_.hasNext()) {
                Status tweet = iterator_.next();
                if(!processTweet(tweet,today_, friend_, dateFormat)){
                    friend_.setLastUpadateDate(dateFormat.format(DateUtils.getCurrentDate()));
                    return false;
                }
            }
        Log.v(TAG, "I'm done with processTimeline with shouldRunOnce:" + shouldRunOnce_);
        //might as well the lastUpdateTime while were at it
        friend_.setLastUpadateDate(dateFormat.format(DateUtils.getCurrentDate()));

        if(shouldRunOnce_){
            Log.v(TAG, "breaking from processtimeline");
            return false;
        }

        Log.v(TAG, "not breaking returing true");
       return true;
    }



    /**
     *
     * Process the actual tweet to convert it to a {@link com.sun.tweetfiltrr.parcelable.ParcelableTimeLineEntry} and add it
     * to the user's internal {@link java.util.Collection} of {@link com.sun.tweetfiltrr.parcelable.ParcelableTimeLineEntry} tweets
     * @param tweet {@link twitter4j.Status} which contains the tweet info
     * @param today_ {@link java.util.Date} is used to break early if the tweet {@link java.util.Date} does not match the
     *                                     specified date and so we can filter out tweets older than
     *                                     the specified {@link java.util.Date}. Can be null.
     * @param friend_ {@link com.sun.tweetfiltrr.parcelable.ParcelableUser} to associate the tweet to
     * @param dateFormat_ {@link java.text.SimpleDateFormat} used to format the date to a readable format
     * @return true if the tweet {@link java.util.Date} is newer than the one specified in the parameter
     */
    private boolean processTweet(Status tweet, Date today_,
                                 ParcelableUser friend_, SimpleDateFormat dateFormat_){
        Date tweetCreateAt = tweet.getCreatedAt();

        if(today_ != null){
            if (tweetCreateAt.before(today_)) // have this configurable, so that user can check however old he wants
                return false;
        }

        Log.v(TAG, "------------------------------------------------");
        Log.v(TAG, tweet.getText());
        Log.v(TAG, "" + tweet.getInReplyToScreenName());
        Log.v(TAG, "Tweet date: " + tweetCreateAt.toString());

        ParcelableTimeLineEntry timeLineEntry = getTimeLineEntry(tweet, friend_, dateFormat_, tweetCreateAt);

        processTweet(timeLineEntry);

        friend_.addTimeLineEntry(timeLineEntry);
        Log.v(TAG, "timeline ToString + " + timeLineEntry.toString());
        return true;
    }

    private ParcelableTimeLineEntry getTimeLineEntry(Status tweet_,ParcelableUser friend_,
                                                     SimpleDateFormat dateFormat_, Date tweetCreateAt_ ){
       return  new ParcelableTimeLineEntry(tweet_, dateFormat_.format(tweetCreateAt_), tweet_.getUser().getId());
    }


    protected abstract void processTweet(ParcelableTimeLineEntry tweetToProcess_);


}
