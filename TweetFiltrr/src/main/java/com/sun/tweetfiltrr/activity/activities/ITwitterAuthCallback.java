package com.sun.tweetfiltrr.activity.activities;

import com.sun.tweetfiltrr.twitter.twitterretrievers.api.UserBundle;

/**
 * Created by Sundeep.Kahlon on 20/02/14.
 */
public interface ITwitterAuthCallback {
    public void onSuccessTwitterOAuth(UserBundle userBundle);
    public void onFailTwitterOAuth(Exception e);
}