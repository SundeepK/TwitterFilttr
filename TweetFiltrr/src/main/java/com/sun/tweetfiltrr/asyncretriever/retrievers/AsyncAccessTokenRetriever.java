package com.sun.tweetfiltrr.asyncretriever.retrievers;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.sun.tweetfiltrr.parcelable.ParcelableUser;
import com.sun.tweetfiltrr.utils.TwitterConstants;
import com.sun.tweetfiltrr.utils.TwitterUtil;

import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

public class AsyncAccessTokenRetriever extends AsyncTask<String, String, ParcelableUser> {

	private static final String TAG = AsyncAccessTokenRetriever.class.getName();

	private Context _context;
	
	public AsyncAccessTokenRetriever(Context context_){
		_context = context_;
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

                AccessToken accessToken = TwitterUtil.getInstance().getTwitter().getOAuthAccessToken(
						requestToken, params[0]);

//                AccessToken accessToken = new AccessToken(requestToken.getToken(), requestToken.getTokenSecret());
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
				user = new ParcelableUser(TwitterUtil.getInstance()
						.getTwitter().showUser(accessToken.getUserId()));

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
