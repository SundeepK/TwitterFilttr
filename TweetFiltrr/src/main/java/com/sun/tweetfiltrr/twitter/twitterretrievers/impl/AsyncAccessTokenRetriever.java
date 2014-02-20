package com.sun.tweetfiltrr.twitter.twitterretrievers.impl;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.sun.tweetfiltrr.cursorToParcelable.FriendToParcelable;
import com.sun.tweetfiltrr.database.dao.FriendDao;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;

import twitter4j.auth.RequestToken;

public class AsyncAccessTokenRetriever extends AsyncTask<String, String, ParcelableUser> {

	private static final String TAG = AsyncAccessTokenRetriever.class.getName();

	private Context _context;
	private FriendDao _userDao;
    private AccessTokenRetrieverFromPref _tokenRetriever;
    private OnTokenFinish _lis;
    RequestToken token;
    public interface OnTokenFinish {
        public void OnTokenFinish(ParcelableUser parcelableUser);
    }

    public AsyncAccessTokenRetriever(Context context_, OnTokenFinish list){
        _context = context_;
        _lis = list;
        _userDao = new FriendDao(_context.getContentResolver(), new FriendToParcelable());
        _tokenRetriever = new AccessTokenRetrieverFromPref(_userDao);
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
        _tokenRetriever = new AccessTokenRetrieverFromPref(_userDao);
	}
	
	@Override
    protected ParcelableUser doInBackground(String... params) {

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(_context);
        ParcelableUser user = null;
        synchronized (this) {
//            try {

            //    UserBundle bundle =
                 //       _tokenRetriever.retrieverAccessToken(sharedPreferences, params[0]);
//                AccessToken accessToken = bundle.getAccessToken();
//                user = bundle.getUser();

//                setUserPreferences(editor, accessToken);
//                persistUserDetails(editor, user);
//                editor.commit();
//                TwitterUtil.getInstance().setCurrentUser(user);
//                TwitterUtil.getInstance().setTwitterFactories(accessToken);
//
//            } catch (TwitterException e) {
//                e.printStackTrace();
//            }
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
	

}
