package com.sun.tweetfiltrr.twitter.retrievers.api;

import com.sun.tweetfiltrr.parcelable.parcelable.api.ICachedUser;

/**
 * Created by Sundeep on 19/01/14.
 */
public interface ITwitterRetriever<T> {

    public T retrieveTwitterData(ICachedUser user_);

}
