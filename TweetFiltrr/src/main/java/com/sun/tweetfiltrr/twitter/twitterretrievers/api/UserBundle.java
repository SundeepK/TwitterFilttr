package com.sun.tweetfiltrr.twitter.twitterretrievers.api;

import com.sun.tweetfiltrr.parcelable.ParcelableUser;

import twitter4j.auth.AccessToken;

/**
 * Created by Sundeep on 20/02/14.
 */
public class UserBundle {
    private ParcelableUser _user;
    private AccessToken _accessToken;

    public UserBundle(ParcelableUser user_, AccessToken accessToken_) {
        _user = user_;
        _accessToken = accessToken_;
    }

    public ParcelableUser getUser() {
        return _user;
    }

    public AccessToken getAccessToken() {
        return _accessToken;
    }

}
