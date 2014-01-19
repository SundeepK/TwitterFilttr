package com.sun.tweetfiltrr.activity.api;

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;
import com.sun.tweetfiltrr.utils.UserRetrieverUtils;

/**
 * Created by Sundeep on 10/01/14.
 */
public class ATwitterActivity extends SherlockFragmentActivity {

    private final static String  TAG = ATwitterActivity.class.getName();
    private ParcelableUser _currentUser;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _currentUser = UserRetrieverUtils.getCurrentLoggedInUser(this);

    }

    protected ParcelableUser getCurrentUser() {
        return _currentUser;
    }

}
