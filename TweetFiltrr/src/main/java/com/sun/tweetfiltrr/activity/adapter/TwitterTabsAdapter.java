package com.sun.tweetfiltrr.activity.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.sun.tweetfiltrr.fragment.fragments.FriendsTab;
import com.sun.tweetfiltrr.fragment.fragments.UserDetailsTimelineTab;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;
import com.sun.tweetfiltrr.utils.TwitterConstants;


public class TwitterTabsAdapter  extends FragmentPagerAdapter {
    private static final String TAG = TwitterTabsAdapter.class.getName();
    private ParcelableUser _currentUser;
    private OnFragmentChange _onFragmentChangeLis;
    private FragmentManager _fragManager;
    private Fragment _first;
    private Fragment _currentFragment;

    public TwitterTabsAdapter(FragmentManager fm, ParcelableUser currentUser_, OnFragmentChange onFragmentChangeLis_) {
		super(fm);
        _fragManager = fm;
		_currentUser = currentUser_;
        _onFragmentChangeLis = onFragmentChangeLis_;
		// TODO Auto-generated constructor stub
	}



    public void onSwitchToNextFragment(int index) {
        _fragManager.beginTransaction().remove(_currentFragment)
                .commitAllowingStateLoss();

        Bundle bundle = new Bundle();
        bundle.putParcelable(TwitterConstants.FRIENDS_BUNDLE, _currentUser);
        Fragment frag = null;
        if(index == 3){
            Log.v(TAG, "Current tab number" + index);

            frag =  new Fragment();

        }else{

            frag = new FriendsTab();
            frag.setArguments(bundle);
        }
        _currentFragment = frag;
        notifyDataSetChanged();

    }


    public interface OnFragmentChange{
        public Fragment getFragment(int index);
    }

	@Override
	    public Fragment getItem(int index) {

	    Bundle bundle = new Bundle();
	    bundle.putParcelable(TwitterConstants.FRIENDS_BUNDLE, _currentUser);
	    Fragment frag = null;

	        switch (index) {
	        case 0:

                if(_first == null){
                    _first =  new UserDetailsTimelineTab();
                    _first.setArguments(bundle);
                }

                return _first;
	        case 1:

                if(_currentFragment==null){
                    _currentFragment = new FriendsTab();
                Log.v(TAG, "current tab name" + _currentFragment.toString());
                }

                return _currentFragment;

	        case 2:

	        }
	 
	        return null;
	    }
	 
	    @Override
	    public int getCount() {
	        // get item count - equal to number of tabs
	        return 2;
	    }

    @Override
    public int getItemPosition(Object object)
    {
        if (object instanceof UserDetailsTimelineTab)
            return POSITION_UNCHANGED;

        return POSITION_NONE;
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
