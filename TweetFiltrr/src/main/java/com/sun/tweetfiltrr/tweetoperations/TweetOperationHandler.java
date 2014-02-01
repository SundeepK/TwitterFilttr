package com.sun.tweetfiltrr.tweetoperations;

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

/**
 * Created by Sundeep.Kahlon on 29/01/14.
 */
public class TweetOperationHandler implements SingleTweetAdapter.OnTweetOperation, SubmittableTask.OnTwitterTaskComplete {

    private final IProgress _progressBar;
    private final ConcurrentHashMap<ParcelableTweet, Collection<ITwitterOperationTask<ITwitterOperation>>> _twitterOperationsMap;
    private final IDBDao<ParcelableTweet> _tweetDao;
    private final ITwitterOperation _favouriteTweet = new FavouriteTweet();

    public TweetOperationHandler(IProgress progressBar_, IDBDao<ParcelableTweet> tweetDao_){
        _progressBar = progressBar_;
        _twitterOperationsMap = new ConcurrentHashMap<ParcelableTweet, Collection<ITwitterOperationTask<ITwitterOperation>>>();
        _tweetDao = tweetDao_;
    }


    @Override
    public void onTweetFav(View view_, ParcelableTweet tweet_) {
        Collection<ITwitterOperationTask<ITwitterOperation>> operations = _twitterOperationsMap.get(tweet_);
        if(operations != null){
            submitTask(operations, view_,tweet_, _favouriteTweet);
        }else{
            operations = new ArrayList<ITwitterOperationTask<ITwitterOperation>>();
            ITwitterOperationTask<ITwitterOperation> op =  getSubmittableTask(view_, tweet_, _favouriteTweet);
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
            if(!task.submitNewTask(operation_)){
                if(!itr.hasNext()){
                    newTask = getSubmittableTask(view_,tweet_, operation_);
                }
            }
        }

        if(newTask != null){
            operations_.add(newTask);
        }
    }

    private ITwitterOperationTask<ITwitterOperation> getSubmittableTask(View view_, ParcelableTweet tweet_, ITwitterOperation operation_){
        SubmittableTask task = new SubmittableTask(_progressBar,_tweetDao, tweet_, this );
        task.execute(operation_);
        return new TwitterOperationTask(view_, task);

    }

    @Override
    public void onReTweet(View view_,ParcelableTweet tweet_) {

    }

    @Override
    public void onReplyTweet(View view_,ParcelableTweet tweet_) {

    }

    @Override
    public void onQuoteTweet(View view_,ParcelableTweet tweet_) {

    }

    @Override
    public void onComplete(ParcelableTweet tweet_) {
        Collection<ITwitterOperationTask<ITwitterOperation>> operations = _twitterOperationsMap.get(tweet_);
        final Iterator<ITwitterOperationTask<ITwitterOperation>> itr = operations.iterator();
        while(itr.hasNext()){
            ITwitterOperationTask<ITwitterOperation> task = itr.next();
            if(task.isComplete()){
                itr.remove();
            }
        }
    }

}
