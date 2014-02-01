package com.sun.tweetfiltrr.tweetoperations;

import android.util.Log;

import com.sun.tweetfiltrr.database.dao.IDBDao;
import com.sun.tweetfiltrr.fragment.pulltorefresh.IProgress;
import com.sun.tweetfiltrr.parcelable.ParcelableTweet;
import com.sun.tweetfiltrr.tweetoperations.api.IOnTweetOperationFail;
import com.sun.tweetfiltrr.tweetoperations.api.ISubmittable;
import com.sun.tweetfiltrr.tweetoperations.api.ITwitterOperation;

import java.util.ArrayList;
import java.util.Collection;

import twitter4j.TwitterException;

/**
 * Created by Sundeep on 01/01/14.
 */
public class SubmittableTask extends AsyncSmoothProgressBarTask<ITwitterOperation, Void, Collection<ParcelableTweet>>
        implements ISubmittable<ITwitterOperation> , IOnTweetOperationFail {

    private static final String TAG = SubmittableTask.class.getName() ;
    private final IDBDao<ParcelableTweet> _timelineDao;
    private final Collection<ITwitterOperation> _operations = new ArrayList<ITwitterOperation>();
    private final ParcelableTweet _tweetToProcess;
    private final OnTwitterTaskComplete _listener;
    private final Collection<TwitterException> _exceptions;
    private boolean _isFailed = false;

    @Override
    public void onTweetOperationFail(TwitterException exception_) {
        _exceptions.add(exception_);
    }

    public interface OnTwitterTaskComplete{
        public void onSuccessfulComplete(ParcelableTweet tweet_);
        public void onTaskFail(ParcelableTweet failedTweet_,TwitterException exception_);
    }

    public SubmittableTask(IProgress progressBar_, IDBDao<ParcelableTweet> timelineDao_,
                           ParcelableTweet tweetToProcess_, OnTwitterTaskComplete listener_){
        super(progressBar_);
        _timelineDao = timelineDao_;
        _tweetToProcess = tweetToProcess_;
        _listener = listener_;
        _exceptions = new ArrayList<TwitterException>(1);  //we will only have exception if something goes wrong
    }

    @Override
    protected void onPostExecute( Collection<ParcelableTweet> status) {
        _timelineDao.insertOrUpdate(status);
        Log.v(TAG, "Updating database");

        if(!_exceptions.isEmpty()){
            _listener.onTaskFail(_tweetToProcess,_exceptions.iterator().next());
            _isFailed = true;
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


}