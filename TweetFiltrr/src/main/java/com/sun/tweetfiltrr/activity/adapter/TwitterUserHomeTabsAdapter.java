package com.sun.tweetfiltrr.activity.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.sun.tweetfiltrr.fragment.fragments.CustomKeywordTimelineTab;
import com.sun.tweetfiltrr.fragment.fragments.MentionsTab;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;
import com.sun.tweetfiltrr.utils.TwitterConstants;


public class TwitterUserHomeTabsAdapter extends FragmentPagerAdapter {
    ParcelableUser _currentUser;
    private static final String[] titles = { "Filtered timeline", "Mentions" };

    public TwitterUserHomeTabsAdapter(FragmentManager fm, ParcelableUser currentUser_) {
        super(fm);
        _currentUser = currentUser_;
    }


    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    @Override
    public Fragment getItem(int index) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(TwitterConstants.FRIENDS_BUNDLE, _currentUser);
        Fragment frag;

        switch (index) {
            case 0:
               frag = new CustomKeywordTimelineTab();
               frag.setArguments(bundle);
               return frag;

//                  frag = new Fragment();
//                   return frag;

//                frag = new UserProfileFragment();
//                frag.setArguments(bundle);
//                return frag;

            case 1:
                  frag = new MentionsTab();
                   return frag;
//                frag = new MentionsTab();
//                frag.setArguments(bundle);
//                return frag;
//
//                frag = new FollowersTab();
//                frag.setArguments(bundle);
//                return frag;
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }


}
