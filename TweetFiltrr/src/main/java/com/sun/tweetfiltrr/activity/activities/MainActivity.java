package com.sun.tweetfiltrr.activity.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
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
import com.sun.tweetfiltrr.database.dao.FriendDao;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;
import com.sun.tweetfiltrr.twitter.twitterretrievers.api.IAccessTokenRetrieverFromPref;
import com.sun.tweetfiltrr.twitter.twitterretrievers.api.UserBundle;
import com.sun.tweetfiltrr.twitter.twitterretrievers.impl.AccessTokenRetrieverFromPref;
import com.sun.tweetfiltrr.twitter.twitterretrievers.impl.AsyncAccessTokenRetriever;
import com.sun.tweetfiltrr.utils.ImageLoaderUtils;
import com.sun.tweetfiltrr.utils.TwitterConstants;
import com.sun.tweetfiltrr.utils.TwitterUtil;

import java.util.Collection;

import javax.inject.Inject;

import twitter4j.auth.AccessToken;

public class MainActivity extends SherlockFragmentActivity {

    private static final String TAG = MainActivity.class.getName();
    private ImageView _profile;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        ((TweetFiltrrApplication)getApplication()).getObjectGraph().inject(this);

        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        boolean hasSignedIn = hasUserSignedInBefore(sharedPreferences);
        if(!hasSignedIn){

        }else{
            setContentView(R.layout.login_screen_auto_sign_in);
        }

		if (TwitterUtil.hasInternetConnection(this)) {

		} else {
			displayConnectionAlert();
		}
	}

    private boolean hasUserSignedInBefore(SharedPreferences sharedPreferences){
       return   sharedPreferences.getBoolean(
                TwitterConstants.PREFERENCE_TWITTER_IS_LOGGED_IN, false);
    }

    private void authenticateUser(boolean hasSignedInBefore_,SharedPreferences sharedPreferences_) {
        if (!hasSignedInBefore_) {

        }else{
            IAccessTokenRetrieverFromPref tokenRetrieverFromPref = new AccessTokenRetrieverFromPref(_friendDao);
            Collection<UserBundle> userBundles = tokenRetrieverFromPref.retrieveAccessTokenFromSharedPref(sharedPreferences_);
            UserBundle bundle = userBundles.iterator().next();
            finishSignIn(bundle);
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

    public void OnTokenFinish(ParcelableUser parcelableUser) {
        _profile.clearAnimation();
        Intent intent = new Intent(MainActivity.this, TwitterFilttrLoggedInUserHome.class);
        intent.putExtra(TwitterConstants.FRIENDS_BUNDLE, parcelableUser);
        startActivity(intent);
   //     overridePendingTransition(0, R.anim.display_anim_top_bot_top);
        this.finish();

    }
}
