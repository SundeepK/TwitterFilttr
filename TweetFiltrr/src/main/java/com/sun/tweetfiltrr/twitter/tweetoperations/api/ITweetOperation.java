package com.sun.tweetfiltrr.twitter.tweetoperations.api;

import com.sun.tweetfiltrr.parcelable.ParcelableTweet;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;
import com.sun.tweetfiltrr.twitter.api.ITwitterAPICall;

/**
 * Created by Sundeep.Kahlon on 24/01/14.
 */
public interface ITweetOperation extends ITwitterAPICall{

    public ParcelableTweet performTwitterOperation(ParcelableUser tweet_, IOnTweetOperationFail failLister_);

}
