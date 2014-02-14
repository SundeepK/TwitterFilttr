package com.sun.tweetfiltrr.twitter.twitterretrievers.impl;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.sun.tweetfiltrr.cursorToParcelable.FriendToParcelable;
import com.sun.tweetfiltrr.database.dao.FriendDao;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;
import com.sun.tweetfiltrr.utils.TwitterConstants;
import com.sun.tweetfiltrr.utils.TwitterUtil;

import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;

public class AsyncAccessTokenRetriever extends AsyncTask<String, String, ParcelableUser> {

	private static final String TAG = AsyncAccessTokenRetriever.class.getName();

	private Context _context;
	private FriendDao _userDao;
    private AccessTokenRetriever _tokenRetriever;
    private OnTokenFinish _lis;
    public interface OnTokenFinish {
        public void OnTokenFinish(ParcelableUser parcelableUser);
    }

    public AsyncAccessTokenRetriever(Context context_, OnTokenFinish list){
        _context = context_;
        _lis = list;
        _userDao = new FriendDao(_context.getContentResolver(), new FriendToParcelable());
        _tokenRetriever = new AccessTokenRetriever(_userDao);
    }

    @Override
    protected void onPostExecute(ParcelableUser parcelableUser) {
        super.onPostExecute(parcelableUser);
        if(_lis !=null){
            _lis.OnTokenFinish(parcelableUser);
        }
    }

    public AsyncAccessTokenRetriever(Context context_){
		_context = context_;
        _userDao = new FriendDao(_context.getContentResolver(), new FriendToParcelable());
        _tokenRetriever = new AccessTokenRetriever(_userDao);
	}
	
	@Override
    protected ParcelableUser doInBackground(String... params) {

//        try {
//            Thread.sleep(2000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(_context);
        SharedPreferences.Editor editor = sharedPreferences.edit();


        ParcelableUser user = null;
        synchronized (this) {
            try {

                AccessTokenRetriever.UserBundle bundle =
                        _tokenRetriever.retrieverAccessToken(sharedPreferences, params[0]);
                AccessToken accessToken = bundle.getAccessToken();
                user = bundle.getUser();
                setUserPreferences(editor, accessToken);
                persistUserDetails(editor, user);
                TwitterUtil.getInstance().setCurrentUser(user);
                TwitterUtil.getInstance().setTwitterFactories(accessToken);
                editor.commit();

            } catch (TwitterException e) {
                e.printStackTrace();
            }
        }

        return user;
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
        editor_.putBoolean(TwitterConstants.PREFERENCE_TWITTER_IS_LOGGED_IN, true);
	}
}
