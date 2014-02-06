package com.sun.tweetfiltrr.twitter.tweetoperations.api;

import com.sun.tweetfiltrr.parcelable.ParcelableTweet;

/**
 * Created by Sundeep.Kahlon on 24/01/14.
 */
public interface ITweetOperation {

    public enum TweetOperationType {
        RETWEET,
        POSTTWEET,
        FAVOURITE;
    }

    public ParcelableTweet performTwitterOperation(ParcelableTweet tweet_, IOnTweetOperationFail failLister_);
    public TweetOperationType getTweetOperationType();

}
