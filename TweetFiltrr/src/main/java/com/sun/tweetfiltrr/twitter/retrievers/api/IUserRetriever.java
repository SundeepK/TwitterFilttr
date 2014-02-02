package com.sun.tweetfiltrr.twitter.retrievers.api;

import com.sun.tweetfiltrr.parcelable.ParcelableUser;
import com.sun.tweetfiltrr.parcelable.parcelable.api.ICachedUser;

import java.util.Collection;

/**
 * Created by Sundeep on 19/01/14.
 */
public interface IUserRetriever {

    public Collection<ParcelableUser> retrieveUsers(ICachedUser user_);

}
