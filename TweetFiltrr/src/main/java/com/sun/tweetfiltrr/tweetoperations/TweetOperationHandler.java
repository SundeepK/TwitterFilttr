package com.sun.tweetfiltrr.tweetoperations;

import android.graphics.Color;
import android.util.Log;
import android.view.View;

import com.sun.tweetfiltrr.activity.adapter.mergeadapters.SingleTweetAdapter;
import com.sun.tweetfiltrr.database.dao.IDBDao;
import com.sun.tweetfiltrr.fragment.pulltorefresh.IProgress;
import com.sun.tweetfiltrr.parcelable.ParcelableTweet;
import com.sun.tweetfiltrr.tweetoperations.api.ITwitterOperation;
import com.sun.tweetfiltrr.tweetoperations.api.ITwitterOperationTask;
import com.sun.tweetfiltrr.tweetoperations.api.TwitterOperationTask;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import twitter4j.TwitterException;

/**
 * Created by Sundeep.Kahlon on 29/01/14.
 */
public class TweetOperationHandler implements SingleTweetAdapter.OnTweetOperation, TweetOperationTask.OnTwitterTaskComplete {

    private static final String TAG = TweetOperationHandler.class.getName();
    private final IProgress _progressBar;
    private final ConcurrentHashMap<ParcelableTweet, Collection<ITwitterOperationTask<ITwitterOperation>>> _twitterOperationsMap;
    private final IDBDao<ParcelableTweet> _tweetDao;
    private final ITwitterOperation _favouriteTweet = new FavouriteTweet();
    private final ITwitterOperation _retweet = new RetweetTweet();

    public TweetOperationHandler(IProgress progressBar_, IDBDao<ParcelableTweet> tweetDao_){
        _progressBar = progressBar_;
        _twitterOperationsMap = new ConcurrentHashMap<ParcelableTweet, Collection<ITwitterOperationTask<ITwitterOperation>>>();
        _tweetDao = tweetDao_;
    }


    @Override
    public void onTweetFav(View view_, ParcelableTweet tweet_) {
        view_.setEnabled(false);
        view_.setBackgroundColor(Color.rgb(71,71,71));
        submitTask(view_, tweet_, _favouriteTweet);
    }

    @Override
    public void onReTweet(View view_,ParcelableTweet tweet_) {
        view_.setEnabled(false);
        view_.setBackgroundColor(Color.rgb(71,71,71));
        submitTask(view_, tweet_, _retweet);
    }

    @Override
    public void onReplyTweet(View view_,ParcelableTweet tweet_) {

    }

    @Override
    public void onQuoteTweet(View view_,ParcelableTweet tweet_) {

    }

    private void submitTask(View view_,
                                        ParcelableTweet tweet_, ITwitterOperation operation_){
        Collection<ITwitterOperationTask<ITwitterOperation>> operations = _twitterOperationsMap.get(tweet_);
        if(operations != null){
            submitTask(operations, view_,tweet_, operation_);
        }else{
            operations = new ArrayList<ITwitterOperationTask<ITwitterOperation>>();
            ITwitterOperationTask<ITwitterOperation> op =  getSubmittableTask(view_, tweet_, operation_);
            operations.add(op);
            _twitterOperationsMap.put(tweet_,operations);
        }
    }

    private void submitTask( Collection<ITwitterOperationTask<ITwitterOperation>> operations_, View view_,
                             ParcelableTweet tweet_, ITwitterOperation operation_){
        final Iterator<ITwitterOperationTask<ITwitterOperation>> itr = operations_.iterator();
        ITwitterOperationTask<ITwitterOperation> newTask = null;
        while(itr.hasNext()){
            ITwitterOperationTask<ITwitterOperation> task = itr.next();
                if(!itr.hasNext()){
                    newTask = getSubmittableTask(view_,tweet_, operation_);
                }
        }

        if(newTask != null){
            operations_.add(newTask);
        }
    }

    private ITwitterOperationTask<ITwitterOperation> getSubmittableTask(View view_, ParcelableTweet tweet_, ITwitterOperation operation_){
        TweetOperationTask task = new TweetOperationTask(_progressBar,_tweetDao, tweet_, this );
        task.execute(operation_);
        return new TwitterOperationTask(view_, task);

    }



    @Override
    public void onSuccessfulComplete(ParcelableTweet tweet_) {
        Collection<ITwitterOperationTask<ITwitterOperation>> operations = _twitterOperationsMap.get(tweet_);

        if (!operations.isEmpty()) {

            final Iterator<ITwitterOperationTask<ITwitterOperation>> itr = operations.iterator();
            while (itr.hasNext()) {
                ITwitterOperationTask<ITwitterOperation> task = itr.next();
                if (task.isComplete()) {
                    if (!task.isFailed()) {
                        itr.remove();
                    }
                }
            }
        } else {
            _twitterOperationsMap.remove(tweet_);
        }

    }

    @Override
    public void onTaskFail(ParcelableTweet tweetThatFailed_,TwitterException exception_) {
        Collection<ITwitterOperationTask<ITwitterOperation>> operations = _twitterOperationsMap.get(tweetThatFailed_);
        Log.v(TAG, "on task failed");

        if (!operations.isEmpty()) {
            final Iterator<ITwitterOperationTask<ITwitterOperation>> itr = operations.iterator();
            while (itr.hasNext()) {
                ITwitterOperationTask<ITwitterOperation> task = itr.next();
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
            _twitterOperationsMap.remove(tweetThatFailed_);
        }
    }

}
