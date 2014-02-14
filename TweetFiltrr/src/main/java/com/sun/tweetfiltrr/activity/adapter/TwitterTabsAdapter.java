package com.sun.tweetfiltrr.activity.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.actionbarsherlock.app.ActionBar;
import com.sun.tweetfiltrr.fragment.fragments.FollowersTab;
import com.sun.tweetfiltrr.fragment.fragments.FriendsTab;
import com.sun.tweetfiltrr.fragment.fragments.UserDetailsTimelineTab;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;
import com.sun.tweetfiltrr.utils.TwitterConstants;

import java.util.ArrayList;
import java.util.List;


public class TwitterTabsAdapter  extends FragmentPagerAdapter implements ActionBar.TabListener, ViewPager.OnPageChangeListener{
    private static final String TAG = TwitterTabsAdapter.class.getName();
    private ActionBar _actionBar;
    private FragmentManager _fragmentManager;
    private ArrayList<ParcelableUser> _users;
    private static final String[] titles = { "Tweets", "Friends", "Followers" };


   private final List<Fragment> fragments = new ArrayList<Fragment>();

    @Override
    public void onPageScrolled(int i, float v, int i2) {

    }

    @Override
    public void onPageSelected(int i) {

    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    static final class TabInfo {
        private final Class<?> clss;
        private final Bundle args;

        TabInfo(Class<?> _class, Bundle _args) {
            clss = _class;
            args = _args;
        }
    }

    public TwitterTabsAdapter(FragmentManager fm, ArrayList<ParcelableUser> currentUser_, ActionBar actionBar_) {
		super(fm);
        _users = currentUser_;
        _actionBar = actionBar_;
//        _fragmentManager=fm;
	}


	@Override
	    public Fragment getItem(int index) {

//         return _fragmentManager.findFragmentById(R.id.tweets_container);

	    Bundle bundle = new Bundle();
//	    bundle.putParcelable(TwitterConstants.FRIENDS_BUNDLE, _users);
        bundle.putParcelableArrayList(TwitterConstants.PARCELABLE_USER_QUEUE, _users);

        Fragment frag = null;

	        switch (index) {
            case 0:
                 frag =  new UserDetailsTimelineTab();
                 frag.setArguments(bundle);
                 frag.setUserVisibleHint(true);
                 return frag;
	        case 1:
                Log.v(TAG, "tab is switched to second");
                frag =  new FriendsTab();
                frag.setArguments(bundle);
                return frag;
	        case 2:
                Log.v(TAG, "tab is switched to followers");
                frag =  new FollowersTab();
                frag.setArguments(bundle);
                return frag;
	        }

	        return null;
	    }
    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }


	    @Override
	    public int getCount() {
	        // get item count - equal to number of tabs
	        return 3;
	    }

    @Override
    public float getPageWidth(int position) {
        switch (position){
            case 1:
                return 0.8f;
            case 2:
                return 0.8f;
            default:
                return 1;
      }
    }


}
