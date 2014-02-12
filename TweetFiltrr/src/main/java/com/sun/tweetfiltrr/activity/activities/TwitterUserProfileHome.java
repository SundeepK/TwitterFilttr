package com.sun.tweetfiltrr.activity.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.ActionBar.TabListener;
import com.sun.tweetfiltrr.R;
import com.sun.tweetfiltrr.activity.adapter.TwitterTabsAdapter;
import com.sun.tweetfiltrr.activity.api.ATwitterActivity;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;
import com.sun.tweetfiltrr.utils.TwitterConstants;
import com.sun.tweetfiltrr.utils.UserRetrieverUtils;

import java.util.ArrayList;

public class TwitterUserProfileHome extends ATwitterActivity implements TabListener{
    private static final String TAG = TwitterUserProfileHome.class.getName();
    private ViewPager _viewPager;
    private TwitterTabsAdapter _mAdapter;
    private ParcelableUser _currentUser;
    private ArrayList<ParcelableUser> _userQueue; // not a queue but going to use it like one

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.twitter_filttr_home);

        _userQueue = UserRetrieverUtils.getUserQueue(this);

        if(_userQueue.isEmpty()){
            Log.v(TAG, "queue is empty");
            _currentUser = UserRetrieverUtils.getCurrentFocusedUser(this);
        }else{
            _currentUser = _userQueue.get(_userQueue.size() - 1);
            Log.v(TAG, "queue is not empty with user " + _currentUser);
        }

        _viewPager = (ViewPager) findViewById(R.id.user_view_pager);
        _mAdapter = new TwitterTabsAdapter(getSupportFragmentManager(), _userQueue, getSupportActionBar());
        _viewPager.setAdapter(_mAdapter);

    }

    @Override
    public void onTabSelected(Tab tab,
                              android.support.v4.app.FragmentTransaction ft) {
        //_viewPager.setCurrentItem(tab.getPosition(), true);

//        Bundle bundle = new Bundle();
//        bundle.putParcelable(TwitterConstants.FRIENDS_BUNDLE, _currentUser);
//        Fragment frag = null;
//        int tabNumber = tab.getPosition();
//
//        switch (tabNumber){
//            case 0:
//
//                frag = getSupportFragmentManager().findFragmentByTag("user");
//
//                if(frag == null){
//                    frag =  new UserDetailsTimeLineTab();
//                    frag.setArguments(bundle);
//                    getSupportFragmentManager().beginTransaction()
//                            .replace(R.id.tweets_container, frag, "user").addToBackStack("user").commit();
//                }
//
//
//                break;
//            case 1:
//                Log.v(TAG, "tab is switched to second");
//
//                frag = getSupportFragmentManager().findFragmentByTag("friend");
//
//                if(frag == null){
//                frag =  new FriendsFragment();
//                frag.setArguments(bundle);
//                getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.tweets_container, frag, "friend").addToBackStack("friend").commit();
//                }
//                break;
//        }



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
    public void onBackPressed() {

        if(!_userQueue.isEmpty()){

            ParcelableUser user = _userQueue.remove(_userQueue.size()-1);
            Log.v(TAG, "_userQueue is not empty, removing user " + user.getScreenName());
            Intent i = new Intent(TwitterUserProfileHome.this, TwitterUserProfileHome.class);
            ArrayList<ParcelableUser> users = new ArrayList<ParcelableUser>();
            users.addAll(_userQueue);
            i.putExtra(TwitterConstants.PARCELABLE_USER_QUEUE, users);
            this.startActivity(i);
        }

        super.onBackPressed();
        this.finish();

    }
    //    @Override
//    protected ActionBar loadActionBar() {
//        ActionBar bar = super.loadActionBar();
//        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
//        bar.addTab(bar.newTab().setText("Tweets")
//                .setTabListener(this));
//        bar.addTab(bar.newTab().setText("Friends")
//                .setTabListener(this));
////        bar.addTab(bar.newTab().setText("Followers")
////                .setTabListener(this));
//        return bar;
//    }
}
