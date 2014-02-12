package com.sun.tweetfiltrr.twitter.tweetoperations.api;

import com.sun.tweetfiltrr.parcelable.ParcelableTweet;
import com.sun.tweetfiltrr.twitter.api.ITwitterAPICall;

import twitter4j.TwitterException;

/**
 * Created by Sundeep.Kahlon on 12/02/14.
 */
public interface ITwitterOperationListener  {

        public void onTaskSuccessfulComplete(ParcelableTweet tweet_);
        public void onTaskFail(ParcelableTweet failedTweet_, TwitterException exception_, ITwitterAPICall.TwitterAPICallType tweetType_);
}
