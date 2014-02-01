package com.sun.tweetfiltrr.activity.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;

import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.ActionBar.TabListener;
import com.sun.tweetfiltrr.R;
import com.sun.tweetfiltrr.activity.adapter.TwitterTabsAdapter;
import com.sun.tweetfiltrr.activity.api.ATwitterActivity;
import com.sun.tweetfiltrr.alarm.TwitterUpdateReceiver;
import com.sun.tweetfiltrr.fragment.fragments.FollowersTabA;
import com.sun.tweetfiltrr.fragment.fragments.FriendsTabA;
import com.sun.tweetfiltrr.utils.TwitterConstants;

public class TwitterFilttrUserHome extends ATwitterActivity implements TabListener, TwitterTabsAdapter.OnFragmentChange {
    private static final String TAG = TwitterFilttrUserHome.class.getName();
    private ViewPager _viewPager;
    private TwitterTabsAdapter _mAdapter;
    private BroadcastReceiver _broadCastReceiver;
    private int _currentTabType;
//    public void addUser(View view) {
//
//        //scheduleAlarmReceiver();
//
//        //Collection<ParcelableUser> friends = _friendDao.getEntries(null,null, FriendColumn.COLUMN_LAST_DATETIME_SYNC + " ASC, " + FriendColumn.COLUMN_PAGE_NO + " DESC " );
//        Collection<ParcelableUser> friends = _friendDao.getEntries(null, null, FriendColumn.COLUMN_MAXID.s() + " DESC, " + FriendColumn.COLUMN_LAST_DATETIME_SYNC.s() + " ASC ");
//
//        for (ParcelableUser friend : friends) {
//            Log.v(TAG, friend.toString() + " with date: " + friend.getLastUpadateDate() + " with pageNo: " + friend.getMaxId());
//        }
//
//    }

//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
//            this.moveTaskToBack(true);
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.twitter_filttr_home);

        _viewPager = (ViewPager) findViewById(R.id.pager);
        _mAdapter = new TwitterTabsAdapter(getSupportFragmentManager(), getCurrentUser(), this);
        _viewPager.setAdapter(_mAdapter);
        _broadCastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                _currentTabType = intent.getExtras().getInt(TwitterConstants.TWITTER_CURRENT_TAB);
                Log.d(TAG, "Current tab type is " + _currentTabType);
                _mAdapter.onSwitchToNextFragment(_currentTabType);
                                TwitterFilttrUserHome.this._viewPager.setCurrentItem(1);

            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(_broadCastReceiver,
                new IntentFilter(TwitterConstants.ON_NEW_TAB_BROADCAST));

    }



    private void scheduleAlarmReceiver(View view) {
        Log.v(TAG, "Setting up alarm receiver");

        AlarmManager alarmMgr = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(this, 0, new Intent(this, TwitterUpdateReceiver.class),
                        PendingIntent.FLAG_CANCEL_CURRENT);

        // Use inexact repeating which is easier on battery (system can phase events and not wake at exact times)
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 60 * 60, pendingIntent);
//		      setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, TwitterConstants.ALARM_TRIGGER_AT_TIME,
//		    		  TwitterConstants.ALARM_INTERVAL, pendingIntent);
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
            frag =  new FollowersTabA();
            frag.setArguments(bundle);


        }else{

            frag = new FriendsTabA();
            frag.setArguments(bundle);
        }

        return frag;
    }
}
