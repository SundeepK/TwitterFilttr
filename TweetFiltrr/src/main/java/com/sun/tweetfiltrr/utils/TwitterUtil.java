package com.sun.tweetfiltrr.utils;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.Log;

import com.sun.imageloader.core.UrlImageLoader;
import com.sun.imageloader.core.UrlImageLoaderConfiguration;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;

import java.text.SimpleDateFormat;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;


public final class TwitterUtil {

    private static final String TAG = TwitterUtil.class.getName();
    private RequestToken _requestToken = null;
    private TwitterFactory _twitterFactory = null;
    private Twitter _twitter;
    private ThreadPoolExecutor _threadPoolExecutor;
    private static TwitterUtil instance = new TwitterUtil();
    private ParcelableUser _currentUser;
    private UrlImageLoader _imageloader;
    private TwitterUtil() {
    	ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
    	configurationBuilder.setDebugEnabled(true);
        configurationBuilder.setOAuthConsumerKey(TwitterConstants.TWITTER_CONSUMER_KEY);
        configurationBuilder.setOAuthConsumerSecret(TwitterConstants.TWITTER_CONSUMER_SECRET);
//        configurationBuilder.setOAuthAccessToken(TwitterConstants.TWITTER_OAUTH_KEY);
//        configurationBuilder.setOAuthAccessTokenSecret(TwitterConstants.TWITTER_OAUTH_SECRET);
        configurationBuilder.setUseSSL(true);
    //    configurationBuilder.setApplicationOnlyAuthEnabled(true);


        Configuration  configuration = configurationBuilder.build();
        _twitterFactory = new TwitterFactory(configuration);
        _twitter = _twitterFactory.getInstance();
        _threadPoolExecutor =  new ThreadPoolExecutor(4, 10, 60, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>());
                
    }

    private static ThreadLocal<SimpleDateFormat> _threadLocal = new ThreadLocal<SimpleDateFormat>(){
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat(TwitterConstants.SIMPLE_DATE_FORMATE);
        }

    };

    public synchronized void setCurrentUser(ParcelableUser user_){
        _currentUser = user_;
    }

    public synchronized ParcelableUser getCurrentUser(){
       return  _currentUser;
    }

    public UrlImageLoader getGlobalImageLoader(Context context_){

        if(_imageloader == null){
            UrlImageLoaderConfiguration   imageloaderConfigs = new UrlImageLoaderConfiguration.Builder()
                    .setMaxCacheMemorySize(10)
                    .setDirectoryName(TwitterConstants.SIC_SAVE_DIRECTORY)
                    .setImageQuality(100)
                    .setThreadExecutor(TwitterUtil.getInstance().getGlobalExecutor())
                    .setImageType(Bitmap.CompressFormat.JPEG)
                    .useExternalStorage(true)
                  //  .setMaxDeleteTime(1, TimeUnit.DAYS).setTimeOut(5, 10000, 10000)
                    .build(context_);


            _imageloader = new UrlImageLoader(imageloaderConfigs);
        }

        return _imageloader;

    }

    public ThreadLocal<SimpleDateFormat> getSimpleDateFormatThreadLocal(){
        return _threadLocal;
    }

    public  ThreadPoolExecutor getGlobalExecutor(){
    	if(_threadPoolExecutor == null){
    	     _threadPoolExecutor =  new ThreadPoolExecutor(4, 10, 60, TimeUnit.SECONDS,
    	                new LinkedBlockingQueue<Runnable>());
    	}
    	
    	return _threadPoolExecutor;
    }
    

    public void setTwitterFactories(AccessToken accessToken)
    {
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        configurationBuilder.setDebugEnabled(true);
        configurationBuilder.setOAuthConsumerKey(TwitterConstants.TWITTER_CONSUMER_KEY);
        configurationBuilder.setOAuthConsumerSecret(TwitterConstants.TWITTER_CONSUMER_SECRET);
        configurationBuilder.setOAuthAccessToken(accessToken.getToken());
        configurationBuilder.setOAuthAccessTokenSecret(accessToken.getTokenSecret());
        Configuration configuration = configurationBuilder.build();
        _twitterFactory = new TwitterFactory(configuration);
        _twitter = _twitterFactory.getInstance(accessToken);
      //  _twitter.setOAuthAccessToken(accessToken);
    }

    public Twitter getTwitter()
    {
        return _twitter;
    }

    public RequestToken getRequestToken() {
        if (_requestToken == null) {
            try {
                _requestToken = _twitterFactory.getInstance().getOAuthRequestToken(TwitterConstants.TWITTER_CALLBACK_URL);
            } catch (TwitterException e) {
                Log.v(TAG, "error gettings requesttoken");
                e.printStackTrace();
            }
        }
        return _requestToken;
    }
    
    
	public static boolean hasInternetConnection(Context context_ ) {
		ConnectivityManager cm = (ConnectivityManager) context_.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo wifiNetwork = cm
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (wifiNetwork != null && wifiNetwork.isConnected()) {
			return true;
		}

		NetworkInfo mobileNetwork = cm
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if (mobileNetwork != null && mobileNetwork.isConnected()) {
			return true;
		}

		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		if (activeNetwork != null && activeNetwork.isConnected()) {
			return true;
		}

		return false;
	}

    public static TwitterUtil getInstance() {
        return instance;
    }


    public void reset() {
        instance = new TwitterUtil();
    }
    
	public long getCurrentLoggedInUserId(Context context_){
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context_);
		long userId  = sharedPreferences.getLong(
                TwitterConstants.AUTH_USER_ID, -1l);
		return userId;
	}
}
