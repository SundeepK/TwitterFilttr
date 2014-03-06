package com.sun.tweetfiltrr.customviews.webview.api;

import com.sun.tweetfiltrr.twitter.twitterretrievers.api.UserBundle;

/**
 * Created by Sundeep.Kahlon on 20/02/14.
 */
public interface ITwitterAuthCallback {
    public void onSuccessTwitterOAuth(UserBundle userBundle);
    public void onFailTwitterOAuth(Exception e);
}