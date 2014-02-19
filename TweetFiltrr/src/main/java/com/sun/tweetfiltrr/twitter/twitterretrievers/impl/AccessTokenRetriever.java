package com.sun.tweetfiltrr.twitter.twitterretrievers.impl;

import android.content.SharedPreferences;
import android.util.Log;

import com.sun.tweetfiltrr.database.dao.FriendDao;
import com.sun.tweetfiltrr.database.tables.FriendTable;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;
import com.sun.tweetfiltrr.twitter.twitterretrievers.api.IAccessTokenRetriever;
import com.sun.tweetfiltrr.utils.TwitterConstants;
import com.sun.tweetfiltrr.utils.TwitterUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

/**
 * Created by Sundeep on 13/02/14.
 */
public class AccessTokenRetriever implements IAccessTokenRetriever {


    private static final String TAG =AccessTokenRetriever.class.getName();
    private FriendDao _userDao;

    public AccessTokenRetriever(FriendDao userDao_){
        _userDao = userDao_;
    }


    @Override
    public UserBundle retrieverAccessToken(SharedPreferences sharedPreferences_
            , String verifier_) throws TwitterException {

        UserBundle user = null;
        ParcelableUser parcelableUser = null;
        AccessToken accessToken = null;
        TwitterUtil util = TwitterUtil.getInstance();
        Twitter twitter = util.getTwitter();
        try{
//            if (!(verifier_ == "")) {

      //                RequestToken requestToken = TwitterUtil.getInstance()
//                        .getRequestToken();
//                Log.v(TAG, "Verifier is not null so doing OAuth with request token" + requestToken.getToken()
//                        + " secrect  " + requestToken.getTokenSecret());
                Log.v(TAG, "params is " +verifier_ );
//                AccessToken accessToken = TwitterUtil.getInstance().getTwitter().getOAuthAccessToken(
//						requestToken, params[0]);


             //   accessToken = new AccessToken(TwitterConstants.TWITTER_OAUTH_KEY, TwitterConstants.TWITTER_OAUTH_SECRET);
               // RequestToken requestToken = twitter.getOAuthRequestToken(TwitterConstants.TWITTER_AUTH_URL);
             //   accessToken =   twitter.getOAuthAccessToken(requestToken);

//                parcelableUser = new ParcelableUser(TwitterUtil.getInstance().getTwitter().showUser(accessToken
//                        .getUserId()));
                RequestToken toek =   twitter.getOAuthRequestToken();
            accessToken =   twitter.getOAuthAccessToken(toek);

            parcelableUser = new ParcelableUser(twitter.showUser(accessToken
                        .getUserId()));
                Collection<ParcelableUser> users =  new ArrayList<ParcelableUser>();
                users.add(parcelableUser);
                _userDao.insertOrUpdate(users);
                user = new UserBundle(parcelableUser, accessToken);
//            } else {
//
//                accessToken = getAccessTokenFromPreferences(sharedPreferences_);
//                TwitterUtil.getInstance().setTwitterFactories(accessToken);
//                //attempt to get user from DB, we should almost always get a user if we are here
//                parcelableUser = getParcelableUserFromDB(sharedPreferences_);
//                //incase we don't have a user for whatever reason, we query twitter for it
//                if(parcelableUser == null){
//                    parcelableUser = getParcelableUserFromTwitter(accessToken);
//                    Log.v(TAG, "Found user from twitter network call");
//                }
//
//                TwitterUtil.getInstance().setCurrentUser(parcelableUser);
//                user = new UserBundle(parcelableUser, accessToken);
//            }
        } catch (TwitterException e) {

          //  accessToken =   twitter.getOAuthAccessToken();

            throw new TwitterException(e);
        }


        return user;
    }

    private AccessToken getAccessTokenFromPreferences(SharedPreferences sharedPreferences_){
        String accessTokenString = sharedPreferences_.getString(
                TwitterConstants.PREFERENCE_TWITTER_OAUTH_TOKEN, "");
        String accessTokenSecret = sharedPreferences_.getString(
                TwitterConstants.PREFERENCE_TWITTER_OAUTH_TOKEN_SECRET,
                "");
      return new AccessToken(accessTokenString,
                accessTokenSecret);
    }

    private ParcelableUser getParcelableUserFromTwitter(AccessToken accessToken_) throws TwitterException {
        return new ParcelableUser(TwitterUtil.getInstance()
                .getTwitter().showUser(accessToken_.getUserId()));

    }

    private ParcelableUser getParcelableUserFromDB(SharedPreferences sharedPreferences_ )
            throws TwitterException {
        ParcelableUser parcelableUser = null;
        long userId = sharedPreferences_.getLong(
                TwitterConstants.AUTH_USER_ID, -1l);
        Collection<ParcelableUser> users = new ArrayList<ParcelableUser>(1);
        if(userId > -1){
            users.addAll(_userDao.getEntries(FriendTable.FriendColumn.FRIEND_ID.s()
                    + " = ? ", new String[]{Long.toString(userId)}, null));
        }
        //we only expect one user since userid is always unique
        if(!users.isEmpty()){
            Log.v(TAG, "Found user from DB");
            if(users.size() > 1){
                Log.w(TAG, "Found  more then 1 user from DB for some reason, picking first in collection: "
                        + Arrays.toString(users.toArray()));
            }
            parcelableUser = users.iterator().next();
        }
        return parcelableUser;
    }

    public class UserBundle{
        private ParcelableUser _user;
        private AccessToken _accessToken;
        public UserBundle(ParcelableUser user_, AccessToken accessToken_){
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

}
