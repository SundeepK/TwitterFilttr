package com.sun.tweetfiltrr.activity.activities;

import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.ActionBar.TabListener;
import com.sun.tweetfiltrr.R;
import com.sun.tweetfiltrr.activity.adapter.TwitterTabsAdapter;
import com.sun.tweetfiltrr.activity.api.ATwitterActivity;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;
import com.sun.tweetfiltrr.utils.UserRetrieverUtils;

public class TwitterUserProfileHome extends ATwitterActivity implements TabListener{
    private static final String TAG = TwitterUserProfileHome.class.getName();
    private ViewPager _viewPager;
    private TwitterTabsAdapter _mAdapter;
    private ParcelableUser _currentUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.twitter_filttr_home);
        _currentUser = UserRetrieverUtils.getCurrentLoggedInUser(this);

        _viewPager = (ViewPager) findViewById(R.id.user_view_pager);
        _mAdapter = new TwitterTabsAdapter(getSupportFragmentManager(), _currentUser, getSupportActionBar());
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
//                    frag =  new UserDetailsTimelineTab();
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
//                frag =  new FriendsTabA();
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
