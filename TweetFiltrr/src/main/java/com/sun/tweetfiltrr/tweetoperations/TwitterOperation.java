package com.sun.tweetfiltrr.tweetoperations;

import com.sun.tweetfiltrr.parcelable.ParcelableTimeLineEntry;
import com.sun.tweetfiltrr.tweetoperations.api.ITwitterOperation;

import java.util.Collection;

/**
 * Created by Sundeep.Kahlon on 24/01/14.
 */
public class TwitterOperation {

    private ITwitterOperation _twitterOperation;
    private Collection<ParcelableTimeLineEntry> _tweets;
    public TwitterOperation(ITwitterOperation twitterOperation_, Collection<ParcelableTimeLineEntry> tweets_){
        _twitterOperation = twitterOperation_;
        _tweets = tweets_;
    }

    public Collection<ParcelableTimeLineEntry> processTwitterOperations(){
        for(ParcelableTimeLineEntry tweet : _tweets){
            _twitterOperation.performTwitterOperation(tweet);
        }
        return _tweets;
    }

}
