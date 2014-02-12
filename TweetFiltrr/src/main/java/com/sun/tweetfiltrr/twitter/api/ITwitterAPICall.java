package com.sun.tweetfiltrr.twitter.api;

import com.sun.tweetfiltrr.parcelable.parcelable.api.ICachedUser;

/**
 * Created by Sundeep.Kahlon on 12/02/14.
 */
public interface ITwitterAPICall<T> {

    public enum TwitterAPICallType {
        POST_RETWEET,
        POST_TWEET,
        GET_TIMELINE,
        GET_FRIENDS,
        POST_FAVOURITE;
    }

    public ITwitterAPICall.TwitterAPICallType getTweetOperationType();
    public T retrieveTwitterData(ICachedUser user_, ITwitterAPICallStatus failLis_);


}
