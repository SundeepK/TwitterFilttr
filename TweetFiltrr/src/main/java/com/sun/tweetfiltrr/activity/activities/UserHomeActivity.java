package com.sun.tweetfiltrr.activity.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.sun.tweetfiltrr.R;
import com.sun.tweetfiltrr.activity.adapter.TwitterUserHomeTabsAdapter;
import com.sun.tweetfiltrr.activity.api.ATwitterActivity;
import com.sun.tweetfiltrr.alarm.TwitterUpdateReceiver;
import com.sun.tweetfiltrr.fragment.fragments.SettingsScreen;
import com.sun.tweetfiltrr.fragment.fragments.SlidingMenuFragment;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;
import com.sun.tweetfiltrr.utils.TwitterConstants;
import com.sun.tweetfiltrr.utils.UserRetrieverUtils;

public class UserHomeActivity extends ATwitterActivity implements
        ListView.OnItemClickListener {

    private static final String TAG = UserHomeActivity.class.getName();
    private ParcelableUser _currentUser;

	@Override
	public void onCreate(Bundle arg0) {
		super.onCreate(arg0);
        setContentView(R.layout.user_home_viewpager);
        _currentUser = UserRetrieverUtils.getCurrentFocusedUser(this);
        final ViewPager _asyncBackgroundViewPager = (ViewPager) findViewById(R.id.user_view_pager);
        final TwitterUserHomeTabsAdapter _tabsAdapter =
                new TwitterUserHomeTabsAdapter(getSupportFragmentManager(), _currentUser);
		_asyncBackgroundViewPager.setAdapter(_tabsAdapter);

        final SlidingMenu menu = new SlidingMenu(this);
        final SlidingMenuFragment frag = new SlidingMenuFragment();
       // menu.setMode(SlidingMenu.SLIDING_WINDOW);
    //    menu.setTouchModeAbove(SlidingMenu.LEFT);
        menu.setFadeDegree(0.35f);
        menu.attachToActivity(this, SlidingMenu.SLIDING_WINDOW);
        menu.setMenu(R.layout.sliding_menu_fragment);
        menu.setTouchModeAbove(SlidingMenu.LEFT);
        menu.setBehindOffset(170);
        menu.setFadeEnabled(true);
        menu.setOnOpenedListener(frag);
        menu.setOnClosedListener(frag);
//        menu.setExternalOnPageChangeListener(new CustomViewAbove.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                frag.setOpactiy(positionOffset);
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//
//            }
//        });
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.menu_frame,frag)
                .commit();
	}


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Intent i = null;
        switch (position) {
            case 0:
                i = new Intent(UserHomeActivity.this, UserProfileHomeActivity.class);
                i.putExtra(TwitterConstants.FRIENDS_BUNDLE, _currentUser);
                startActivity(i);

                break;
            case 1:
                i = new Intent(UserHomeActivity.this, KeywordGroupActivity.class);
                startActivity(i);
                break;
            case 2:
                i = new Intent(UserHomeActivity.this, SettingsScreen.class);
                startActivity(i);
                break;
            default:
                break;
        }
    }


    private void scheduleAlarmReceiver() {
        Log.v(TAG, "Setting up alarm receiver");
        AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(this, 0, new Intent(this, TwitterUpdateReceiver.class),
                        PendingIntent.FLAG_CANCEL_CURRENT);
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP,  System.currentTimeMillis(), 1000 * 60 * 15, pendingIntent);
//		      setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, TwitterConstants.ALARM_TRIGGER_AT_TIME,
//		    		  TwitterConstants.ALARM_INTERVAL, pendingIntent);
    }

}
