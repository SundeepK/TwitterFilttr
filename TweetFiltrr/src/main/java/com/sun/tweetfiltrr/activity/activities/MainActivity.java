package com.sun.tweetfiltrr.activity.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.sun.imageloader.core.UrlImageLoader;
import com.sun.imageloader.core.UrlImageLoaderConfiguration;
import com.sun.tweetfiltrr.R;
import com.sun.tweetfiltrr.utils.TwitterConstants;
import com.sun.tweetfiltrr.utils.TwitterUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.ThreadPoolExecutor;

import twitter4j.auth.RequestToken;

public class MainActivity extends SherlockFragmentActivity {

    private static final String TAG = MainActivity.class.getName();
    Button _signInButton;
	ImageView _bGImage;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_screen);

		if (TwitterUtil.hasInternetConnection(this)) {

			_signInButton = (Button) findViewById(R.id.login_to_twitter);
			_bGImage = (ImageView) findViewById(R.id.login_screen_background);
			ImageView profileImage = (ImageView) findViewById(R.id.friend_profile_image);
			TextView userName = (TextView) findViewById(R.id.friend_name);
			TextView userDesc = (TextView) findViewById(R.id.friend_description);


			ThreadPoolExecutor _threadExecutor = TwitterUtil.getInstance().getGlobalExecutor();

			 		   
			   SharedPreferences sharedPreferences = PreferenceManager
						.getDefaultSharedPreferences(this);
			   String url = sharedPreferences.getString(
						TwitterConstants.AUTH_USER_SCREEN_BG, null);
			   String profileUrl = sharedPreferences.getString(
						TwitterConstants.LOGIN_PROFILE_BG, null);
			   String name = sharedPreferences.getString(
						TwitterConstants.AUTH_USER_NAME_BG, null);
			   String desc = sharedPreferences.getString(
						TwitterConstants.AUTH_USER_DESC_BG, null);
			   
			   userName.setText(name);
			   userDesc.setText(desc);

			   
				if (!TextUtils.isEmpty(profileUrl)) {
					 UrlImageLoaderConfiguration configs = new UrlImageLoaderConfiguration.Builder()
				        .setMaxCacheMemorySize(1) 
				        .setDirectoryName("/storage/sdcard0/Pictures/twitterFiltrr") 
				        .setImageQuality(100)
				         .setThreadExecutor(_threadExecutor)
				        .setImageType(CompressFormat.JPEG) 
				        .setImageConfig(Bitmap.Config.ARGB_8888) 
				        .useExternalStorage(true)
				        .build(this);
				        UrlImageLoader.getInstance().init(configs);
				        
					   UrlImageLoader  _sicImageLoader = UrlImageLoader.getInstance();					
					try {
						
						if(!TextUtils.isEmpty(url)){
							_sicImageLoader.displayImage(url, _bGImage, 1);
	
						}					
						_sicImageLoader.displayImage(profileUrl, profileImage, 1);

					} catch (NullPointerException e) {
						e.printStackTrace();
					} catch (URISyntaxException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}


		} else {
			displayConnectionAlert();
		}
		
	

	}
	
	@Override
	public void finish() {
		super.finish();
	};

	public void authenticateUser(View view) {

		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		   UrlImageLoader.getInstance().invalidate();

		   if (!sharedPreferences.getBoolean(
				TwitterConstants.PREFERENCE_TWITTER_IS_LOGGED_IN, false)) {

				new TwitterAuthenticateTask().execute();
                finish();

		} else {
             Log.v(TAG, "User has logged in before so not authenticating");
		     Intent intent = new Intent(MainActivity.this, TwitterFilttrLoggedInUserHome.class);
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
//		  fos=new FileOutputStream("/storage/extSdCard/db_dump_new5.db");
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
		 
	    @Override
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
