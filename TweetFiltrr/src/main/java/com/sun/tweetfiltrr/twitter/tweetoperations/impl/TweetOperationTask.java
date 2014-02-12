package com.sun.tweetfiltrr.twitter.tweetoperations.impl;

import android.util.Log;

import com.sun.tweetfiltrr.database.dao.IDBDao;
import com.sun.tweetfiltrr.database.tables.TimelineTable;
import com.sun.tweetfiltrr.fragment.pulltorefresh.IProgress;
import com.sun.tweetfiltrr.parcelable.CachedFriendDetails;
import com.sun.tweetfiltrr.parcelable.ParcelableTweet;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;
import com.sun.tweetfiltrr.twitter.api.ITwitterAPICall;
import com.sun.tweetfiltrr.twitter.tweetoperations.api.ISubmittable;
import com.sun.tweetfiltrr.twitter.api.ITwitterAPICallStatus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import twitter4j.TwitterException;

/**
 * Created by Sundeep on 01/01/14.
 */
public class TweetOperationTask extends AsyncSmoothProgressBarTask<ITwitterAPICall<ParcelableTweet>, Void, Collection<ParcelableTweet>>
        implements ISubmittable<ITwitterAPICall> ,  ITwitterAPICallStatus {

    private static final String TAG = TweetOperationTask.class.getName() ;
    private final IDBDao<ParcelableTweet> _timelineDao;
    private final Collection<ITwitterAPICall<ParcelableTweet>> _operations = new ArrayList<ITwitterAPICall<ParcelableTweet>>();
    private final ParcelableUser _user;
    private final ITwitterAPICallStatus _listener;
    private final Map< ITwitterAPICall,TwitterException> _exceptions;
    private boolean _isFailed = false;

    public TweetOperationTask(IProgress progressBar_, IDBDao<ParcelableTweet> timelineDao_,
                              ParcelableUser user_, ITwitterAPICallStatus listener_){
        super(progressBar_);
        _timelineDao = timelineDao_;
        _listener = listener_;
        _exceptions = new HashMap< ITwitterAPICall, TwitterException>();
        _user = user_;
    }

    public TweetOperationTask(IDBDao<ParcelableTweet> timelineDao_,
                              ParcelableUser user_, ITwitterAPICallStatus listener_){
        super();
        _timelineDao = timelineDao_;
        _listener = listener_;
        _exceptions = new HashMap<ITwitterAPICall, TwitterException>();
        _user = user_;
    }


    @Override
    protected void onPostExecute( Collection<ParcelableTweet> status) {
        _timelineDao.insertOrUpdate(status, new String[]{TimelineTable.TimelineColumn.IS_FAVOURITE.s(),
                TimelineTable.TimelineColumn.IS_MENTION.s(), TimelineTable.TimelineColumn.IS_RETWEETED.s()});
        Log.v(TAG, "Updating database");

        if(!_exceptions.isEmpty()){
            Log.v(TAG, "exceptions are not empty so calling fail listener");
            _isFailed = true;

            Set<Map.Entry<ITwitterAPICall, TwitterException>> failedTasks = _exceptions.entrySet();
            Iterator<Map.Entry<ITwitterAPICall, TwitterException>> iterator = failedTasks.iterator();

            while(iterator.hasNext()){
                Map.Entry<ITwitterAPICall, TwitterException> entry = iterator.next();
                _listener.onTwitterApiCallFail(_user, entry.getValue(), entry.getKey());
            }

        }else{
            _listener.onTwitterApiCallSuccess(_user);
        }

        super.onPostExecute(status);
    }

    @Override
    protected Collection<ParcelableTweet> doInBackground(ITwitterAPICall<ParcelableTweet>... params) {
        Collection<ParcelableTweet> timeLineEntries = new ArrayList<ParcelableTweet>();
        addArryToCollection(_operations, params);
        for (ITwitterAPICall<ParcelableTweet> twitterOperation : _operations) {
            ParcelableTweet tweet = twitterOperation.retrieveTwitterData(new CachedFriendDetails(_user), this);
            if(tweet != null){
                Log.v(TAG, "Fav tweet was: " + tweet.toString());
                timeLineEntries.add(tweet);
            }
        }
        return timeLineEntries;
    }



    private void addArryToCollection(Collection<ITwitterAPICall<ParcelableTweet>> operations_, ITwitterAPICall<ParcelableTweet>[] arrayOperation_){
        if(arrayOperation_ != null){
            for(ITwitterAPICall<ParcelableTweet> operation : arrayOperation_){
                operations_.add(operation);
            }
        }

    }


    public boolean submitNewTask(ITwitterAPICall submittable_) {
        boolean isSubmitSuccessful = false;

        synchronized (this) {
            if (getStatus() == Status.PENDING) {
                _operations.add(submittable_);
                isSubmitSuccessful = true;
            }
        }
        return isSubmitSuccessful;
    }

    @Override
    public boolean isComplete() {
        boolean isSubmitSuccessful = false;

        synchronized (this) {
            if (getStatus() == Status.FINISHED) {
                isSubmitSuccessful = true;
            }
        }
        return isSubmitSuccessful;
    }

    @Override
    public boolean isFailed() {
        return _isFailed;
    }

    @Override
    public boolean isRunning() {
        boolean isRunning = false;

        synchronized (this) {
            if (getStatus() == Status.RUNNING) {
                isRunning = true;
            }
        }
        return isRunning;
    }


    @Override
    public void onTwitterApiCallSuccess(ParcelableUser user_) {

    }

    @Override
    public void onTwitterApiCallFail(ParcelableUser failedTweet_, TwitterException exception_, ITwitterAPICall tweetType_) {
        _exceptions.put(tweetType_, exception_);

    }
}