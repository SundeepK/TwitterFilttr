package com.sun.tweetfiltrr.twitter.tweetoperations.impl;

import android.graphics.Color;
import android.util.Log;
import android.view.View;

import com.sun.tweetfiltrr.activity.adapter.mergeadapters.SingleTweetAdapter;
import com.sun.tweetfiltrr.database.dao.api.IDBDao;
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
        ParcelableTweet tweet = getTweet(user_);
        if(!tweet.isFavourite()){
            view_.setBackgroundColor(Color.rgb(71,71,71));
//            tweet.setIsFavourite(false);
        }else{
            view_.setBackgroundColor(Color.rgb(0,0,0));
//            tweet.setIsFavourite(true);
        }
        view_.setEnabled(false);
        view_.setTag(tweet);
        submitTask(view_, (user_), _favouriteTweet);
    }

    @Override
    public void onReTweet(View view_,ParcelableUser user_) {
        ParcelableTweet tweet = getTweet(user_);
        if(tweet.isRetweeted()){
            view_.setBackgroundColor(Color.rgb(0,0,0));
            view_.setEnabled(false);
        }else{
            view_.setTag(tweet);
            view_.setBackgroundColor(Color.rgb(71,71,71));
            view_.setEnabled(true);
            tweet.setIsRetweeted(true);
            submitTask(view_, (user_), _retweet);
        }
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
    public void onTwitterApiCallSuccess(ParcelableUser user_, ITwitterAPICall apiCallType_) {
        ParcelableTweet tweet = getTweet(user_);
        Collection<ITwitterOperationTask<ITwitterAPICall>> operations = _twitterOperationsMap.get(tweet);
        Log.v(TAG, "success called");
        if (!operations.isEmpty()) {
            final Iterator<ITwitterOperationTask<ITwitterAPICall>> itr = operations.iterator();
            while (itr.hasNext()) {
                ITwitterOperationTask<ITwitterAPICall> task = itr.next();
                if (task.isRunning() && !task.isFailed()) {
                    ParcelableTweet isFavView = (ParcelableTweet) task.getView().getTag();
                    if (isFavView != null) {
                        if (apiCallType_.getTweetOperationType() == ITwitterAPICall.TwitterAPICallType.POST_FAVOURITE) {
                            task.getView().setEnabled(true);
                        } else {
                            task.getView().setEnabled(false);
                        }
                        task.getView().setBackgroundColor(Color.rgb(71, 71, 71));
                        task.getView().setTag(null);
                    }
                    itr.remove();
                }
            }
        } else {
            _twitterOperationsMap.remove(tweet);
        }

        if (_listener != null) {
            _listener.onTwitterApiCallSuccess(user_, apiCallType_);
        }
    }

    @Override
    public void onTwitterApiCallFail(ParcelableUser failedUser_, TwitterException exception_, ITwitterAPICall tweetType_) {
        ParcelableTweet tweet = getTweet(failedUser_);
        Collection<ITwitterOperationTask<ITwitterAPICall>> operations = _twitterOperationsMap.get(tweet);
        if (!operations.isEmpty()) {
            final Iterator<ITwitterOperationTask<ITwitterAPICall>> itr = operations.iterator();
            while (itr.hasNext()) {
                ITwitterOperationTask<ITwitterAPICall> task = itr.next();
                if (task.isRunning() && task.isFailed()) {
                    ParcelableTweet taggedTweet = (ParcelableTweet) task.getView().getTag();
                    if (taggedTweet != null) {
                        if (tweetType_.getTweetOperationType() == ITwitterAPICall.TwitterAPICallType.POST_FAVOURITE) {
                            taggedTweet.setIsFavourite(false);
                            task.getView().setEnabled(true);
                        } else if (tweetType_.getTweetOperationType() == ITwitterAPICall.TwitterAPICallType.POST_RETWEET) {
                            if (exception_.getStatusCode() != 403) {
                                taggedTweet.setIsRetweeted(false);
                                task.getView().setEnabled(true);
                            }
                        }
                        task.getView().setTag(null);
                    }
                    itr.remove();
                    View v = task.getView();
                    v.setEnabled(true);
                    v.setBackgroundColor(Color.BLACK);
                }
            }
        } else {
            _twitterOperationsMap.remove(tweet);
        }

        if (_listener != null) {
            _listener.onTwitterApiCallFail(failedUser_, exception_, tweetType_);
        }
    }
}
