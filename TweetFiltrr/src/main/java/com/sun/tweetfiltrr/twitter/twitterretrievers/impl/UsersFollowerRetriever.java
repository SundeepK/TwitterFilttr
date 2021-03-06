package com.sun.tweetfiltrr.twitter.twitterretrievers.impl;

import com.sun.tweetfiltrr.parcelable.ParcelableUser;
import com.sun.tweetfiltrr.parcelable.parcelable.api.ICachedUser;
import com.sun.tweetfiltrr.twitter.twitterretrievers.api.AUserRetriever;

import twitter4j.IDs;
import twitter4j.Twitter;
import twitter4j.TwitterException;

/**
 * Created by Sundeep on 19/01/14.
 */
public class UsersFollowerRetriever extends AUserRetriever {

    private boolean _isAuthenticatingUser;

    public UsersFollowerRetriever(boolean isAuthenticatingUser_){
        _isAuthenticatingUser = isAuthenticatingUser_;
    }


    @Override
    protected void processFriend(ParcelableUser user_) {
        user_.setIsFriend(_isAuthenticatingUser);
    }


    protected IDs getUsersIds(ICachedUser user_, Twitter twitter_) throws TwitterException {
        if(user_.getLastArrayIndex() > 5000){
            return twitter_.getFollowersIDs(user_.getUser().getUserId(), user_.getLastPageNumber());
        }else{
            return twitter_.getFollowersIDs(user_.getUser().getUserId(), -1l);
        }
    }
}
