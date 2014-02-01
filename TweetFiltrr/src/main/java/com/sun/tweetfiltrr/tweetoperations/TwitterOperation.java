package com.sun.tweetfiltrr.tweetoperations;

import com.sun.tweetfiltrr.parcelable.ParcelableTweet;
import com.sun.tweetfiltrr.tweetoperations.api.ITwitterOperation;

import java.util.Collection;

/**
 * Created by Sundeep.Kahlon on 24/01/14.
 */
public class TwitterOperation {

    private ITwitterOperation _twitterOperation;
    private Collection<ParcelableTweet> _tweets;
    public TwitterOperation(ITwitterOperation twitterOperation_, Collection<ParcelableTweet> tweets_){
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
