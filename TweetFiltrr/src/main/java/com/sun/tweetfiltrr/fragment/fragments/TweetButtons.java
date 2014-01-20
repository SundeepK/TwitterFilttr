package com.sun.tweetfiltrr.fragment.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.actionbarsherlock.app.SherlockFragment;
import com.sun.tweetfiltrr.R;
import com.sun.tweetfiltrr.asyncretriever.retrievers.AsyncAccessTokenRetriever;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;
import com.sun.tweetfiltrr.utils.TwitterConstants;
import com.sun.tweetfiltrr.utils.TwitterUtil;

import java.util.concurrent.ExecutionException;

import twitter4j.Twitter;
import twitter4j.TwitterException;

/**
 * Created by Sundeep on 01/01/14.
 */
public class TweetButtons extends SherlockFragment {
    private final static  String TAG = TweetButtons.class.getName();
    private ParcelableUser _loggedInUser;
    private Button retweet;
    private Button copyTweet;
    private Button favTweet;
    private static View view;
    private ParcelableUser _currentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

//        View rootView = inflater.inflate(R.layout.tweet_buttons_convo, container, false);
//
//        return rootView;

        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.tweet_buttons_convo, container, false);
        } catch (InflateException e) {
        /* map is already there, just return view as it is */
        }
        return view;

    }




    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {

       // super.initControl();

            Uri uri = getActivity().getIntent().getData();
            Bundle bundle = getActivity().getIntent().getExtras();

            if (_currentUser == null) {
                _currentUser = bundle
                        .getParcelable(TwitterConstants.FRIENDS_BUNDLE);

                if (_currentUser == null) {
                    if (uri != null
                            && uri.toString().startsWith(
                            TwitterConstants.TWITTER_CALLBACK_URL)) {
                        String verifier = uri
                                .getQueryParameter(TwitterConstants.URL_PARAMETER_TWITTER_OAUTH_VERIFIER);
                        Log.v(TAG, "Verifier is " + verifier);
                        _currentUser = new AsyncAccessTokenRetriever(getActivity())
                                .execute(verifier).get();
                    } else {
                        _currentUser = new AsyncAccessTokenRetriever(getActivity())
                                .execute("").get();
                    }

                }
            }

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Twitter twitter = TwitterUtil.getInstance().getTwitter();
                try {
                    _loggedInUser = new ParcelableUser(twitter.showUser(TwitterUtil.getInstance().getCurrentLoggedInUserId(getActivity())));
                } catch (TwitterException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
        t.join();

        } catch (InterruptedException e2) {
            e2.printStackTrace();
        } catch (ExecutionException e2) {
            e2.printStackTrace();
        }
    }


}
