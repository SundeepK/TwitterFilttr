package com.sun.tweetfiltrr.twitter.tweetoperations.impl;

import android.util.Log;

import com.sun.tweetfiltrr.database.dao.api.IDBDao;
import com.sun.tweetfiltrr.database.tables.TimelineTable;
import com.sun.tweetfiltrr.fragment.pulltorefresh.IProgress;
import com.sun.tweetfiltrr.parcelable.CachedFriendDetails;
import com.sun.tweetfiltrr.parcelable.ParcelableTweet;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;
import com.sun.tweetfiltrr.twitter.api.ITwitterAPICall;
import com.sun.tweetfiltrr.twitter.api.ITwitterAPICallStatus;
import com.sun.tweetfiltrr.twitter.tweetoperations.api.ISubmittable;

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
public class TweetOperationTask extends AsyncSmoothProgressBarTask<ITwitterAPICall<ParcelableTweet>, Void, Collection<TweetOperationTask.TwitterResult>>
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
    protected void onPostExecute( Collection<TwitterResult> status) {
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
            Log.v(TAG, "Updating database");
            for(TwitterResult result : status){
                _listener.onTwitterApiCallSuccess(_user, result._apiCallType);
            }
        }

        super.onPostExecute(status);
    }

    @Override
    protected Collection<TwitterResult> doInBackground(ITwitterAPICall<ParcelableTweet>... params) {
        Collection<TwitterResult> timeLineEntries = new ArrayList<TwitterResult>();
        Collection<ParcelableTweet> tweets = new ArrayList<ParcelableTweet>();
        addArryToCollection(_operations, params);
        for (ITwitterAPICall<ParcelableTweet> twitterOperation : _operations) {
            ParcelableTweet tweet = twitterOperation.retrieveTwitterData(new CachedFriendDetails(_user), this);
            if(tweet != null){
                Log.v(TAG, "Fav tweet was: " + tweet.toString());
                timeLineEntries.add(new TwitterResult(tweet, twitterOperation));
                tweets.add(tweet);
            }
        }

        _timelineDao.insertOrUpdate(tweets, new String[]{TimelineTable.TimelineColumn.TWEET_ID.s(),
                TimelineTable.TimelineColumn.IS_FAVOURITE.s(),
                TimelineTable.TimelineColumn.IS_MENTION.s(), TimelineTable.TimelineColumn.IS_RETWEETED.s()} );
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
    public void onTwitterApiCallSuccess(ParcelableUser user_,  ITwitterAPICall apiCallType_) {

    }

    @Override
    public void onTwitterApiCallFail(ParcelableUser failedTweet_, TwitterException exception_, ITwitterAPICall tweetType_) {
        _exceptions.put(tweetType_, exception_);

    }

    public class TwitterResult{
        private ParcelableTweet _user;
        private ITwitterAPICall _apiCallType;
        public TwitterResult(ParcelableTweet user_, ITwitterAPICall apiCallType_){
            _user = user_;
            _apiCallType = apiCallType_;
        }
    }
}