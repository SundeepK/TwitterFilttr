package com.sun.tweetfiltrr.fragment.api;

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragment;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;
import com.sun.tweetfiltrr.utils.UserRetrieverUtils;

/**
 * Created by Sundeep on 01/01/14.
 */
public abstract class ATwitterFragment extends SherlockFragment {
    private final static String  TAG = ATwitterFragment.class.getName();
    private ParcelableUser _currentUser;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _currentUser = UserRetrieverUtils.getCurrentFocusedUser(getActivity());

     }

    protected ParcelableUser getCurrentUser() {
        return _currentUser;
    }

}
