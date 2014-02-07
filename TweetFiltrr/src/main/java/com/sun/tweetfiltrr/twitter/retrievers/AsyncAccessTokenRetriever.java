package com.sun.tweetfiltrr.twitter.retrievers;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.sun.tweetfiltrr.cursorToParcelable.FriendToParcelable;
import com.sun.tweetfiltrr.database.dao.FriendDao;
import com.sun.tweetfiltrr.database.dao.IDBDao;
import com.sun.tweetfiltrr.database.tables.FriendTable;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;
import com.sun.tweetfiltrr.utils.TwitterConstants;
import com.sun.tweetfiltrr.utils.TwitterUtil;

import java.util.ArrayList;
import java.util.Collection;

import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

public class AsyncAccessTokenRetriever extends AsyncTask<String, String, ParcelableUser> {

	private static final String TAG = AsyncAccessTokenRetriever.class.getName();

	private Context _context;
	IDBDao<ParcelableUser> _userDao;
	public AsyncAccessTokenRetriever(Context context_){
		_context = context_;
        _userDao = new FriendDao(context_.getContentResolver(), new FriendToParcelable());
	}
	
	@Override
	protected ParcelableUser doInBackground(String... params) {

		RequestToken requestToken = TwitterUtil.getInstance()
				.getRequestToken();
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(_context);
		SharedPreferences.Editor editor = sharedPreferences.edit();

		
		ParcelableUser user = null;
		synchronized (this){
		try{
			if (!(params[0] == "")) {
				Log.v(TAG, "Verifier is not null so doing OAuth with request token" + requestToken.getToken() + " secrect  " + requestToken.getTokenSecret());
                Log.v(TAG, "params is " +params[0] );

//                AccessToken accessToken = TwitterUtil.getInstance().getTwitter().getOAuthAccessToken(
//						requestToken, params[0]);

                AccessToken accessToken = new AccessToken(TwitterConstants.TWITTER_OAUTH_KEY, TwitterConstants.TWITTER_OAUTH_SECRET);
				setUserPreferences(editor, accessToken);
				TwitterUtil.getInstance().setTwitterFactories(accessToken);
				
				user = new ParcelableUser(TwitterUtil.getInstance().getTwitter().showUser(accessToken
						.getUserId()));
				persistUserDetails(editor, user);
				editor.commit();

				return user;
			} else {

				String accessTokenString = sharedPreferences.getString(
						TwitterConstants.PREFERENCE_TWITTER_OAUTH_TOKEN, "");
				String accessTokenSecret = sharedPreferences.getString(
						TwitterConstants.PREFERENCE_TWITTER_OAUTH_TOKEN_SECRET,
						"");
				AccessToken accessToken = new AccessToken(accessTokenString,
						accessTokenSecret);
				TwitterUtil.getInstance().setTwitterFactories(accessToken);

                long userId = sharedPreferences.getLong(
                        TwitterConstants.AUTH_USER_ID, -1l);
                Collection<ParcelableUser> users = new ArrayList<ParcelableUser>(1);
                if(userId > -1){
                    users.addAll(_userDao.getEntries(FriendTable.FriendColumn.FRIEND_ID.s()  + " = ? ", new String[]{Long.toString(userId)}, null));
                }

                //we only expect one user since userid is alwats unique
                if(users.size() == 1){
                    Log.v(TAG, "Found user from DB");
                    user = users.iterator().next();
                }else{
                    user = new ParcelableUser(TwitterUtil.getInstance()
                            .getTwitter().showUser(accessToken.getUserId()));
                }
                TwitterUtil.getInstance().setCurrentUser(user);
				return user;

			}
		} catch (TwitterException e) {
			e.printStackTrace();
		}
        }

		return null; 
	}

//    private void setUserPreferences(SharedPreferences.Editor editor_, RequestToken accessToken_ ) {
//        editor_.putString(
//                TwitterConstants.PREFERENCE_TWITTER_OAUTH_TOKEN,
//                accessToken_.getToken());
//        Log.v(TAG, "secrect token: " + accessToken_.getToken());
//
//        editor_.putString(
//                TwitterConstants.PREFERENCE_TWITTER_OAUTH_TOKEN_SECRET,
//                accessToken_.getTokenSecret());
//        Log.v(TAG, "secrect secret: " + accessToken_.getTokenSecret());
//
//        editor_.putBoolean(
//                TwitterConstants.PREFERENCE_TWITTER_IS_LOGGED_IN, true);
//        editor_.commit();
//    }
	
	private void setUserPreferences(SharedPreferences.Editor editor_, AccessToken accessToken_ ) {
		editor_.putString(
				TwitterConstants.PREFERENCE_TWITTER_OAUTH_TOKEN,
				accessToken_.getToken());
		Log.v(TAG, "secrect token: " + accessToken_.getToken());

		editor_.putString(
				TwitterConstants.PREFERENCE_TWITTER_OAUTH_TOKEN_SECRET,
				accessToken_.getTokenSecret());
		Log.v(TAG, "secrect secret: " + accessToken_.getTokenSecret());

		editor_.putBoolean(
				TwitterConstants.PREFERENCE_TWITTER_IS_LOGGED_IN, true);
        editor_.commit();
    }

	private void persistUserDetails(SharedPreferences.Editor editor_, ParcelableUser user_){
		editor_.putString(TwitterConstants.AUTH_USER_SCREEN_BG,	user_.getProfileBackgroundImageUrl());
		editor_.putString(TwitterConstants.LOGIN_PROFILE_BG,user_.getProfileImageUrl());
		editor_.putString(TwitterConstants.AUTH_USER_DESC_BG,user_.getDescription());
		editor_.putString(TwitterConstants.AUTH_USER_NAME_BG,user_.getScreenName());
		editor_.putLong(TwitterConstants.AUTH_USER_ID,user_.getUserId());
	}
}
