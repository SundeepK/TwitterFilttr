package com.sun.tweetfiltrr.utils;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.sun.tweetfiltrr.twitter.retrievers.AsyncAccessTokenRetriever;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;

import java.util.concurrent.ExecutionException;

/**
 * Created by Sundeep on 10/01/14.
 */
public class UserRetrieverUtils {


    private static final String TAG = UserRetrieverUtils.class.getName();



    public static ParcelableUser getCurrentLoggedInUser(Activity context_){

        ParcelableUser loggedInUser = getUserFromBundle(context_);

        if(loggedInUser == null){

                try {
                    loggedInUser = getCurrentUser(context_);

                    if (loggedInUser == null) {
                        Uri uri = context_.getIntent().getData();

                        loggedInUser = getCurrentUserFromURI(uri, context_);
                    }
                } catch (ExecutionException e1) {
                    e1.printStackTrace();
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }

        return loggedInUser;
    }

    private static ParcelableUser getUserFromBundle(Activity context_){
        return context_.getIntent().getParcelableExtra(TwitterConstants.FRIENDS_BUNDLE);
    }


    /**
     * Attempts to load a {@link com.sun.tweetfiltrr.parcelable.ParcelableUser} object using a {@link android.net.Uri}
     *  from a callback
     * @param uri_
     * @param context_
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    private static ParcelableUser getCurrentUserFromURI(Uri uri_, Context context_)
            throws ExecutionException, InterruptedException {
       if(uri_ == null)
           throw new IllegalArgumentException("Uri passed in cannot be null");

       if(!uri_.toString().startsWith(TwitterConstants.TWITTER_CALLBACK_URL))
           throw new  IllegalArgumentException("Uri passed in does not begin with:" + TwitterConstants.TWITTER_CALLBACK_URL);

        String verifier = uri_.getQueryParameter(TwitterConstants.URL_PARAMETER_TWITTER_OAUTH_VERIFIER);
            Log.v(TAG, "Verifier is " + verifier);
            return new AsyncAccessTokenRetriever(context_)
                    .execute(verifier).get();
    }



    /**
     * This method will current block until the logged in user is retrieved from twitter and converted to a {@link com.sun.tweetfiltrr.parcelable.ParcelableUser}
     *
     * @param context_
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    private static ParcelableUser getCurrentUser(Context context_) throws ExecutionException, InterruptedException {
        return new AsyncAccessTokenRetriever(context_)
                .execute("").get();
    }

}
