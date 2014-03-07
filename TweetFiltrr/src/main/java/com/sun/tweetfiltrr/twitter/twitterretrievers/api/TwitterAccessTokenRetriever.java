package com.sun.tweetfiltrr.twitter.twitterretrievers.api;

import android.text.TextUtils;
import android.util.Log;

import com.sun.tweetfiltrr.parcelable.ParcelableUser;

import java.util.ArrayList;
import java.util.Collection;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

/**
 * Created by Sundeep on 20/02/14.
 *
 */
public class TwitterAccessTokenRetriever  implements ITwitterAccessTokenRetriever{
    private static final String TAG = TwitterAccessTokenRetriever.class.getName();

    public TwitterAccessTokenRetriever(){}

    @Override
    public Collection<UserBundle> retrieverAccessTokenFromTwitter(RequestToken requestToken_,
                                                                  String verifier_, Twitter twitter_) throws TwitterException {
        final Collection<UserBundle> userBundles = new ArrayList<UserBundle>();
        try {
            if (!TextUtils.isEmpty(verifier_)) {
                Log.v(TAG, "Verifier recieved: " + verifier_);
                final AccessToken token = twitter_.getOAuthAccessToken(requestToken_, verifier_);
                final ParcelableUser parcelableUser = new ParcelableUser(twitter_.showUser(token
                        .getUserId()));
                final UserBundle user =
                        new UserBundle(parcelableUser, token);
                userBundles.add(user);
            }
        } catch (TwitterException e) {
            throw new TwitterException(e);
        }
        return userBundles;
    }
}
