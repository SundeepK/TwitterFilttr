package com.sun.tweetfiltrr.activity.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.sun.imageloader.core.UrlImageLoader;
import com.sun.tweetfiltrr.R;
import com.sun.tweetfiltrr.application.TweetFiltrrApplication;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;
import com.sun.tweetfiltrr.utils.ImageLoaderUtils;
import com.sun.tweetfiltrr.utils.TwitterConstants;
import com.sun.tweetfiltrr.utils.TwitterUtil;
import com.sun.tweetfiltrr.utils.UserRetrieverUtils;

import javax.inject.Inject;

import twitter4j.auth.RequestToken;

public class MainActivity extends SherlockFragmentActivity {

    private static final String TAG = MainActivity.class.getName();

    @Inject UrlImageLoader _sicImageLoader;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_screen);
        ((TweetFiltrrApplication)getApplication()).getObjectGraph().inject(this);

		if (TwitterUtil.hasInternetConnection(this)) {

            Button _signInButton = (Button) findViewById(R.id.login_to_twitter);
            ImageView _bGImage = (ImageView) findViewById(R.id.login_screen_background);
			ImageView profileImage = (ImageView) findViewById(R.id.friend_profile_image);
			TextView userName = (TextView) findViewById(R.id.friend_name);
			TextView userDesc = (TextView) findViewById(R.id.friend_description);

			   SharedPreferences sharedPreferences = PreferenceManager
						.getDefaultSharedPreferences(this);
			   String backgroundUrl = sharedPreferences.getString(
						TwitterConstants.AUTH_USER_SCREEN_BG, null);
			   String profileUrl = sharedPreferences.getString(
						TwitterConstants.LOGIN_PROFILE_BG, null);
			   String name = sharedPreferences.getString(
						TwitterConstants.AUTH_USER_NAME_BG, null);
			   String desc = sharedPreferences.getString(
						TwitterConstants.AUTH_USER_DESC_BG, null);
			   userName.setText(name);
			   userDesc.setText(desc);

            ImageLoaderUtils.attemptLoadImage(_bGImage, _sicImageLoader,backgroundUrl,1,null);
            ImageLoaderUtils.attemptLoadImage(profileImage, _sicImageLoader,profileUrl,1,null);
		} else {
			displayConnectionAlert();
		}
	}

	public void authenticateUser(View view) {

		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this);

        if (!sharedPreferences.getBoolean(
				TwitterConstants.PREFERENCE_TWITTER_IS_LOGGED_IN, false)) {
				new TwitterAuthenticateTask().execute();
                finish();
        }else{
            ParcelableUser user = UserRetrieverUtils.getCurrentFocusedUser(this);
            Log.v(TAG, "User has logged in before so not authenticating");
            Intent intent = new Intent(MainActivity.this, TwitterFilttrLoggedInUserHome.class);
            intent.putExtra(TwitterConstants.FRIENDS_BUNDLE, user);
            startActivity(intent);
            this.finish();
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
	
//	private void initControl() throws InterruptedException, ExecutionException {
//		Uri uri = getActivity().getIntent().getData();
//		if (uri != null
//				&& uri.toString().startsWith(
//						TwitterConstants.TWITTER_CALLBACK_URL)) {
//			String verifier = uri
//					.getQueryParameter(TwitterConstants.URL_PARAMETER_TWITTER_OAUTH_VERIFIER);
//			Log.v(TAG, "Verifier is " + verifier);
//			_userID = new AsyncAccessTokenRetriever(getActivity()).execute(verifier).get();
//		} else
//			_userID = new AsyncAccessTokenRetriever(getActivity()).execute("").get();
//	}



	class TwitterAuthenticateTask extends AsyncTask<String, String, RequestToken> {
		 
	    @Override //TODO have a timeout here just incase we cant reach twitter and display an error message
	    protected void onPostExecute(RequestToken requestToken) {
	    	Log.v("Authentication url",Uri.parse(requestToken.getAuthenticationURL()).toString());
	        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(requestToken.getAuthenticationURL()));
	        startActivity(intent);
	        finish();
	    }
	 
	    @Override
	    protected RequestToken doInBackground(String... params) {
	        return TwitterUtil.getInstance().getRequestToken();
	    }
	}

}
