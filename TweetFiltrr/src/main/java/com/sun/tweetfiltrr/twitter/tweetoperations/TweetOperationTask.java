package com.sun.tweetfiltrr.twitter.tweetoperations;

import android.util.Log;

import com.sun.tweetfiltrr.database.dao.IDBDao;
import com.sun.tweetfiltrr.database.tables.TimelineTable;
import com.sun.tweetfiltrr.fragment.pulltorefresh.IProgress;
import com.sun.tweetfiltrr.parcelable.ParcelableTweet;
import com.sun.tweetfiltrr.twitter.tweetoperations.api.IOnTweetOperationFail;
import com.sun.tweetfiltrr.twitter.tweetoperations.api.ISubmittable;
import com.sun.tweetfiltrr.twitter.tweetoperations.api.ITwitterOperation;

import java.util.ArrayList;
import java.util.Collection;

import twitter4j.TwitterException;

/**
 * Created by Sundeep on 01/01/14.
 */
public class TweetOperationTask extends AsyncSmoothProgressBarTask<ITwitterOperation, Void, Collection<ParcelableTweet>>
        implements ISubmittable<ITwitterOperation> , IOnTweetOperationFail {

    private static final String TAG = TweetOperationTask.class.getName() ;
    private final IDBDao<ParcelableTweet> _timelineDao;
    private final Collection<ITwitterOperation> _operations = new ArrayList<ITwitterOperation>();
    private final ParcelableTweet _tweetToProcess;
    private final OnTwitterTaskComplete _listener;
    private final Collection<TwitterException> _exceptions;
    private boolean _isFailed = false;



    public interface OnTwitterTaskComplete{
        public void onSuccessfulComplete(ParcelableTweet tweet_);
        public void onTaskFail(ParcelableTweet failedTweet_,TwitterException exception_);
    }

    public TweetOperationTask(IProgress progressBar_, IDBDao<ParcelableTweet> timelineDao_,
                              ParcelableTweet tweetToProcess_, OnTwitterTaskComplete listener_){
        super(progressBar_);
        _timelineDao = timelineDao_;
        _tweetToProcess = tweetToProcess_;
        _listener = listener_;
        _exceptions = new ArrayList<TwitterException>(1);  //we will only have one exception if something goes wrong
    }

    @Override
    public void onTweetOperationFail(TwitterException exception_) {
        _exceptions.add(exception_);
    }

    @Override
    protected void onPostExecute( Collection<ParcelableTweet> status) {
        _timelineDao.insertOrUpdate(status, new String[]{TimelineTable.TimelineColumn.IS_FAVOURITE.s(),
                TimelineTable.TimelineColumn.IS_MENTION.s(), TimelineTable.TimelineColumn.IS_RETWEETED.s()});
        Log.v(TAG, "Updating database");

        if(!_exceptions.isEmpty()){
            Log.v(TAG, "exceptions are not empty so calling fail listener");
            _isFailed = true;
            _listener.onTaskFail(_tweetToProcess,_exceptions.iterator().next());
        }else{
            _listener.onSuccessfulComplete(_tweetToProcess);
        }

        super.onPostExecute(status);
    }

    @Override
    protected Collection<ParcelableTweet> doInBackground(ITwitterOperation... params) {
        Collection<ParcelableTweet> timeLineEntries = new ArrayList<ParcelableTweet>();
        addArryToCollection(_operations, params);
        for (ITwitterOperation twitterOperation : _operations) {
            ParcelableTweet tweet = twitterOperation.performTwitterOperation(_tweetToProcess, this);
            if(tweet != null){
                Log.v(TAG, "Fav tweet was: " + tweet.toString());
                timeLineEntries.add(tweet);
            }
        }
        return timeLineEntries;
    }



    private void addArryToCollection(Collection<ITwitterOperation> operations_, ITwitterOperation[] arrayOperation_){
        if(arrayOperation_ != null){
            for(ITwitterOperation operation : arrayOperation_){
                operations_.add(operation);
            }
        }

    }


    public boolean submitNewTask(ITwitterOperation submittable_) {
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


}