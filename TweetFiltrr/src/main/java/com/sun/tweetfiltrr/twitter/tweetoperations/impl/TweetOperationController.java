package com.sun.tweetfiltrr.twitter.tweetoperations.impl;

import android.graphics.Color;
import android.util.Log;
import android.view.View;

import com.sun.tweetfiltrr.activity.adapter.mergeadapters.SingleTweetAdapter;
import com.sun.tweetfiltrr.database.dao.IDBDao;
import com.sun.tweetfiltrr.fragment.pulltorefresh.IProgress;
import com.sun.tweetfiltrr.parcelable.ParcelableTweet;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;
import com.sun.tweetfiltrr.twitter.api.ITwitterAPICall;
import com.sun.tweetfiltrr.twitter.tweetoperations.api.ITwitterOperationTask;
import com.sun.tweetfiltrr.twitter.tweetoperations.api.TwitterOperationTask;
import com.sun.tweetfiltrr.twitter.api.ITwitterAPICallStatus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import twitter4j.TwitterException;

/**
 * Created by Sundeep.Kahlon on 29/01/14.
 */
public class TweetOperationController implements SingleTweetAdapter.OnTweetOperation, ITwitterAPICallStatus {

    private static final String TAG = TweetOperationController.class.getName();
    private final IProgress _progressBar;
    private final ConcurrentHashMap<ParcelableTweet, Collection<ITwitterOperationTask<ITwitterAPICall>>> _twitterOperationsMap;
    private final IDBDao<ParcelableTweet> _tweetDao;
    private final ITwitterAPICall<ParcelableTweet> _favouriteTweet = new FavouriteTweet();
    private final ITwitterAPICall<ParcelableTweet> _retweet = new RetweetTweet();
    private final ITwitterAPICallStatus _listener;

    public TweetOperationController(IProgress progressBar_, IDBDao<ParcelableTweet> tweetDao_){
            this(progressBar_, tweetDao_, null);
    }

    public TweetOperationController(IProgress progressBar_, IDBDao<ParcelableTweet> tweetDao_, ITwitterAPICallStatus listener_){
        _progressBar = progressBar_;
        _twitterOperationsMap = new ConcurrentHashMap<ParcelableTweet, Collection<ITwitterOperationTask<ITwitterAPICall>>>();
        _tweetDao = tweetDao_;
        _listener = listener_;
    }


    @Override
    public void onTweetFav(View view_, ParcelableUser user_) {
        view_.setEnabled(false);
        view_.setBackgroundColor(Color.rgb(71,71,71));
        submitTask(view_, (user_), _favouriteTweet);
    }

    @Override
    public void onReTweet(View view_,ParcelableUser user_) {
        view_.setEnabled(false);
        view_.setBackgroundColor(Color.rgb(71,71,71));
        submitTask(view_, (user_), _retweet);
    }

    @Override
    public void onReplyTweet(View view_,ParcelableUser user_) {

    }

    @Override
    public void onQuoteTweet(View view_,ParcelableUser user_) {

    }

    private ParcelableTweet getTweet(ParcelableUser user_){
       return user_.getUserTimeLine().iterator().next(); //we only expect one tweet to reply too
    }

    private void submitTask(View view_,
                            ParcelableUser user_, ITwitterAPICall<ParcelableTweet> operation_){
        Collection<ITwitterOperationTask<ITwitterAPICall>> operations = _twitterOperationsMap.get(user_);
        if(operations != null){
            submitTask(operations, view_,user_, operation_);
        }else{
            operations = new ArrayList<ITwitterOperationTask<ITwitterAPICall>>();
            ITwitterOperationTask<ITwitterAPICall> op =  getSubmittableTask(view_, user_, operation_);
            operations.add(op);
            _twitterOperationsMap.put(getTweet(user_),operations);
        }
    }

    private void submitTask( Collection<ITwitterOperationTask<ITwitterAPICall>> operations_, View view_,
                             ParcelableUser user_, ITwitterAPICall<ParcelableTweet> operation_){
        final Iterator<ITwitterOperationTask<ITwitterAPICall>> itr = operations_.iterator();
        ITwitterOperationTask<ITwitterAPICall> newTask = null;
        while(itr.hasNext()){
            ITwitterOperationTask<ITwitterAPICall> task = itr.next();
                if(!itr.hasNext()){
                    newTask = getSubmittableTask(view_,user_, operation_);
                }
        }

        if(newTask != null){
            operations_.add(newTask);
        }
    }

    private ITwitterOperationTask<ITwitterAPICall> getSubmittableTask(View view_, ParcelableUser user_, ITwitterAPICall<ParcelableTweet> operation_){
        TweetOperationTask task = new TweetOperationTask(_progressBar,_tweetDao, user_, this );
        task.execute(operation_);
        return new TwitterOperationTask(view_, task);

    }

    @Override
    public void onTwitterApiCallSuccess(ParcelableUser user_) {
        ParcelableTweet tweet = getTweet(user_);
        Collection<ITwitterOperationTask<ITwitterAPICall>> operations = _twitterOperationsMap.get(tweet);

        if (!operations.isEmpty()) {

            final Iterator<ITwitterOperationTask<ITwitterAPICall>> itr = operations.iterator();
            while (itr.hasNext()) {
                ITwitterOperationTask<ITwitterAPICall> task = itr.next();
                if (task.isComplete()) {
                    if (!task.isFailed()) {
                        itr.remove();
                    }
                }
            }
        } else {
            _twitterOperationsMap.remove(tweet);
        }

        if(_listener != null){
            _listener.onTwitterApiCallSuccess(user_);
        }
    }

    @Override
    public void onTwitterApiCallFail(ParcelableUser failedUser_, TwitterException exception_, ITwitterAPICall tweetType_) {
        ParcelableTweet tweet = getTweet(failedUser_);
        Collection<ITwitterOperationTask<ITwitterAPICall>> operations = _twitterOperationsMap.get(tweet);
        Log.v(TAG, "on task failed");

        if (!operations.isEmpty()) {
            final Iterator<ITwitterOperationTask<ITwitterAPICall>> itr = operations.iterator();
            while (itr.hasNext()) {
                ITwitterOperationTask<ITwitterAPICall> task = itr.next();
                if (task.isRunning()) {
                    Log.v(TAG, "task complete");

                    if (task.isFailed()) {
                        Log.v(TAG, "task is failed");

                        itr.remove();
                        View v = task.getView();
                        v.setEnabled(true);
                        v.setBackgroundColor(Color.BLACK);
                    }
                }
            }
        } else {
            _twitterOperationsMap.remove(tweet);
        }

        if(_listener != null){
            _listener.onTwitterApiCallFail(failedUser_, exception_, tweetType_);
        }
    }
}
