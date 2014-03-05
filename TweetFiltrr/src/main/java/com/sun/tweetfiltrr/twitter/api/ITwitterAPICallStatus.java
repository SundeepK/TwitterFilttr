package com.sun.tweetfiltrr.twitter.api;

import com.sun.tweetfiltrr.parcelable.ParcelableUser;

import twitter4j.TwitterException;

/**
 * Created by Sundeep.Kahlon on 12/02/14.
 */
public interface ITwitterAPICallStatus {

    public void onTwitterApiCallSuccess(ParcelableUser user_,  ITwitterAPICall apiCallType_);
    public void onTwitterApiCallFail(ParcelableUser failedTweet_,
                                     TwitterException exception_, ITwitterAPICall apiCallType_);

}
