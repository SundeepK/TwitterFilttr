package com.sun.tweetfiltrr.tweetoperations;

import com.sun.tweetfiltrr.activity.adapter.mergeadapters.SingleTweetAdapter;
import com.sun.tweetfiltrr.parcelable.ParcelableTweet;
import com.sun.tweetfiltrr.smoothprogressbarwrapper.SmoothProgressBarWrapper;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Future;

/**
 * Created by Sundeep.Kahlon on 29/01/14.
 */
public class TweetOperationHandler implements SingleTweetAdapter.OnTweetOperation {

    private final SmoothProgressBarWrapper _progressBar;
    private ConcurrentHashMap<ParcelableTweet, ConcurrentLinkedQueue<Callable<Void>>> _TweetKeydToTasks;
    private Map<ParcelableTweet, Future<Void>> _tweetsKeyedToWaitingTasks;
    public TweetOperationHandler(SmoothProgressBarWrapper progressBar_){
        _progressBar = progressBar_;
        _tweetsKeyedToWaitingTasks = new HashMap<ParcelableTweet, Future<Void>>();
    }


    @Override
    public void onTweetFav(ParcelableTweet tweet_) {

    }

    @Override
    public void onReTweet(ParcelableTweet tweet_) {

    }

    @Override
    public void onReplyTweet(ParcelableTweet tweet_) {

    }

    @Override
    public void onQuoteTweet(ParcelableTweet tweet_) {

    }

}
