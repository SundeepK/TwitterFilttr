package com.sun.tweetfiltrr.activity.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.ActionBar.TabListener;
import com.sun.tweetfiltrr.R;
import com.sun.tweetfiltrr.activity.adapter.KeywordGroupTabsAdapter;
import com.sun.tweetfiltrr.activity.api.ATwitterActivity;
import com.sun.tweetfiltrr.parcelable.ParcelableKeywordGroup;
import com.sun.tweetfiltrr.utils.TwitterConstants;

public class KeywordGroup extends ATwitterActivity implements TabListener {
    private static final String TAG = KeywordGroup.class.getName();
    private ViewPager _viewPager;
    private KeywordGroupTabsAdapter _mAdapter;
    private BroadcastReceiver _broadCastReceiver;
    private ParcelableKeywordGroup _keywordGroup;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.twitter_filttr_home);
        _viewPager = (ViewPager) findViewById(R.id.pager);
        _mAdapter = new KeywordGroupTabsAdapter(getSupportFragmentManager());
        _viewPager.setAdapter(_mAdapter);

        _broadCastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                _keywordGroup = intent.getExtras().getParcelable(TwitterConstants.PARCELABLE_KEYWORDGROUP_BUNDLE);
                Log.v(TAG, "receiving intent now" + _keywordGroup);

                _mAdapter.onSwitchToNextFragment(_keywordGroup);
                KeywordGroup.this._viewPager.setCurrentItem(1);

            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(_broadCastReceiver,
                new IntentFilter(TwitterConstants.ON_BROADCAST_GROUP_SELECTED));


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


}
