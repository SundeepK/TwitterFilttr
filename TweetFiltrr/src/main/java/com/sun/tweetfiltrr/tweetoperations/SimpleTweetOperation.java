package com.sun.tweetfiltrr.tweetoperations;

import android.util.Log;

import com.sun.tweetfiltrr.database.dao.IDBDao;
import com.sun.tweetfiltrr.parcelable.ParcelableTimeLineEntry;
import com.sun.tweetfiltrr.smoothprogressbarwrapper.SmoothProgressBarWrapper;
import com.sun.tweetfiltrr.utils.TwitterUtil;

import java.util.ArrayList;
import java.util.Collection;

import twitter4j.Twitter;

/**
 * Created by Sundeep on 01/01/14.
 */
public abstract class SimpleTweetOperation extends AsyncSmoothProgressBarTask<ParcelableTimeLineEntry, Void, Collection<ParcelableTimeLineEntry>> {

    private static final String TAG = SimpleTweetOperation.class.getName() ;
    private final IDBDao<ParcelableTimeLineEntry> _timelineDao;

    public SimpleTweetOperation(SmoothProgressBarWrapper progressBar_, IDBDao<ParcelableTimeLineEntry> timelineDao_){
        super(progressBar_);
        _timelineDao = timelineDao_;
    }

    @Override
    protected void onPostExecute( Collection<ParcelableTimeLineEntry> status) {
        _timelineDao.insertOrUpdate(status);
        Log.v(TAG, "Updating database");
        super.onPostExecute(status);
    }

    @Override
    protected Collection<ParcelableTimeLineEntry> doInBackground(ParcelableTimeLineEntry... params) {
        Collection<ParcelableTimeLineEntry> timeLineEntries = new ArrayList<ParcelableTimeLineEntry>();
        Twitter twitter = TwitterUtil.getInstance().getTwitter();
        for (ParcelableTimeLineEntry tweetToRetweet : params) {
                performSimpleOperation(tweetToRetweet, twitter);
                timeLineEntries.add(tweetToRetweet);
        }
        return timeLineEntries;
    }


    protected abstract void performSimpleOperation(ParcelableTimeLineEntry tweets_, Twitter twitter_);

}
