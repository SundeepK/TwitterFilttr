package com.sun.tweetfiltrr.fragment.fragments;

import android.os.Bundle;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.actionbarsherlock.app.SherlockFragment;
import com.sun.tweetfiltrr.R;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;
import com.sun.tweetfiltrr.utils.UserRetrieverUtils;

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
        _currentUser = UserRetrieverUtils.getCurrentFocusedUser(getActivity());
    }


}
