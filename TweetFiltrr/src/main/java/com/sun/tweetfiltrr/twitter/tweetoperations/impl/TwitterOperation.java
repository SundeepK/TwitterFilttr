package com.sun.tweetfiltrr.twitter.tweetoperations.impl;

import com.sun.tweetfiltrr.parcelable.ParcelableTweet;
import com.sun.tweetfiltrr.twitter.tweetoperations.api.ITweetOperation;

import java.util.Collection;

/**
 * Created by Sundeep.Kahlon on 24/01/14.
 */
public class TwitterOperation {

    private ITweetOperation _twitterOperation;
    private Collection<ParcelableTweet> _tweets;
    public TwitterOperation(ITweetOperation twitterOperation_, Collection<ParcelableTweet> tweets_){
        _twitterOperation = twitterOperation_;
        _tweets = tweets_;
    }

    public Collection<ParcelableTweet> processTwitterOperations(){
//        for(ParcelableTweet tweet : _tweets){
//            _twitterOperation.performTwitterOperation(tweet);
//        }
        return _tweets;
    }

}
