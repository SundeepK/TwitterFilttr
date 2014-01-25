package com.sun.tweetfiltrr.activity.activities;

import android.content.BroadcastReceiver;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.ActionBar.TabListener;
import com.sun.tweetfiltrr.R;
import com.sun.tweetfiltrr.activity.adapter.TwitterTabsAdapter;
import com.sun.tweetfiltrr.activity.api.ATwitterActivity;
import com.sun.tweetfiltrr.fragment.fragments.FollowersTab;
import com.sun.tweetfiltrr.fragment.fragments.FriendsTab;
import com.sun.tweetfiltrr.utils.TwitterConstants;

public class KeywordGroup extends ATwitterActivity implements TabListener, TwitterTabsAdapter.OnFragmentChange {
    private static final String TAG = KeywordGroup.class.getName();
    private ViewPager _viewPager;
    private TwitterTabsAdapter _mAdapter;
    private BroadcastReceiver _broadCastReceiver;
    private int _currentTabType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.twitter_filttr_home);
        _viewPager = (ViewPager) findViewById(R.id.pager);
        _mAdapter = new TwitterTabsAdapter(getSupportFragmentManager(), getCurrentUser(), this);
        _viewPager.setAdapter(_mAdapter);

    }



    @Override
    public void onTabSelected(Tab tab,
                              android.support.v4.app.FragmentTransaction ft) {
        _viewPager.setCurrentItem(tab.getPosition(), true);

    }

    @Override
    public void onTabUnselected(Tab tab,
                                android.support.v4.app.FragmentTransaction ft) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onTabReselected(Tab tab,
                                android.support.v4.app.FragmentTransaction ft) {
        // TODO Auto-generated method stub

    }


    @Override
    public Fragment getFragment(int index) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(TwitterConstants.FRIENDS_BUNDLE, getCurrentUser());
        Fragment frag = null;
                if(_currentTabType == 3){
            Log.v(TAG, "Current tab number" + _currentTabType);
            frag =  new FollowersTab();
            frag.setArguments(bundle);


        }else{

            frag = new FriendsTab();
            frag.setArguments(bundle);
        }

        return frag;
    }
}
