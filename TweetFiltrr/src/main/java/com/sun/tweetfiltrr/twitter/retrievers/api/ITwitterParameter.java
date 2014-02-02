package com.sun.tweetfiltrr.twitter.retrievers.api;

import com.sun.tweetfiltrr.parcelable.parcelable.api.ICachedUser;

/**
 * Created by Sundeep on 24/01/14.
 */
public interface ITwitterParameter<T> {

    public T getTwitterParameter(ICachedUser user_, boolean shouldLookForOldTweets_);

}
