package com.sun.tweetfiltrr.twitter.twitterretrievers.impl;

import android.content.SharedPreferences;
import android.util.Log;

import com.sun.tweetfiltrr.database.dao.FriendDao;
import com.sun.tweetfiltrr.database.tables.FriendTable;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;
import com.sun.tweetfiltrr.twitter.twitterretrievers.api.IAccessTokenRetrieverFromPref;
import com.sun.tweetfiltrr.twitter.twitterretrievers.api.UserBundle;
import com.sun.tweetfiltrr.utils.TwitterConstants;
import com.sun.tweetfiltrr.utils.TwitterUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;

/**
 * Created by Sundeep on 13/02/14.
 */
public class AccessTokenRetrieverFromPref implements IAccessTokenRetrieverFromPref {

    private static final String TAG = AccessTokenRetrieverFromPref.class.getName();
    private FriendDao _userDao;


    public AccessTokenRetrieverFromPref(FriendDao userDao_) {
        _userDao = userDao_;
    }

    @Override
    public Collection<UserBundle> retrieveAccessTokenFromSharedPref(SharedPreferences sharedPreferences_) {
        ArrayList<UserBundle> userBundles = new ArrayList<UserBundle>();
        try {
            final AccessToken accessToken = retrieveAccessTokenFromPreferences(sharedPreferences_);
            TwitterUtil.getInstance().setTwitterFactories(accessToken);
            Twitter twitter =    TwitterUtil.getInstance().getTwitter();
            final ParcelableUser parcelableUserFromTwitter = new ParcelableUser(twitter.showUser(accessToken
                    .getUserId()));
            Log.v(TAG, "got user from twitter" + parcelableUserFromTwitter);
            insertUpdatedTwitterUser(parcelableUserFromTwitter);
            //attempt to get user from DB, we should almost always get a user if we are here
            final ParcelableUser parcelableUser = getParcelableUserFromDB(sharedPreferences_);
            Log.v(TAG, "user from db" + parcelableUser);
            final UserBundle user = new UserBundle(parcelableUser, accessToken);
            userBundles.add(user);
        } catch (TwitterException e) {
            e.printStackTrace();
        }
        return userBundles;
    }

    private AccessToken retrieveAccessTokenFromPreferences(SharedPreferences sharedPreferences_) {
        String accessTokenString = sharedPreferences_.getString(
                TwitterConstants.PREFERENCE_TWITTER_OAUTH_TOKEN, "");
        String accessTokenSecret = sharedPreferences_.getString(
                TwitterConstants.PREFERENCE_TWITTER_OAUTH_TOKEN_SECRET,
                "");
        return new AccessToken(accessTokenString,
                accessTokenSecret);
    }

    private void insertUpdatedTwitterUser(ParcelableUser user_){
        String[] cols = new String[]{FriendTable.FriendColumn.FRIEND_ID.s(),
                FriendTable.FriendColumn.TWEET_COUNT.s(), FriendTable.FriendColumn.FOLLOWER_COUNT.s(), FriendTable.FriendColumn.FRIEND_COUNT.s(),
                FriendTable.FriendColumn.FRIEND_NAME.s(), FriendTable.FriendColumn.FRIEND_SCREENNAME.s(),
                FriendTable.FriendColumn.PROFILE_IMAGE_URL.s(),FriendTable.FriendColumn.BACKGROUND_PROFILE_IMAGE_URL.s(),
                FriendTable.FriendColumn.BANNER_PROFILE_IMAE_URL.s(), FriendTable.FriendColumn.DESCRIPTION.s()};
        Collection<ParcelableUser> users = new ArrayList<ParcelableUser>();
        users.add(user_);
        _userDao.insertOrUpdate(users, cols);
    }

    private ParcelableUser getParcelableUserFromDB(SharedPreferences sharedPreferences_) {
        ParcelableUser parcelableUser = null;
        long userId = sharedPreferences_.getLong(
                TwitterConstants.AUTH_USER_ID, -1l);
        Collection<ParcelableUser> users = new ArrayList<ParcelableUser>(1);
        if (userId > -1) {
            users.addAll(_userDao.getEntries(FriendTable.FriendColumn.FRIEND_ID.s()
                    + " = ? ", new String[]{Long.toString(userId)}, null));
        }
        //we only expect one user since userid is always unique
        if (!users.isEmpty()) {
            Log.v(TAG, "Found user from DB");
            if (users.size() > 1) {
                Log.w(TAG, "Found  more then 1 user from DB for some reason, picking first in collection: "
                        + Arrays.toString(users.toArray()));
            }
            parcelableUser = users.iterator().next();
        }
        return parcelableUser;
    }

}