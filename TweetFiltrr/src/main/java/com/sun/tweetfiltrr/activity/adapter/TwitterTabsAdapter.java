package com.sun.tweetfiltrr.activity.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.sun.tweetfiltrr.fragment.fragments.FriendsTab;
import com.sun.tweetfiltrr.fragment.fragments.UserDetailsTimelineTab;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;
import com.sun.tweetfiltrr.utils.TwitterConstants;


public class TwitterTabsAdapter  extends FragmentPagerAdapter {
	ParcelableUser _currentUser;
	public TwitterTabsAdapter(FragmentManager fm, ParcelableUser currentUser_) {
		super(fm);
		_currentUser = currentUser_;
		// TODO Auto-generated constructor stub
	}


	@Override
	    public Fragment getItem(int index) {
	 
	    Bundle bundle = new Bundle();
	    bundle.putParcelable(TwitterConstants.FRIENDS_BUNDLE, _currentUser);
	    Fragment frag = null;
		
	        switch (index) {
	        case 0:
//			    frag =  new TimelineTab();
//			    frag.setArguments(bundle);
//	        	return frag;
                frag =  new UserDetailsTimelineTab();
                frag.setArguments(bundle);
                return frag;
	        case 1:

//			    frag =  new FriendsTab();
//			    frag.setArguments(bundle);
//	        	return frag;

                frag =  new FriendsTab();
                frag.setArguments(bundle);
                return frag;

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
