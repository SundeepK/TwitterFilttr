package com.sun.tweetfiltrr.twitter.tweetoperations;

import android.util.Log;

import com.sun.tweetfiltrr.database.dao.IDBDao;
import com.sun.tweetfiltrr.database.tables.TimelineTable;
import com.sun.tweetfiltrr.fragment.pulltorefresh.IProgress;
import com.sun.tweetfiltrr.parcelable.ParcelableTweet;
import com.sun.tweetfiltrr.twitter.tweetoperations.api.IOnTweetOperationFail;
import com.sun.tweetfiltrr.twitter.tweetoperations.api.ISubmittable;
import com.sun.tweetfiltrr.twitter.tweetoperations.api.ITweetOperation;

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
public class TweetOperationTask extends AsyncSmoothProgressBarTask<ITweetOperation, Void, Collection<ParcelableTweet>>
        implements ISubmittable<ITweetOperation> , IOnTweetOperationFail {

    private static final String TAG = TweetOperationTask.class.getName() ;
    private final IDBDao<ParcelableTweet> _timelineDao;
    private final Collection<ITweetOperation> _operations = new ArrayList<ITweetOperation>();
    private final ParcelableTweet _tweetToProcess;
    private final TwitterTaskListener _listener;
    private final Map<ITweetOperation,TwitterException> _exceptions;
    private boolean _isFailed = false;



    public interface TwitterTaskListener {
        public void onTaskSuccessfulComplete(ParcelableTweet tweet_);
        public void onTaskFail(ParcelableTweet failedTweet_,TwitterException exception_, ITweetOperation tweetType_);
    }

    public TweetOperationTask(IProgress progressBar_, IDBDao<ParcelableTweet> timelineDao_,
                              ParcelableTweet tweetToProcess_, TwitterTaskListener listener_){
        super(progressBar_);
        _timelineDao = timelineDao_;
        _tweetToProcess = tweetToProcess_;
        _listener = listener_;
        _exceptions = new HashMap<ITweetOperation, TwitterException>();
    }

    public TweetOperationTask(IDBDao<ParcelableTweet> timelineDao_,
                              ParcelableTweet tweetToProcess_, TwitterTaskListener listener_){
        super();
        _timelineDao = timelineDao_;
        _tweetToProcess = tweetToProcess_;
        _listener = listener_;
        _exceptions = new HashMap<ITweetOperation, TwitterException>();
    }

    @Override
    public void onTweetOperationFail(TwitterException exception_, ITweetOperation operation_) {
        _exceptions.put(operation_, exception_);
    }

    @Override
    protected void onPostExecute( Collection<ParcelableTweet> status) {
        _timelineDao.insertOrUpdate(status, new String[]{TimelineTable.TimelineColumn.IS_FAVOURITE.s(),
                TimelineTable.TimelineColumn.IS_MENTION.s(), TimelineTable.TimelineColumn.IS_RETWEETED.s()});
        Log.v(TAG, "Updating database");

        if(!_exceptions.isEmpty()){
            Log.v(TAG, "exceptions are not empty so calling fail listener");
            _isFailed = true;

            Set<Map.Entry<ITweetOperation, TwitterException>> failedTasks = _exceptions.entrySet();
            Iterator<Map.Entry<ITweetOperation, TwitterException>> iterator = failedTasks.iterator();

            while(iterator.hasNext()){
                Map.Entry<ITweetOperation, TwitterException> entry = iterator.next();
                _listener.onTaskFail(_tweetToProcess,entry.getValue(),entry.getKey());
            }

        }else{
            _listener.onTaskSuccessfulComplete(_tweetToProcess);
        }

        super.onPostExecute(status);
    }

    @Override
    protected Collection<ParcelableTweet> doInBackground(ITweetOperation... params) {
        Collection<ParcelableTweet> timeLineEntries = new ArrayList<ParcelableTweet>();
        addArryToCollection(_operations, params);
        for (ITweetOperation twitterOperation : _operations) {
            ParcelableTweet tweet = twitterOperation.performTwitterOperation(_tweetToProcess, this);
            if(tweet != null){
                Log.v(TAG, "Fav tweet was: " + tweet.toString());
                timeLineEntries.add(tweet);
            }
        }
        return timeLineEntries;
    }



    private void addArryToCollection(Collection<ITweetOperation> operations_, ITweetOperation[] arrayOperation_){
        if(arrayOperation_ != null){
            for(ITweetOperation operation : arrayOperation_){
                operations_.add(operation);
            }
        }

    }


    public boolean submitNewTask(ITweetOperation submittable_) {
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