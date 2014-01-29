package com.sun.tweetfiltrr.tweetoperations;

import android.util.Log;

import com.sun.tweetfiltrr.database.dao.IDBDao;
import com.sun.tweetfiltrr.parcelable.ParcelableTweet;
import com.sun.tweetfiltrr.smoothprogressbarwrapper.SmoothProgressBarWrapper;
import com.sun.tweetfiltrr.tweetoperations.api.ISubmittable;
import com.sun.tweetfiltrr.tweetoperations.api.ITwitterOperation;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Sundeep on 01/01/14.
 */
public class SubmittableTask extends AsyncSmoothProgressBarTask<ITwitterOperation, Void, Collection<ParcelableTweet>> implements ISubmittable<ITwitterOperation> {

    private static final String TAG = SubmittableTask.class.getName() ;
    private final IDBDao<ParcelableTweet> _timelineDao;
    private final Collection<ITwitterOperation> _operations = new ArrayList<ITwitterOperation>();
    private final ParcelableTweet _tweetToProcess;
    private final OnTwitterTaskComplete _listener;
    public interface OnTwitterTaskComplete{
        public void onComplete(ParcelableTweet tweet_);
    }

    public SubmittableTask(SmoothProgressBarWrapper progressBar_, IDBDao<ParcelableTweet> timelineDao_,
                           ParcelableTweet tweetToProcess_, OnTwitterTaskComplete listener_){
        super(progressBar_);
        _timelineDao = timelineDao_;
        _tweetToProcess = tweetToProcess_;
        _listener = listener_;
    }

    @Override
    protected void onPostExecute( Collection<ParcelableTweet> status) {
        _timelineDao.insertOrUpdate(status);
        Log.v(TAG, "Updating database");
        _listener.onComplete(_tweetToProcess);
        super.onPostExecute(status);
    }

    @Override
    protected Collection<ParcelableTweet> doInBackground(ITwitterOperation... params) {
        Collection<ParcelableTweet> timeLineEntries = new ArrayList<ParcelableTweet>();
        addArryToCollection(_operations, params);
        for (ITwitterOperation twitterOperation : _operations) {
            ParcelableTweet tweet = twitterOperation.performTwitterOperation(_tweetToProcess);
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


    @Override
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
}