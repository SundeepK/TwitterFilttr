package com.sun.tweetfiltrr.activity.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.sun.imageloader.core.ImageSettings;
import com.sun.imageloader.core.UrlImageLoader;
import com.sun.imageloader.core.api.FailedTaskReason;
import com.sun.imageloader.core.api.ImageTaskListener;
import com.sun.tweetfiltrr.R;
import com.sun.tweetfiltrr.application.TweetFiltrrApplication;
import com.sun.tweetfiltrr.customviews.views.CircleCroppedDrawable;
import com.sun.tweetfiltrr.customviews.webview.AuthenticationDetails;
import com.sun.tweetfiltrr.customviews.webview.TwitterAuthView;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;
import com.sun.tweetfiltrr.twitter.twitterretrievers.api.UserBundle;
import com.sun.tweetfiltrr.twitter.twitterretrievers.impl.AsyncAccessTokenRetriever;
import com.sun.tweetfiltrr.utils.ImageLoaderUtils;
import com.sun.tweetfiltrr.utils.TwitterConstants;
import com.sun.tweetfiltrr.utils.TwitterUtil;

import javax.inject.Inject;

import twitter4j.TwitterException;
import twitter4j.auth.RequestToken;

public class MainActivity extends SherlockFragmentActivity implements ImageTaskListener,
        AsyncAccessTokenRetriever.OnTokenFinish, TwitterAuthView.TwitterAuthCallback {

    private static final String TAG = MainActivity.class.getName();
    private ImageView _profile;
    private TwitterAuthView _authWebView;

    @Inject UrlImageLoader _sicImageLoader;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_screen);
        ((TweetFiltrrApplication)getApplication()).getObjectGraph().inject(this);
		if (TwitterUtil.hasInternetConnection(this)) {

            _profile = (ImageView) findViewById(R.id.friend_profile_image);
			TextView userName = (TextView) findViewById(R.id.friend_name);
            SharedPreferences sharedPreferences = PreferenceManager
						.getDefaultSharedPreferences(this);
			   String profileUrl = sharedPreferences.getString(
						TwitterConstants.LOGIN_PROFILE_BG, null);
			   String name = sharedPreferences.getString(
						TwitterConstants.AUTH_USER_NAME_BG, null);
			   String desc = sharedPreferences.getString(
						TwitterConstants.AUTH_USER_DESC_BG, null);
			   userName.setText(name);
            ImageLoaderUtils.attemptLoadImage(_profile, _sicImageLoader, profileUrl, 1, this);
            _authWebView = (TwitterAuthView) findViewById(R.id.twitter_auth_web_view);
            _authWebView.setSuccessLis(this);
            authenticateUser();
		} else {
			displayConnectionAlert();
		}
	}

    public void authenticateUser() {

		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this);
        if (!sharedPreferences.getBoolean(
				TwitterConstants.PREFERENCE_TWITTER_IS_LOGGED_IN, false)) {
			//	new TwitterAuthenticateTask().execute();
           // new AsyncAccessTokenRetriever(this, this).execute("");
            AuthenticationDetails details = new AuthenticationDetails(TwitterConstants.TWITTER_CONSUMER_KEY,
                    TwitterConstants.TWITTER_CONSUMER_SECRET, "https://twitterfiltrr.com");
            _authWebView.setVisibility(View.VISIBLE);
            _authWebView.startTwitterAuthentication(details);
         //   finish();
        }else{
            new AsyncAccessTokenRetriever(this, this).execute("");
        }


//		File f=new File("/data/data/com.sun.tweetfiltrr/databases/tweetFiltrr.db");
//		FileInputStream fis=null;
//		FileOutputStream fos=null;
//
//		try
//		{
//		  fis=new FileInputStream(f);
//		  fos=new FileOutputStream("/storage/extSdCard/db_dump_new6.db");
//		  while(true)
//		  {
//		    int i=fis.read();
//		    if(i!=-1)
//		    {fos.write(i);}
//		    else
//		    {break;}
//		  }
//		  fos.flush();
//		  Toast.makeText(this, "DB dump OK", Toast.LENGTH_LONG).show();
//		}
//		catch(Exception e)
//		{
//		  e.printStackTrace();
//		  Toast.makeText(this, "DB dump ERROR", Toast.LENGTH_LONG).show();
//		}
//		finally
//		{
//		  try
//		  {
//		    fos.close();
//		    fis.close();
//		  }
//		  catch(IOException ioe)
//		  {}
//		}

	}

	public void displayConnectionAlert() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
    	alertDialogBuilder.setTitle("Connection failure");
		alertDialogBuilder
				.setMessage("Unable to get internet connection - please try again");
		alertDialogBuilder.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				});
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();

	}

    @Override
    public void preImageLoad(ImageSettings imageSettings) {

    }

    @Override
    public void onImageLoadComplete(Bitmap bitmap, ImageSettings imageSettings) {
        CircleCroppedDrawable d = new CircleCroppedDrawable(bitmap);
        _profile.setImageBitmap(null);
        _profile.setImageDrawable(d);
        _profile.setBackground(d);
        Animation anim = getZoomAnimation(1f,1.1f, 1f, 1.1f);
        _profile.startAnimation(anim);
    }

    private Animation getZoomAnimation(float fromX_, float toX_, float fromY_, float toY_){
        Animation  scaleAnimation = new ScaleAnimation(
                fromX_, toX_,
                fromY_, toY_,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setFillAfter(true);
        scaleAnimation.setDuration(600);
        scaleAnimation.setInterpolator(new CycleInterpolator(1));
        scaleAnimation.setRepeatCount(Animation.INFINITE);
        scaleAnimation.setRepeatMode(Animation.INFINITE);
        return scaleAnimation;
    }

    @Override
    public void onImageLoadFail(FailedTaskReason failedTaskReason, ImageSettings imageSettings) {

    }

    @Override
    public void OnTokenFinish(ParcelableUser parcelableUser) {
        _profile.clearAnimation();
        Intent intent = new Intent(MainActivity.this, TwitterFilttrLoggedInUserHome.class);
        intent.putExtra(TwitterConstants.FRIENDS_BUNDLE, parcelableUser);
        startActivity(intent);
   //     overridePendingTransition(0, R.anim.display_anim_top_bot_top);
        this.finish();

    }

    @Override
    public void finish() {
        super.finish();

    }

    @Override
    public void onSuccessTwitterOAuth(UserBundle bundle) {
        TwitterUtil.getInstance().setCurrentUser(bundle.getUser());
        Intent intent = new Intent(this, TwitterFilttrLoggedInUserHome.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onFailTwitterOAuth(Exception e) {

    }

    class TwitterAuthenticateTask extends AsyncTask<String, String, RequestToken> {
		 
	    @Override //TODO have a timeout here just incase we cant reach twitter and display an error message
	    protected void onPostExecute(RequestToken requestToken) {
	    	//Log.v("Authentication url",Uri.parse(requestToken.getAuthenticationURL()).toString());
	        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(requestToken.getAuthenticationURL()));
	        startActivity(intent);
	        finish();
	    }
	 
	    @Override
	    protected RequestToken doInBackground(String... params) {
            try {
                return TwitterUtil.getInstance().getRequestToken();
            } catch (TwitterException e) {
                e.printStackTrace();
            }
            return null;//TODO not return null, instead add extra handling
        }
	}

}
