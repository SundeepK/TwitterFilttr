package com.sun.tweetfiltrr.activity.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.sun.tweetfiltrr.R;
import com.sun.tweetfiltrr.fragment.fragments.AutoSignInFragmentI;
import com.sun.tweetfiltrr.fragment.fragments.OAuthSignInFragmentI;
import com.sun.tweetfiltrr.utils.TwitterConstants;
import com.sun.tweetfiltrr.utils.TwitterUtil;

public class MainActivity extends SherlockFragmentActivity {

    private static final String TAG = MainActivity.class.getName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.generic_fragment);

        if (!TwitterUtil.hasInternetConnection(this)) {
            displayConnectionAlert();
        }
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        boolean hasSignedIn = hasUserSignedInBefore(sharedPreferences);
        Fragment loginFragment;
        if(!hasSignedIn){
            loginFragment = new OAuthSignInFragmentI();
        }else{
            loginFragment = new AutoSignInFragmentI();
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.replaceable_fragment, loginFragment);
        transaction.addToBackStack(null);
        transaction.commit();


	}

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private boolean hasUserSignedInBefore(SharedPreferences sharedPreferences){
       return   sharedPreferences.getBoolean(
                TwitterConstants.PREFERENCE_TWITTER_IS_LOGGED_IN, false);
    }

    private void extractDB() {

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

}
