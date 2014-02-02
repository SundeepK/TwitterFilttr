package com.sun.tweetfiltrr.twitter.tweetoperations;

import android.util.Log;

import com.sun.tweetfiltrr.database.dao.IDBDao;
import com.sun.tweetfiltrr.parcelable.ParcelableTweet;
import com.sun.tweetfiltrr.smoothprogressbarwrapper.SmoothProgressBarWrapper;
import com.sun.tweetfiltrr.utils.TwitterUtil;

import java.util.ArrayList;
import java.util.Collection;

import twitter4j.Twitter;

/**
 * Created by Sundeep on 01/01/14.
 */
public abstract class PerformTweetOperation extends AsyncSmoothProgressBarTask<TwitterOperation, Void, Collection<ParcelableTweet>> {

    private static final String TAG = PerformTweetOperation.class.getName() ;
    private final IDBDao<ParcelableTweet> _timelineDao;

    public PerformTweetOperation(SmoothProgressBarWrapper progressBar_, IDBDao<ParcelableTweet> timelineDao_){
        super(progressBar_);
        _timelineDao = timelineDao_;
    }

    @Override
    protected void onPostExecute( Collection<ParcelableTweet> status) {
        _timelineDao.insertOrUpdate(status);
        Log.v(TAG, "Updating database");
        super.onPostExecute(status);
    }

    @Override
    protected Collection<ParcelableTweet> doInBackground(TwitterOperation... params) {
        Collection<ParcelableTweet> timeLineEntries = new ArrayList<ParcelableTweet>();
        Twitter twitter = TwitterUtil.getInstance().getTwitter();
        for (TwitterOperation tweetToRetweet : params) {
            timeLineEntries.addAll(tweetToRetweet.processTwitterOperations());
        }
        return timeLineEntries;
    }

}
